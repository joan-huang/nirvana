//
// Created by huangfan on 2017/6/28.
//
#include <string.h>

#include "com_yy_pillar_ffmpeg_FFmpegNative.h"
#include "android_log.h"
#include "ffmpeg_thread.h"
#include "include/libavformat/avformat.h"
#include "include/libavcodec/avcodec.h"
#include "include/libavutil/avutil.h"

static JavaVM *jvm = NULL;

//当前类(面向java) 到时候异步调用java方法进行命令结果回调
static jclass m_clazz = NULL;

/**
 * 回调执行Java方法
 * 参看 Jni反射+Java反射
 */
void callJavaMethod(JNIEnv *env, jclass clazz, int ret) {
    if (clazz == NULL) {
        LOGE("---------------clazz isNULL---------------");
        return;
    }
    //获取方法ID onExecuted 指的是java层调用native方法的类中的回调方法名称
    jmethodID methodID = (*env)->GetStaticMethodID(env, clazz, "onExecuted", "(I)V");
    if (methodID == NULL) {
        LOGE("---------------methodID isNULL---------------");
        return;
    }
    //调用该java方法
    (*env)->CallStaticVoidMethod(env, clazz, methodID, ret);
}

/**
 * cmdutils.c中exit_program返回结果异步回调给java
 */
static void ffmpeg_callback(int ret) {
    JNIEnv *env;
    //附加到当前线程从JVM中取出JNIEnv, C/C++从子线程中直接回到Java里的方法时  必须经过这个步骤
    (*jvm)->AttachCurrentThread(jvm, (void **) &env, NULL);
    callJavaMethod(env, m_clazz, ret);

    //完毕-脱离当前线程
    (*jvm)->DetachCurrentThread(jvm);
}

JNIEXPORT jstring JNICALL
Java_com_yy_pillar_ffmpeg_FFmpegNative_avcodecinfo
        (JNIEnv *env, jobject obj){
    char info[40000] = { 0 };
    av_register_all();
    AVCodec *c_temp = av_codec_next(NULL);
    while(c_temp!=NULL){
        if (c_temp->decode!=NULL){
            sprintf(info, "%s[Dec]", info);
        }
        else{
            sprintf(info, "%s[Enc]", info);
        }
        switch (c_temp->type){
            case AVMEDIA_TYPE_VIDEO:
                sprintf(info, "%s[Video]", info);
                break;
            case AVMEDIA_TYPE_AUDIO:
                sprintf(info, "%s[Audio]", info);
                break;
            default:
                sprintf(info, "%s[Other]", info);
                break;
        }
        sprintf(info, "%s[%10s]\n", info, c_temp->name);
        c_temp=c_temp->next;
    }
    //LOGE("%s", info);
    return (*env)->NewStringUTF(env, info);
}

JNIEXPORT jint JNICALL
Java_com_yy_pillar_ffmpeg_FFmpegNative_executeCmd__ILjava_lang_String_3_093_2(JNIEnv *env,
                                                                              jclass type,
                                                                              jint cmdNum,
                                                                              jobjectArray cmd) {
    // TODO
    char **argv = NULL;
    jstring *str = NULL;

    (*env)->GetJavaVM(env, &jvm);
    m_clazz = (*env)->NewGlobalRef(env, type);

    if (cmd != NULL) {
        argv = (char **) malloc(sizeof(char *) * cmdNum);
        str = (jstring *) malloc(sizeof(jstring) * cmdNum);
        for (int i = 0; i < cmdNum; ++i) {
            str[i] = (jstring)(*env)->GetObjectArrayElement(env, cmd, i);
            argv[i] = (char *) (*env)->GetStringUTFChars(env, str[i], 0);
        }
    }

    int ret = ffmpeg_thread_run_cmd(cmdNum, argv);
    ffmpeg_thread_callback(ffmpeg_callback);

    //释放内存
    free(str);
    return ret;
}






