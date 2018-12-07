package cn.jzvd.task;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZVideoPlayer;

import static cn.jzvd.JZVideoPlayer.TAG;

public class ProgressTimerTask extends TimerTask {

    private static ProgressTimerTask task;
    private static Timer UPDATE_PROGRESS_TIMER;

    private final JZVideoPlayer player;

    public static void start(JZVideoPlayer player) {
        finish();
        UPDATE_PROGRESS_TIMER = new Timer();
        task = new ProgressTimerTask(player);
        Log.i(TAG, "start: " + " [" + task.hashCode() + "] ");
        UPDATE_PROGRESS_TIMER.schedule(task, 0, 300);
    }

    private ProgressTimerTask(JZVideoPlayer player) {
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
        if (player.getStateMachine().currentStatePlaying() || player.getStateMachine().currentStatePause()) {
//                Log.v(TAG, "onProgressUpdate " + "[" + this.hashCode() + "] ");
            player.post(new Runnable() {
                @Override
                public void run() {
                    player.setProgressAndText();
                }
            });
        }
    }
}
