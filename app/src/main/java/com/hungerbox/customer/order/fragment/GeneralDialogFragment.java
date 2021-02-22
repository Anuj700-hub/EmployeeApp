package com.hungerbox.customer.order.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;


/**
 * Created by ranjeet on 07,January,2019
 */
public class GeneralDialogFragment extends DialogFragment {

    private OnDialogFragmentClickListener mListener;

    public static GeneralDialogFragment newInstance(String title, String message, OnDialogFragmentClickListener mListener) {
        GeneralDialogFragment frag = new GeneralDialogFragment();
        Bundle args = new Bundle();
        frag.mListener = mListener;
        args.putString("title", title);
        args.putString("msg", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString("title")).
                setMessage(getArguments().getString("msg")).
                setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(mListener!=null) {
                    mListener.onPositiveInteraction(GeneralDialogFragment.this);
                }
                dismissAllowingStateLoss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(mListener!=null) {
                    mListener.onNegativeInteraction(GeneralDialogFragment.this);
                }
                dismissAllowingStateLoss();

            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnDialogFragmentClickListener {
        void onPositiveInteraction(GeneralDialogFragment dialog);

        void onNegativeInteraction(GeneralDialogFragment dialog);
    }
}
