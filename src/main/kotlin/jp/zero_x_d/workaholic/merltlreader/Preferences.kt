package jp.zero_x_d.workaholic.merltlreader

import com.sys1yagi.mastodon4j.api.Scope
import java.io.File
import jp.zero_x_d.workaholic.merltlreader.MerLTLReader.appDir

data class Preferences(
        val instance_url: String,
        val scope: Scope = Scope(Scope.Name.ALL)
) {
    val dir: File
        get() = File(appDir, instance_url).apply {
            if (!exists()) mkdir()
        }
}