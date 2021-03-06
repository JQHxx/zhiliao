package com.dev.rexhuang.zhiliao;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

/**
 * *  created by RexHuang
 * *  on 2019/9/16
 */
public class DialogHelper {

    public static BottomSheetDialog createQueueDialog(Activity context) {
        //初始化BottomSheetDialog
        BottomSheetDialog queueDialog = new BottomSheetDialog(context);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.6);

        //填入布局
        View view = View.inflate(context, R.layout.dialog_playqueue, null);
            /*TextView tv_close = view.findViewById(R.id.tv_close);
            RecyclerView recyclerView = view.findViewById(R.id.rcv_songs);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(queueAdapter);
            queueAdapter.bindToRecyclerView(recyclerView);
            tv_close.setOnClickListener(onClickListener);*/
        queueDialog.setContentView(view);

        //设置Dialog的位置和宽高
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
        Window window = queueDialog.getWindow();
        WindowManager.LayoutParams lp = null;
        if (window != null) {
            lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = height;
            window.setAttributes(lp);
            window.findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
            mBehavior.setPeekHeight(lp.height);
        }
        View bottomSheet = queueDialog.findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.getLayoutParams().height = height;
        }

        //设置Dialog的操作逻辑
        queueDialog.setCanceledOnTouchOutside(true);
       /* if (MusicManager.getInstance().getNowPlayingIndex() >= 0) {
            ((RecyclerView)queueDialog.findViewById(R.id.rcv_songs)).scrollToPosition(MusicManager.getInstance().getNowPlayingIndex());
        }
        queueDialog.show();*/
        return queueDialog;
    }

    public static BottomSheetDialog createMoreDialog(Activity context) {
        //初始化BottomSheetDialog
        BottomSheetDialog moreDialog = new BottomSheetDialog(context);
        //填入布局
        View view = View.inflate(context, R.layout.dialog_more, null);
        moreDialog.setContentView(view);

        Window window = moreDialog.getWindow();
        WindowManager.LayoutParams lp = null;
        if (window != null) {
            lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        }

        //设置Dialog的操作逻辑
        moreDialog.setCanceledOnTouchOutside(true);
        return moreDialog;
    }
}
