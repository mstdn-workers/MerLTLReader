package jp.zero_x_d.workaholic.merltlreader.app.guifallback

import com.sys1yagi.mastodon4j.api.entity.Status
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import jp.zero_x_d.workaholic.merltlreader.LoginData
import jp.zero_x_d.workaholic.merltlreader.MerLTLReader
import jp.zero_x_d.workaholic.merltlreader.Preferences
import jp.zero_x_d.workaholic.merltlreader.app.ILTLReaderApp
import jp.zero_x_d.workaholic.merltlreader.status.readContent
import kotlin.concurrent.thread


private var logArea: TextArea? = null

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
        override fun start(primaryStage: Stage) {
            val root = StackPane()
            val scene = Scene(root, 640.0, 480.0)
            logArea = TextArea().apply {
                textProperty().addListener { _ ->  
                    scrollTop = Double.MAX_VALUE
                }
            }
            root.children.add(logArea)
            primaryStage.scene = scene
            thread {
                MerLTLReader.appInstance.run(*parameters.raw.toTypedArray())
            }
            primaryStage.setOnCloseRequest { e ->
                Platform.exit()
                println("close")
                // TODO あとできちんとInterruptする
                System.exit(0)
            }
            primaryStage.show()
        }
    }

    override fun onStatus(stats: Status) {
        logArea?.appendText(stats.readContent + "\n")
        println(stats.readContent)
    }
}