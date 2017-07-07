package jp.zero_x_d.workaholic.merltlreader

import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.PreferenceFragment
import android.util.Log

/**
 * Created by quartz on 17/05/20.
 */
class SettingsFragment: PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)

        val url_edittext = findPreference("pref_key_instance_url") as EditTextPreference
        url_edittext.summary = url_edittext.text
        // Log.i(getString(R.string.pref_title_instance_url), url_edittext.text)
    }


}