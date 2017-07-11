package jp.zero_x_d.workaholic.merltlreader

import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.PreferenceFragment


/**
 * Created by quartz on 17/05/20.
 */
class SettingsFragment: PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)

        fun validInstanceURL(urlString: String?): Boolean {
            if (urlString.isNullOrBlank()) return false
            // TODO まともに有効なインスタンスURLか判定する
            return true
        }
        fun setSummary4IinstanceURL(
                pref: EditTextPreference,
                newValue: String?
        ): Boolean {
            val defaultSummary = getString(R.string.pref_summary_instance_url)
            return if (validInstanceURL(newValue)) {
                pref.summary = "$defaultSummary: $newValue"
                true
            } else {
                false
            }
        }

        val pref_instance_url = findPreference("pref_key_instance_url") as EditTextPreference
        setSummary4IinstanceURL(pref_instance_url, pref_instance_url.text)
        pref_instance_url.setOnPreferenceChangeListener { pref, any ->
            setSummary4IinstanceURL(pref as EditTextPreference, any as String?)
        }
    }
}