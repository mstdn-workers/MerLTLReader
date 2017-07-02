package jp.zero_x_d.workaholic.merltlreader

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.method.Public
import jp.zero_x_d.workaholic.merltlreader.status.isSpam
import okhttp3.OkHttpClient
import java.util.LinkedList

/**
 * Created by D on 17/06/01.
 */
class Timeline(credentials: Credentials) {
    private val queue = LinkedList<Status>()
    var since: Long? = null

    val client: MastodonClient =
            MastodonClient.Builder(
                    instanceName = credentials.instanceURL,
                    okHttpClientBuilder = OkHttpClient.Builder(),
                    gson = Gson()
            ).accessToken(credentials.accessToken)
            .build()
    val timelines = Public(client)

    operator fun iterator() = this
    operator fun hasNext() = true

    operator fun next(): Status {
        while (queue.size == 0) {
            val statuses = timelines.getLocalPublic(Range(sinceId = since)).execute().part
            queue.addAll(statuses.filter { s -> !s.isSpam }.reversed())
            if (queue.size > 0) break
            try {
                Thread.sleep(5 * 1000L)
            } catch (e: InterruptedException) {
                // finish
                e.printStackTrace()
            }
        }
        since = queue.last().id
        return queue.remove()
    }
}