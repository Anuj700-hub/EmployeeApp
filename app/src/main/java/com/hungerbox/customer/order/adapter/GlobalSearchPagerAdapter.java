package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hungerbox.customer.model.FeatureSearch;
import com.hungerbox.customer.model.FeatureSearchVendorMenu;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.navmenu.fragment.BlankFragment;
import com.hungerbox.customer.order.activity.GlobalSearchActivity;
import com.hungerbox.customer.order.fragment.SearchDishesFragment;
import com.hungerbox.customer.order.fragment.SearchVendorFragment;
import com.hungerbox.customer.util.ApplicationConstants;

import java.util.ArrayList;
import java.util.TreeMap;

public class GlobalSearchPagerAdapter extends FragmentPagerAdapter {


    private ArrayList<String> tabTitle;
    private Activity activity;
    private TreeMap<Integer, Fragment> fragmentHashMap = new TreeMap<>();
    //private FeatureSearch featureSearchResponseObject;
    ArrayList<Vendor> listOfVendors;
    Fragment fragment;
    SearchVendorFragment searchVendorFragment;
    SearchDishesFragment searchDishesFragment;



    public GlobalSearchPagerAdapter(FragmentManager fm, Activity activity, ArrayList<String> tabTitle) {
        super(fm);
        this.tabTitle = tabTitle;
        this.activity = activity;
    }

    public void setData(ArrayList<Vendor> vendors, ArrayList<Object> products, int dishQuantity){
        //this.featureSearchResponseObject = featureSearchResponseObject;

            if (getFragment(1)instanceof SearchVendorFragment) {
                ((SearchVendorFragment) getFragment(1)).setVendors(vendors);
            }
            if (getFragment(0)instanceof SearchDishesFragment) {
                ((SearchDishesFragment) getFragment(0)).setProducts(products,dishQuantity);
            }
    }

    @Override
    public Fragment getItem(int i) {

        fragment = new BlankFragment();
        switch (i) {
            case 1: {
                searchVendorFragment = SearchVendorFragment.newInstance();
                fragment = searchVendorFragment;
            }
            break;
            case 0: {
                searchDishesFragment = SearchDishesFragment.newInstance();
                fragment = searchDishesFragment;
            }
            break;
            default:
        }

        fragmentHashMap.put(i,fragment);
        return fragment;
    }

    public Fragment getFragment(int i) {
        if (fragmentHashMap.containsKey(i)){
            return fragmentHashMap.get(i);
        } else{
            return new BlankFragment();
        }
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
        return tabTitle.get(position);
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        fragmentHashMap.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return tabTitle.size();
    }
}

