package com.mobiliteam.dbrepoexample;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.mobiliteam.dbrepo.IDBChangeListener;
import com.mobiliteam.dbrepo.IDatabaseRepository;
import com.mobiliteam.dbrepo.OrmLiteRepository;
import com.mobiliteam.dbrepoexample.model.User;

/**
 * Created by swapnilnandgave on 16/04/18.
 */

public class ApplicationEx extends Application {

    private IDatabaseRepository dbRepo;

    private static ApplicationEx INSTANCE;

    public static ApplicationEx getInstance(){
        return INSTANCE;
    }

    public IDatabaseRepository getDbRepo() {
        return dbRepo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        dbRepo = new OrmLiteRepository(this, dbChangeListener);
        createUserTable();
    }


    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private final IDBChangeListener dbChangeListener = new IDBChangeListener() {
        @Override
        public void dbCreated() {
            Log.i(this.getClass().getSimpleName(), "DB CREATED");
        }

        @Override
        public void dbUpgraded() {
            Log.i(this.getClass().getSimpleName(), "DB UPDATED");
        }

        @Override
        public int dbVersion() {
            return 1;
        }

        @Override
        public String dbName() {
            return "mobiliteam_demo.db";
        }

        @Override
        public boolean withOfflineSync() {
            return true;
        }

        @Override
        public String dbPassword() {
            return "smaplePassword";
        }
    };

    private void createUserTable() {
        dbRepo.createTable(User.class);
    }

}
