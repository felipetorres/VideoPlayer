package cn.jzvd.dialog;

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZUserActionStandard;
import cn.jzvd.JZVideoPlayerStandard;

public class JZDialogs {

    protected final JZVideoPlayerStandard player;
    protected final List<JZDialog> dialogs;

    public JZDialogs(JZVideoPlayerStandard player, List<JZDialog> dialogs) {
        this.player = player;
        this.dialogs = dialogs == null ? new ArrayList<JZDialog>() : dialogs;
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
