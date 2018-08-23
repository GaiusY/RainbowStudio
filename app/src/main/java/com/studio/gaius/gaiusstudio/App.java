package com.studio.gaius.gaiusstudio;

import android.app.Application;

public class App extends Application {

    private static App mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static App getApplication() {
        return mContext;
    }
}
