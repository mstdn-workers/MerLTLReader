package jp.zero_x_d.workaholic.merltlreader.engine

/**
 * Created by D on 17/06/20.
 */
interface ITTSEngine {
    fun say(readtext: String)
    fun say_username(username: String)
    fun say_content(content: String)
}