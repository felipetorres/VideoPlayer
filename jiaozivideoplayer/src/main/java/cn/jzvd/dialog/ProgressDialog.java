package cn.jzvd.dialog;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;

public class ProgressDialog extends JZDialog {

    private Dialog mProgressDialog;
    private ProgressBar mDialogProgressBar;
    private TextView mDialogSeekTime;
    private TextView mDialogTotalTime;
    private ImageView mDialogIcon;

    private float deltaX;
    private String seekTime;
    private long seekTimePosition;
    private String totalTime;
    private long totalTimeDuration;

    public ProgressDialog(JZVideoPlayerStandard player) {
        super(player);
    }

    public void setProperties(float deltaX, String seekTime, long seekTimePosition, String totalTime, long totalTimeDuration) {
        this.deltaX = deltaX;
        this.seekTime = seekTime;
        this.seekTimePosition = seekTimePosition;
        this.totalTime = totalTime;
        this.totalTimeDuration = totalTimeDuration;
    }

    @Override
    public void show() {
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

    @Override
    public void dismiss() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
