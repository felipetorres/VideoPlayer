package cn.jzvd.component;

import android.content.Context;
import android.widget.FrameLayout;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayerStandard;

public abstract class JZUIComponent {

    protected JZVideoPlayerStandard player;
    protected Context context;

    public JZUIComponent(JZVideoPlayerStandard player) {
        this.player = player;
        this.init(player);
    }

    protected void init(FrameLayout frameLayout) {
        this.context = frameLayout.getContext();
    }

    abstract public String getName();

    abstract public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects);

    public void onClickUiToggle() { }
}
