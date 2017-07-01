import jp.zero_x_d.workaholic.merltlreader.Preferences
import jp.zero_x_d.workaholic.merltlreader.Timeline
import jp.zero_x_d.workaholic.merltlreader.engine.ITTSEngine
import jp.zero_x_d.workaholic.merltlreader.engine.JTalkConnecter
import jp.zero_x_d.workaholic.merltlreader.status.readContent
import jp.zero_x_d.workaholic.merltlreader.status.readName
import org.luaj.vm2.LuaError


fun main(arg: Array<String>) {
    val tl = Timeline(Preferences("mstdn-workers.com"))
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