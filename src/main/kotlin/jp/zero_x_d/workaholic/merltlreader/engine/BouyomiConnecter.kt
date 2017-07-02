package jp.zero_x_d.workaholic.merltlreader.engine

import org.snowink.bouyomichan.BouyomiChan4J

/**
 * Created by jun on 2017/07/01.
 */
class BouyomiConnecter(val engine_params: Map<String, Any>): ITTSEngine {

    private val b4j = BouyomiChan4J()

    private fun say(readtext: String, params: Map<String, Any>) {
        /*
         * 音量・速度・音程・声質を指定して棒読みちゃんを読み上げさせます。
         * @param volume 読み上げる音量(0～100, デフォルトは-1)
         * @param speed 読み上げる速度(50～300, デフォルトは-1)
         * @param tone 読み上げる音程(50～200, デフォルトは-1)
         * @param voice 読み上げる音程声質(1～8, デフォルトは0)
         * @param message 読ませたい文字列
         */
        b4j.talk(
                params.getOrDefault("volume", -1) as Int,
                params.getOrDefault("speed", -1) as Int,
                params.getOrDefault("tone", -1) as Int,
                params.getOrDefault("voice", 0) as Int,
                readtext
        )
    }
    private val username_voice_params = mapOf(
            "voice" to 2,
            "speed" to 120)
    private val content_voice_params = mapOf(
            "voice" to 0,
            "speed" to 140)

    override fun say_content(content: String) {
        say(content, content_voice_params)
    }

    override fun say_username(username: String) {
        say(username, username_voice_params)
    }

    override fun say(readtext: String) {
        say(readtext, emptyMap())
    }

}
