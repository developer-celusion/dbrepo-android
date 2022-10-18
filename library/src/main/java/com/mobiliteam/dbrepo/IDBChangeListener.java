package com.mobiliteam.dbrepo;

/**
 * Created by swapnilnandgave on 15/04/18.
 */

public interface IDBChangeListener {

    void dbCreated();

    void dbUpgraded();

    int dbVersion();

    String dbName();

    boolean withOfflineSync();

    String dbPassword();

}
