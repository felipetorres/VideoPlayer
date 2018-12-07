package cn.jzvd.task;

import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZVideoPlayer;

public class DismissControlViewTimerTask extends TimerTask {

    private static Timer DISMISS_CONTROL_VIEW_TIMER;
    private static DismissControlViewTimerTask task;
    private final JZVideoPlayer player;


    public static void start(JZVideoPlayer player) {
        finish();
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        task = new DismissControlViewTimerTask(player);
        DISMISS_CONTROL_VIEW_TIMER.schedule(task, 2500);
    }

    private DismissControlViewTimerTask(JZVideoPlayer player) {
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
        if (!player.getStateMachine().currentStateNormal()
                && !player.getStateMachine().currentStateError()
                && !player.getStateMachine().currentStateAutoComplete()) {
            player.post(new Runnable() {
                @Override
                public void run() {
                    //TODO FELIPE: ESSES CONTAINERES FICARAO NO PAI
//                    player.bottomContainer.setVisibility(View.INVISIBLE);
//                    player.topContainer.setVisibility(View.INVISIBLE);
                    player.dismissRegisteredComponents();
                }
            });
        }
    }

    public static void oneShot() {
        if(task != null) { task.run(); }
    }
}
