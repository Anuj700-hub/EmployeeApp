package com.hungerbox.customer.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.bluetooth.Model.UserViolation;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.model.AppEvent;
import com.hungerbox.customer.model.DataClass;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.util.AppUtils;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * Created by peeyush on 20/7/16.
 */
public class DatabaseOpenHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "hubox";
    private static final int DATABASE_VERSION = BuildConfig.VERSION_CODE;

    private Dao<Order, Long> orderDao;
    private Dao<Location, Long> locationDao;
    private Dao<Config, Long> configDao;
    private Dao<AppEvent, Long> appEventDao;
    private Dao<OrderOffline,Long> orderOfflineDao;
    private Dao<DataClass , Long> dataClassesDao;
    private Dao<Vendor, Long> vendorDao;
    private Dao<UserViolation, Long> userViolationDao;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseOpenHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    public DatabaseOpenHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, int configFileId) {
        super(context, databaseName, factory, databaseVersion, configFileId);
    }

    public DatabaseOpenHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, File configFile) {
        super(context, databaseName, factory, databaseVersion, configFile);
    }

    public DatabaseOpenHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, InputStream stream) {
        super(context, databaseName, factory, databaseVersion, stream);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            /**
             * creates the Todo database table
             */
            TableUtils.createTableIfNotExists(connectionSource, Order.class);
            TableUtils.createTableIfNotExists(connectionSource, Location.class);
            TableUtils.createTableIfNotExists(connectionSource, AppEvent.class);
            TableUtils.createTableIfNotExists(connectionSource, Config.class);
            TableUtils.createTableIfNotExists(connectionSource,OrderOffline.class);
            TableUtils.createTableIfNotExists(connectionSource, DataClass.class);
            TableUtils.createTableIfNotExists(connectionSource, Vendor.class);
            TableUtils.createTableIfNotExists(connectionSource, UserViolation.class);
        } catch (SQLException e) {
            AppUtils.logException(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            /**
             * Recreates the database when onUpgrade is called by the framework
             */
            TableUtils.dropTable(connectionSource, Order.class, true);
            TableUtils.dropTable(connectionSource, Location.class, true);
            TableUtils.dropTable(connectionSource, Config.class, true);
            TableUtils.dropTable(connectionSource, AppEvent.class, true);
            TableUtils.dropTable(connectionSource, OrderOffline.class,true);
            TableUtils.dropTable(connectionSource , DataClass.class ,true);
            TableUtils.dropTable(connectionSource,Vendor.class,true);
            TableUtils.dropTable(connectionSource , UserViolation.class ,true);
            onCreate(database, connectionSource);

        } catch (SQLException e) {
            AppUtils.logException(e);
            e.printStackTrace();
        }
    }


    public Dao<Order, Long> getOrderDao() throws SQLException {
        if (orderDao == null) {
            orderDao = getDao(Order.class);
        }
        return orderDao;
    }

    public Dao<Location, Long> getLocationDao() throws SQLException {
        if (locationDao == null) {
            locationDao = getDao(Location.class);
        }
        return locationDao;
    }

    public Dao<Config, Long> getConfigDao() throws SQLException {
        if (configDao == null) {
            configDao = getDao(Config.class);
        }
        return configDao;
    }


    public Dao<AppEvent, Long> getAppEventDao() throws SQLException {
        if (appEventDao == null) {
            appEventDao = getDao(AppEvent.class);
        }
        return appEventDao;
    }

    public Dao<OrderOffline,Long> getOrderOfflineDao() throws SQLException{
        if(orderOfflineDao == null){
            orderOfflineDao = getDao(OrderOffline.class);
        }
        return orderOfflineDao;
    }

    public Dao<DataClass,Long> getDataClassesDao() throws SQLException{
        if(dataClassesDao == null){
            dataClassesDao = getDao(DataClass.class);
        }
        return dataClassesDao;
    }

    public Dao<Vendor,Long> getVendorDao() throws SQLException{
        if(vendorDao == null){
            vendorDao = getDao(Vendor.class);
        }
        return vendorDao;
    }

    public Dao<UserViolation,Long> getUserViolationDao() throws SQLException{
        if(userViolationDao == null){
            userViolationDao = getDao(UserViolation.class);
        }
        return userViolationDao;
    }
}
