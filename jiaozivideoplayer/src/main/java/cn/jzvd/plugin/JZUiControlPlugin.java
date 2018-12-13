package cn.jzvd.plugin;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.dialog.WifiDialog;

public abstract class JZUiControlPlugin extends JZCorePlugin {

    protected static WifiDialog wifiDialog;

    public void setPlayer(JZVideoPlayer player) {
        super.setPlayer(player);
        wifiDialog = new WifiDialog(player);
    }

    protected boolean withWifiDialog() {
        return true;
    }

    public abstract void onNormal(int currentScreen);

    public abstract void onPreparing(int currentScreen);

    public abstract void onPlayingShow(int currentScreen);

    public abstract void onPlayingClear(int currentScreen);

    public abstract void onPauseShow(int currentScreen);

    public abstract void onPauseClear(int currentScreen);

    public abstract void onComplete(int currentScreen);

    public abstract void onError(int currentScreen);

    public void onAutoCompletion() { }

    public void onPreparingChangingUrl() { }
}
