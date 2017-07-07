package jp.zero_x_d.workaholic.merltlreader

import com.sys1yagi.mastodon4j.api.Scope
import java.io.File

data class Preferences(
        val appName: String,
        val baseDir: File,
        val instance_url: String,
        val scope: Scope = Scope(Scope.Name.ALL),
        val tts_engine: String = "auto"
) {
    val dir: File
        get() = File(baseDir, instance_url).apply {
            if (!exists()) mkdir()
        }

    companion object {
        class Builder {
            var appName: String? = null
            var baseDir: File? = null

            fun create(): Preferences {
                return Preferences(
                        appName = requireNotNull(appName),
                        baseDir = requireNotNull(baseDir),
                        instance_url = "mstdn-workers.com"
                )
            }
        }

        fun fromArgs(
                f: Builder.() -> Unit,
                vararg args: String
        ): Preferences {
            val b = Builder()
            // TODO process args for preferences
            b.f()
            return b.create()
        }
    }
}