package jp.zero_x_d.workaholic.merltlreader.status

import com.sys1yagi.mastodon4j.api.entity.*
import org.luaj.vm2.LuaValue
import org.luaj.vm2.LuaValue.*

/**
 * Created by D on 17/06/24.
 */

inline fun valueOf(long: Long?): LuaValue {
    return if (long != null) valueOf(long.toString()) else NIL
}

fun valueOf(mention: Mention): LuaValue = tableOf(arrayOf(
        valueOf("acct"), valueOf(mention.acct),
        valueOf("id"), valueOf(mention.id),
        valueOf("url"), valueOf(mention.url),
        valueOf("mailAddress"), valueOf(mention.username)))

fun valueOf(tag: Tag): LuaValue = tableOf(arrayOf(
        valueOf("name"), valueOf(tag.name),
        valueOf("url"), valueOf(tag.url)))

fun valueOf(mediaAttachment: Attachment): LuaValue = tableOf(arrayOf(
        valueOf("id"), valueOf(mediaAttachment.id),
        valueOf("previewUrl"), valueOf(mediaAttachment.previewUrl),
        valueOf("remoteUrl"), valueOf(mediaAttachment.remoteUrl),
        valueOf("textUrl"), valueOf(mediaAttachment.textUrl),
        valueOf("type"), valueOf(mediaAttachment.type),
        valueOf("url"), valueOf(mediaAttachment.url)))


fun valueOf(account: Account?): LuaValue {
    if (account == null) return NIL
    return LuaValue.tableOf(arrayOf(
            valueOf("acct"), valueOf(account.acct),
            valueOf("avatar"), valueOf(account.avatar),
            valueOf("createdAt"), valueOf(account.createdAt),
            valueOf("displayName"), valueOf(account.displayName),
            valueOf("followersCount"), valueOf(account.followersCount),
            valueOf("followingCount"), valueOf(account.followingCount),
            valueOf("header"), valueOf(account.header),
            valueOf("id"), valueOf(account.id),
            valueOf("isLocked"), valueOf(account.isLocked),
            valueOf("note"), valueOf(account.note),
            valueOf("statusesCount"), valueOf(account.statusesCount),
            valueOf("url"), valueOf(account.url),
            valueOf("userName"), valueOf(account.userName)))
}

fun valueOf(status: Status?): LuaValue {
    if (status == null) return NIL
    return LuaValue.tableOf(arrayOf(
            valueOf("content"), valueOf(status.content),
            valueOf("account"), valueOf(status.account),
            valueOf("application"), valueOf(status.application.toString()),
            valueOf("createdAt"), valueOf(status.createdAt),
            valueOf("favouritesCount"), valueOf(status.favouritesCount),
            valueOf("id"), valueOf(status.id),
            valueOf("inReplyToAccountId"), valueOf(status.inReplyToAccountId),
            valueOf("inReplyToId"), valueOf(status.inReplyToId),
            valueOf("isFavourited"), valueOf(status.isFavourited),
            valueOf("isReblogged"), valueOf(status.isReblogged),
            valueOf("isSensitive"), valueOf(status.isSensitive),
            valueOf("mediaAttachments"), listOf(status.mediaAttachments.map { valueOf(it) }.toTypedArray()),
            valueOf("mentions"), listOf(status.mentions.map { valueOf(it) }.toTypedArray()),
            valueOf("reblog"), valueOf(status.reblog),
            valueOf("reblogsCount"), valueOf(status.reblogsCount),
            valueOf("spoilerText"), valueOf(status.spoilerText),
            valueOf("tags"), listOf(status.tags.map { valueOf(it) }.toTypedArray()),
            valueOf("uri"), valueOf(status.uri),
            valueOf("url"), valueOf(status.url),
            valueOf("visibility"), valueOf(status.visibility)
            ))
}
