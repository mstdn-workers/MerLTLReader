package org.snowink.bouyomichan

import java.io.DataOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.ConnectException
import java.net.Socket

/**
 * github.com/howalunar/BouyomiChan4J
 */
class BouyomiChan4J(
        private val host: String = DEFAULT_BOUYOMI_HOST,
        private val port: Int = DEFAULT_BOUYOMI_PORT) {

    /**
     * 残りの文章をキャンセルします。
     */
    fun clear() {
        command(host, port, 64)
    }

    /**
     * 読み上げを一時停止します。
     */
    fun pasuse() {
        command(host, port, 16)
    }

    /**
     * 読み上げを再開します。
     */
    fun resume() {
        command(host, port, 32)
    }

    /**
     * 次の文章を読み上げます。
     */
    fun skip() {
        command(host, port, 48)
    }

    /**
     * 棒読みちゃんを読み上げさせます。音量・速度・音程・声質はデフォルトの設定となります。
     * @param message 読ませたい文字列
     */
    fun talk(message: String) {
        talk(host, port, -1, -1, -1, 0, message)
    }


    /**
     * 音量・速度・音程・声質を指定して棒読みちゃんを読み上げさせます。
     * @param volume 読み上げる音量(0～100, デフォルトは-1)
     * *
     * @param speed 読み上げる速度(50～300, デフォルトは-1)
     * *
     * @param tone 読み上げる音程(50～200, デフォルトは-1)
     * *
     * @param voice 読み上げる音程声質(1～8, デフォルトは0)
     * *
     * @param message 読ませたい文字列
     */
    fun talk(volume: Int, speed: Int, tone: Int, voice: Int, message: String) {
        talk(host, port, volume.toShort(), speed.toShort(), tone.toShort(), voice.toShort(), message)
    }

    private fun command(host: String, port: Int, command: Int) {
        val data = ByteArray(2)
        data[0] = (command.ushr(0) and 0xFF).toByte()
        data[1] = (command.ushr(8) and 0xFF).toByte()
        send(host, port, data)
    }

    private fun send(host: String, port: Int, data: ByteArray) {
        var socket: Socket? = null
        var out: DataOutputStream? = null
        try {
            socket = Socket(host, port)
            //			System.out.println("接続しました" + socket.getRemoteSocketAddress());
            out = DataOutputStream(socket.getOutputStream())
            out.write(data)
        } catch (e: ConnectException) {
            println("接続できませんでした")
            //			e.printStackTrace();
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (out != null) {
                    out.close()
                }
                if (socket != null) {
                    socket.close()
                    //					System.out.println("切断します");
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            //			System.out.println("終了します");
        }
    }

    private fun talk(host: String, port: Int, volume: Short, speed: Short, tone: Short, voice: Short, message: String) {

        infix fun Short.ushr(bitCount: Int): Short =
                (this.toInt() ushr bitCount).toShort()
        infix fun Short.and(other: Int): Byte =
                (this.toInt() and other).toByte()

        var messageData: ByteArray? = null
        try {
            messageData = message.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        val length = messageData!!.size
        val data = ByteArray(15 + length)

        data[0] = 1                         // コマンド 1桁目
        data[1] = 0                         // コマンド 2桁目
        data[2] = (speed ushr 0) and 0xFF   // 速度 1桁目
        data[3] = (speed ushr 8) and 0xFF   // 速度 2桁目
        data[4] = (tone ushr 0) and 0xFF    // 音程 1桁目
        data[5] = (tone ushr 8) and 0xFF    // 音程 2桁目
        data[6] = (volume ushr 0) and 0xFF  // 音量 1桁目
        data[7] = (volume ushr 8) and 0xFF  // 音量 2桁目
        data[8] = (voice ushr 0) and 0xFF   // 声質 1桁目
        data[9] = (voice ushr 8) and 0xFF   // 声質 2桁目
        data[10] = 0                        // エンコード(0: UTF-8)
        data[11] = ((length ushr 0) and 0xFF).toByte()  // 長さ 1桁目
        data[12] = ((length ushr 8) and 0xFF).toByte()  // 長さ 2桁目
        data[13] = ((length ushr 16) and 0xFF).toByte() // 長さ 3桁目
        data[14] = ((length ushr 24) and 0xFF).toByte() // 長さ 4桁目
        System.arraycopy(messageData, 0, data, 15, length)
        send(host, port, data)
    }

    companion object {
        val DEFAULT_BOUYOMI_HOST = "localhost"
        val DEFAULT_BOUYOMI_PORT = 50001
    }

}