package com.dev.rexhuang.zhiliao_ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

/**
 * *  created by RexHuang
 * *  on 2019/9/13
 */
public class StrongBottomSheetDialog extends BottomSheetDialog {

    private int mPeekHeight;
    private int mMaxHeight;
    private Window mWindow;
    private boolean mCreated;
    private BottomSheetBehavior mBottomSheetBehavior;

    // 便捷的构造器
    public StrongBottomSheetDialog(@NonNull Context context, int peekHeight, int maxHeight) {
        this(context);

        mPeekHeight = peekHeight;
        mMaxHeight = maxHeight;
    }

    public StrongBottomSheetDialog(@NonNull Context context) {
        super(context);
        mWindow = getWindow();
    }

    public StrongBottomSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
        mWindow = getWindow();
    }

    protected StrongBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mWindow = getWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackgroup();
        mCreated = true;
        setPeekHeight();
        setMaxHeight();
        setBottomSheetCallback();
    }

    private void setBackgroup() {
        mWindow.findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
    }

    private void setPeekHeight(int peekHeight) {
        mPeekHeight = peekHeight;
        if (mCreated) {
            setPeekHeight();
        }
    }

    private void setPeekHeight() {
        if (mPeekHeight <= 0) {
            return;
        }

        if (getBottomSheetBehavior() != null) {
            mBottomSheetBehavior.setPeekHeight(mPeekHeight);
        }
    }

    public void setMaxHeight(int height) {
        mMaxHeight = height;

        if (mCreated) {
            setMaxHeight();
        }
    }

    private void setMaxHeight() {
        if (mMaxHeight <= 0) {
            return;
        }

        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, mMaxHeight);
        mWindow.setGravity(Gravity.BOTTOM);
    }

    private void setBottomSheetCallback() {
        if (getBottomSheetBehavior() != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetCallback);
        }
    }

    private BottomSheetBehavior getBottomSheetBehavior() {
        if (mBottomSheetBehavior != null) {
            return mBottomSheetBehavior;
        }

        View view = mWindow.findViewById(R.id.design_bottom_sheet);
        if (view == null) {
            return null;
        }
        mBottomSheetBehavior = BottomSheetBehavior.from(view);
        return mBottomSheetBehavior;
    }

    private final BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };
}
