package com.mobiliteam.dbrepo.offlinesync;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by swapnilnandgave on 15/04/18.
 */
@DatabaseTable
public class SyncMapping {

    public static final String COL_SYNC_STATUS = "syncStatus";
    public static final String COL_SYNC_METHOD = "syncMethod";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int localid;

    @DatabaseField
    private String entity;

    @DatabaseField
    private SyncMethod syncMethod;

    @DatabaseField
    private SyncStatus syncStatus;

    @DatabaseField
    private Date createdAt;

    @DatabaseField
    private String title;

    @DatabaseField
    private String description;

    @DatabaseField
    private String failedMsg;

    public SyncMapping() {
        
    }

    public SyncMapping(String entity, int localid) {
        this.entity = entity;
        this.localid = localid;
        this.syncMethod = SyncMethod.POST;
        this.syncStatus = SyncStatus.NeedSync;
        this.createdAt = new Date();
        this.title = entity;
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

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public SyncMethod getSyncMethod() {
        return syncMethod;
    }

    public void setSyncMethod(SyncMethod syncMethod) {
        this.syncMethod = syncMethod;
    }

    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFailedMsg() {
        return failedMsg;
    }

    public void setFailedMsg(String failedMsg) {
        this.failedMsg = failedMsg;
    }

}
