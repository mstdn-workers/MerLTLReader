package jp.zero_x_d.workaholic.merltlreader.app

import jp.zero_x_d.workaholic.merltlreader.*
import jp.zero_x_d.workaholic.merltlreader.engine.ITTSEngine
import jp.zero_x_d.workaholic.merltlreader.engine.JTalkConnecter
import jp.zero_x_d.workaholic.merltlreader.status.readContent
import jp.zero_x_d.workaholic.merltlreader.status.readName
import org.luaj.vm2.LuaError


interface ILTLReaderApp {
    var preferences: Preferences?

    fun login(): LoginData
    fun launch(vararg args: String): Unit

    fun run(vararg args: String) {
        preferences = Preferences("mstdn-workers.com")
        val credentials =
                Credentials.Builder(preferences!!)
                        .loadOrAppRegister().loadOrLogin {
                    mailPass = login()
                }

        val tl = Timeline(credentials)
        val tts_engine: ITTSEngine = JTalkConnecter(emptyMap())
        for (status in tl) {
            try {
                status.readName?.let { tts_engine.say_username(it) }
                status.readContent?.let { tts_engine.say_content(it) }
                println(status.readContent)
            } catch (e: LuaError) {
                e.printStackTrace()
                tts_engine.say("るあえらーが発生しました")
            }
        }
    }
}