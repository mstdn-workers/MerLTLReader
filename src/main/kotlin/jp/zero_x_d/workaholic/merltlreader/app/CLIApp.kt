package jp.zero_x_d.workaholic.merltlreader.app

import com.sys1yagi.mastodon4j.api.entity.Status
import jp.zero_x_d.workaholic.merltlreader.LoginData
import jp.zero_x_d.workaholic.merltlreader.Preferences
import jp.zero_x_d.workaholic.merltlreader.status.readContent

class CLIApp: ILTLReaderApp {
    override var preferences: Preferences? = null


    override fun launch(vararg args: String) {
        run(*args)
    }

    override fun login(): LoginData {
        val console = requireNotNull(System.console())
        val mail = console.readLine("mail: ")?.toString()
        val pass = console.readPassword("password for %s: ", mail)?.toString()
        return LoginData(requireNotNull(mail), requireNotNull(pass))
    }

    override fun onStatus(stats: Status) {
        println(stats.readContent)
    }
}