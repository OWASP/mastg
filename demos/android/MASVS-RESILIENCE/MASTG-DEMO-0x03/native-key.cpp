#include <jni.h>
#include <dlfcn.h>
#include <elf.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <unistd.h>
#include <cstdio>
#include <cstdint>
#include <cstring>

namespace {
constexpr char secret[] = "sk-OWASP-MAS-SuperSecretNativeKey-1234567890";
constexpr size_t size = sizeof(secret) - 1;
constexpr size_t ciphertextSize = size + 16; // AES-GCM tag

// Match the branch opcode Frida wrote at these function entries on the tested devices.
__attribute__((always_inline)) inline bool hasFridaTrampoline(const void *entry) {
#if defined(__aarch64__)
    auto code = static_cast<volatile const uint32_t *>(entry);
    return (code[1] & 0xfffffc1f) == 0xd61f0000; // br xN
#elif defined(__x86_64__)
    auto code = static_cast<volatile const uint8_t *>(entry);
    return code[0] == 0xe9; // jmp rel32
#else
    return false;
#endif
}

void fail(JNIEnv *env, const char *message) {
    env->ThrowNew(env->FindClass("java/io/IOException"), message);
}

uint64_t checksum(const unsigned char *bytes, size_t length) {
    uint64_t hash = 14695981039346656037ULL; // FNV-1a
    for (size_t i = 0; i < length; ++i) hash = (hash ^ bytes[i]) * 1099511628211ULL;
    return hash;
}

bool checkLibcText(JNIEnv *env) {
#if __LP64__
    using Header = Elf64_Ehdr;
    using Section = Elf64_Shdr;
#else
    using Header = Elf32_Ehdr;
    using Section = Elf32_Shdr;
#endif
    Dl_info info{};
    if (!dladdr(reinterpret_cast<const void *>(&fopen), &info) || !info.dli_fname || !info.dli_fbase) {
        fail(env, "Cannot locate libc");
        return false;
    }
    int fd = open(info.dli_fname, O_RDONLY | O_CLOEXEC);
    struct stat st{};
    if (fd < 0) { fail(env, "Cannot open libc on disk"); return false; }
    bool valid = fstat(fd, &st) == 0 && st.st_size >= static_cast<off_t>(sizeof(Header));
    void *file = valid ? mmap(nullptr, st.st_size, PROT_READ, MAP_PRIVATE, fd, 0) : MAP_FAILED;
    close(fd);
    if (file == MAP_FAILED) { fail(env, "Cannot map libc on disk"); return false; }

    auto *bytes = static_cast<const unsigned char *>(file);
    size_t length = st.st_size;
    auto range = [length](size_t offset, size_t count) {
        return offset <= length && count <= length - offset;
    };
    auto *header = reinterpret_cast<const Header *>(bytes);
    valid = memcmp(header->e_ident, ELFMAG, SELFMAG) == 0 &&
            header->e_shentsize == sizeof(Section) && header->e_shnum > 0 &&
            header->e_shstrndx < header->e_shnum &&
            header->e_shoff <= length && header->e_shnum <= (length - header->e_shoff) / sizeof(Section);
    if (valid) {
        auto *sections = reinterpret_cast<const Section *>(bytes + header->e_shoff);
        const auto &names = sections[header->e_shstrndx];
        valid = range(names.sh_offset, names.sh_size);
        for (size_t i = 0; valid && i < header->e_shnum; ++i) {
            const auto &text = sections[i];
            if (text.sh_name > names.sh_size || names.sh_size - text.sh_name < sizeof(".text") ||
                memcmp(bytes + names.sh_offset + text.sh_name, ".text", sizeof(".text")) != 0) continue;
            valid = text.sh_size > 0 && range(text.sh_offset, text.sh_size);
            if (valid) {
                auto *memory = static_cast<const unsigned char *>(info.dli_fbase) + text.sh_addr;
                bool matches = checksum(memory, text.sh_size) == checksum(bytes + text.sh_offset, text.sh_size);
                munmap(file, length);
                if (!matches) env->ThrowNew(env->FindClass("java/lang/SecurityException"), "libc .text modified in memory");
                return matches;
            }
        }
    }
    munmap(file, length);
    fail(env, "Cannot find libc .text section");
    return false;
}

jbyteArray doFinal(JNIEnv *env, jobject cipher, jbyteArray bytes) {
    jclass type = env->GetObjectClass(cipher);
    jmethodID method = env->GetMethodID(type, "doFinal", "([B)[B");
    return method ? static_cast<jbyteArray>(env->CallObjectMethod(cipher, method, bytes)) : nullptr;
}
} // namespace

extern "C" JNIEXPORT jbyteArray JNICALL
Java_org_owasp_mastestapp_MastgTest_encryptNativeSecret(JNIEnv *env, jobject, jobject cipher) {
    if (hasFridaTrampoline(reinterpret_cast<const void *>(&Java_org_owasp_mastestapp_MastgTest_encryptNativeSecret))) {
        env->ThrowNew(env->FindClass("java/lang/SecurityException"), "Native encrypt hook detected");
        return nullptr;
    }
    if (!checkLibcText(env)) return nullptr;
    jbyteArray plaintext = env->NewByteArray(size);
    if (!plaintext) return nullptr;
    env->SetByteArrayRegion(plaintext, 0, size, reinterpret_cast<const jbyte *>(secret));
    if (env->ExceptionCheck()) return nullptr;
    jbyteArray encrypted = doFinal(env, cipher, plaintext);
    if (!encrypted) return nullptr;
    if (env->GetArrayLength(encrypted) != ciphertextSize) {
        if (!env->ExceptionCheck()) fail(env, "Unexpected AES-GCM output");
        return nullptr;
    }
    return encrypted;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_org_owasp_mastestapp_MastgTest_decryptNativeSecret(JNIEnv *env, jobject, jobject cipher, jbyteArray data) {
    if (hasFridaTrampoline(reinterpret_cast<const void *>(&Java_org_owasp_mastestapp_MastgTest_decryptNativeSecret))) {
        env->ThrowNew(env->FindClass("java/lang/SecurityException"), "Native decrypt hook detected");
        return nullptr;
    }
    if (!checkLibcText(env)) return nullptr;
    if (!data || env->GetArrayLength(data) != ciphertextSize) {
        fail(env, "Unexpected AES-GCM ciphertext size");
        return nullptr;
    }
    return doFinal(env, cipher, data);
}

// Checks the decrypted bytes against the secret hardcoded in this file, so the
// plaintext constant never needs to exist in Java.
extern "C" JNIEXPORT jboolean JNICALL
Java_org_owasp_mastestapp_MastgTest_verifyNativeSecret(JNIEnv *env, jobject, jbyteArray data) {
    if (!data || env->GetArrayLength(data) != size) {
        fail(env, "Unexpected native secret length");
        return JNI_FALSE;
    }
    jbyte *bytes = env->GetByteArrayElements(data, nullptr);
    if (!bytes) return JNI_FALSE;
    bool matches = memcmp(bytes, secret, size) == 0;
    env->ReleaseByteArrayElements(data, bytes, JNI_ABORT);
    return matches ? JNI_TRUE : JNI_FALSE;
}