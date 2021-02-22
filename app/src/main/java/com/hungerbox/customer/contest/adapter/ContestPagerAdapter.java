package com.hungerbox.customer.contest.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.contest.fragment.ContestFragment;
import com.hungerbox.customer.navmenu.fragment.BlankFragment;
import com.hungerbox.customer.util.ApplicationConstants;

import java.util.ArrayList;
import java.util.TreeMap;

public class ContestPagerAdapter extends FragmentPagerAdapter {


    private ArrayList<String> offerType;
    private Activity activity;
    private TreeMap<Integer, Fragment> fragmentHashMap = new TreeMap<>();
    private String source;



    public ContestPagerAdapter(FragmentManager fm,Activity activity,ArrayList<String> offerType,String source) {
        super(fm);
        this.offerType = offerType;
        this.activity = activity;
        this.source = source;
    }

    @Override
    public Fragment getItem(int i) {

        Fragment fragment = new BlankFragment();
        String title = offerType.get(i);
        switch(title){
            case ApplicationConstants.CONTEST_TITLE_CAMPAIGN:
                fragment = ContestFragment.newInstance(source);
                break;
             default:
        }

        fragmentHashMap.put(i,fragment);
        return fragment;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return super.isViewFromObject(view, object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return offerType.get(position);
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        fragmentHashMap.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return offerType.size();
    }
}
