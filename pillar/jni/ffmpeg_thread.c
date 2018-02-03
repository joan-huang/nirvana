//
// Created by huangfan on 2017/6/28.
//

#include "ffmpeg_thread.h"

pthread_t ntid;
char **argvs = NULL;
int num = 0;

static void (*ffmpeg_callback)(int ret);

/**
 * 执行ffmpeg命令方法
 * @param arg
 * @return
 */
void *thread(void *arg){
    int result = ffmpeg_exec(num, argvs);
    //回调java方法传递result
//    ffmpeg_callback(result);
    return ((void *)0);
}
/**
 * 新建子线程执行ffmpeg命令
 */
int ffmpeg_thread_run_cmd(int cmdNum, char **argv){
    num = cmdNum;
    argvs = argv;

    int temp = pthread_create(&ntid, NULL, thread, NULL);
    if(temp != 0)return 1;
    return 0;
}

/**
 * 注册线程回调
 */
void ffmpeg_thread_callback(void (*cb)(int ret)){
    ffmpeg_callback = cb;
}

/**
 * 退出线程
 */
void ffmpeg_thread_exit(int ret){
    if (ffmpeg_callback) {
        ffmpeg_callback(ret);
    }
    pthread_exit("ffmpeg_thread_exit");
}




