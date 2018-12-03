package cn.jzvd.dialog;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;

public class BrightnessDialog extends JZDialog {

    private Dialog mBrightnessDialog;
    private ProgressBar mDialogBrightnessProgressBar;
    private TextView mDialogBrightnessTextView;
    private int brightnessPercent;

    public BrightnessDialog(JZVideoPlayerStandard player) {
        super(player);
    }

    public void setBrightness(int brightness) {
        this.brightnessPercent = brightness;
    }

    @Override
    public void show() {
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

    @Override
    public void dismiss() {
        if (mBrightnessDialog != null) {
            mBrightnessDialog.dismiss();
        }
    }
}
