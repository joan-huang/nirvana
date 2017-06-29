package com.yy.pillar.ffmpeg;

/**
 * Created by huangfan on 2017/6/26.
 */

public class FFmpegNative {

    /**
     * 执行ffmpeg命令异步回调
     */
    public interface OnExecuteListener{
        void onExecuted(int ret);
    }

    /**
     *加载ffmpeg以及相关实现 so
     * ffmpeg 版本 3.3.2
     */
    static {
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avformat-57");
        System.loadLibrary("avutil-55");
        System.loadLibrary("swresample-2");
        System.loadLibrary("swscale-4");
        System.loadLibrary("ffmpeg");
    }

    private static OnExecuteListener mOnExecuteListener;

    /**
     * jni调用通知执行结果
     * @param ret
     */
    public static void onExecuted(int ret) {
        if (mOnExecuteListener != null) {
            mOnExecuteListener.onExecuted(ret);
        }
    }

    /**
     * 执行ffmoeg命令
     * @param cmd
     * @param listener
     */
    public static void execute(String[] cmd, OnExecuteListener listener) {
        FFmpegNative.mOnExecuteListener = listener;
        executeCmd(cmd.length, cmd);
    }


    /**
     * native 方法调用ffmpeg中的ffmlspeg.c中的exeCmd方法
     * @param argc
     * @param argv
     * @return
     */
    public static native int executeCmd(int argc, String[] argv);

    /**
     * 测试ffmpeg集成
     * @return
     */
    public static native String avcodecinfo();
}
