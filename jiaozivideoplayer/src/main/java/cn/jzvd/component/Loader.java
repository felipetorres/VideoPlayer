package cn.jzvd.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class Loader {

    private Map<String, JZUIComponent> registeredComponents = new HashMap<>();
    private Map<String, JZUIControlComponent> registeredControlComponents = new HashMap<>();

    public void registerControlComponents(JZVideoPlayer player) {
        registerControl(new TopContainer(player),
                        new ThumbComponent(player),
                        new RetryComponent(player),
                        new StartButtonComponent(player),
                        new ProgressComponent(player));
    }

    public void registerComponents(JZVideoPlayerStandard player) {
        register(new BatteryComponent(player),
                 new TitleComponent(player),
                 new FullscreenComponent(player),
                 new TinyBackButton(player),
                 new BackButtonComponent(player),
                 new ClarityComponent(player));
    }

    public void register(JZUIComponent... components) {
        for (JZUIComponent component : components) {
            registeredComponents.put(component.getName(), component);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T extends JZUIControlComponent> T getControlComponent(Class<T> _class) {
        return (T) registeredControlComponents.get(_class.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public <T extends JZUIComponent> T getUIComponent(Class<T> _class) {
        return (T) registeredComponents.get(_class.getSimpleName());
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
