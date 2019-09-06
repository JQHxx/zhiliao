package com.dev.rexhuang.zhiliao_core.player;

import android.os.RemoteException;

import com.dev.rexhuang.zhiliao_core.IMusicService;
import com.dev.rexhuang.zhiliao_core.bean.Music;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/8/2
 */
public class IMusicServiceStub extends IMusicService.Stub {

    private final WeakReference<MusicPlayerService> mService;

    public IMusicServiceStub(MusicPlayerService mService) {
        this.mService = new WeakReference<>(mService);
    }

    @Override
    public void nextPlay(Music music) throws RemoteException {
//        mService.get().nextPlay(music);
    }

    @Override
    public void playMusic(Music music) throws RemoteException {
        mService.get().play(music);
    }

    @Override
    public void playPlaylist(List<Music> playlist, int id, String pid) throws RemoteException {

    }

    @Override
    public void play(int id) throws RemoteException {

    }

    @Override
    public void playPause() throws RemoteException {

    }

    @Override
    public void pause() throws RemoteException {

    }

    @Override
    public void stop() throws RemoteException {

    }

    @Override
    public void prev() throws RemoteException {

    }

    @Override
    public void next() throws RemoteException {

    }

    @Override
    public void setLoopMode(int loopmode) throws RemoteException {

    }

    @Override
    public void seekTo(int ms) throws RemoteException {

    }

    @Override
    public int position() throws RemoteException {
        return 0;
    }

    @Override
    public int getDuration() throws RemoteException {
        return 0;
    }

    @Override
    public int getCurrentPosition() throws RemoteException {
        return 0;
    }

    @Override
    public boolean isPlaying() throws RemoteException {
        return false;
    }

    @Override
    public boolean isPause() throws RemoteException {
        return false;
    }

    @Override
    public String getSongName() throws RemoteException {
        return null;
    }

    @Override
    public String getSongArtist() throws RemoteException {
        return null;
    }

    @Override
    public Music getPlayingMusic() throws RemoteException {
        return null;
    }

    @Override
    public List<Music> getPlayList() throws RemoteException {
        return null;
    }

    @Override
    public void removeFromQueue(int position) throws RemoteException {

    }

    @Override
    public void clearQueue() throws RemoteException {

    }

    @Override
    public void showDesktopLyric(boolean show) throws RemoteException {

    }

    @Override
    public int AudioSessionId() throws RemoteException {
        return 0;
    }
}
