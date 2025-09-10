package dev.simonas.quies.utils

import android.util.Log

inline fun <reified T> T.logd(msg: String) {
    Log.d(T::class.simpleName, msg)
}

inline fun <reified T> T.loge(msg: String) {
    Log.e(T::class.simpleName, msg)
}

inline fun <reified T> T.loge(msg: String, tr: Throwable?) {
    Log.e(T::class.simpleName, msg, tr)
}

inline fun <reified T> T.loge(t: Throwable) {
    Log.e(T::class.simpleName, null, t)
}
