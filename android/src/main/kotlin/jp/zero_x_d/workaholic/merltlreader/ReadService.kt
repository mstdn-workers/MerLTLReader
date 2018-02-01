package jp.zero_x_d.workaholic.merltlreader

import android.app.IntentService
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.RemoteInput
import android.util.Log
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import jp.zero_x_d.workaholic.merltlreader.db.database
import jp.zero_x_d.workaholic.merltlreader.db.getAccessToken
import jp.zero_x_d.workaholic.merltlreader.status.readContent
import jp.zero_x_d.workaholic.merltlreader.status.readName
import jp.zero_x_d.workaholic.merltlreader.tls.setTLSv12
import okhttp3.FormBody
import okhttp3.OkHttpClient
import org.luaj.vm2.Globals
import org.luaj.vm2.Lua
import org.luaj.vm2.lib.jse.JsePlatform
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by D on 17/05/30.
 */
val SEND_TOOT = "send_toot"
val NF_ID = 1
class ReadService: IntentService("ReadService") {
    var running = false

    fun TextToSpeech.speak(
            text: String,
            queueMode: Int,
            params: TTSParams,
            utteranceId: String
    ): Int {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val paramsBundle = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, params.volume)
                putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, params.streamType)
            }
            speak(text, queueMode, paramsBundle, utteranceId)
        } else {
            val paramsHashMap = hashMapOf(
                    TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID to utteranceId,
                    TextToSpeech.Engine.KEY_PARAM_VOLUME to params.volume.toString(),
                    TextToSpeech.Engine.KEY_PARAM_STREAM to params.streamType.toString()
            )
            speak(text, queueMode, paramsHashMap)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        var isTTSInitted = false
        val tts = TextToSpeech(applicationContext, { status ->
            when (status) {
                TextToSpeech.SUCCESS -> isTTSInitted = true
                else -> Log.d("TextToSpeech", "Error Init")
            }
        })
        try {
            running = true
            updateNotify("待機中...")
            while (!isTTSInitted) Thread.sleep(100)
            tts.language = Locale.JAPANESE

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String?) {
                    //println("onDone")
                    when {
//                    utteranceId!!.startsWith("toot") ->
                    }
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onError(utteranceId: String?) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onStart(utteranceId: String?) {
                    //println("onStart")
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            val instance_url = sharedPref.getString("pref_key_instance_url", "")
            val tl = Timeline(
                    Credentials(instance_url, null),
                    OkHttpClient.Builder().setTLSv12()
            )

            for (status in tl) {
                while (tts.isSpeaking) Thread.sleep(100)
                if (!running) break
                val streamTypes = mapOf(
                        "STREAM_ALARM" to AudioManager.STREAM_ALARM,
                        "STREAM_DTMF" to AudioManager.STREAM_DTMF,
                        "STREAM_MUSIC" to AudioManager.STREAM_MUSIC,
                        "STREAM_NOTIFICATION" to AudioManager.STREAM_NOTIFICATION,
                        "STREAM_RING" to AudioManager.STREAM_RING,
                        "STREAM_SYSTEM" to AudioManager.STREAM_SYSTEM,
                        "STREAM_VOICE_CALL" to AudioManager.STREAM_VOICE_CALL
                )
                val streamType = sharedPref.getString(
                        "pref_key_stream_type",
                        "USE_DEFAULT_STREAM_TYPE"
                )
                val masterVolume = sharedPref.getInt(
                        "pref_key_volume_master",
                        100
                ) / 100.0F
                val nameVolumeM = sharedPref.getInt(
                        "pref_key_volume_name",
                        100
                ) / 100.0F
                val content_params = TTSParams(
                        volume = masterVolume,
                        streamType = streamTypes[streamType]!!
                )
                val name_params = TTSParams(
                        volume = masterVolume * nameVolumeM,
                        streamType = streamTypes[streamType]!!
                )

                val speech_str = status.readContent
                println(speech_str)
                LTLActivity.add(status)
                tts.speak(
                        status.readName,
                        TextToSpeech.QUEUE_ADD,
                        name_params,
                        status.id.toString() + status.account?.userName
                )
                tts.speak(
                        speech_str,
                        TextToSpeech.QUEUE_ADD,
                        content_params,
                        "toot" + status.id.toString()
                )
                updateNotify(
                        status?.account?.displayName + ": "
                                + status.readContent
                )
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        } finally {
            tts.stop()
            tts.shutdown()
        }
    }

    var isTTSInitted: Boolean = false

    val nm by lazy { NotificationManagerCompat.from(applicationContext) }

    val ltlActivityIntent by lazy { PendingIntent.getActivity(
            applicationContext,
            2,
            Intent(applicationContext, LTLActivity::class.java),
            0)
    }

    val deleteIntent by lazy{ PendingIntent.getBroadcast(
            applicationContext,
            0,
            Intent(this, DeleteReceiver::class.java),
            0
    ) }
    val tootPendingIntent by lazy { PendingIntent.getBroadcast(
            applicationContext,
            1,
            Intent(this, TootReceiver::class.java),
            0
    ) }

    val remoteInput by lazy { RemoteInput.Builder(SEND_TOOT)
            .setLabel(getString(R.string.compose_form_placeholder))
            .build() }
    val action by lazy { NotificationCompat.Action.Builder(android.R.drawable.ic_menu_send,
            getString(R.string.compose_form_publish_loud), tootPendingIntent)
            .addRemoteInput(remoteInput)
            .build() }

    val notificationBuilder by lazy { NotificationCompat.Builder(applicationContext)
            .setVisibility(VISIBILITY_PUBLIC)
            .apply {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                    setSmallIcon(applicationInfo.icon)
                } else {
                    setSmallIcon(R.drawable.ic_mastodon_logo)
                }
            }
            .setSmallIcon(applicationInfo.icon)
            .setContentIntent(ltlActivityIntent)
            .addAction(android.R.drawable.ic_menu_delete, "Kill", deleteIntent)
            .apply {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    addAction(action)
                }
            }
            .setDeleteIntent(deleteIntent)
            .setAutoCancel(true) }

    fun updateNotify(s: String) {
        val n = notificationBuilder
                .setContentText(s)
                .build()
        nm.notify(NF_ID, n)
        startForeground(NF_ID, n)
    }

    override fun onDestroy() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        running = false
        super.onDestroy()
    }

    class DeleteReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val reader = Intent(context?.applicationContext, ReadService::class.java)
            context?.stopService(reader)
        }
    }
    class TootReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            val toot = remoteInput?.getString(SEND_TOOT)
            thread {
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
                val instance_url = sharedPref.getString("pref_key_instance_url", null)
                val mail = sharedPref.getString("pref_key_email", null)
                val accessToken = context!!.database.getAccessToken(instance_url, mail)
                val client: MastodonClient =
                        MastodonClient.Builder(
                                instance_url,
                                OkHttpClient.Builder().setTLSv12(),
                                Gson())
                        .accessToken(requireNotNull(accessToken).accessToken)
                        .build()
                client.post("statuses", FormBody.Builder().add("status", toot).build())
            }.join()
        }
    }
}