package jp.zero_x_d.workaholic.merltlreader.engine

import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ConnectException
import java.net.Socket

/**
 * Created by D on 17/06/20.
 */

class JTalkConnecter(val engine_params: Map<String, Any>): ITTSEngine {
    val username_voice_params = mapOf(
            "Voice" to "miku-beta",
            "Speed" to 1.5)
    val content_voice_params = mapOf(
            "Voice" to "mei",
            "Speed" to 1.9)
    val DEFAULT_JTALK_HOST = "localhost"
    val DEFAULT_JTALK_PORT = 8000

    override fun test(): Boolean {
        var socket: Socket? = null
        try {
            socket = Socket(
                    DEFAULT_JTALK_HOST,
                    DEFAULT_JTALK_PORT)
        } catch (_: ConnectException) {
            return false
        } finally {
            socket?.close()
        }
        return true
    }

    private fun say(readtext: String, params: Map<String, Any>) {
        val socket = Socket(DEFAULT_JTALK_HOST, DEFAULT_JTALK_PORT)

        val output = socket.getOutputStream()
        val input = socket.getInputStream()

        val send_stream = PrintWriter(OutputStreamWriter(output))
        params.forEach {
            send_stream.println("${it.key}: ${it.value}")
        }
        send_stream.println("")
        send_stream.println(readtext)
        send_stream.flush()
        while (input.read() != -1) {
        }
        socket.close()
    }

    override fun say(readtext: String) {
        say(readtext, emptyMap())
    }

    override fun say_username(username: String) {
        say(username, username_voice_params)
    }

    override fun say_content(content: String) {
        say(content, content_voice_params)
    }
}