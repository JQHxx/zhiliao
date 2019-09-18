package com.dev.rexhuang.zhiliao.find;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.find.adapter.SearchAdapter;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoMainFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class FindFragment extends ZhiliaoMainFragment {

    private static final String TAG = FindFragment.class.getSimpleName();
    private String mMediaId;
    private SearchAdapter mSearchAdapter;
    private String[] testMode = new String[]{
            "顺序播放", "随机播放", "列表循环"
    };
    private int modeIndex = 0;

    @BindView(R.id.next_btn)
    AppCompatButton next_btn;
    @BindView(R.id.previous_btn)
    AppCompatButton previous_btn;
    @BindView(R.id.test_mode_btn)
    AppCompatButton test_mode_btn;
    @BindView(R.id.test_btn)
    AppCompatButton test_btn;
    @BindView(R.id.list_search)
    RecyclerView rv_search;

    @OnClick(R.id.test_btn)
    void onClick() {
        test_btn.setText(mMediaId);
    }

    @OnClick(R.id.next_btn)
    void onClickNext() {
    }

    @OnClick(R.id.previous_btn)
    void onClickPre() {
    }

    @OnClick(R.id.test_mode_btn)
    void onClickMode() {
        modeIndex = modeIndex < 2 ? modeIndex + 1 : 0;
        test_mode_btn.setText(testMode[modeIndex]);

    }

    public FindFragment(String mediaId) {
        super();
        mMediaId = mediaId;
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_find;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        mSearchAdapter = new SearchAdapter(R.layout.fragment_media_list_item, new ArrayList<>());
        mSearchAdapter.setOnItemClickListener((adapter, view1, position) -> {

        });
        rv_search.setLayoutManager(new LinearLayoutManager(get_mActivity()));
        rv_search.setAdapter(mSearchAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
