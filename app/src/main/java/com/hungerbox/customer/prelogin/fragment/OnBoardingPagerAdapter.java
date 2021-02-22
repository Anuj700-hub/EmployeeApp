package com.hungerbox.customer.prelogin.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.model.OnBoard;

import java.util.ArrayList;

/**
 * Created by peeyush on 24/6/16.
 */
public class OnBoardingPagerAdapter extends FragmentPagerAdapter {

    ArrayList<OnBoard> onBoards;


    public OnBoardingPagerAdapter(FragmentManager fm, ArrayList<OnBoard> onBoards) {
        super(fm);
        this.onBoards = onBoards;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return super.isViewFromObject(view, object);
    }

    @Override
    public Fragment getItem(int position) {
        OnBoardingFragment fragment = OnBoardingFragment.newInstance(position, getCount(), onBoards.get(position));
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }


    @Override
    public int getCount() {
        return onBoards.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return onBoards.get(position).getTitle();
    }


}
