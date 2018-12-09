package cn.jzvd.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.R;
import cn.jzvd.component.JZCoreComponent;

public class Container extends RelativeLayout {

    private List<JZCoreComponent> registeredComponents = new ArrayList<>();

    public Container(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.container, this);
    }

    public void register(JZCoreComponent component) {
        registeredComponents.add(component);
    }

    public void render() {
        for (JZCoreComponent component : registeredComponents) {
            PluginLocation location = component.getLocation();
            if (location != null) {
                ViewGroup parent = getParentLocation(location);
                component.init(parent);
            } else {
                component.init(this);
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
