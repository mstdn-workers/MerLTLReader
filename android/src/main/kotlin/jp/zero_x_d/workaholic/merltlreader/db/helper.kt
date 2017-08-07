package jp.zero_x_d.workaholic.merltlreader.db

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import org.jetbrains.anko.db.StringParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update

/**
 * Created by D on 17/08/07.
 */

private val gson = Gson()

fun AppDatabaseOpenHelper.getAppRegistration(instance_url: String): AppRegistration? {
    return use {
        select("AppRegistration", "json")
                .whereSimple("instance_url = ?", instance_url)
                .parseOpt(StringParser)
    }?.let { gson.fromJson(it, AppRegistration::class.java) }
}

fun AppDatabaseOpenHelper.setAppRegistration(
        instance_url: String,
        appRegistration: AppRegistration) {
    use {
        insert("AppRegistration",
                "instance_url" to instance_url,
                "json" to gson.toJson(appRegistration))
    }
}

fun AppDatabaseOpenHelper.getAccessToken(instance_url: String, mail: String): AccessToken? {
    return use {
        select("AccessToken", "json")
                .whereSimple("(instance_url = ?) and (user = ?)",
                        instance_url, mail)
                .parseOpt(StringParser)
    }?.let { gson.fromJson(it, AccessToken::class.java) }
}

fun AppDatabaseOpenHelper.setAccessToken(
        instance_url: String,
        mail: String,
        accessToken: AccessToken) {
    use {
        insert("AccessToken",
                "instance_url" to instance_url,
                "user" to mail,
                "json" to gson.toJson(accessToken))
    }
}