package cn.jzvd.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;

import cn.jzvd.JZUserActionStandard;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;

public class WifiDialog {

    private static boolean WIFI_TIP_DIALOG_SHOWED = false;
    private JZVideoPlayerStandard player;

    public WifiDialog(JZVideoPlayerStandard player) {
        this.player = player;
    }

    public boolean showed() {
        return WIFI_TIP_DIALOG_SHOWED;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(player.getContext());
        builder.setMessage(player.getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(player.getResources().getString(R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                player.onEvent(JZUserActionStandard.ON_CLICK_START_WIFIDIALOG);
                player.startVideo();
                WIFI_TIP_DIALOG_SHOWED = true;
            }
        });
        builder.setNegativeButton(player.getResources().getString(R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                player.onQuitFullscreenOrTinyWindow();
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
}
