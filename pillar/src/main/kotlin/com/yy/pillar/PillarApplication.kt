package com.yy.pillar

import android.app.ActivityManager
import android.app.Application
import android.content.Context

/**
 * Created by huangfan on 2017/6/21.
 */

open class PillarApplication : Application() {

    companion object{
        fun getContext() : Application{
            return mApp
        }

        lateinit private var mApp : Application
    }

    override fun onCreate() {
        super.onCreate()
        mApp = this
    }

    protected fun isMainProcess(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val myPid = android.os.Process.myPid()
        return appProcesses.any { it.pid == myPid && it.processName == "com.yy.nirvana" }
    }
}