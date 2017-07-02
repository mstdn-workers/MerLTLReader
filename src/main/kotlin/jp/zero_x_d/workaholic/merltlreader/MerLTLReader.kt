package jp.zero_x_d.workaholic.merltlreader

import jp.zero_x_d.workaholic.merltlreader.app.CLIApp
import jp.zero_x_d.workaholic.merltlreader.app.ILTLReaderApp
import jp.zero_x_d.workaholic.merltlreader.app.guifallback.GUIApp
import net.harawata.appdirs.AppDirsFactory
import java.io.File


object MerLTLReader {
    val appName = "MerLTLReader"
    private val appVersion = "alpha"
    private val appAuthor = "called.0xd"

    private var instance: ILTLReaderApp? = null
    val appInstance: ILTLReaderApp
        get() = requireNotNull(instance)

    val preferences: Preferences
        get() = requireNotNull(appInstance?.preferences)


    val appDir by lazy {
        AppDirsFactory.getInstance().getUserConfigDir(appName, appVersion, appAuthor).let {
            File(it).apply { if (!exists()) mkdirs() }
        }
    }

    @JvmStatic fun main(arg: Array<String>) {
        instance = if (System.console() != null) {
            CLIApp()
        } else { // Fallback code
            GUIApp()
        }
        instance!!.launch(*arg)
    }
}
