package cn.jzvd.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.JZVideoPlayerStandard;

public class Loader {

    private Map<String, JZUIComponent> registeredComponents = new HashMap<>();

    public Loader(JZVideoPlayerStandard player) {
        register(new BatteryComponent(player),
                 new TitleComponent(player),
                 new FullscreenComponent(player));
    }

    public void register(JZUIComponent... components) {
        for (JZUIComponent component : components) {
            registeredComponents.put(component.getName(), component);
        }
    }

    public void remove(Class<? extends JZUIComponent> component) {
        registeredComponents.remove(component.getSimpleName());
    }

    public List<JZUIComponent> getRegisteredComponents() {
        return new ArrayList<>(registeredComponents.values());
    }
}
