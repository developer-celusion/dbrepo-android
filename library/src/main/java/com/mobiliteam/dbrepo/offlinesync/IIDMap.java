package com.mobiliteam.dbrepo.offlinesync;

/**
 * Created by swapnilnandgave on 15/04/18.
 */

public interface IIDMap {

    void serverIntercept(int serverID);

    <T> void localIntercept(int localID, T responseItem);

    int getKeyID();

    String getEntityName();

    String getUrl();

    String getUpdateUrl();

}
