#include <jni.h>

#include <fcntl.h>

#include <cstddef>
#include <cstdlib>
#include <cstring>

namespace {

constexpr int kError = -1;
constexpr int kUnsupportedArch = -2;

#if defined(__aarch64__)
constexpr long kNrOpenat = 56;
constexpr long kNrClose = 57;
constexpr long kNrRead = 63;
#elif defined(__x86_64__)
constexpr long kNrRead = 0;
constexpr long kNrClose = 3;
constexpr long kNrOpenat = 257;
#else
constexpr long kNrOpenat = 0;
constexpr long kNrClose = 0;
constexpr long kNrRead = 0;
#endif

inline long raw_syscall3(long n, long a1, long a2, long a3) {
#if defined(__aarch64__)
    register long x8 __asm__("x8") = n;
    register long x0 __asm__("x0") = a1;
    register long x1 __asm__("x1") = a2;
    register long x2 __asm__("x2") = a3;
    __asm__ volatile("svc #0"
                     : "+r"(x0)
                     : "r"(x8), "r"(x1), "r"(x2)
                     : "memory");
    return x0;
#elif defined(__x86_64__)
    long ret;
    __asm__ volatile("syscall"
                     : "=a"(ret)
                     : "a"(n), "D"(a1), "S"(a2), "d"(a3)
                     : "rcx", "r11", "memory");
    return ret;
#else
    (void)n;
    (void)a1;
    (void)a2;
    (void)a3;
    return kUnsupportedArch;
#endif
}

inline long raw_syscall1(long n, long a1) {
#if defined(__aarch64__)
    register long x8 __asm__("x8") = n;
    register long x0 __asm__("x0") = a1;
    __asm__ volatile("svc #0"
                     : "+r"(x0)
                     : "r"(x8)
                     : "memory");
    return x0;
#elif defined(__x86_64__)
    long ret;
    __asm__ volatile("syscall"
                     : "=a"(ret)
                     : "a"(n), "D"(a1)
                     : "rcx", "r11", "memory");
    return ret;
#else
    (void)n;
    (void)a1;
    return kUnsupportedArch;
#endif
}

int parse_tracer_pid(const char* data) {
    const char* key = "TracerPid:";
    const char* pos = std::strstr(data, key);
    if (pos == nullptr) {
        return kError;
    }

    pos += std::strlen(key);
    while (*pos == ' ' || *pos == '\t') {
        ++pos;
    }

    char* end = nullptr;
    const long parsed = std::strtol(pos, &end, 10);
    if (end == pos || parsed < 0 || parsed > 2147483647L) {
        return kError;
    }

    return static_cast<int>(parsed);
}

int read_tracer_pid_with_inline_syscalls() {
    char buffer[4096] = {0};

    const long fd = raw_syscall3(kNrOpenat,
                                 static_cast<long>(AT_FDCWD),
                                 reinterpret_cast<long>("/proc/self/status"),
                                 static_cast<long>(O_RDONLY));
    if (fd == kUnsupportedArch) {
        return kUnsupportedArch;
    }
    if (fd < 0) {
        return kError;
    }

    const long bytes_read = raw_syscall3(kNrRead,
                                         fd,
                                         reinterpret_cast<long>(buffer),
                                         static_cast<long>(sizeof(buffer) - 1));
    raw_syscall1(kNrClose, fd);

    if (bytes_read <= 0 || bytes_read >= static_cast<long>(sizeof(buffer))) {
        return kError;
    }

    buffer[bytes_read] = '\0';
    return parse_tracer_pid(buffer);
}

}  // namespace

extern "C" JNIEXPORT jint JNICALL
Java_org_owasp_mastestapp_MastgTest_getTracerPidInlineSyscallNative(JNIEnv* env, jobject thiz) {
    (void)env;
    (void)thiz;
    return read_tracer_pid_with_inline_syscalls();
}
