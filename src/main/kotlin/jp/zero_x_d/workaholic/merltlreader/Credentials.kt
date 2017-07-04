package jp.zero_x_d.workaholic.merltlreader

import jp.zero_x_d.workaholic.merltlreader.MerLTLReader.appName
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import com.sys1yagi.mastodon4j.api.method.Apps
import okhttp3.OkHttpClient
import java.io.File

/**
 * Created by D on 17/07/02.
 */
data class LoginData(
        val mail: String,
        val pass: String
)
data class Credentials(
        val instanceURL: String,
        val accessToken: String?
) {
    class CredentialsBuilder(val preferences: Preferences) {
        private var appRegistration: AppRegistration? = null
        private var accessToken: AccessToken? = null
        private val gson by lazy { Gson() }
        var mailPass: LoginData? = null
        private val client: MastodonClient by lazy {
            MastodonClient.Builder(
                    preferences.instance_url,
                    OkHttpClient.Builder(),
                    gson
            ).build()
        }
        private val apps: Apps by lazy { Apps(client) }

        fun loadOrAppRegister() = apply {
            val f = File(preferences.dir, "appRegistration.json")
            appRegistration = if (f.exists()) {
                println("use local registration file")
                f.reader().use { gson.fromJson(it, AppRegistration::class.java) }
            } else {
                apps.createApp(
                        clientName = appName,
                        scope = preferences.scope
                ).execute().apply {
                    f.writer().use { gson.toJson(this, it) }
                }
            }
        }

        fun loadOrLogin(
                setUserNameAndPasswordFunction: CredentialsBuilder.() -> Unit
        ): CredentialsBuilder {
            val f = File(preferences.dir, "accessToken.json")
            accessToken = if (f.exists()) {
                println("use local accessToken file")
                f.reader().use { gson.fromJson(it, AccessToken::class.java) }
            } else {
                setUserNameAndPasswordFunction()
                apps.postUserNameAndPassword(
                        clientId = appRegistration!!.clientId,
                        clientSecret = appRegistration!!.clientSecret,
                        scope = preferences.scope,
                        userName = requireNotNull(mailPass?.mail),
                        password = requireNotNull(mailPass?.pass)
                ).execute().apply {
                    f.writer().use { gson.toJson(this, it) }
                }
            }
            return this
        }

        fun create(): Credentials {
            return Credentials(
                    instanceURL = preferences.instance_url,
                    accessToken = accessToken?.accessToken
            )
        }
    }

    companion object {
        fun Builder(preferences: Preferences) =
                CredentialsBuilder(preferences)
    }
}