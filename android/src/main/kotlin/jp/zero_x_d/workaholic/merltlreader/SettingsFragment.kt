package jp.zero_x_d.workaholic.merltlreader

import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import jp.zero_x_d.workaholic.merltlreader.db.database
import jp.zero_x_d.workaholic.merltlreader.db.getAccessToken
import jp.zero_x_d.workaholic.merltlreader.login.LoginDialogFragment


/**
 * Created by quartz on 17/05/20.
 */
class SettingsFragment: PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)

        fun validInstanceURL(urlString: String?): Boolean {
            if (urlString.isNullOrBlank()) return false
            return android.util.Patterns.WEB_URL.matcher(urlString).matches()
        }

        val pref_instance_url = findPreference("pref_key_instance_url") as EditTextPreference
        fun setSummary4InstanceURL(newValue: String?) {
            val defaultSummary = getString(R.string.pref_summary_instance_url)
            pref_instance_url.summary = "$defaultSummary: $newValue"
        }
        setSummary4InstanceURL(pref_instance_url.text)
        pref_instance_url.setOnPreferenceChangeListener { pref, newValue ->
            if (validInstanceURL(newValue as String?)) {
                setSummary4InstanceURL(newValue as String?)
                val mail = PreferenceManager.getDefaultSharedPreferences(activity)
                        .getString("pref_key_email", null)
                if (mail == null
                        || activity.database.getAccessToken(newValue as String, mail) == null) {
                    LoginDialogFragment().show(fragmentManager, "login")
                }
                true
            } else false
        }

    }
}