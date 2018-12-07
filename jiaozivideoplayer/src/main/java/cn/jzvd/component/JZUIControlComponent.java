package cn.jzvd.component;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.dialog.WifiDialog;

public abstract class JZUIControlComponent extends JZCoreComponent {

    protected static WifiDialog wifiDialog;

    public JZUIControlComponent(JZVideoPlayer player) {
        super(player);
        wifiDialog = new WifiDialog(player);
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
