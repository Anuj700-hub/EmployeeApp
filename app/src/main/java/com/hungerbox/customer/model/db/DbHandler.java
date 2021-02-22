package com.hungerbox.customer.model.db;

import android.content.Context;

import com.hungerbox.customer.bluetooth.Model.UserViolation;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.model.AppEvent;
import com.hungerbox.customer.model.DataClass;
import com.hungerbox.customer.model.DataKeyClass;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by peeyush on 20/7/16.
 */
public class DbHandler {


    private static DbHandler dbHandler;
    private static DatabaseOpenHelper databaseOpenHelper;

    private DbHandler() {

    }

    public static DbHandler getDbHandler(Context context) {
        if (dbHandler == null) {
            dbHandler = new DbHandler();
            databaseOpenHelper = new DatabaseOpenHelper(context);
        } else if (!databaseOpenHelper.isOpen()) {
            try {
                databaseOpenHelper.close();
            } catch (Exception e) {
                AppUtils.logException(e);
            }
            databaseOpenHelper = new DatabaseOpenHelper(context);
        }


        return dbHandler;
    }

    public static boolean isStarted() {
        return (databaseOpenHelper != null && databaseOpenHelper.isOpen());
    }

    public static void start(Context context) {
        if (databaseOpenHelper == null || !databaseOpenHelper.isOpen()) {
            if (databaseOpenHelper != null)
                try {
                    databaseOpenHelper.close();
                } catch (Exception e) {

                }

            databaseOpenHelper = new DatabaseOpenHelper(context);
        }
    }


