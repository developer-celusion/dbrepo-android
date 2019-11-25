package com.mobiliteam.dbrepo;

import android.content.Context;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase;

import com.j256.ormlite.android.AndroidDatabaseResults;
//import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.cipher.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mobiliteam.dbrepo.offlinesync.SyncUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by administrator on 4/1/18.
 */

public class OrmLiteRepository extends OrmLiteSqliteOpenHelper implements IDatabaseRepository {

    private static final String DATABASE_NAME = "Mobiliteam.db";
    private static final int DATABASE_VERSION = 1;

    private static OrmLiteRepository ormLiteRepository;
    private static IDBChangeListener dbChangeListener;
    private Context context;

    public static synchronized void configure(IDBChangeListener changeListener, Context context) {
        dbChangeListener = changeListener;
        OrmLiteRepository repository = getInstance(context);
        if (dbChangeListener.withOfflineSync()) {
            repository.offline();
        }
    }

    public static synchronized OrmLiteRepository getInstance(Context context) {
        if (ormLiteRepository == null) {
            ormLiteRepository = new OrmLiteRepository(context);
        }
        return ormLiteRepository;
    }

    private OrmLiteRepository(Context context) {
        super(context, dbChangeListener.dbName(), null, dbChangeListener.dbversion());
        this.context = context;
        SQLiteDatabase.loadLibs(context);
    }

    public void setup() {

    }

