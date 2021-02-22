package com.hungerbox.customer.util.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.hungerbox.customer.model.AppEvent;
import com.hungerbox.customer.model.db.DbHandler;


/**
 * Created by sandipanmitra on 4/19/17.
 */

public class VendorEventStoreTask extends AsyncTask<Void, Integer, Void> {

    AppEvent appEvent;
    Context context;

    public VendorEventStoreTask(Context context, AppEvent appEvent) {
        this.appEvent = appEvent;
        this.context = context;
    }

    public static void addEventToQueue(Context context, AppEvent appEvent) {
        try {
            new VendorEventStoreTask(context, appEvent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (Error er) {
            er.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (!DbHandler.isStarted())
                DbHandler.start(context);
            DbHandler dbHandler = DbHandler.getDbHandler(context);

            dbHandler.addAppEvent(appEvent);
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } catch (Error er) {
            er.printStackTrace();
            return null;
        }
    }
}
