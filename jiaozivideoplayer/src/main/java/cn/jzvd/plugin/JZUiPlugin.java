package cn.jzvd.plugin;

import cn.jzvd.JZVideoPlayerStandard;

public abstract class JZUiPlugin extends JZCorePlugin {

    protected JZVideoPlayerStandard player;

    public void setPlayer(JZVideoPlayerStandard player) {
        super.setPlayer(player);
        this.player = player;
    }
}