    public void offline() {
        SyncUtils.getInstance(context).setup();
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        /*try {
            TableUtils.createTable(getDao(User.class));
            //TableUtils.createTable(connectionSource, User.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        dbChangeListener.dbCreated();
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        dbChangeListener.dbUpgraded();
    }

    @Override
    public <T> T add(Class<T> modelClass, T item) {
        try {
            getDao(modelClass).create(item);
            // can also use
            // add(Collections.singletonList(item));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public <T> T update(Class<T> modelClass, T item) {
        try {
            getDao(modelClass).update(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public <T> void delete(Class<T> modelClass, T item) {
        try {
            getDao(modelClass).delete(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void deleteAll(final Class<T> modelClass, final Collection<T> items) {
        try {
            TransactionManager.callInTransaction(getDao(modelClass).getConnectionSource(), new Callable() {
                @Override
                public Object call() throws Exception {
                    for (T item :
                            items) {
                        getDao(modelClass).delete(item);
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void deleteAll(Class<T> modelClass, List<T> items) {
        deleteAll(modelClass, (Collection<T>) items);
    }

    @Override
    public <T> void add(final Class<T> modelClass, final Collection<T> items) {
        try {
            TransactionManager.callInTransaction(getDao(modelClass).getConnectionSource(), new Callable() {
                @Override
                public Object call() throws Exception {
                    getDao(modelClass).create(items);
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void add(Class<T> modelClass, List<T> items) {
        add(modelClass, (Collection<T>) items);
    }

    @Override
    public <T> void addOrUpdate(final Class<T> modelClass, final Collection<T> items) {
        try {
            TransactionManager.callInTransaction(getDao(modelClass).getConnectionSource(), new Callable() {
                @Override
                public Object call() throws Exception {
                    for (T item :
                            items) {
                        getDao(modelClass).createOrUpdate(item);
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void addOrUpdate(Class<T> modelClass, List<T> items) {
        addOrUpdate(modelClass, (Collection<T>) items);
    }

    @Override
    public <T> void addOrUpdate(Class<T> modelClass, final T item) {
        try {
            getDao(modelClass).createOrUpdate(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public <T> T getById(Class<T> modelClass, Object recordId) {
        try {
            return getDao(modelClass).queryBuilder().where().eq("id", recordId).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> Collection<T> getAll(Class<T> modelClass) {
        try {
            return getDao(modelClass).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> getList(Class<T> modelClass) {
        return new ArrayList<>(getAll(modelClass));
    }

    @Override
    public <T> Cursor allCursor(Class<T> modelClass) {
        QueryBuilder<T, ?> queryBuilder = null;
        try {
            queryBuilder = getDao(modelClass).queryBuilder();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CloseableIterator<T> iterator = null;
        try {
            iterator = getDao(modelClass).iterator(queryBuilder.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
            return results.getRawCursor();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (iterator != null) {
                iterator.closeQuietly();
            }
        }
        return null;
    }

    @Override
    public <T> GenericRawResults<String[]> getRawResults(Class<T> modelClass, String query) {
        GenericRawResults<String[]> rawResults = null;
        try {
            rawResults = getDao(modelClass).queryRaw(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rawResults;
    }

    @Override
    public <T> JSONArray getResultAsJsonArray(Class<T> modelClass, String query) {
        JSONArray jsonArray = new JSONArray();
        GenericRawResults<String[]> rawResults = getRawResults(modelClass, query);

        try {
            if (rawResults != null) {
                String[] columnNames = rawResults.getColumnNames();
                List<String[]> results = rawResults.getResults();
                int numberOfColumns = rawResults.getNumberColumns();

                for (int rowCount = 0; rowCount < results.size(); rowCount++) {
                    JSONObject jsonObject = new JSONObject();
                    for (int i = 0; i < numberOfColumns; i++) {
                        jsonObject.put(columnNames[i], results.get(rowCount)[i]);
                    }
                    jsonArray.put(jsonObject);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    @Override
    public <T> QueryBuilder<T, ?> getQueryBuilder(Class<T> modelClass) {
        try {
            return getDao(modelClass).queryBuilder();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> long count(Class<T> modelClass) {
        try {
            return getDao(modelClass).countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public <T> Collection<T> findAll(Class<T> modelClass, PreparedQuery<T> preparedQuery) {
        try {
            return getDao(modelClass).query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> findList(Class<T> modelClass, PreparedQuery<T> preparedQuery) {
        return new ArrayList<>(findAll(modelClass, preparedQuery));
    }

    @Override
    public <T> Collection<T> findAll(Class<T> modelClass, QueryBuilder<T, Integer> queryBuilder) {
        try {
            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> findList(Class<T> modelClass, QueryBuilder<T, Integer> queryBuilder) {
        return new ArrayList<>(findAll(modelClass, queryBuilder));
    }

    @Override
    public <T> T find(Class<T> modelClass, PreparedQuery<T> preparedQuery) {
        try {
            return getDao(modelClass).queryForFirst(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T executeRaw(Class<T> modelClass, String query) {
        try {
            return getDao(modelClass).queryRaw(query, getDao(modelClass).getRawRowMapper()).getFirstResult();
//            daoObject.queryRaw(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> Collection<T> executeRawCollection(Class<T> modelClass, String query) {
        try {
            return getDao(modelClass).queryRaw(query, getDao(modelClass).getRawRowMapper()).getResults();
//            daoObject.queryRaw(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> executeRawList(Class<T> modelClass, String query) {
        return new ArrayList<>(executeRawCollection(modelClass, query));
    }

    @Override
    public <T> void raw(Class<T> modelClass, PreparedQuery<T> preparedQuery) {
        try {
            getDao(modelClass).query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void createTable(Class<T> modelClass) {
        try {
            //TableUtils.createTable(getDao(modelClass));
            TableUtils.createTableIfNotExists(getConnectionSource(), modelClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void clearTable(Class<T> modelClass) {
        try {
            TableUtils.clearTable(getConnectionSource(), modelClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void dropTable(Class<T> modelClass) {
        try {
            TableUtils.dropTable(getConnectionSource(), modelClass, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run database operation in Transaction
     *
     * @param callable
     * @throws Exception
     */

    @Override
    public void inTransaction(Callable callable) throws Exception {
        TransactionManager.callInTransaction(getConnectionSource(), callable);
    }

    /**
     * Use this only in inTransaction Method
     *
     * @param modelClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> Dao<T, ?> myDao(Class<T> modelClass) throws Exception {
        return getDao(modelClass);
    }

    @Override
    public <T> void batch(final Class<T> modelClass, final List<T> items) throws Exception {
        getDao(modelClass).callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (T item : items) {
                    getDao(modelClass).create(item);
                }
                return null;
            }
        });
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    protected String getPassword() {
        return dbChangeListener.dbPassword();
    }


}
