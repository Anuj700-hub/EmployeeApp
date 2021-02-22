package com.hungerbox.customer.order.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.marketing.AutoRefreshBannerFragment;
import com.hungerbox.customer.marketing.BannerFragment;
import com.hungerbox.customer.model.HomeBannerItem;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.listeners.UpdateCartListener;
import com.hungerbox.customer.util.ApplicationConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by peeyush on 28/6/16.
 */
public class RecommendationPagerAdapter extends FragmentStatePagerAdapter implements UpdateCartListener {

    private final long ocassionId;
    Realm realm;
    RealmResults<Vendor> vendorList;
    boolean vendorListAvailable;
    ArrayList<Object> baseBanners;
    LayoutInflater inflater;
    ClickInterface clickInterface;
    HashMap<Long, Vendor> vendorHash = new HashMap<>();
    MainApplication mainApplication;
    List<Fragment> recommendedFragments = new ArrayList<>();

    public RecommendationPagerAdapter(FragmentManager fm, LayoutInflater inflater, final ArrayList<Object> baseBanners,
                                      ClickInterface clickInterface, MainApplication mainApplication, long ocassionId) {
        super(fm);
        this.inflater = inflater;
        this.baseBanners = baseBanners;
        this.clickInterface = clickInterface;
        this.mainApplication = mainApplication;
        this.ocassionId = ocassionId;

//        resetVendors();
    }

//    public void resetVendors() {
//        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
//        vendorList = realm.where().findAll();
//        vendorListAvailable = createHash(vendorList);
//    }

//    private boolean createHash(RealmResults<Vendor> vendorList) {
//        vendorHash.clear();
//        for (Vendor vendor : vendorList) {
//            vendorHash.put(vendor.getId(), vendor);
//        }
//        if (vendorHash.size() > 0)
//            return true;
//        else
//            return false;
//    }

    @Override
    public Fragment getItem(int position) {

        Object item = baseBanners.get(position);
        Fragment fragment = null;
        if (item instanceof HomeBannerItem) {
            HomeBannerItem homeBannerItem = (HomeBannerItem) item;
            if (homeBannerItem.getType()!=null && homeBannerItem.getType().equalsIgnoreCase(ApplicationConstants.CONTEST_BANNER)) {
                fragment = AutoRefreshBannerFragment.newInstance(homeBannerItem);
            } else {
                fragment = BannerFragment.newInstance(homeBannerItem);
            }
        }
        return fragment;
    }

    public void updateFragments() {
        for (Fragment recommendationFragment : recommendedFragments) {
            if (recommendationFragment instanceof UpdateCartListener) {
                ((UpdateCartListener) recommendationFragment).onOrderItemChanged();
            }
        }
    }

    @Override
    public int getCount() {
        return baseBanners.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return super.isViewFromObject(view, object);
    }

    @Override
    public void onOrderItemChanged() {
        updateFragments();
    }

    @Override
    public void onBannerAdded(ArrayList<Object> banners) {
        this.notifyDataSetChanged();
    }

    public interface ClickInterface {
        void OnAddToCart(Vendor vendor, Product product, boolean isBuffet);

        void OnCheckout(Vendor vendor, Product product, boolean isBuffet);

        void GoToOrderReview();
    }

}
