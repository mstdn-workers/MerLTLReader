package jp.zero_x_d.workaholic.merltlreader.app.guifallback

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import jp.zero_x_d.workaholic.merltlreader.*
import jp.zero_x_d.workaholic.merltlreader.engine.ITTSEngine
import jp.zero_x_d.workaholic.merltlreader.status.readContent
import kotlin.concurrent.thread

class GUIApp: Application() {
    override fun start(primaryStage: Stage) {
        Platform.setImplicitExit(true)
        val root = StackPane()
        val scene = Scene(root, 640.0, 480.0)
        var logArea = TextArea().apply {
            textProperty().addListener { _ ->
                scrollTop = Double.MAX_VALUE
            }
        }

        MerLTLReader.onStatus += { status ->
            logArea?.appendText(status.readContent + "\n")
        }

        root.children.add(logArea)
        primaryStage.scene = scene

        primaryStage.setOnCloseRequest { event ->
            Platform.exit()
            println("close")
            // TODO あとできちんとInterruptする
            System.exit(0)
        }
        primaryStage.show()

        val credentials =
                Credentials.Builder(MerLTLReader.preferences!!)
                        .loadOrAppRegister().loadOrLogin {
                    val dialog = LoginDialog(preferences)
                    mailPass = requireNotNull(dialog.showAndWait())
                }

        thread {
            val tl = Timeline(credentials)
            val tts = ITTSEngine.getEngineFromPreferences(MerLTLReader.preferences!!)
            MerLTLReader.runWithTTS(tl, tts)
        }
    }
}