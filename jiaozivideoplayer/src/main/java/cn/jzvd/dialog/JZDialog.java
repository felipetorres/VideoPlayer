package cn.jzvd.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.JZVideoPlayerStateMachine;
import cn.jzvd.R;

public abstract class JZDialog {

    static final int THRESHOLD = 80;

    private final int mScreenWidth;
    private final int mScreenHeight;
    private JZVideoPlayerStandard player;

    protected JZDialog(JZVideoPlayerStandard player) {
        this.player = player;
        this.mScreenWidth = player.getResources().getDisplayMetrics().widthPixels;
        this.mScreenHeight = player.getResources().getDisplayMetrics().heightPixels;
    }

    Context getContext() {
        return player.getContext();
    }

    protected JZVideoPlayerStandard getPlayer() {
        return player;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public abstract void onTouch(MotionEvent event, JZDialogs dialogs);

    public abstract void dismiss();

    public abstract boolean isHidden();

    Dialog createDialogWithView(View localView) {
        Dialog dialog = new Dialog(getContext(), R.style.jz_style_dialog_progress);
        dialog.setContentView(localView);
        Window window = dialog.getWindow();
        window.addFlags(Window.FEATURE_ACTION_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        window.setLayout(-2, -2);
        WindowManager.LayoutParams localLayoutParams = window.getAttributes();
        localLayoutParams.gravity = Gravity.CENTER;
        window.setAttributes(localLayoutParams);
        return dialog;
    }

    void onClickUiToggleToClear() {
        int bottomContainerVisibility = player.getBottomContainerVisibility();
        JZVideoPlayerStateMachine stateMachine = player.getStateMachine();

        if (bottomContainerVisibility == View.VISIBLE) {
            if (stateMachine.currentStatePreparing()) {
                player.changeUiToPreparing();
            } else if (stateMachine.currentStatePlaying()) {
                player.changeUiToPlayingClear();
            } else if (stateMachine.currentStatePause()) {
                player.changeUiToPauseClear();
            } else if (stateMachine.currentStateAutoComplete()) {
                player.changeUiToComplete();
            }
        }
    }
}
