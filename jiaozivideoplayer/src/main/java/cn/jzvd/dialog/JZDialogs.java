package cn.jzvd.dialog;

import android.view.MotionEvent;

import java.util.Arrays;
import java.util.List;

import cn.jzvd.JZUserActionStandard;
import cn.jzvd.JZVideoPlayerStandard;

public class JZDialogs {

    private final JZVideoPlayerStandard player;
    private final List<JZDialog> dialogs;

    public JZDialogs(JZVideoPlayerStandard player) {
        this.player = player;
        this.dialogs = Arrays.asList(new VolumeDialog(player),
                                     new ProgressDialog(player),
                                     new BrightnessDialog(player));
    }

    public boolean hasAllHidden() {
        for (JZDialog dialog : this.dialogs) {
            if(!dialog.isHidden()) return false;
        }
        return true;
    }

    public void onTouch(MotionEvent event) {
        for (JZDialog dialog : dialogs) {
            dialog.onTouch(event, this);
        }

        if(event.getAction() == MotionEvent.ACTION_UP) {
            if(hasAllHidden()) {
                player.onEvent(JZUserActionStandard.ON_CLICK_BLANK);
                player.onClickUiToggle();
            }
        }
    }

    public void dismiss() {
        for (JZDialog dialog : dialogs) {
            dialog.dismiss();
        }
    }
}
