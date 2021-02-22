package com.hungerbox.customer.order.adapter.viewholder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.hungerbox.customer.R;
import com.hungerbox.customer.util.AppUtils;

public class VendingMachineViewHolder extends RecyclerView.ViewHolder {

    public ShapeableImageView ivVMImage;
    public TextView tvVMName,tvDesc;

    public VendingMachineViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView);
        ivVMImage = itemView.findViewById(R.id.iv_vm);
        tvVMName = itemView.findViewById(R.id.tv_vm_name);
        tvDesc = itemView.findViewById(R.id.tv_desc);

        float radius = 25f;
        if (activity!=null) {
            radius = AppUtils.convertDpToPixel(10, activity);
        }

        ivVMImage.setShapeAppearanceModel(ivVMImage.getShapeAppearanceModel()
                .toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
                .build());
    }
}
