package cn.jzvd.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;

import cn.jzvd.JZUserActionStandard;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;

public class WifiDialog extends JZDialog {

    private static boolean WIFI_TIP_DIALOG_SHOWED = false;

    public WifiDialog(JZVideoPlayerStandard player) {
        super(player);
    }

    public boolean showed() {
        return WIFI_TIP_DIALOG_SHOWED;
    }

    @Override
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getPlayer().getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(getPlayer().getResources().getString(R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getPlayer().onEvent(JZUserActionStandard.ON_CLICK_START_WIFIDIALOG);
                getPlayer().startVideo();
                WIFI_TIP_DIALOG_SHOWED = true;
            }
        });
        builder.setNegativeButton(getPlayer().getResources().getString(R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getPlayer().clearFloatScreen();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void dismiss() {
        throw new RuntimeException("WifiDialog is dismissed on its own cancel button.");
    }
}
