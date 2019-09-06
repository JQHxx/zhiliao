package com.dev.rexhuang.zhiliao_core.find;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.R2;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoMainFragment;
import com.dev.rexhuang.zhiliao_core.callback.SwitchMediaFragmentListener;
import com.dev.rexhuang.zhiliao_core.find.adapter.SearchAdapter;
import com.dev.rexhuang.zhiliao_core.player2.PlayState;
import com.dev.rexhuang.zhiliao_core.player2.playback.QueueManager;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

//import com.dev.rexhuang.zhiliao_core.R;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class FindFragment extends ZhiliaoMainFragment {

    private static final String TAG = FindFragment.class.getSimpleName();
    private String mMediaId;
    private SearchAdapter mSearchAdapter;
    private SwitchMediaFragmentListener mMediaFragmentListener;
    private String[] tests = new String[]{
            "陈奕迅", "林俊杰", "周杰伦"
    };
    private String[] testMode = new String[]{
            "顺序播放", "随机播放", "列表循环"
    };
    private int index = 0;
    private int modeIndex = 0;

    @BindView(R2.id.next_btn)
    AppCompatButton next_btn;
    @BindView(R2.id.previous_btn)
    AppCompatButton previous_btn;
    @BindView(R2.id.test_mode_btn)
    AppCompatButton test_mode_btn;
    @BindView(R2.id.test_btn)
    AppCompatButton test_btn;
    @BindView(R2.id.list_search)
    RecyclerView rv_search;

    @OnClick(R2.id.test_btn)
    void onClick() {
        subscribeData();
        test_btn.setText(mMediaId);
    }

    @OnClick(R2.id.next_btn)
    void onClickNext() {
        QueueManager.getInstance().next();
    }

    @OnClick(R2.id.previous_btn)
    void onClickPre() {
        QueueManager.getInstance().previous();
    }

    @OnClick(R2.id.test_mode_btn)
    void onClickMode() {
        modeIndex = modeIndex < 2 ? modeIndex + 1 : 0;
        test_mode_btn.setText(testMode[modeIndex]);
        QueueManager.getInstance().setPlayMode(modeIndex);

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
        mMediaFragmentListener = (SwitchMediaFragmentListener) getParentFragment();
        mSearchAdapter = new SearchAdapter(R.layout.fragment_media_list_item, new ArrayList<>());
        mSearchAdapter.setOnItemClickListener((adapter, view1, position) -> {
            MediaBrowserCompat.MediaItem mediaItem = (MediaBrowserCompat.MediaItem) adapter.getItem(position);
            if (!PlayState.isPlaying()) {
                mMediaFragmentListener.onMediaItemSelected(mediaItem);
            } else {
                QueueManager.getInstance().addToPlayingQueue(mediaItem);
            }
        });
        rv_search.setLayoutManager(new LinearLayoutManager(get_mActivity()));
        rv_search.setAdapter(mSearchAdapter);
//        MediaBrowserCompat mediaBrowser = mMediaFragmentListener.getMediaBrowser();
//        if (mediaBrowser.isConnected()) {
//            onConnected();
//        }
    }

    private void onConnected() {
        if (isDetached()) {
            return;
        }
        mMediaId = getMediaId();
        if (mMediaId == null) {
            mMediaId = mMediaFragmentListener.getMediaBrowser().getRoot();
        }

        // Unsubscribing before subscribing is required if this mediaId already has a subscriber
        // on this MediaBrowser instance. Subscribing to an already subscribed mediaId will replace
        // the callback, but won't trigger the initial callback.onChildrenLoaded.
        //
        // This is temporary: A bug is being fixed that will make subscribe
        // consistently call onChildrenLoaded initially, no matter if it is replacing an existing
        // subscriber or not. Currently this only happens if the mediaID has no previous
        // subscriber or if the media content changes on the service side, so we need to
        // unsubscribe first.
        Logger.d("onConnectd subscribe");
        mMediaFragmentListener.getMediaBrowser().unsubscribe(mMediaId);
        mMediaFragmentListener.getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);

        // Add MediaController callback so we can redraw the list when metadata changes:
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(Objects.requireNonNull(getActivity()));
        if (controller != null) {
            controller.registerCallback(mMediaControllerCallback);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
//        MediaBrowserCompat mediaBrowser = mMediaFragmentListener.getMediaBrowser();
//        if (mediaBrowser != null && mediaBrowser.isConnected() && mMediaId != null) {
//            mediaBrowser.unsubscribe(mMediaId);
//        }
//        MediaControllerCompat controller = MediaControllerCompat.getMediaController(Objects.requireNonNull(getActivity()));
//        if (controller != null) {
//            controller.unregisterCallback(mMediaControllerCallback);
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mMediaFragmentListener = null;
    }

    private String getMediaId() {
        return mMediaId;
    }

    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
//                    if (metadata == null) {
//                        return;
//                    }
                }

                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    Logger.t(TAG).d("onPlaybackStateChanged" + state.getState());
                    mMediaFragmentListener.onPlaybackStateChanged(state);
//                    switch (state.getState()) {
//                        case PlaybackStateCompat.STATE_PLAYING:
//
//                            break;
//                        case 2:
//                    }
                }
            };

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    try {
                        Toast.makeText(get_mActivity(), "fragment onChildrenLoaded, parentId=" + parentId +
                                "  count=" + children.size(), Toast.LENGTH_SHORT).show();
                        mSearchAdapter.getData().clear();
                        for (MediaBrowserCompat.MediaItem item : children) {
//                            mSearchAdapter.addData(item);
                        }
                        mSearchAdapter.notifyDataSetChanged();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    Toast.makeText(getActivity(), "onError", Toast.LENGTH_LONG).show();
                }
            };

    private void subscribeData() {
        Logger.t(TAG).d("onClick subscribe");
        mMediaId = tests[index];
        index++;
        mMediaFragmentListener.getMediaBrowser().unsubscribe(mMediaId);
        mMediaFragmentListener.getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);

    }


}
