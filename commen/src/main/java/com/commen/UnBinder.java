package com.commen;

import android.support.annotation.UiThread;

/**
 * Created by Administrator on 2017/12/27.
 */

public interface UnBinder {
    @UiThread
    void unBind();

    UnBinder EMPTY = new UnBinder() {
        @Override
        public void unBind() {
        }
    };
}
