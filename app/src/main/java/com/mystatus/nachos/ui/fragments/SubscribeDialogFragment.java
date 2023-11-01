package com.mystatus.nachos.ui.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mystatus.nachos.R;
import com.mystatus.nachos.ui.listener.SubscribeDialogListener;


public class SubscribeDialogFragment extends DialogFragment {

    private SubscribeDialogListener subscribeDialogListener;
//    private Button text_view_go_pro;
    private RelativeLayout relative_layout_watch_ads;
    private Boolean withAds;
    private int layout;

    public SubscribeDialogFragment(SubscribeDialogListener subscribeDialogListener, Boolean withAds, int layout) {
        this.subscribeDialogListener = subscribeDialogListener;
        this.withAds = withAds;
        this.layout = layout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(layout, container, false);
        findViewByIds(view);
        initView();
        setListeners();
        return view;
    }

    private void findViewByIds(View view) {
//        text_view_go_pro = (Button) view.findViewById(R.id.text_view_go_pro);
        relative_layout_watch_ads = (RelativeLayout) view.findViewById(R.id.relative_layout_watch_ads);
    }

    private void initView() {
        if (withAds) {
            relative_layout_watch_ads.setVisibility(View.VISIBLE);
        } else {
            relative_layout_watch_ads.setVisibility(View.GONE);
        }
    }

    private void setListeners() {
//        text_view_go_pro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    text_view_go_pro.setBackground(getResources().getDrawable(R.drawable.bg_main_focus_stroke_10));
//                } else {
//                    text_view_go_pro.setBackground(null);
//                    text_view_go_pro.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                }
//            }
//        });

//        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                subscribeDialogListener.onSubscribeClicked();
//                dismiss();
//            }
//        });

        relative_layout_watch_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribeDialogListener.onAdClicked();
                dismiss();
            }
        });

//        text_view_go_pro.setFocusable(true);
//        text_view_go_pro.setFocusableInTouchMode(true);
//        text_view_go_pro.requestFocus();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}