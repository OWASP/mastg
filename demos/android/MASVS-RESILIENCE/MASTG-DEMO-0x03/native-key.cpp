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
#include <string>

namespace {
constexpr char secret[] = "sk-OWASP-MAS-SuperSecretNativeKey-1234567890";
constexpr size_t size = sizeof(secret) - 1;
constexpr size_t ivSize = 12;
constexpr size_t blobSize = ivSize + size + 16; // IV + AES-GCM ciphertext + tag

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

std::string pathOf(JNIEnv *env, jstring file) {
    const char *path = env->GetStringUTFChars(file, nullptr);
    if (!path) return {};
    std::string result(path);
    env->ReleaseStringUTFChars(file, path);
    return result;
}

jbyteArray doFinal(JNIEnv *env, jobject cipher, jbyteArray bytes) {
    jclass type = env->GetObjectClass(cipher);
    jmethodID method = env->GetMethodID(type, "doFinal", "([B)[B");
    return method ? static_cast<jbyteArray>(env->CallObjectMethod(cipher, method, bytes)) : nullptr;
}
} // namespace

extern "C" JNIEXPORT void JNICALL
Java_org_owasp_mastestapp_MastgTest_storeNativeSecret(JNIEnv *env, jobject, jobject cipher, jstring file) {
    if (hasFridaTrampoline(reinterpret_cast<const void *>(&Java_org_owasp_mastestapp_MastgTest_storeNativeSecret))) {
        env->ThrowNew(env->FindClass("java/lang/SecurityException"), "Native store hook detected");
        return;
    }
    if (!checkLibcText(env)) return;
    jbyteArray plaintext = env->NewByteArray(size);
    if (!plaintext) return;
    env->SetByteArrayRegion(plaintext, 0, size, reinterpret_cast<const jbyte *>(secret));
    if (env->ExceptionCheck()) return;
    jbyteArray encrypted = doFinal(env, cipher, plaintext);
    if (!encrypted) return;

    jclass type = env->GetObjectClass(cipher);
    jmethodID getIv = env->GetMethodID(type, "getIV", "()[B");
    if (!getIv) return;
    auto iv = static_cast<jbyteArray>(env->CallObjectMethod(cipher, getIv));
    if (!iv || env->GetArrayLength(iv) != ivSize || env->GetArrayLength(encrypted) != size + 16) {
        if (!env->ExceptionCheck()) fail(env, "Unexpected AES-GCM output");
        return;
    }
    unsigned char blob[blobSize];
    env->GetByteArrayRegion(iv, 0, ivSize, reinterpret_cast<jbyte *>(blob));
    env->GetByteArrayRegion(encrypted, 0, size + 16, reinterpret_cast<jbyte *>(blob + ivSize));
    if (env->ExceptionCheck()) return;

    std::string path = pathOf(env, file);
    if (env->ExceptionCheck()) return;
    std::string temp = path + ".tmp";
    int fd = open(temp.c_str(), O_WRONLY | O_CREAT | O_TRUNC | O_NOFOLLOW, 0600);
    if (fd < 0) { fail(env, "Cannot create private file"); return; }
    FILE *out = fdopen(fd, "wb");
    if (!out) { close(fd); unlink(temp.c_str()); fail(env, "Cannot write private file"); return; }
    bool ok = fwrite(blob, 1, blobSize, out) == blobSize && fflush(out) == 0 && fsync(fd) == 0;
    if (fclose(out) != 0) ok = false;
    if (ok) ok = rename(temp.c_str(), path.c_str()) == 0;
    if (!ok) { unlink(temp.c_str()); fail(env, "Cannot save encrypted secret"); }
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_org_owasp_mastestapp_MastgTest_recoverNativeSecret(JNIEnv *env, jobject, jobject cipher, jstring file) {
    if (hasFridaTrampoline(reinterpret_cast<const void *>(&Java_org_owasp_mastestapp_MastgTest_recoverNativeSecret))) {
        env->ThrowNew(env->FindClass("java/lang/SecurityException"), "Native recover hook detected");
        return nullptr;
    }
    if (!checkLibcText(env)) return nullptr;
    std::string path = pathOf(env, file);
    if (env->ExceptionCheck()) return nullptr;
    FILE *in = fopen(path.c_str(), "rb");
    if (!in) { fail(env, "Cannot open encrypted secret"); return nullptr; }
    unsigned char blob[blobSize];
    bool ok = fread(blob, 1, blobSize, in) == blobSize && fgetc(in) == EOF && !ferror(in);
    fclose(in);
    if (!ok) { fail(env, "Invalid encrypted secret size"); return nullptr; }

    jbyteArray encrypted = env->NewByteArray(size + 16);
    if (!encrypted) return nullptr;
    env->SetByteArrayRegion(encrypted, 0, size + 16, reinterpret_cast<jbyte *>(blob + ivSize));
    return env->ExceptionCheck() ? nullptr : doFinal(env, cipher, encrypted);
}
