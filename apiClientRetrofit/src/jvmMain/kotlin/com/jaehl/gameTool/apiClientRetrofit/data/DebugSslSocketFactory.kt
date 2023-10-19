package com.jaehl.gameTool.apiClientRetrofit.data

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*


class DebugSslSocketFactory {
    fun buildTrustManager() : X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }

    fun buildSSLSocketFactory() : SSLSocketFactory {
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(buildTrustManager()), SecureRandom())
        return sslContext.socketFactory
    }

    fun buildHostnameVerifier() : HostnameVerifier {
        return HostnameVerifier { hostname, session -> true }
    }
}