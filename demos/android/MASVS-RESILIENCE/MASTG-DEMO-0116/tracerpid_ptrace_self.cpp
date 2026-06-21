#include <jni.h>

#include <cerrno>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <unistd.h>

extern "C" JNIEXPORT jint JNICALL
Java_org_owasp_mastestapp_MastgTest_ptraceSelfDetectNative(JNIEnv *env, jobject thiz)
{
    (void)env;
    (void)thiz;

    const pid_t child_pid = fork();
    if (child_pid < 0)
    {
        return 2;
    }

    if (child_pid == 0)
    {
        const pid_t parent_pid = getppid();

        errno = 0;
        if (ptrace(PTRACE_SEIZE, parent_pid, nullptr, nullptr) == 0)
        {
            _exit(0);
        }

        if (errno == EPERM)
        {
            _exit(1);
        }

        _exit(2);
    }

    int status = 0;
    if (waitpid(child_pid, &status, 0) < 0)
    {
        return 2;
    }

    if (!WIFEXITED(status))
    {
        return 2;
    }

    const int code = WEXITSTATUS(status);
    if (code == 0 || code == 1 || code == 2)
    {
        return code;
    }

    return 2;
}
