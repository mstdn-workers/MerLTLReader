package jp.zero_x_d.workaholic.merltlreader

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceActivity

/**
 * Created by quartz on 17/05/20.
 */
class InstanceSettingActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }
}