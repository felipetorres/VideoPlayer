package cn.jzvd;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import cn.jzvd.plugin.StartButtonPlugin;

import static cn.jzvd.JZVideoPlayer.releaseAllVideos;

public class JZAudioManager implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "JZAudioManager";

    private static JZAudioManager INSTANCE;
    private final AudioManager mAudioManager;
    private JZVideoPlayerStandard player;

    public static JZAudioManager getInstance(JZVideoPlayerStandard player) {
        if(INSTANCE == null) {
            INSTANCE = new JZAudioManager(player);
            INSTANCE.player = player;
        }
        return INSTANCE;
    }

    private JZAudioManager(JZVideoPlayer player) {
        this.mAudioManager = (AudioManager) player.getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    public void requestAudioFocus() {
        this.mAudioManager.requestAudioFocus(INSTANCE, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    public void abandonAudioFocus() {
        this.mAudioManager.abandonAudioFocus(INSTANCE);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                releaseAllVideos();
                Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                try {
                    if (player != null && player.getStateMachine().currentStatePlaying()) {
                        StartButtonPlugin startButton = player.loader.getControlPlugin(StartButtonPlugin.class);
                        startButton.performClick();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                break;
        }
    }
}
