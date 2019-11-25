package com.mobiliteam.dbrepo;

import android.database.Cursor;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by administrator on 19/1/18.
 */

public interface IDatabaseRepository {

    <T> T getById(Class<T> modelClass, Object recordId);

    <T> T add(Class<T> modelClass, T item);

    <T> T update(Class<T> modelClass, T item);

    <T> void delete(Class<T> modelClass, T item);

    <T> void deleteAll(Class<T> modelClass, Collection<T> item);

    <T> void deleteAll(Class<T> modelClass, List<T> items);

    <T> void add(Class<T> modelClass, Collection<T> items);

    <T> void add(Class<T> modelClass, List<T> items);

    <T> Collection<T> getAll(Class<T> modelClass);

    <T> List<T> getList(Class<T> modelClass);

    <T> long count(Class<T> modelClass);

    <T> Cursor allCursor(Class<T> modelClass);

    <T> QueryBuilder<T, ?> getQueryBuilder(Class<T> modelClass);

    <T> Collection<T> findAll(Class<T> modelClass, PreparedQuery<T> preparedQuery);

    <T> List<T> findList(Class<T> modelClass, PreparedQuery<T> preparedQuery);

    <T> Collection<T> findAll(Class<T> modelClass, QueryBuilder<T, Integer> queryBuilder);

    <T> List<T> findList(Class<T> modelClass, QueryBuilder<T, Integer> queryBuilder);

    <T> T find(Class<T> modelClass, PreparedQuery<T> preparedQuery);

    <T> void raw(Class<T> modelClass, PreparedQuery<T> preparedQuery);

    <T> T executeRaw(Class<T> modelClass, String query);

    <T> Collection<T> executeRawCollection(Class<T> modelClass, String query);

    <T> List<T> executeRawList(Class<T> modelClass, String query);

    <T> void addOrUpdate(Class<T> modelClass, T item);

    <T> void addOrUpdate(Class<T> modelClass, Collection<T> items);

    <T> void addOrUpdate(Class<T> modelClass, List<T> items);

    <T> GenericRawResults<String[]> getRawResults(Class<T> modelClass, String query);

    <T> JSONArray getResultAsJsonArray(Class<T> modelClass, String query);

    <T> void createTable(Class<T> modelClass);

    <T> void clearTable(Class<T> modelClass);

    <T> void dropTable(Class<T> modelClass);

    void inTransaction(Callable callable) throws Exception;

    <T> Dao<T, ?> myDao(Class<T> modelClass) throws Exception;

    <T> void batch(Class<T> modelClass, List<T> items) throws Exception;

}
