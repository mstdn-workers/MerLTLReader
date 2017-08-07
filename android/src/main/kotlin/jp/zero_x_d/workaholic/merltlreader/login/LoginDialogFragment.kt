package jp.zero_x_d.workaholic.merltlreader.login

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.widget.EditText
import android.widget.Toast
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import jp.zero_x_d.workaholic.merltlreader.Credentials
import jp.zero_x_d.workaholic.merltlreader.LoginData
import jp.zero_x_d.workaholic.merltlreader.Preferences
import jp.zero_x_d.workaholic.merltlreader.R
import jp.zero_x_d.workaholic.merltlreader.db.*


/**
 * Created by D on 17/08/07.
 */
class LoginDialogFragment: DialogFragment() {
    class MyAsyncTask(val context: Context, val f: (Context) -> Unit): AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg p0: Unit?) {
            this.f(context)
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        // Get the layout inflater
        val inflater = activity.layoutInflater

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        val view = inflater.inflate(R.layout.dialog_signin, null)
        PreferenceManager.getDefaultSharedPreferences(activity)
                .getString("pref_key_email", null)
                ?.let { view.findViewById<EditText>(R.id.email).setText(it) }

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.signin, { dialog, id ->
                    val handler = Handler()
                    val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
                    val instance_url = sharedPref.getString("pref_key_instance_url", "")
                    val mail = view.findViewById<EditText>(R.id.email).text.toString()
                    sharedPref.edit().putString("pref_key_email", mail).apply()
                    val pass = view.findViewById<EditText>(R.id.password).text.toString()

                    val db = activity.database

                    val pref = Preferences(
                            appName = getString(R.string.app_name),
                            instance_url = instance_url,
                            baseDir = activity.dataDir
                    )
                    MyAsyncTask(activity) { context ->
                        try {
                            val credentials = Credentials.Builder(pref).loadOrAppRegister(
                                    loadFunction = { db.getAppRegistration(instance_url) },
                                    saveFunction = { db.setAppRegistration(instance_url, it) }
                            ).loadOrLogin(
                                    loadFunction = { db.getAccessToken(instance_url, mail) },
                                    setUserNameAndPasswordFunction = { mailPass = LoginData(mail, pass) },
                                    saveFunction = { token -> db.setAccessToken(instance_url, mail, token) }
                            ).create()
                            if (credentials.accessToken != null) {
                                handler.post {
                                    Toast.makeText(context, "Login succeeded", Toast.LENGTH_SHORT)
                                            .show()
                                }
                            }
                        } catch (e: Mastodon4jRequestException) {
                            handler.post {
                                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }.execute()
                })
                .setNegativeButton(R.string.cancel, { dialog, _ -> dialog.cancel() })
        return builder.create()
    }

    override fun onPause() {
        super.onPause()

        dismiss()
    }
}