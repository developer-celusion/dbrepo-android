package com.mobiliteam.dbrepo.offlinesync;

import android.content.Context;

import com.j256.ormlite.stmt.PreparedQuery;
import com.mobiliteam.dbrepo.IDBChangeListener;
import com.mobiliteam.dbrepo.IDatabaseRepository;
import com.mobiliteam.dbrepo.OrmLiteRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by swapnilnandgave on 15/04/18.
 */

public class SyncUtils {

    private static SyncUtils instance;
    private final Context context;
    private final IDatabaseRepository databaseRepository;

    private final static String COL_LOCAL_ID = "localid";
    private final static String COL_ENTITY = "entity";
    private final static String COL_SERVER_ID = "serverid";

    private String title = null;
    private String description = null;

    private ISyncTrackListener syncTrackListener;

    private SyncUtils(Context context) {
        this.context = context;
        this.databaseRepository = OrmLiteRepository.getInstance(context);
        init();
    }

    private SyncUtils(Context context, OrmLiteRepository repository) {
        this.context = context;
        this.databaseRepository = repository;
        init();
    }

    public static synchronized SyncUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SyncUtils(context);
        }
        return instance;
    }

    public static synchronized SyncUtils getInstance(Context context, OrmLiteRepository repository) {
        if (instance == null) {
            instance = new SyncUtils(context, repository);
        }
        return instance;
    }

    private void init() {
        databaseRepository.createTable(SyncMapping.class);
        databaseRepository.createTable(IDMapping.class);
    }

    public void setup() {

    }

    public void resetLabels() {
        title = null;
        description = null;
    }

    public void setLabels(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void setSyncTrackListener(ISyncTrackListener syncTrackListener) {
        this.syncTrackListener = syncTrackListener;
    }

    public IDatabaseRepository getDatabaseRepository() {
        return databaseRepository;
    }

    public <T> void mapItem(Class<T> modelClass, T item) {
        final int serverid = ((IIDMap) item).getKeyID();
        databaseRepository.add(modelClass, item);
        IIDMap idMap = (IIDMap) item;
        IDMapping idMapping = new IDMapping(idMap.getEntityName(), idMap.getKeyID());
        idMapping.setServerid(serverid);
        databaseRepository.add(IDMapping.class, idMapping);
    }

    public <T> void mapItems(Class<T> modelClass, List<T> items) {
        List<IDMapping> idMappingList = new ArrayList<>();
        for (T item : items) {
            IIDMap idMap = (IIDMap) item;
            IDMapping idMapping = new IDMapping(idMap.getEntityName(), idMap.getKeyID());
            idMapping.setServerid(idMap.getKeyID());
            idMappingList.add(idMapping);
        }
        databaseRepository.add(modelClass, items);
        // Change LocalID after Insertion
        for (int i = 0; i < items.size(); i++) {
            idMappingList.get(i).setLocalid(((IIDMap) items.get(i)).getKeyID());
        }
        databaseRepository.add(IDMapping.class, new ArrayList<IDMapping>(idMappingList));

    }

    public <T> void mapItemsByBatch(Class<T> modelClass, List<T> items) throws Exception {
        List<IDMapping> idMappingList = new ArrayList<>();
        for (T item : items) {
            IIDMap idMap = (IIDMap) item;
            IDMapping idMapping = new IDMapping(idMap.getEntityName(), idMap.getKeyID());
            idMapping.setServerid(idMap.getKeyID());
            idMappingList.add(idMapping);
        }
        databaseRepository.batch(modelClass, items);
        // Change LocalID after Insertion
        for (int i = 0; i < items.size(); i++) {
            idMappingList.get(i).setLocalid(((IIDMap) items.get(i)).getKeyID());
        }
        databaseRepository.batch(IDMapping.class, idMappingList);
    }

    public void clear() {
        databaseRepository.deleteAll(IDMapping.class, databaseRepository.getAll(IDMapping.class));
        databaseRepository.deleteAll(SyncMapping.class, databaseRepository.getAll(SyncMapping.class));
    }

    /**
     * It deletes record on Server but it's not recommended.
     * Use PATCH or PUT and do soft DELETE
     *
     * @param idMap
     * @throws Exception
     */

    public void enqueueAsDelete(IIDMap idMap) throws Exception {
        enqueue(idMap, SyncMethod.DELETE);
    }

    public void enqueueAsPatch(IIDMap idMap) throws Exception {
        enqueue(idMap, SyncMethod.PATCH);
    }

    public <T extends IIDMap> void enqueue(Class<T> clazz, List<T> items) throws Exception {
        for (T item : items) {
            enqueue(item);
        }
    }

    public void enqueue(IIDMap idMap) throws Exception {
        enqueue(idMap, SyncMethod.PUT);
    }

    private void enqueue(IIDMap idMap, SyncMethod syncMethod) throws Exception {

        // TODO Havn't Checked DELETE CONDITION BECAUSE OF SOFT DELETE

        PreparedQuery<IDMapping> preparedQuery = databaseRepository.getQueryBuilder(IDMapping.class)
                .where()
                .eq(COL_ENTITY, idMap.getEntityName())
                .and()
                .eq(COL_LOCAL_ID, idMap.getKeyID())
                .prepare();
        IDMapping idMapping = databaseRepository.find(IDMapping.class, preparedQuery);
        if (idMapping == null) {
            idMapping = new IDMapping(idMap.getEntityName(), idMap.getKeyID());
            databaseRepository.add(IDMapping.class, idMapping);
            if (syncTrackListener != null) {
                syncTrackListener.pushedInIDMapping(idMapping);
            }
        }

        PreparedQuery<SyncMapping> preparedQuerySync = databaseRepository.getQueryBuilder(SyncMapping.class)
                .where()
                .eq(SyncMapping.COL_SYNC_STATUS, SyncStatus.NeedSync)
                .and()
                .eq(COL_ENTITY, idMap.getEntityName())
                .and()
                .eq(COL_LOCAL_ID, idMap.getKeyID())
                .prepare();

        SyncMapping syncMapping = databaseRepository.find(SyncMapping.class, preparedQuerySync);
        if (syncMapping == null) {
            syncMapping = new SyncMapping(idMap.getEntityName(), idMap.getKeyID());
            if (idMapping.getServerid() != 0) {
                syncMapping.setSyncMethod(syncMethod);
            }
            if (title != null) {
                syncMapping.setTitle(title);
            }
            if (description != null) {
                syncMapping.setDescription(description);
            }
            databaseRepository.add(SyncMapping.class, syncMapping);
            //resetLabels();
            if (syncTrackListener != null) {
                syncTrackListener.pushedInSync(syncMapping);
            }
        }

    }

    public Collection<SyncMapping> unsynced() throws Exception {
        PreparedQuery<SyncMapping> preparedQuery = databaseRepository.getQueryBuilder(SyncMapping.class)
                .where()
                .eq(SyncMapping.COL_SYNC_STATUS, SyncStatus.NeedSync)
                .prepare();
        return databaseRepository.findAll(SyncMapping.class, preparedQuery);
    }

    public int getLocalID(final String entityName, final int serverid) throws Exception {
        PreparedQuery<IDMapping> preparedQuery = databaseRepository.getQueryBuilder(IDMapping.class)
                .where()
                .eq(COL_SERVER_ID, serverid)
                .and()
                .eq(COL_ENTITY, entityName)
                .prepare();
        IDMapping idMapping = databaseRepository.find(IDMapping.class, preparedQuery);
        return idMapping.getLocalid();
    }

    public int getServerID(SyncMapping syncMapping) throws Exception {
        PreparedQuery<IDMapping> preparedQuery = databaseRepository.getQueryBuilder(IDMapping.class)
                .where()
                .eq(COL_LOCAL_ID, syncMapping.getLocalid())
                .and()
                .eq(COL_ENTITY, syncMapping.getEntity())
                .prepare();
        IDMapping idMapping = databaseRepository.find(IDMapping.class, preparedQuery);
        return idMapping.getServerid();
    }

    public int getServerID(final String entityName, final int localid) throws Exception {
        PreparedQuery<IDMapping> preparedQuery = databaseRepository.getQueryBuilder(IDMapping.class)
                .where()
                .eq(COL_LOCAL_ID, localid)
                .and()
                .eq(COL_ENTITY, entityName)
                .prepare();
        IDMapping idMapping = databaseRepository.find(IDMapping.class, preparedQuery);
        return idMapping.getServerid();
    }

    public void setServerID(SyncMapping syncMapping, int serverID) throws Exception {
        PreparedQuery<IDMapping> preparedQuery = databaseRepository.getQueryBuilder(IDMapping.class)
                .where()
                .eq(COL_LOCAL_ID, syncMapping.getLocalid())
                .and()
                .eq(COL_ENTITY, syncMapping.getEntity())
                .prepare();
        IDMapping idMapping = databaseRepository.find(IDMapping.class, preparedQuery);
        idMapping.setServerid(serverID);
        databaseRepository.addOrUpdate(IDMapping.class, idMapping);
    }

    public void deleteMapping(SyncMapping syncMapping) throws Exception {
        PreparedQuery<IDMapping> preparedQuery = databaseRepository.getQueryBuilder(IDMapping.class)
                .where()
                .eq(COL_LOCAL_ID, syncMapping.getLocalid())
                .and()
                .eq(COL_ENTITY, syncMapping.getEntity())
                .prepare();
        IDMapping idMapping = databaseRepository.find(IDMapping.class, preparedQuery);
        databaseRepository.delete(IDMapping.class, idMapping);
    }

    public List<SyncMapping> failedRecords() {
        List<SyncMapping> list = new ArrayList<>();
        try {
            PreparedQuery<SyncMapping> preparedQuery = databaseRepository.getQueryBuilder(SyncMapping.class)
                    .where()
                    .eq("syncStatus", SyncStatus.SyncFailed)
                    .prepare();
            list = databaseRepository.findList(SyncMapping.class, preparedQuery);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int resetFailedRecords() {
        List<SyncMapping> list = failedRecords();
        for(SyncMapping item: list) {
            item.setSyncStatus(SyncStatus.NeedSync);
        }
        databaseRepository.addOrUpdate(SyncMapping.class,list);
        return list.size();
    }

//    public <T> void execute(SyncMapping syncMapping, Class<T> modelClass, T item) throws Exception {
//        NetworkResponse networkResponse = null;
//        final String url = ((IIDMap) item).getUrl();
//        final String updateUrl = ((IIDMap) item).getUpdateUrl();
//        switch (syncMapping.getSyncMethod()) {
//            case POST:
//                networkResponse = NetworkRepository.getInstance(context).post(modelClass, url, item);
//                if (networkResponse.isSuccess()) {
//                    T serverItem = (T) networkResponse.getResponse();
//                    SyncUtils.getInstance(context).setServerID(syncMapping, ((IIDMap) serverItem).getKeyID());
//                    ((IIDMap) item).localIntercept(syncMapping.getLocalid(), serverItem);
//                }
//                break;
//            case PUT:
//                networkResponse = NetworkRepository.getInstance(context).put(modelClass, updateUrl, item);
//                break;
//            case PATCH:
//                networkResponse = NetworkRepository.getInstance(context).patch(modelClass, updateUrl, item);
//                break;
//            case DELETE:
//                networkResponse = NetworkRepository.getInstance(context).delete(updateUrl);
//                if (networkResponse.isSuccess()) {
//                    SyncUtils.getInstance(context).deleteMapping(syncMapping);
//                }
//                break;
//        }
//        if (networkResponse != null) {
//            if (networkResponse.isSuccess()) {
//                syncMapping.setSyncStatus(SyncStatus.Synced);
//            } else {
//                syncMapping.setFailedMsg("" + networkResponse.getErrorMsg());
//                syncMapping.setSyncStatus(SyncStatus.SyncFailed);
//            }
//            databaseRepository.addOrUpdate(SyncMapping.class, syncMapping);
//        }
//    }

}
