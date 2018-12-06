package cn.jzvd.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.JZVideoPlayerStandard;

public class Loader {

    private Map<String, JZUIComponent> registeredComponents = new HashMap<>();
    private Map<String, JZUIControlComponent> registeredControlComponents = new HashMap<>();

    public Loader(JZVideoPlayerStandard player) {
        register(new BatteryComponent(player),
                new TitleComponent(player),
                new FullscreenComponent(player),
                new TinyBackButton(player),
                new BackButtonComponent(player),
                new ClarityComponent(player));

        registerControl(new ThumbComponent(player));
    }

    public void register(JZUIComponent... components) {
        for (JZUIComponent component : components) {
            registeredComponents.put(component.getName(), component);
        }
    }

    public void registerControl(JZUIControlComponent... components) {
        for (JZUIControlComponent component : components) {
            registeredControlComponents.put(component.getName(), component);
        }
    }

    public List<JZCoreComponent> getAllRegisteredComponents() {
        ArrayList<JZCoreComponent> all = new ArrayList<>();
        all.addAll(registeredComponents.values());
        all.addAll(registeredControlComponents.values());
        return all;
    }

    public List<JZUIComponent> getRegisteredUIComponents() {
        return new ArrayList<>(registeredComponents.values());
    }

    public List<JZUIControlComponent> getRegisteredControlComponents() {
        return new ArrayList<>(registeredControlComponents.values());
    }
}
