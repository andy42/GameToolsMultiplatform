package com.jaehl.gameTool.apiClientRetrofit.util

import okhttp3.Interceptor
import okhttp3.Response

class DelayInterceptor(
    private val delay : Long = 0L
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (delay > 0) {
            try {
                Thread.sleep(delay)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return chain.proceed(chain.request())
    }
}