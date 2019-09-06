package com.dev.rexhuang.zhiliao_core.player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.dev.rexhuang.zhiliao_core.IMusicService;
import com.dev.rexhuang.zhiliao_core.bean.Music;

import java.util.WeakHashMap;

/**
 * *  created by RexHuang
 * *  on 2019/8/1
 */
public class PlayManager {
    public static IMusicService mService = null;
    private static final WeakHashMap<Context, ServiceBinder> mConnectionMap;

    static {
        mConnectionMap = new WeakHashMap<>();
    }

    public static final ServiceToken bindToService(Context context,
                                                   ServiceConnection callback) {
        Activity realActivity = ((Activity) context).getParent();
        if (realActivity == null) {
            realActivity = (Activity) context;
        }
        ContextWrapper contextWrapper = new ContextWrapper(realActivity);
        contextWrapper.startService(new Intent(contextWrapper, MusicPlayerService.class));
        final ServiceBinder binder = new ServiceBinder(callback,
                contextWrapper.getApplicationContext());
        if (contextWrapper.bindService(new Intent().setClass(contextWrapper,
                MusicPlayerService.class), binder, 0)) {
            mConnectionMap.put(contextWrapper, binder);
            return new ServiceToken(contextWrapper);
        }
        return null;
    }

    public static final class ServiceBinder implements ServiceConnection {
        private final ServiceConnection mCallback;
        private final Context mContext;

        public ServiceBinder(ServiceConnection callback, Context context) {
            this.mCallback = callback;
            this.mContext = context;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IMusicService.Stub.asInterface(service);
            if (mCallback != null) {
                mCallback.onServiceConnected(name, service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mCallback != null) {
                mCallback.onServiceDisconnected(name);
            }
            mService = null;
        }
    }

    public static final class ServiceToken {

        public ContextWrapper mWrappedContext;

        public ServiceToken(final ContextWrapper context) {
            mWrappedContext = context;
        }
    }

    public static void playOnline(Music music) {
        try {
            if (mService != null) {
                Log.e("rex", "playOnline");
                mService.playMusic(music);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
