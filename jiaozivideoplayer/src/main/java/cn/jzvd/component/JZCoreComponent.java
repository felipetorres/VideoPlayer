package cn.jzvd.component;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.ui.ContainerLocation;
import cn.jzvd.ui.PluginLocation;

public abstract class JZCoreComponent implements Comparable<JZCoreComponent>{

    protected JZVideoPlayer player;
    protected Context context;
    protected ContainerLocation container = null;
    protected PluginLocation location = null;
    private boolean registered = false;
    protected Integer orderIfSameLocation = 0;

    public JZCoreComponent(JZVideoPlayer player) {
        this.player = player;
        this.context = player.getContext();
    }

    public void init(ViewGroup parent) {
        if(getLayoutId() != 0 && !isRegistered()) {
            View.inflate(parent.getContext(), getLayoutId(), parent);
        }
        this.registered = true;
    }

    public abstract @LayoutRes int getLayoutId();

    public abstract String getName();

    public abstract void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects);

    public void onClickUiToggle() { }

    public void onDismissControlView() { }

    public ContainerLocation getContainer() {
        return container;
    }

    public PluginLocation getLocation() {
        return location;
    }

    private boolean isRegistered() {
        return registered;
    }

    @Override
    public int compareTo(@NonNull JZCoreComponent other) {
        if(container != other.container) {
            return container.compareTo(other.container);
        } else if(location == other.location) {
            return orderIfSameLocation.compareTo(other.orderIfSameLocation);
        }
        return -1;
    }
}
