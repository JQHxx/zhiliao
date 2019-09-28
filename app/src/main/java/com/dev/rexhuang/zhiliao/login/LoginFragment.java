package com.dev.rexhuang.zhiliao.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.rexhuang.zhiliao.MainActivity;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.timer.BaseTimerTask;
import com.dev.rexhuang.zhiliao.timer.ITimerListener;
import com.dev.rexhuang.zhiliao_core.api.zhiliao.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.api.zhiliao.ZhiliaoApiHelper;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.entity.User;
import com.dev.rexhuang.zhiliao_core.entity.ZhiliaoEntity;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dyhdyh.widget.loadingbar2.LoadingBar;

import org.json.JSONException;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Timer;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * *  created by RexHuang
 * *  on 2019/9/20
 */
public class LoginFragment extends ZhiliaoFragment implements ITimerListener {

    private String currentUser;
    private String currentVerify;

    @BindView(R.id.et_user)
    TextView et_user;

    @BindView(R.id.et_verify)
    TextView et_verify;

    @BindView(R.id.bt_verify)
    Button bt_verify;

    @BindView(R.id.bt_login)
    Button bt_login;

    @OnClick(R.id.bt_verify)
    void onClickVerify() {
        if (et_user != null) {
            currentUser = et_user.getText().toString();
            if (!TextUtils.isEmpty(currentUser) && (UserHelper.isEmail(currentUser) || UserHelper.isUsername(currentUser) || UserHelper.isMobile(currentUser))) {
                ZhiliaoApi.getVerify(currentUser, "signin", verifyRequest, (ISuccess<ZhiliaoEntity>) response -> {
                    if (ZhiliaoApiHelper.isSuccess(response)) {
                        Toast.makeText(getActivity(), "验证码已发送，请及时查看！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, null, null);
            } else {
                Toast.makeText(getActivity(), "请检查用户名格式是否正确!?", Toast.LENGTH_LONG).show();
            }
        }
    }

    @OnClick(R.id.bt_login)
    void onClickLogin() {
        if (et_user != null && et_verify != null) {
            currentUser = et_user.getText().toString();
            currentVerify = et_verify.getText().toString();
            if (!TextUtils.isEmpty(currentUser) && !TextUtils.isEmpty(currentVerify)) {
                if (UserHelper.isEmail(currentUser) || UserHelper.isUsername(currentUser) || UserHelper.isMobile(currentUser)) {
                    try {
                        ZhiliaoApi.signIn(currentUser, currentVerify, loginRequest, (ISuccess<User>) user -> {
                            if (ZhiliaoApiHelper.isSuccess(user)) {
                                if (UserManager.getInstance().putUser(user)) {
                                    Toast.makeText(getActivity(), "登录成功!", Toast.LENGTH_SHORT).show();
                                    startTomain();
                                }
                            } else {
                                Toast.makeText(getActivity(), user.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }, null, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "请检查用户名格式是否正确!?", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "用户名和验证码都不能为空!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private final int DEFAULT_COUNT = 60;
    private int currentCount = DEFAULT_COUNT;
    private Timer mTimer;
    private IRequest verifyRequest = new IRequest() {
        @Override
        public void onRequestStart() {
            mTimer = new Timer();
            BaseTimerTask timerTask = new BaseTimerTask(LoginFragment.this);
            mTimer.schedule(timerTask, 0, 1000);
        }

        @Override
        public void onRequestEnd() {

        }
    };

    private IRequest loginRequest = new IRequest() {
        @Override
        public void onRequestStart() {
            LoadingBar.dialog(getActivity()).setFactoryFromResource(R.layout.dialog_loading).show();
        }

        @Override
        public void onRequestEnd() {
            LoadingBar.dialog(getActivity()).cancel();
        }
    };

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_login;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {

    }

    @Override
    public void onTimer() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (bt_verify != null) {
                bt_verify.setClickable(false);
                bt_verify.setText(MessageFormat.format("{0}s", currentCount));
                currentCount--;
                if (currentCount < 0) {
                    cancelTimer();
                    resetVerify();
                }
            }
        });

    }

    private void resetVerify() {
        if (bt_verify != null) {
            bt_verify.setClickable(true);
            bt_verify.setText(getActivity().getString(R.string.get_verify));
            currentCount = DEFAULT_COUNT;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelTimer();
    }

    private void startTomain() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
        getActivity().finish();
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
