package jp.zero_x_d.workaholic.merltlreader.app.guifallback

import javafx.application.Application
import javafx.stage.Stage
import jp.zero_x_d.workaholic.merltlreader.LoginData
import jp.zero_x_d.workaholic.merltlreader.MerLTLReader
import jp.zero_x_d.workaholic.merltlreader.Preferences
import jp.zero_x_d.workaholic.merltlreader.app.ILTLReaderApp


class GUIApp: ILTLReaderApp {
    override var preferences: Preferences? = null

    override fun login(): LoginData {
        val dialog = LoginDialog(MerLTLReader.preferences)
        val result = dialog.showAndWait()
        return requireNotNull(result)
    }

    override fun launch(vararg args: String) {
        Application.launch(FXApp::class.java, *args)
    }
    class FXApp: Application() {
        override fun start(primaryStage: Stage?) {
            MerLTLReader.appInstance.run(*parameters.raw.toTypedArray())
        }
    }
}