    public List<Order> getAllOrder() {
        try {
            return databaseOpenHelper.getOrderDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Order getOrder(long orderId) {
        try {
            return databaseOpenHelper.getOrderDao().queryForEq("id", orderId).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createOrder(Order order) {
        try {
            return databaseOpenHelper.getOrderDao().create(order) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createOrders(List<Order> orders) {
        boolean result = true;
        for (Order order : orders) {
            result &= createOrder(order);
        }
        return result;
    }

    public List<OrderOffline> getAllOrderOffline(){
        try{

            QueryBuilder<OrderOffline, Long> query = databaseOpenHelper.getOrderOfflineDao().queryBuilder();
            query.orderBy("createdAt",false);

            return databaseOpenHelper.getOrderOfflineDao().query(
                    query.prepare()
            );


        }catch (SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public OrderOffline getOrderOffline(long orderId){
        try {
            return databaseOpenHelper.getOrderOfflineDao().queryForEq("id",orderId).get(0);
        }catch (SQLException e){
            e.printStackTrace();;
            return null;
        }
    }

    public boolean createOrderOffline(OrderOffline orderOffline){
        try{
            return databaseOpenHelper.getOrderOfflineDao().create(orderOffline)==1;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateOrderOffline(OrderOffline orderOffline){
        try {
            return databaseOpenHelper.getOrderOfflineDao().update(orderOffline)==1;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteOrderOffline(OrderOffline orderOffline){
        try {
            return databaseOpenHelper.getOrderOfflineDao().delete(orderOffline)==1;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOrder(Order order){
        try {
            return databaseOpenHelper.getOrderDao().delete(order)==1;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean createUserViolation(UserViolation userViolation) {
        try {
            return databaseOpenHelper.getUserViolationDao().create(userViolation) == 1;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public UserViolation getUserViolationFromDB(UserViolation violation){
        try {
            QueryBuilder<UserViolation, Long> query = databaseOpenHelper.getUserViolationDao().queryBuilder();
            query.where()
                    .eq("contact_id",violation.getContact_id())
                    .and()
                    .ge("timestamp", DateTimeUtil.getTodayTimeStamp())
                    .and()
                    .eq("location_id", violation.getLocation_id());

            List<UserViolation> userViolations = databaseOpenHelper.getUserViolationDao().query(query.prepare());

            if(userViolations != null && userViolations.size() > 0){
                return userViolations.get(0);
            }
            else{
                return null;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UserViolation> getViolationsForGivenTimeStamp(long time){
        try {
            QueryBuilder<UserViolation, Long> query = databaseOpenHelper.getUserViolationDao().queryBuilder();
            query.where().ge("timestamp", time);

            List<UserViolation> userViolations = databaseOpenHelper.getUserViolationDao().query(query.prepare());

            if(userViolations != null){
                return userViolations;
            }
            else{
                return new ArrayList<>();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void updateUserViolationForCurrentDay(UserViolation violation){
        try {
            UpdateBuilder<UserViolation, Long> updateBuilder = databaseOpenHelper.getUserViolationDao().updateBuilder();
            updateBuilder.updateColumnValue("count", violation.getCount() + 1);
            updateBuilder.updateColumnValue("timestamp", violation.getTimestamp());
            updateBuilder.where()
                    .eq("contact_id", violation.getContact_id())
                    .and()
                    .ge("timestamp", DateTimeUtil.getTodayTimeStamp())
                    .and()
                    .eq("location_id", violation.getLocation_id());
            updateBuilder.update();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUserViolationTable(){
        try {
            DeleteBuilder<UserViolation,Long> deleteBuilder = databaseOpenHelper.getUserViolationDao().deleteBuilder();
            deleteBuilder.delete();
            AppUtils.HbLog("db-operation","user violation table deleted");
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteViolationsBefore(long time){

        try {
            DeleteBuilder<UserViolation,Long> deleteBuilder = databaseOpenHelper.getUserViolationDao().deleteBuilder();
            deleteBuilder.where().lt("timestamp", time);
            databaseOpenHelper.getUserViolationDao().delete(deleteBuilder.prepare());
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean createLocation(Location location) {
        try {
            return databaseOpenHelper.getLocationDao().createOrUpdate(location).getNumLinesChanged() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createLocations(List<Location> locations) {
        boolean result = true;
        for (Location location : locations) {
            result &= createLocation(location);
        }
        return result;
    }

    public Location getLocationFor(long locationId) {
        try {
            return databaseOpenHelper.getLocationDao().queryForId(locationId);
        } catch (SQLException e) {
            AppUtils.logException(e);
            return null;
        } catch (Exception e) {
            AppUtils.logException(e);
            return null;
        }
    }

    public List<Location> getLocations() {
        try {
            return databaseOpenHelper.getLocationDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void createDataClass(ArrayList<DataClass> dataClassArrayList){

        for (DataClass dataClass : dataClassArrayList) {
          createDataClass(dataClass);
        }
    }

    public void createDataClass(DataClass dataClass){
        try {
             databaseOpenHelper.getDataClassesDao().createOrUpdate(dataClass);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public DataClass getDataClass(DataKeyClass dataKeyClass){
        try{

            QueryBuilder<DataClass, Long> query = databaseOpenHelper.getDataClassesDao().queryBuilder();
            query.where().eq("apiKey",dataKeyClass.getApiKey()).and().eq("object1Id",dataKeyClass.getObject1Id()).and()
           .eq("object2Id",dataKeyClass.getObject2Id());

            List<DataClass> dataClasses = databaseOpenHelper.getDataClassesDao().query(query.prepare());

            if(dataClasses!=null && dataClasses.size()>0)
                return dataClasses.get(0);
            else
                return null;

        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    public void deleteDataClass(DataKeyClass dataKeyClass){
        try {
            DeleteBuilder<DataClass,Long> deleteBuilder = databaseOpenHelper.getDataClassesDao().deleteBuilder();
            deleteBuilder.where().eq("apiKey",dataKeyClass.getApiKey()).and().eq("object1Id",dataKeyClass.getObject1Id()).and()
                    .eq("object2Id",dataKeyClass.getObject2Id());

            databaseOpenHelper.getDataClassesDao().delete(deleteBuilder.prepare());

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteDataTable(){
        try {
            DeleteBuilder<DataClass,Long> deleteBuilder = databaseOpenHelper.getDataClassesDao().deleteBuilder();
            deleteBuilder.delete();
            AppUtils.HbLog("db-operation","data table deleted");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public Config getConfig(Context context) {
        try {
            return databaseOpenHelper.getConfigDao().queryForAll().get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            EventUtil.FbEventLog(context, "Config Error", e.toString());
            return new Config();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            EventUtil.FbEventLog(context, "Config Error", e.toString());
            return new Config();
        }
    }

    public void setConfig(Config configToSet) {
        try {
            DeleteBuilder<Config, Long> deleteBuilder = databaseOpenHelper.getConfigDao().deleteBuilder();
            deleteBuilder.where()
                    .ne("company_id", configToSet.getCompany_id());
            databaseOpenHelper.getConfigDao().delete(deleteBuilder.prepare());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            databaseOpenHelper.getConfigDao().createOrUpdate(configToSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAppEvent(AppEvent appEvent) {
        return;
//        try{
//            databaseOpenHelper.getAppEventDao().create(appEvent);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public AppEvent getFirstAppEvent() {
        try {
            return databaseOpenHelper.getAppEventDao().queryForFirst(
                    databaseOpenHelper.getAppEventDao().queryBuilder()
                            .prepare()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void removeAppEvent(AppEvent appEvent) {
        try {
            databaseOpenHelper.getAppEventDao().delete(appEvent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAppEvents(List<AppEvent> appEvents) {
        try {
            databaseOpenHelper.getAppEventDao().delete(appEvents);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<AppEvent> getFirstAppEvents() {
        try {
            return databaseOpenHelper.getAppEventDao().query(
                    databaseOpenHelper.getAppEventDao().queryBuilder()
                            .limit(25l)
                            .prepare()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createVendors(List<Vendor>  vendors){
        boolean result = true;
        deleteAllVendors();
        for (Vendor vendor : vendors){
            result &= createVendor(vendor);
        }
        return result;
    }

    public boolean createVendor(Vendor vendor){
        try {
            return databaseOpenHelper.getVendorDao().createOrUpdate(vendor).getNumLinesChanged()==1;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public void deleteAllVendors(){

        try{
            DeleteBuilder<Vendor , Long> deleteBuilder = databaseOpenHelper.getVendorDao().deleteBuilder();
            databaseOpenHelper.getVendorDao().delete(deleteBuilder.prepare());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Vendor getVendorFor(long vendorId){
        try {
            return databaseOpenHelper.getVendorDao().queryForEq("id",vendorId).get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<Vendor> getActiveVendorList(){
        try {
            return databaseOpenHelper.getVendorDao().queryForEq("active",1);
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }




}
