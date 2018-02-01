package jp.zero_x_d.workaholic.merltlreader

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import jp.zero_x_d.workaholic.merltlreader.db.database
import jp.zero_x_d.workaholic.merltlreader.db.getAccessToken
import jp.zero_x_d.workaholic.merltlreader.status.readContent
import jp.zero_x_d.workaholic.merltlreader.tls.setTLSv12
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import kotlin.concurrent.thread

class LTLActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val toots: ArrayList<TootAdapter.Toot> = arrayListOf()
    private val adapter = TootAdapter(toots)
    private val ltlRecyclerView by lazy { findViewById<RecyclerView>(R.id.LTL)!! }

    class PostTask(
            builder: MastodonClient.Builder,
            val doInBackground_: PostTask.() -> Unit = {},
            val onPostExecute_: (Response?) -> Unit = { _ -> },
            val onExeption: PostTask.(Mastodon4jRequestException) -> Unit = { e ->
                e.printStackTrace()
            }
    ): AsyncTask<String, Unit, Response>() {
        val client by lazy { builder.build() }
        override fun doInBackground(vararg p0: String?): Response? {
            try {
                doInBackground_()
                return client.post(
                        "statuses",
                        FormBody.Builder().add("status", p0[0]).build()
                )
            } catch (e: Mastodon4jRequestException) {
                onExeption(e)
            }
            return null
        }
        override fun onPostExecute(result: Response?) {
            onPostExecute_(result)
            super.onPostExecute(result)
        }
        fun post(toot: String): AsyncTask<String, Unit, Response>? {
            return execute(toot)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ltl)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab_startReading = findViewById<FloatingActionButton>(R.id.fab_start)
        val fab_stopReading = findViewById<FloatingActionButton>(R.id.fab_stop)
        fab_startReading.setOnClickListener { _ ->
            startActivity(Intent(applicationContext, LauncherActivity::class.java))
            fab_startReading.hide()
            fab_stopReading.show()
        }
        fab_stopReading.setOnClickListener { _ ->
            sendBroadcast(Intent(applicationContext, ReadService.DeleteReceiver::class.java))
            fab_stopReading.hide()
            fab_startReading.show()
        }


        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val tootEditText = findViewById<EditText>(R.id.edit_reply)
        val sendButton = findViewById<ImageButton>(R.id.button_send)
        sendButton.setOnClickListener { _ ->
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            val instanceURL: String? = sharedPref.getString("pref_key_instance_url", null)
            val email: String? = sharedPref.getString("pref_key_email", null)
            if ((instanceURL != null) && (email != null)) {
                val text = tootEditText.text.toString()
                if (text.isNotBlank()) {
                    val accessToken = database.getAccessToken(instanceURL, email)
                    if (accessToken != null) {
                        val handler = Handler()
                        PostTask(
                                builder = MastodonClient.Builder(
                                        instanceURL,
                                        OkHttpClient.Builder().setTLSv12(),
                                        Gson()
                                ).accessToken(accessToken.accessToken),
                                onPostExecute_ = { response ->
                                    if (response?.isSuccessful ?: false) {
                                        tootEditText.text.clear()
                                    } else {
                                        Toast.makeText(this, "post failed", Toast.LENGTH_LONG).show()
                                    }
                                },
                                onExeption = { _ ->
                                    handler.post {
                                        Toast.makeText(applicationContext, "post failed", Toast.LENGTH_LONG).show()
                                    }
                                }
                        ).post(text)
                    } else {
                        Toast.makeText(
                                this,
                                getString(R.string.requireLogin),
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        ltlRecyclerView.adapter = adapter
    }


    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.ltl, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
//            R.id.nav_camera -> {
//                // Handle the camera action
//            }
//            R.id.nav_gallery -> {
//
//            }
//            R.id.nav_slideshow -> {
//
//            }
            R.id.nav_ltl -> {
                startActivity(Intent(applicationContext, LauncherActivity::class.java))
            }
            R.id.nav_tts -> {
                val tts_settings_intent = Intent()
                tts_settings_intent.action = "com.android.settings.TTS_SETTINGS"
                tts_settings_intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(tts_settings_intent)
            }
            R.id.nav_manage -> {
                val intent = Intent(
                        this, InstanceSettingActivity::class.java)
                startActivity(intent)
            }
            /*
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
            */
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        instance = this
    }

    override fun onPause() {
        instance?.toots?.apply { synchronized(this) { clear() }}
        instance?.adapter?.notifyDataSetChanged()
        instance = null
        super.onPause()
    }

    companion object {
        private var instance: LTLActivity? = null
        val handler = Handler()

        fun add(toot: Status) {
            handler.post {
                instance?.apply {
                    // http://qiita.com/u_nation/items/282e3220ae863e6d21e5
                    val scrollTop = !ltlRecyclerView.canScrollVertically(-1)
                    synchronized(toots) {
                        toots.add(0, TootAdapter.Toot(
                                toot.account!!.displayName,
                                toot.readContent!!,
                                toot.account!!.avatar
                        ))
                    }
                    adapter.notifyItemInserted(0)
                    if (scrollTop) ltlRecyclerView.scrollToPosition(0)
                }
            }
        }
    }
}
