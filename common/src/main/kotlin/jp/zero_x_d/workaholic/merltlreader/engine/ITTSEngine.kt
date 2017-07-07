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

    private object SilentEngine: ITTSEngine {
        override fun test(): Boolean = true
        override fun say(readtext: String) {}
        override fun say_username(username: String) {}
        override fun say_content(content: String) {}
    }

    companion object {
        val engines = mutableMapOf<String, ITTSEngine>()

        fun getEngineFromPreferences(preferences: Preferences): ITTSEngine {
            if (engines.containsKey(preferences.tts_engine))
                return engines[preferences.tts_engine]!!
            return when(preferences.tts_engine) {
                "auto" -> autoSelect()
                else -> SilentEngine
            }
        }

        private fun autoSelect(): ITTSEngine {
            // JTalkConnecter(emptyMap()),
            // BouyomiConnecter(emptyMap())
            return (engines + ("silent" to SilentEngine))
                    .filter { it.value.test() }
                    .values.first()
        }
    }
}