package cn.jzvd.component;

import cn.jzvd.JZVideoPlayerStandard;

public abstract class JZUIControlComponent extends JZCoreComponent {

    public JZUIControlComponent(JZVideoPlayerStandard player) {
        super(player);
    }

    public abstract void onNormal(int currentScreen);

    public abstract void onPreparing(int currentScreen);

    public abstract void onPlayingShow(int currentScreen);

    public abstract void onPlayingClear(int currentScreen);

    public abstract void onPauseShow(int currentScreen);

    public abstract void onPauseClear(int currentScreen);

    public abstract void onComplete(int currentScreen);

    public abstract void onError(int currentScreen);

    public void onAutoCompletion(int currentScreen) { }
}
