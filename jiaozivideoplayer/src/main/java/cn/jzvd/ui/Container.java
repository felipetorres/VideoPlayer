package cn.jzvd.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.R;
import cn.jzvd.plugin.JZCorePlugin;

public class Container extends RelativeLayout {

    private List<JZCorePlugin> pluginsInThisContainer = new ArrayList<>();

    public Container(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.container, this);
    }

    public void register(JZCorePlugin plugin) {
        pluginsInThisContainer.add(plugin);
    }

    public void render() {
        for (JZCorePlugin plugin : pluginsInThisContainer) {
            PluginLocation location = plugin.getLocation();
            if (location != null) {
                ViewGroup parent = getParentLocation(location);
                plugin.init(parent);
            } else {
                plugin.init(this);
            }
        }
    }

    public ViewGroup getParentLocation(PluginLocation location) {
        switch (location) {
            case LEFT: return findViewById(R.id.left_container);
            case CENTER: return findViewById(R.id.middle_container);
            case RIGHT: return findViewById(R.id.right_container);
            default: throw new IllegalArgumentException("Plugin doesn't have a valid location: should be LEFT, CENTER or RIGHT");
        }
    }
}
