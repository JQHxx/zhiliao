package com.dev.rexhuang.zhiliao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dev.rexhuang.zhiliao_core.utils.Utils;

/**
 * *  created by RexHuang
 * *  on 2019/9/20
 */
public class WarningDialog extends Dialog {

    private Button btn_cancel;
    private Button btn_firm;
    private TextView tv_message;

    public WarningDialog(Context context) {
        super(context);
        init(context);
    }

    protected WarningDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    protected WarningDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void init(Context context) {
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//设置dialog显示居中
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_warning, null);
        setContentView(view);
//        ViewGroup.LayoutParams viewLayoutParams = view.getLayoutParams();
//        viewLayoutParams.width = Utils.dp2px(400);
//        view.setLayoutParams(viewLayoutParams);
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        Point size = new Point();
        display.getSize(size);
        lp.width = size.x * 5 / 6;// 设置dialog宽度为屏幕的4/5
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        btn_cancel = findViewById(R.id.bt_cancel);
        btn_firm = findViewById(R.id.bt_firm);
        tv_message = findViewById(R.id.tv_message);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCanceledOnTouchOutside(true);
    }

    public void setOnFirmListener(OnFirmListener listener) {
        btn_firm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFirm();
                }
                dismiss();
            }
        });
    }

    public void setMessage(String message) {
        if (tv_message != null) {
            tv_message.setText(message);
        }
    }

    public interface OnFirmListener {
        void onFirm();
    }
}
