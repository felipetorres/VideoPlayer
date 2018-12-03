package cn.jzvd;

import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import static cn.jzvd.JZVideoPlayer.CURRENT_STATE_AUTO_COMPLETE;
import static cn.jzvd.JZVideoPlayer.CURRENT_STATE_ERROR;
import static cn.jzvd.JZVideoPlayer.CURRENT_STATE_NORMAL;
import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_TINY;

public class DismissControlViewTimerTask extends TimerTask {

    private static Timer DISMISS_CONTROL_VIEW_TIMER;
    private static DismissControlViewTimerTask task;
    private final JZVideoPlayerStandard player;


    public static void start(JZVideoPlayerStandard player) {
        finish();
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        task = new DismissControlViewTimerTask(player);
        DISMISS_CONTROL_VIEW_TIMER.schedule(task, 2500);
    }

    private DismissControlViewTimerTask(JZVideoPlayerStandard player) {
        this.player = player;
    }

    public static void finish() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER.cancel();
        }
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public void run() {
        if (player.currentState != CURRENT_STATE_NORMAL
                && player.currentState != CURRENT_STATE_ERROR
                && player.currentState != CURRENT_STATE_AUTO_COMPLETE) {
            player.post(new Runnable() {
                @Override
                public void run() {
                    player.bottomContainer.setVisibility(View.INVISIBLE);
                    player.topContainer.setVisibility(View.INVISIBLE);
                    player.startButton.setVisibility(View.INVISIBLE);
                    if (player.clarityPopWindow != null) {
                        player.clarityPopWindow.dismiss();
                    }
                    if (player.currentScreen != SCREEN_WINDOW_TINY) {
                        player.bottomProgressBar.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public static void oneShot() {
        if(task != null) { task.run(); }
    }
}
