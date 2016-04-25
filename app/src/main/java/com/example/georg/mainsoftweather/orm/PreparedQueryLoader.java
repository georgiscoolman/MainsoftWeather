package com.example.georg.mainsoftweather.orm;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Georg on 25.04.2016.
 */
public class PreparedQueryLoader<T, ID> extends AsyncTaskLoader<List<T>> {

    public final String TAG = getClass().getSimpleName();
    protected Dao<T, ID> dao;
    private PreparedQuery<T> preparedQuery;

    public PreparedQueryLoader(Context context, Dao<T, ID> dao, PreparedQuery<T> preparedQuery) {
        super(context);
        this.dao = dao;
        this.preparedQuery = preparedQuery;
    }

    @Override
    public List<T> loadInBackground() {
        Log.d(TAG, "loadInBackground");

        if (dao == null) {
            throw new IllegalStateException("Dao is not initialized.");
        }
        if (preparedQuery == null) {
            throw new IllegalStateException("PreparedQuery is not initialized.");
        }

        return getEntitys();
    }

    @Override
    public void forceLoad() {
        Log.d(TAG, "forceLoad");
        super.forceLoad();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d(TAG, "onStartLoading");
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        Log.d(TAG, "onStopLoading");
    }

    @Override
    public void deliverResult(List<T> data) {
        super.deliverResult(data);
        Log.d(TAG, "deliverResult");
    }

    private List<T> getEntitys() {
        List<T> res = new ArrayList<>();

        try {
            res = dao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }
}
