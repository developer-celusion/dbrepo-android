package com.mobiliteam.dbrepoexample;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.mobiliteam.dbrepo.IDBChangeListener;
import com.mobiliteam.dbrepo.OrmLiteRepository;

/**
 * Created by swapnilnandgave on 16/04/18.
 */

public class ApplicationEx extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        OrmLiteRepository.configure(dbChangeListener, this);
    }


    public static Context getContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        context = null;
        super.onTerminate();
    }

    private IDBChangeListener dbChangeListener = new IDBChangeListener() {
        @Override
        public void dbCreated() {
            Log.i(this.getClass().getSimpleName(), "DB CREATED");
        }

        @Override
        public void dbUpgraded() {
            Log.i(this.getClass().getSimpleName(), "DB UPDATED");
        }

        @Override
        public int dbversion() {
            return 1;
        }

        @Override
        public String dbName() {
            return "Mobiliteam.db";
        }

        @Override
        public boolean withOfflineSync() {
            return true;
        }

        @Override
        public String dbPassword() {
            return null;
        }
    };

}
