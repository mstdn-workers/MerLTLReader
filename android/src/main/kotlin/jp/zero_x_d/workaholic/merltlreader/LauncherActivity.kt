package jp.zero_x_d.workaholic.merltlreader

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/**
 * Created by D on 17/06/02.
 */
class LauncherActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reader = Intent(applicationContext, ReadService::class.java)
        startService(reader)
        finish()
    }
}