package com.mobiliteam.dbrepo.offlinesync;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by swapnilnandgave on 15/04/18.
 */
@DatabaseTable
public class IDMapping {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int localid;

    @DatabaseField
    private int serverid;

    @DatabaseField
    private String entity;

    public IDMapping() {

    }

    public IDMapping(final String entity, final int localid) {
        this.entity = entity;
        this.localid = localid;
        this.serverid = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocalid() {
        return localid;
    }

    public void setLocalid(int localid) {
        this.localid = localid;
    }

    public int getServerid() {
        return serverid;
    }

    public void setServerid(int serverid) {
        this.serverid = serverid;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
