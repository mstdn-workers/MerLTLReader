package jp.zero_x_d.workaholic.merltlreader

import com.sys1yagi.mastodon4j.api.entity.Status
import javafx.application.Application
import jp.zero_x_d.workaholic.merltlreader.app.guifallback.GUIApp
import jp.zero_x_d.workaholic.merltlreader.engine.ITTSEngine
import jp.zero_x_d.workaholic.merltlreader.status.readContent
import jp.zero_x_d.workaholic.merltlreader.status.readName
import net.harawata.appdirs.AppDirsFactory
import org.luaj.vm2.LuaError
import java.io.File


object MerLTLReader {
    val appName = "MerLTLReader"
    private val appVersion = "alpha"
    private val appAuthor = "called_D"

    val appDir by lazy {
        AppDirsFactory
                .getInstance()
                .getUserConfigDir(appName, appVersion, appAuthor)
                .let {
                    File(it).apply { if (!exists()) mkdirs() }
                }
    }

    var preferences: Preferences? = null

    val onStatus = mutableListOf<(Status) -> Unit>({ status ->
        println(status.readContent)
    })

    @JvmStatic fun main(args: Array<String>) {
        preferences = Preferences.fromArgs(*args)
        if (System.console() == null) {
            Application.launch(GUIApp::class.java, *args)
        } else {
            launch(*args)
        }
    }

    private fun launch(vararg args: String) {
        val credentials =
                Credentials.Builder(preferences!!)
                        .loadOrAppRegister().loadOrLogin {
                    mailPass = login(preferences)
                }
        val tl = Timeline(credentials)
        val tts = ITTSEngine.getEngineFromPreferences(preferences!!)

        runWithTTS(tl, tts)
    }

    fun runWithTTS(tl: Timeline, tts: ITTSEngine) {
        for (status in tl) {
            try {
                onStatus.forEach { it(status) }
                status.readName?.let { tts.say_username(it) }
                status.readContent?.let { tts.say_content(it) }
            } catch (e: LuaError) {
                e.printStackTrace()
                tts.say("るあえらーが発生しました")
            }
        }
    }

    private fun login(preferences: Preferences): LoginData {
        print("login for " + preferences.instance_url)
        val console = requireNotNull(System.console())
        val mail = console.readLine("mail: ")?.toString()
        val pass = console.readPassword("password for %s: ", mail)?.toString()
        return LoginData(requireNotNull(mail), requireNotNull(pass))
    }
}
