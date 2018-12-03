package cn.jzvd.component;

import cn.jzvd.JZVideoPlayerStandard;

abstract class JZComponent {

    private JZVideoPlayerStandard player;

    public JZComponent(JZVideoPlayerStandard player) {
        this.player = player;
    }

    public JZVideoPlayerStandard getPlayer() {
        return player;
    }
}
