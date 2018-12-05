package cn.jzvd.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.jzvd.JZUserAction;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.task.ProgressTimerTask;
import cn.jzvd.R;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;

public class VolumeDialog extends JZDialog {

    private final AudioManager mAudioManager;
    private Dialog mVolumeDialog;
    private ProgressBar mDialogVolumeProgressBar;
    private TextView mDialogVolumeTextView;
    private ImageView mDialogVolumeImageView;
    private int mGestureDownVolume;

    private float deltaY;
    private int volumePercent;
    private boolean mChangeVolume = false;
    private float mDownX;
    private float mDownY;

    public VolumeDialog(JZVideoPlayerStandard player) {
        super(player);
        this.mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onTouch(MotionEvent event, JZDialogs dialogs) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mChangeVolume = false;
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
        if (mVolumeDialog != null) {
            mVolumeDialog.dismiss();
        }
    }

    @Override
    public boolean isHidden() {
        return !mChangeVolume;
    }

    private void onActionUp() {
        dismiss();
        if (mChangeVolume) {
            getPlayer().onEvent(JZUserAction.ON_TOUCH_SCREEN_SEEK_VOLUME);
        }
    }

    private void onActionMove(JZDialogs dialogs) {
        float absDeltaY = Math.abs(deltaY);

        if (getPlayer().currentScreen == SCREEN_WINDOW_FULLSCREEN && dialogs.hasAllHidden()) {
            ProgressTimerTask.finish();

            if (absDeltaY > THRESHOLD && mDownX > getScreenWidth() * 0.5f) {
                mChangeVolume = true;
                mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            }
        }
        run();
    }

    private void run() {
        if (mChangeVolume) {
            this.deltaY = -deltaY;
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int deltaV = (int) (max * deltaY * 3 / getScreenHeight());
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
            int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / getScreenHeight());
            this.volumePercent = volumePercent;
            show();
        }
    }

    private void show() {
        if (mVolumeDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jz_dialog_volume, null);
            mDialogVolumeImageView = localView.findViewById(R.id.volume_image_tip);
            mDialogVolumeTextView = localView.findViewById(R.id.tv_volume);
            mDialogVolumeProgressBar = localView.findViewById(R.id.volume_progressbar);
            mVolumeDialog = createDialogWithView(localView);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        if (volumePercent <= 0) {
            mDialogVolumeImageView.setBackgroundResource(R.drawable.jz_close_volume);
        } else {
            mDialogVolumeImageView.setBackgroundResource(R.drawable.jz_add_volume);
        }
        if (volumePercent > 100) {
            volumePercent = 100;
        } else if (volumePercent < 0) {
            volumePercent = 0;
        }
        mDialogVolumeTextView.setText(volumePercent + "%");
        mDialogVolumeProgressBar.setProgress(volumePercent);
        onClickUiToggleToClear();
    }
}
