package cn.jzvd.dialog;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUserAction;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.task.ProgressTimerTask;
import cn.jzvd.R;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;

public class ProgressDialog extends JZDialog {

    private Dialog mProgressDialog;
    private ProgressBar mDialogProgressBar;
    private TextView mDialogSeekTime;
    private TextView mDialogTotalTime;
    private ImageView mDialogIcon;

    private String seekTime;
    private long seekTimePosition;
    private String totalTime;
    private long totalTimeDuration;
    private long mGestureDownPosition;
    private boolean mChangePosition = false;
    private long mSeekTimePosition;
    private float mDownX;
    private float deltaX;

    public ProgressDialog(JZVideoPlayerStandard player) {
        super(player);
    }

    private void setProperties(float deltaX, String seekTime, long seekTimePosition, String totalTime, long totalTimeDuration) {
        this.deltaX = deltaX;
        this.seekTime = seekTime;
        this.seekTimePosition = seekTimePosition;
        this.totalTime = totalTime;
        this.totalTimeDuration = totalTimeDuration;
    }

    @Override
    public void onTouch(MotionEvent event, JZDialogs dialogs) {
        float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mChangePosition = false;
                break;
            case MotionEvent.ACTION_MOVE:
                deltaX = x - mDownX;
                onActionMove(dialogs);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp();
                break;
        }
    }

    @Override
    public void dismiss() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean isHidden() {
        return !mChangePosition;
    }

    private void onActionUp() {
        dismiss();
        if (mChangePosition) {
            getPlayer().onEvent(JZUserAction.ON_TOUCH_SCREEN_SEEK_POSITION);
            JZMediaManager.seekTo(mSeekTimePosition);
            long duration = getPlayer().getDuration();
            int progress = (int) (mSeekTimePosition * 100 / (duration == 0 ? 1 : duration));
            getPlayer().setProgress(progress);
        }
    }

    private void onActionMove(JZDialogs dialogs) {
        float absDeltaX = Math.abs(deltaX);

        if (getPlayer().currentScreen == SCREEN_WINDOW_FULLSCREEN && dialogs.hasAllHidden()) {
            ProgressTimerTask.finish();

            if (absDeltaX >= THRESHOLD) {
                if (!getPlayer().getStateMachine().currentStateError()) {
                    mChangePosition = true;
                    mGestureDownPosition = getPlayer().getCurrentPositionWhenPlaying();
                }
            }
        }
        run();
    }

    private void run() {
        if (mChangePosition) {
            long totalTimeDuration = getPlayer().getDuration();
            mSeekTimePosition = (int) (mGestureDownPosition + deltaX * totalTimeDuration / getScreenWidth());

            if (mSeekTimePosition > totalTimeDuration)
                mSeekTimePosition = totalTimeDuration;
            String seekTime = JZUtils.stringForTime(mSeekTimePosition);
            String totalTime = JZUtils.stringForTime(totalTimeDuration);

            setProperties(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);
            show();
        }
    }

    private void show() {
        if (mProgressDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jz_dialog_progress, null);
            mDialogProgressBar = localView.findViewById(R.id.duration_progressbar);
            mDialogSeekTime = localView.findViewById(R.id.tv_current);
            mDialogTotalTime = localView.findViewById(R.id.tv_duration);
            mDialogIcon = localView.findViewById(R.id.duration_image_tip);
            mProgressDialog = createDialogWithView(localView);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        mDialogSeekTime.setText(seekTime);
        mDialogTotalTime.setText(" / " + totalTime);
        mDialogProgressBar.setProgress(totalTimeDuration <= 0 ? 0 : (int) (seekTimePosition * 100 / totalTimeDuration));
        if (deltaX > 0) {
            mDialogIcon.setBackgroundResource(R.drawable.jz_forward_icon);
        } else {
            mDialogIcon.setBackgroundResource(R.drawable.jz_backward_icon);
        }
        onClickUiToggleToClear();
    }
}
