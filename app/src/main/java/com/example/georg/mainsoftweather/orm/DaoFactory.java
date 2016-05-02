package com.example.georg.mainsoftweather.orm;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Georg on 20.04.2016.
 */
public class DaoFactory {
    private static DaoFactory instance = new DaoFactory();
    private DatabaseHelper helper = HelperFactory.getHelper();
    private static Map<Class, Map<Class<? extends Number>, Dao>> daoMap;
    private DaoFactory() {
        daoMap = new HashMap<Class, Map<Class<? extends Number>, Dao>>();
    }

    public static DaoFactory getInstance() {
        return instance;
    }

    public synchronized <Type> Dao<Type, Long> getDao(Class<Type> clazz) {
        return getDao(clazz, Long.class);
    }

    public synchronized <Type, Key extends Number> Dao<Type, Key> getDao(Class<Type> type, Class<Key> key) {
        Map<Class<? extends Number>, Dao> map = daoMap.get(type);
        Dao<Type, Key> dao = null;
        if (map != null) {
            dao = map.get(key);
        } else {
            map = new HashMap<Class<? extends Number>, Dao>();
            daoMap.put(type, map);
        }
        if (dao == null) {
            try {
                dao = helper.getDao(type);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            map.put(key, dao);
        }
        return dao;
    }

    public void clean() {
        daoMap.clear();
    }
}
