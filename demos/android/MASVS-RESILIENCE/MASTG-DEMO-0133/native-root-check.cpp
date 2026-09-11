#include <jni.h>
#include <cerrno>
#include <string>
#include <unistd.h>

namespace {

constexpr const char *kRootPaths[] = {
    "/system/bin/su",
    "/system/xbin/su",
    "/sbin/su",
};

} // namespace

extern "C"
JNIEXPORT jstring JNICALL
Java_org_owasp_mastestapp_MastgTest_findRootArtifactPath(JNIEnv *env, jobject /* this */) {
    for (const char *path : kRootPaths) {
        const int result = access(path, F_OK);
        if (result == 0 || errno == EACCES) {
            return env->NewStringUTF(path);
        }
    }

    return nullptr;
}
