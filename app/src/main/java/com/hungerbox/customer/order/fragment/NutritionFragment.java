package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Nutrition;
import com.hungerbox.customer.order.adapter.NutritionListAdpater;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NutritionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NutritionFragment extends DialogFragment {


    Nutrition nutrition;
    String productName;
    int count;

    RecyclerView rvItems;

    TextView tvTitle, tvClose, tvTotalCal;

    public NutritionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NutritionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NutritionFragment newInstance(Nutrition nutrition, String name, int count) {
        NutritionFragment fragment = new NutritionFragment();
        fragment.nutrition = nutrition;
        fragment.productName = name;
        fragment.count = count;
        return fragment;
    }


    public static void show(AppCompatActivity activity, Nutrition nutrition, String name, int count) {
        NutritionFragment nutritionFragment = newInstance(nutrition, name, count);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(nutritionFragment, "nutrition")
                .commitAllowingStateLoss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_nutrition, container, false);
        tvTitle = view.findViewById(R.id.tv_title);
        tvClose = view.findViewById(R.id.bt_customize_negative);
        tvTotalCal = view.findViewById(R.id.tv_total_cal);
        rvItems = view.findViewById(R.id.rv_items);

        rvItems.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setAutoMeasureEnabled(false);
        rvItems.setLayoutManager(llm);
        setupList();

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });

        tvTotalCal.setText(String.format("%.2f", (nutrition.getTotalCal() * count)));

        tvTitle.setText("Calories - " + productName);
        return view;
    }

    private void setupList() {
        if (rvItems.getAdapter() == null) {
            NutritionListAdpater nutritionListAdpater = new NutritionListAdpater(getActivity(), nutrition, count);
            rvItems.setAdapter(nutritionListAdpater);
        } else {
            rvItems.getAdapter().notifyDataSetChanged();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
