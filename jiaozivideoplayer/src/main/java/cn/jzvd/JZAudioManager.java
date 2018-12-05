package cn.jzvd;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import static cn.jzvd.JZVideoPlayer.releaseAllVideos;

public class JZAudioManager implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "JZAudioManager";

    private static JZAudioManager INSTANCE;
    private final AudioManager mAudioManager;

    public static JZAudioManager getInstance(JZVideoPlayer player) {
        if(INSTANCE == null) {
            INSTANCE = new JZAudioManager(player);
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
                    JZVideoPlayer player = JZVideoPlayerManager.getCurrentJzvd();
                    if (player != null && player.getStateMachine().currentStatePlaying()) {
                        //TODO FELIPE: RESOLVER ISSO AQUI
//                            player.startButton.performClick();
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
