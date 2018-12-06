package cn.jzvd.component;

import android.content.Context;
import android.widget.FrameLayout;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayerStandard;

public abstract class JZCoreComponent {

    protected JZVideoPlayerStandard player;
    protected Context context;

    public JZCoreComponent(JZVideoPlayerStandard player) {
        this.player = player;
        this.context = player.getContext();
        this.init(player);
    }

    protected abstract void init(FrameLayout frameLayout);

    public abstract String getName();

    public abstract void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects);

    public void onClickUiToggle() { }
}
