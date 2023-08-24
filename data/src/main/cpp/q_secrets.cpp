#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_dev_simonas_quies_data_Secrets_getAESKey(JNIEnv *env, jobject thiz) {
    return (*env).NewStringUTF("7b877b6e14f7bffd15f9dc04ad187be016bcc6e96e298b6607579e3cb56a1743");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_dev_simonas_quies_data_Secrets_getAesIv(JNIEnv *env, jobject thiz) {
    return (*env).NewStringUTF("3d769525eb6fb963c7a507fa26ef8df5");
}
