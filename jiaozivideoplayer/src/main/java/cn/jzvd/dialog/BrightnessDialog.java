package cn.jzvd.dialog;

import android.app.Dialog;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.task.ProgressTimerTask;
import cn.jzvd.R;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;
import static cn.jzvd.JZVideoPlayer.TAG;

public class BrightnessDialog extends JZDialog {

    private Dialog mBrightnessDialog;
    private ProgressBar mDialogBrightnessProgressBar;
    private TextView mDialogBrightnessTextView;
    private int brightnessPercent;
    private float mGestureDownBrightness;
    private boolean mChangeBrightness;
    private float mDownX;
    private float deltaY;
    private float mDownY;

    public BrightnessDialog(JZVideoPlayerStandard player) {
        super(player);
    }

    @Override
    public void onTouch(MotionEvent event, JZDialogs dialogs) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mChangeBrightness = false;
                break;
            case MotionEvent.ACTION_MOVE:
                deltaY = y - mDownY;
                onActionMove(dialogs);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp();
                break;
        }
    }

    @Override
    public void dismiss() {
        if (mBrightnessDialog != null) {
            mBrightnessDialog.dismiss();
        }
    }

    @Override
    public boolean isHidden() {
        return !mChangeBrightness;
    }

    private void onActionUp() {
        dismiss();
    }

    private void onActionMove(JZDialogs dialogs) {
        float absDeltaY = Math.abs(deltaY);

        if (getPlayer().currentScreen == SCREEN_WINDOW_FULLSCREEN && dialogs.hasAllHidden()) {
            ProgressTimerTask.finish();

            if (absDeltaY > THRESHOLD && mDownX < getScreenWidth() * 0.5f) {//左侧改变亮度
                mChangeBrightness = true;
                WindowManager.LayoutParams lp = JZUtils.getWindow(getContext()).getAttributes();
                if (lp.screenBrightness < 0) {
                    try {
                        mGestureDownBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                        Log.i(TAG, "current system brightness: " + mGestureDownBrightness);
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    mGestureDownBrightness = lp.screenBrightness * 255;
                    Log.i(TAG, "current activity brightness: " + mGestureDownBrightness);
                }
            }
        }
        run();
    }


    private void run() {
        if (mChangeBrightness) {
            deltaY = -deltaY;
            int deltaV = (int) (255 * deltaY * 3 / getScreenHeight());
            WindowManager.LayoutParams params = JZUtils.getWindow(getContext()).getAttributes();
            if (((mGestureDownBrightness + deltaV) / 255) >= 1) {//这和声音有区别，必须自己过滤一下负值
                params.screenBrightness = 1;
            } else if (((mGestureDownBrightness + deltaV) / 255) <= 0) {
                params.screenBrightness = 0.01f;
            } else {
                params.screenBrightness = (mGestureDownBrightness + deltaV) / 255;
            }
            JZUtils.getWindow(getContext()).setAttributes(params);
            int brightnessPercent = (int) (mGestureDownBrightness * 100 / 255 + deltaY * 3 * 100 / getScreenHeight());
            this.brightnessPercent = brightnessPercent;
            show();
//        mDownY = y;
        }
    }

    private void show() {
        if (mBrightnessDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jz_dialog_brightness, null);
            mDialogBrightnessTextView = localView.findViewById(R.id.tv_brightness);
            mDialogBrightnessProgressBar = localView.findViewById(R.id.brightness_progressbar);
            mBrightnessDialog = createDialogWithView(localView);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }
        if (brightnessPercent > 100) {
            brightnessPercent = 100;
        } else if (brightnessPercent < 0) {
            brightnessPercent = 0;
        }
        mDialogBrightnessTextView.setText(brightnessPercent + "%");
        mDialogBrightnessProgressBar.setProgress(brightnessPercent);
        onClickUiToggleToClear();
    }
}
