#ifndef UTILS_H
#define UTILS_H
#include <jni.h>
#include <unistd.h>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <functional>
#include <android/log.h>
#include <vector>

typedef unsigned long DWORD;
static uintptr_t libBase;

DWORD findLibrary(const char *library);
DWORD getAbsoluteAddress(const char* libraryName, DWORD relativeAddr);
bool isLibraryLoaded(const char *libraryName);
void ShowToast(JNIEnv *_env, jobject _View, const char *txt, int longer);

DWORD findLibrary(const char *library) {
    char filename[0xFF] = {0},
            buffer[1024] = {0};
    FILE *fp = NULL;
    DWORD address = 0;

    sprintf( filename, "/proc/self/maps");

    fp = fopen( filename, "rt" );
    if( fp == NULL ){
        perror("fopen");
        goto done;
    }

    while( fgets( buffer, sizeof(buffer), fp ) ) {
        if(strstr( buffer, library ) ){
            address = (DWORD)strtoul( buffer, NULL, 16 );
            goto done;
        }
    }

    done:

    if(fp){
        fclose(fp);
    }

    return address;
}

DWORD getAbsoluteAddress(const char* libraryName, DWORD relativeAddr) {
    libBase = findLibrary(libraryName);
    if (libBase == 0)
        return 0;
    return (reinterpret_cast<DWORD>(libBase + relativeAddr));
}

bool isLibraryLoaded(const char *libraryName) {
    char line[512] = {0};
    FILE *fp = fopen("/proc/self/maps", "rt");
    if (fp != NULL) {
        while (fgets(line, sizeof(line), fp)) {
            if (strstr(line, libraryName))
                return true;
        }
        fclose(fp);
    }
    return false;
}


void ShowToast(JNIEnv *_env, jobject _View, const char *txt, int longer) {
    jstring jstr = _env->NewStringUTF(txt); //Edit this text to your desired toast message!
    jclass toast = _env->FindClass("android/widget/Toast");
    jmethodID methodMakeText =
            _env->GetStaticMethodID(
                    toast,
                    "makeText",
                    "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    if (methodMakeText == NULL) {

        return;
    }
    //The last int is the length on how long the toast should be displayed
    //0 = Short, 1 = Long

    jobject toastobj = _env->CallStaticObjectMethod(toast, methodMakeText,
                                                    _View, jstr, longer);

    jmethodID methodShow = _env->GetMethodID(toast, "show", "()V");
    if (methodShow == NULL) {

        return;
    }
    _env->CallVoidMethod(toastobj, methodShow);
}
#endif