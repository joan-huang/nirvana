package com.yy.pillar.rx

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable

/**
 * Created by huangfan on 2017/6/21.
 */

class RxBus {

    interface RxInterface

    companion object {
        @JvmStatic @Volatile var default: RxBus = RxBus()
    }

    lateinit private var mRelay: Relay<Any>

    private fun RxBus(){
        mRelay = PublishRelay.create<Any>().toSerialized()
    }

    /**
     * 向观察者分发事件
     * @param event
     */
    fun post(event: RxInterface) {
        mRelay.accept(event)
    }

    /**
     * 订阅一个事件源
     * 必须用返回的Observable 调用unSubscribe 取消订阅
     * @param cls
     * *
     * @param <T>
     * *
     * @return
    </T> */
    fun <T : RxInterface> register(cls: Class<T>): Observable<T> {
        return mRelay.filter{cls.isInstance(it)}.cast(cls)
    }
}