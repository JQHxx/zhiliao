// IMusicService.aidl
package com.dev.rexhuang.zhiliao_core;

// Declare any non-default types here with import statements
import com.dev.rexhuang.zhiliao_core.bean.Music;

interface IMusicService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void nextPlay(in Music music);
    void playMusic(in Music music);
    void playPlaylist(in List<Music> playlist,int id,String pid);
    void play(int id);
    void playPause();
    void pause();
    void stop();
    void prev();
    void next();
    void setLoopMode(int loopmode);
    void seekTo(int ms);
    int position();
    int getDuration();
    int getCurrentPosition();
    boolean isPlaying();
    boolean isPause();
    String getSongName();
    String getSongArtist();
    Music getPlayingMusic();
    List<Music> getPlayList();
    void removeFromQueue(int position);
    void clearQueue();
    void showDesktopLyric(boolean show);
    int AudioSessionId();
}
