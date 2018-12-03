package cn.jzvd;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static cn.jzvd.JZVideoPlayer.CURRENT_STATE_PAUSE;
import static cn.jzvd.JZVideoPlayer.CURRENT_STATE_PLAYING;
import static cn.jzvd.JZVideoPlayer.TAG;

public class ProgressTimerTask extends TimerTask {

    private static ProgressTimerTask task;
    private static Timer UPDATE_PROGRESS_TIMER;

    private final JZVideoPlayerStandard player;

    public static void start(JZVideoPlayerStandard player) {
        finish();
        UPDATE_PROGRESS_TIMER = new Timer();
        task = new ProgressTimerTask(player);
        Log.i(TAG, "start: " + " [" + task.hashCode() + "] ");
        UPDATE_PROGRESS_TIMER.schedule(task, 0, 300);
    }

    private ProgressTimerTask(JZVideoPlayerStandard player) {
        this.player = player;
    }

    public static void finish() {
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
        }
        if(task != null) {
            task.cancel();
        }
    }

    @Override
    public void run() {
        if (player.currentState == CURRENT_STATE_PLAYING || player.currentState == CURRENT_STATE_PAUSE) {
//                Log.v(TAG, "onProgressUpdate " + "[" + this.hashCode() + "] ");
            player.post(new Runnable() {
                @Override
                public void run() {
                    long position = player.getCurrentPositionWhenPlaying();
                    long duration = player.getDuration();
                    int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
                    player.setProgressAndText(progress, position, duration);
                }
            });
        }
    }
}
