package jp.zero_x_d.workaholic.merltlreader.app.guifallback

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import jp.zero_x_d.workaholic.merltlreader.Credentials
import jp.zero_x_d.workaholic.merltlreader.MerLTLReader
import jp.zero_x_d.workaholic.merltlreader.Timeline
import jp.zero_x_d.workaholic.merltlreader.engine.ITTSEngine
import jp.zero_x_d.workaholic.merltlreader.script.Lua
import jp.zero_x_d.workaholic.merltlreader.status.readContent
import kotlin.concurrent.thread

class GUIApp: Application() {
    override fun start(primaryStage: Stage) {
        Platform.setImplicitExit(true)
        val rootBorderPane = BorderPane()
        val scene = Scene(rootBorderPane, 640.0, 480.0)
        val stackPane = StackPane()
        val logArea = TextArea().apply {
            textProperty().addListener { _ ->
                scrollTop = Double.MAX_VALUE
            }
        }
        val menuBar = MenuBar().apply {
            menus += Menu("Script").apply {
                items += MenuItem("reload").apply {
                    setOnAction { _ -> Lua.reload() }
                }
            }
        }

        MerLTLReader.onStatus += { status ->
            Platform.runLater {
                logArea.appendText(status.readContent + "\n")
            }
        }
        stackPane.children.add(logArea)

        rootBorderPane.top = menuBar
        rootBorderPane.center = stackPane
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
                        .loadOrAppRegister()/*.loadOrLogin {
                    val dialog = LoginDialog(preferences)
                    mailPass = requireNotNull(dialog.showAndWait())
                }*/.create()

        thread {
            val tl = Timeline(credentials)
            val tts = ITTSEngine.getEngineFromPreferences(MerLTLReader.preferences!!)
            MerLTLReader.runWithTTS(tl, tts)
        }
    }
}