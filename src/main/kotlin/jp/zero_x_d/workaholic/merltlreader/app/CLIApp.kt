package jp.zero_x_d.workaholic.merltlreader.app

import jp.zero_x_d.workaholic.merltlreader.LoginData
import jp.zero_x_d.workaholic.merltlreader.Preferences

class CLIApp: ILTLReaderApp {
    override fun launch(vararg args: String) {
        run(*args)
    }

    override var preferences: Preferences? = null

    override fun login(): LoginData {
        val console = requireNotNull(System.console())
        val mail = console.readLine("mail: ")?.toString()
        val pass = console.readPassword("password for %s: ", mail)?.toString()
        return LoginData(requireNotNull(mail), requireNotNull(pass))
    }
}