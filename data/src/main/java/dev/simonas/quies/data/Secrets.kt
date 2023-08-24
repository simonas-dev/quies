package dev.simonas.quies.data

object Secrets {

    init {
        System.loadLibrary("q_secrets")
    }

    external fun getAesIv(): String
    external fun getAESKey(): String
}
