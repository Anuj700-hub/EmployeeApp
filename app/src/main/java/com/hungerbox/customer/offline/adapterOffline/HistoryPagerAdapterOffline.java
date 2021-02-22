package com.hungerbox.customer.offline.adapterOffline;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.navmenu.fragment.BlankFragment;
import com.hungerbox.customer.navmenu.fragment.HistoryFragment;
import com.hungerbox.customer.navmenu.listeners.UpdateListener;
import com.hungerbox.customer.offline.fragmentOffline.OrderHistoryFragmentOffline;
import com.hungerbox.customer.offline.fragmentOffline.OrderHistoryFragmentOrderOffline;
import com.hungerbox.customer.util.ApplicationConstants;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by peeyush on 24/6/16.
 */
public class HistoryPagerAdapterOffline extends FragmentPagerAdapter {

    private final Activity activity;
    private final ArrayList<String> historyTypes;
    TreeMap<Integer, Fragment> fragmentHashMap = new TreeMap<>();
    private String value = "";


    public HistoryPagerAdapterOffline(FragmentManager fm, Activity activity, ArrayList<String> historyTypes, String value) {
        super(fm);
        this.value = value;
        this.activity = activity;
        this.historyTypes = historyTypes;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return super.isViewFromObject(view, object);
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof HistoryFragment) {
            OrderHistoryFragmentOffline f = (OrderHistoryFragmentOffline) object;
            if (f != null) {
                f.update();
            }
        }
        if (object instanceof OrderHistoryFragmentOffline) {
            OrderHistoryFragmentOffline f = (OrderHistoryFragmentOffline) object;
            if (f != null) {
                f.update();
            }
        }

        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new BlankFragment();
        String title = historyTypes.get(position);
        switch (title) {
            case ApplicationConstants.HISTORY_TITLE_FOOD:
                fragment = OrderHistoryFragmentOffline.newInstance(1, ApplicationConstants.HISTORY_TYPE_ORDER, value);
                break;
            case ApplicationConstants.HISTORY_TITLE_FOOD_OFFLINE:
                fragment = OrderHistoryFragmentOrderOffline.newInstance(ApplicationConstants.HISTORY_TITLE_FOOD_OFFLINE);
                break;
            case ApplicationConstants.HISTORY_TITLE_SHARED_ECONOMY:
                fragment = HistoryFragment.newInstance(1, "meeting");
                break;
            default:

        }

        fragmentHashMap.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragmentHashMap.remove(position);
        super.destroyItem(container, position, object);
    }


    @Override
    public int getCount() {
        return historyTypes.size();

    }


    @Override
    public CharSequence getPageTitle(int position) {
        return historyTypes.get(position);
    }

    public void updateFragments() {
        for (Fragment fragment : fragmentHashMap.values()) {
            if (fragment instanceof UpdateListener) {
                UpdateListener updateListener = (UpdateListener) fragment;
                updateListener.updateData();
            }
        }
    }

}
