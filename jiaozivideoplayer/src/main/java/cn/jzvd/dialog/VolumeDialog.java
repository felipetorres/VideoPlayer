package cn.jzvd.dialog;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;

public class VolumeDialog extends JZDialog {

    private Dialog mVolumeDialog;
    private ProgressBar mDialogVolumeProgressBar;
    private TextView mDialogVolumeTextView;
    private ImageView mDialogVolumeImageView;

    private float deltaY;
    private int volumePercent;

    public void setDeltaY(float deltaY) {
        this.deltaY = deltaY;
    }

    public void setVolumePercent(int volumePercent) {
        this.volumePercent = volumePercent;
    }

    public VolumeDialog(JZVideoPlayerStandard player) {
        super(player);
    }

    @Override
    public void show() {
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

    @Override
    public void dismiss() {
        if (mVolumeDialog != null) {
            mVolumeDialog.dismiss();
        }
    }
}
