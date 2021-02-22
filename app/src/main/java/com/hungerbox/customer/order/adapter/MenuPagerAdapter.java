package com.hungerbox.customer.order.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.model.Category;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.fragment.MenuFragment;
import com.hungerbox.customer.order.fragment.MenuListFragment;

import java.util.TreeMap;

import io.realm.RealmResults;

/**
 * Created by peeyush on 24/6/16.
 */
public class MenuPagerAdapter extends FragmentPagerAdapter {

    RealmResults<Category> categories;
    TreeMap<Integer, MenuListFragment> fragmentHashMap = new TreeMap<>();
    Vendor vendor;
    long occasionId;
    boolean containsRecommended;
    private MenuFragment menuFragment;

    public MenuPagerAdapter(FragmentManager fm, RealmResults<Category> element,
                            Vendor vendor, long occasionId, MenuFragment menuFragment, boolean containsRecommended) {
        super(fm);
        this.menuFragment = menuFragment;
        this.vendor = vendor;
        this.occasionId = occasionId;
        categories = element;
        this.containsRecommended = containsRecommended;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return super.isViewFromObject(view, object);
    }

    @Override
    public Fragment getItem(int position) {

        if (containsRecommended) {
            Category category;
            MenuListFragment fragment = fragmentHashMap.get(position);
            if (position == 0) {
                category = null;
                fragment = MenuListFragment.newInstance(position,0, null,
                        vendor, occasionId, menuFragment);
            } else {
                category = categories.get(position - 1);
                fragment = MenuListFragment.newInstance(position,category.getId(), category.getName(),
                        vendor, occasionId, menuFragment);
            }
            return fragment;
        } else {
            Category category = categories.get(position);
            MenuListFragment fragment = fragmentHashMap.get(position);
            fragment = MenuListFragment.newInstance(position,category.getId(), category.getName(),
                    vendor, occasionId, menuFragment);

            return fragment;
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }


    @Override
    public int getCount() {
        if (categories == null)
            return 0;
        else if (containsRecommended)
            return categories.size() + 1;
        else
            return categories.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (containsRecommended) {
            if (position == 0) {
                return "Recommended";
            } else {
                return categories.get(position - 1).getName();
            }
        } else {
            return categories.get(position).getName();
        }
    }


    public void changeCategories(RealmResults<Category> element, Vendor vendor) {
        this.vendor = vendor;
        this.categories = element;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
