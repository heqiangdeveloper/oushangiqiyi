package com.oushang.iqiyi.events;

import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

/**
 * @Author: zeelang
 * @Description: 事件总线，用于组件通信
 * @Time: 2021/9/2 15:07
 * @Since: 1.0
 */
public class EventBusHelper {

    /**
     * 事件监听
     */
    private WeakReference<EventListener> mWeakEventListener;

    public EventBusHelper(EventListener listener) {
        this.mWeakEventListener = new WeakReference<>(listener);
    }

    /**
     * 注册事件总线
     */
    public void register() {
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 解除注册事件总线
     */
    public void unRegister() {
        //移除所有粘性事件
        EventBus.getDefault().removeAllStickyEvents();
        //解除注册
        EventBus.getDefault().unregister(this);
    }

    /**
     *  发送事件
     * @param event 事件
     */
    public static void post(Event event) {
        EventBus.getDefault().post(event);
    }

    /**
     * 发送粘性事件
     * @param event 事件
     */
    public static void postStick(Event event) {
        EventBus.getDefault().postSticky(event);
    }

    /**
     *  创建事件
     * @param eventType 事件类型
     * @return 事件
     */
    public static Event newEvent(int eventType) {
        return new Event(eventType);
    }

    /**
     * 创建事件
     * @param eventType 事件类型
     * @param eventParams 事件参数
     * @return 事件
     */
    public static Event newEvent(int eventType, Bundle eventParams) {
        return new Event(eventType, eventParams);
    }

    /**
     *  处理事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void handleEvent(Event event) {
        EventListener listener = mWeakEventListener.get();
        if (listener != null) {
            listener.onEvent(event);
        }
    }

    /**
     * 事件回调
     */
    public interface EventListener {

        void onEvent(Event event);

    }

    /**
     * 事件
     */
    public static class Event {
        private int eventType;
        private Bundle eventParams;

        public Event(int eventType) {
            this.eventType = eventType;
        }

        public Event(int eventType, Bundle eventParams) {
            this.eventType = eventType;
            this.eventParams = eventParams;
        }

        public int getEventType() {
            return eventType;
        }

        public void setEventType(int eventType) {
            this.eventType = eventType;
        }

        public Bundle getEventParams() {
            return eventParams;
        }

        public void setEventParams(Bundle eventParams) {
            this.eventParams = eventParams;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "eventType=" + eventType +
                    ", eventParams=" + eventParams +
                    '}';
        }
    }
}
