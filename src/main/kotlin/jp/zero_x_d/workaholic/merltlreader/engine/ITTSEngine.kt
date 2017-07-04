package jp.zero_x_d.workaholic.merltlreader.engine

import jp.zero_x_d.workaholic.merltlreader.Preferences

/**
 * Created by D on 17/06/20.
 */
interface ITTSEngine {
    fun test(): Boolean
    fun say(readtext: String)
    fun say_username(username: String)
    fun say_content(content: String)

    enum class Engines {
        JTALK,
        BOUYOMI,
        AUTO
    }

    private object SilentEngine: ITTSEngine {
        override fun test(): Boolean = true
        override fun say(readtext: String) {}
        override fun say_username(username: String) {}
        override fun say_content(content: String) {}
    }

    companion object {
        fun getEngineFromPreferences(preferences: Preferences): ITTSEngine {
            return when(preferences.tts_engine) {
                ITTSEngine.Engines.JTALK -> JTalkConnecter(emptyMap())
                ITTSEngine.Engines.BOUYOMI -> BouyomiConnecter(emptyMap())
                Engines.AUTO -> autoSelect()
            }
        }

        private fun autoSelect(): ITTSEngine {
            return listOf(
                    JTalkConnecter(emptyMap()),
                    BouyomiConnecter(emptyMap()),
                    SilentEngine
            ).filter { it.test() }.first()
        }
    }
}