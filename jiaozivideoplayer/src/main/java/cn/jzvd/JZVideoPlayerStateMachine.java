package cn.jzvd;

import android.util.Log;

public class JZVideoPlayerStateMachine {

    private static final int CURRENT_STATE_NORMAL = 0;
    private static final int CURRENT_STATE_PREPARING = 1;
    private static final int CURRENT_STATE_PREPARING_CHANGING_URL = 2;
    private static final int CURRENT_STATE_PLAYING = 3;
    private static final int CURRENT_STATE_PAUSE = 5;
    private static final int CURRENT_STATE_AUTO_COMPLETE = 6;
    private static final int CURRENT_STATE_ERROR = 7;

    private static final String TAG = JZVideoPlayerStateMachine.class.getSimpleName();

    private JZVideoPlayer player;
    private int currentState;
    private int urlMapIndex;
    private long seekToInAdvance;

    public JZVideoPlayerStateMachine(JZVideoPlayer player) {
        this.player = player;
    }

    public void copyStateFrom(JZVideoPlayer player) {
        JZVideoPlayerStateMachine machine = player.getStateMachine();
        this.urlMapIndex = machine.urlMapIndex;
        this.seekToInAdvance = machine.seekToInAdvance;
        setState(machine.currentState);
    }

    private void setState(int state) {
        this.currentState = state;

        switch (state) {
            case CURRENT_STATE_NORMAL:
                Log.i(TAG, "onStateNormal " + " [" + player.hashCode() + "] ");
                player.onStateNormal();
                break;
            case CURRENT_STATE_PREPARING:
                Log.i(TAG, "onStatePreparing " + " [" + player.hashCode() + "] ");
                player.onStatePreparing();
                break;
            case CURRENT_STATE_PREPARING_CHANGING_URL:
                Log.i(TAG, "onPreparingChangingUrl " + " [" + player.hashCode() + "] ");
                player.onStatePreparingChangingUrl(urlMapIndex);
                break;
            case CURRENT_STATE_PLAYING:
                Log.i(TAG, "onStatePlaying " + " [" + player.hashCode() + "] ");
                player.onStatePlaying();
                break;
            case CURRENT_STATE_PAUSE:
                Log.i(TAG, "onStatePause " + " [" + player.hashCode() + "] ");
                player.onStatePause();
                break;
            case CURRENT_STATE_ERROR:
                Log.i(TAG, "onStateError " + " [" + player.hashCode() + "] ");
                player.onStateError();
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                Log.i(TAG, "onStateAutoComplete " + " [" + player.hashCode() + "] ");
                player.onStateAutoComplete();
                break;
        }
    }

    public boolean currentStateNormal() {
        return currentState == CURRENT_STATE_NORMAL;
    }

    public boolean currentStatePreparing() {
        return currentState == CURRENT_STATE_PREPARING;
    }

    public boolean currentStatePreparingChangingUrl() {
        return currentState == CURRENT_STATE_PREPARING_CHANGING_URL;
    }

    public boolean currentStatePlaying() {
        return currentState == CURRENT_STATE_PLAYING;
    }

    public boolean currentStatePause() {
        return currentState == CURRENT_STATE_PAUSE;
    }

    public boolean currentStateError() {
        return currentState == CURRENT_STATE_ERROR;
    }

    public boolean currentStateAutoComplete() {
        return currentState == CURRENT_STATE_AUTO_COMPLETE;
    }

    public void setNormal() {
        setState(CURRENT_STATE_NORMAL);
    }

    public void setPreparing() {
        setState(CURRENT_STATE_PREPARING);
    }

    public void setPreparingChangingUrl(int index, long currentPositionWhenPlaying) {
        this.urlMapIndex = index;
        this.seekToInAdvance = currentPositionWhenPlaying;
        setState(CURRENT_STATE_PREPARING_CHANGING_URL);
    }

    public void setPlaying() {
        setState(CURRENT_STATE_PLAYING);
    }

    public void setPause() {
        setState(CURRENT_STATE_PAUSE);
    }

    public void setError() {
        setState(CURRENT_STATE_ERROR);
    }

    public void setAutoComplete() {
        setState(CURRENT_STATE_AUTO_COMPLETE);
    }

    public void setPrepared(JZDataSource dataSource) {
        Log.i(TAG, "onPrepared " + " [" + player.hashCode() + "] ");
        if (seekToInAdvance != 0) {
            JZMediaManager.seekTo(seekToInAdvance);
            seekToInAdvance = 0;
        } else {
            long position = JZUtils.getSavedProgress(player.getContext(), dataSource.getCurrentPath(this.urlMapIndex));
            if (position != 0) {
                JZMediaManager.seekTo(position);
            }
        }
        setPlaying();
    }
}
