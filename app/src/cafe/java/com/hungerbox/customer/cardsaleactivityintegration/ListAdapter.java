package com.hungerbox.customer.cardsaleactivityintegration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hungerbox.customer.R;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
	
    ArrayList<String> listData = null;
    Context context;

	public ListAdapter(Context context, ArrayList<String> listData) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
    	
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.customapplstitem, null);
        }
        

        TextView txtItem = (TextView) convertView.findViewById(R.id.menuview_lsttext);
        
        if (listData.get(position) != null)
            txtItem.setText(listData.get(position));
        
        return convertView;
    }
}