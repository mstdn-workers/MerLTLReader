package jp.zero_x_d.workaholic.merltlreader.tls

import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient.Builder
import okhttp3.TlsVersion
import javax.net.ssl.SSLContext


/**
 * Created by D on 17/08/07.
 */

fun Builder.setTLSv12(): Builder {
    val protocol = "TLSv1.2"
    val sslContext = SSLContext.getInstance(protocol).apply {
        init(null, null, null)
    }
    val sslSocketFactory = sslContext.socketFactory

    // http://qiita.com/hatemade/items/e23fd18aaf9eb2b597e7
    return sslSocketFactory(TLSv12SocketFactory(sslSocketFactory))
            .connectionSpecs(mutableListOf(
                    ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_2)
                            .build()))
}