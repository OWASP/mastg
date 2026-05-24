#include <jni.h>

#include <cstdio>
#include <cstring>

extern "C" JNIEXPORT jint JNICALL
Java_org_owasp_mastestapp_MastgTest_getTracerPidNative(JNIEnv* env, jobject thiz) {
    (void)env;
    (void)thiz;

    FILE* file = std::fopen("/proc/self/status", "r");
    if (file == nullptr) {
        return -1;
    }

    char line[256];
    while (std::fgets(line, sizeof(line), file) != nullptr) {
        if (std::strncmp(line, "TracerPid:", 10) == 0) {
            int tracer_pid = -1;
            const int parsed = std::sscanf(line, "TracerPid:\t%d", &tracer_pid);
            std::fclose(file);
            if (parsed == 1) {
                return tracer_pid;
            }
            return -1;
        }
    }

    std::fclose(file);
    return -1;
}
