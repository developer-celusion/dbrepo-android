package com.mobiliteam.dbrepo;

/**
 * Created by swapnilnandgave on 15/04/18.
 */

public interface IDBChangeListener {

    void dbCreated();

    void dbUpgraded();

    int dbversion();

    String dbName();

    boolean withOfflineSync();

    String dbPassword();

}
