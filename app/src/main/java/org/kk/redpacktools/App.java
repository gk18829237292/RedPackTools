package org.kk.redpacktools;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import org.kk.redpacktools.db.RedPackDataBase;

public class App extends Application{

    private static App mInstance;
    private Context mContenxt;

    private static RedPackDataBase mDb;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContenxt = this;
        mDb = Room.databaseBuilder(this,RedPackDataBase.class,"redPackLog").build();
    }

    public static App getInstance(){
        return mInstance;
    }

    public static RedPackDataBase getDB(){
        return mDb;
    }

}
