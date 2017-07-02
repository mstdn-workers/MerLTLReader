package jp.zero_x_d.workaholic.merltlreader.app.guifallback

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.control.ButtonBar.ButtonData
import jp.zero_x_d.workaholic.merltlreader.LoginData
import jp.zero_x_d.workaholic.merltlreader.Preferences


/**
 * Created by D on 17/07/02.
 */
class LoginDialog(preferences: Preferences) {
    val dialog = Dialog<LoginData>().apply {
        title = "Login"
        headerText = "Login for " + preferences.instance_url

        val loginButtonType = ButtonType("Login", ButtonData.OK_DONE)
        dialogPane.buttonTypes.addAll(loginButtonType, ButtonType.CANCEL)

        val mailAddress = TextField().apply { promptText = "user@example.com" }
        val password = PasswordField().apply { promptText = "your password" }
        dialogPane.content = GridPane().apply {
            hgap = 10.0
            vgap = 10.0
            padding = Insets(20.0, 150.0, 10.0, 10.0)

            add(Label("Mail:"), 0, 0)
            add(mailAddress, 1, 0)
            add(Label("Password:"), 0, 1)
            add(password, 1, 1)
        }

        val loginButton = dialogPane.lookupButton(loginButtonType).apply {
            isDisable = true
        }
        mailAddress.textProperty().addListener { _, _, newValue ->
            loginButton.isDisable = newValue.trim().isEmpty()
        }

        Platform.runLater({ mailAddress.requestFocus() })

        setResultConverter {
            when(it) {
                loginButtonType -> LoginData(mailAddress.text, password.text)
                else -> null
            }
        }
    }

    fun showAndWait(): LoginData? =
            dialog.showAndWait().orElse(null)
}