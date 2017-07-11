package jp.zero_x_d.workaholic.merltlreader.status

import com.sys1yagi.mastodon4j.api.entity.Status
import org.apache.commons.lang3.StringEscapeUtils




fun remove_tag(s: String) = s.replace("<[^>]+>".toRegex(), "")
fun remove_url(s: String) = s.replace(
        """http(s)?://([\w-]+\.)+[\w-]+(:\w+)?(/[\w-./?%&=;]*)?""".toRegex(),
        "URL"
)
fun remove_image_url(st: Status, s: String): String {
    var toot = s
    st.mediaAttachments.forEach { media_url ->
        toot = toot.replace(media_url.textUrl,
                (if (st.isSensitive) "不適切" else "") + "画像")
    }
    return toot
}
fun toot_convert(st: Status): String {
    var toot = (if (st.spoilerText.isNotEmpty()) st.spoilerText + " もっと見る " else "") + st.content
    toot = toot.replace("<br />", "\n")
    toot = remove_tag(toot)
    toot = remove_image_url(st, toot)
    toot = remove_url(toot)
    toot = StringEscapeUtils.unescapeHtml4(toot)

    toot = toot.replace("""(?m)^>\s*""".toRegex(), "引用 ")
    return toot
}

val Status.readContent_: String?
    get() {
        val speech_str = toot_convert(this)
        return speech_str
    }
