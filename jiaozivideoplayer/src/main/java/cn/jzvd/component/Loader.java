package cn.jzvd.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;
import cn.jzvd.ui.Container;
import cn.jzvd.ui.ContainerLocation;

import static cn.jzvd.ui.ContainerLocation.BOTTOM;
import static cn.jzvd.ui.ContainerLocation.CENTER;
import static cn.jzvd.ui.ContainerLocation.NONE;
import static cn.jzvd.ui.ContainerLocation.TOP;

public class Loader {

    private Map<String, JZUIComponent> registeredComponents = new HashMap<>();
    private Map<String, JZUIControlComponent> registeredControlComponents = new HashMap<>();

    public void registerControlComponents(JZVideoPlayer player) {
        List<JZUIControlComponent> controlComponents =
                Arrays.asList(new TopContainer(player),
                              new BottomContainer(player),
                              new ThumbComponent(player),
                              new RetryComponent(player),
                              new StartButtonComponent(player),
                              new ProgressComponent(player),
                              new BottomProgressComponent(player));

        for (JZUIControlComponent component : controlComponents) {
            if(registeredControlComponents.get(component.getName()) == null) {
                registerControl(component);
            }
        }
        assignToContainers(player, getRegisteredControlComponents());
    }

    public void registerComponents(JZVideoPlayerStandard player) {
        register(new BatteryComponent(player),
                 new TitleComponent(player),
                 new FullscreenComponent(player),
                 new TinyBackButton(player),
                 new BackButtonComponent(player),
                 new ClarityComponent(player));

        assignToContainers(player, getRegisteredUIComponents());
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
        ArrayList<JZUIComponent> components = new ArrayList<>(registeredComponents.values());
        Collections.sort(components);
        return components;
    }

    public List<JZUIControlComponent> getRegisteredControlComponents() {
        ArrayList<JZUIControlComponent> components = new ArrayList<>(registeredControlComponents.values());
        Collections.sort(components);
        return components;
    }

    private void assignToContainers(JZVideoPlayer player, List<? extends JZCoreComponent> components) {
        for (JZCoreComponent component : components) {
            ContainerLocation containerLocation = component.getContainer();
            if(containerLocation != null) {
                Container container = getContainerAt(player, containerLocation);
                container.register(component);
            }
        }
        render(player);
    }

    private void render(JZVideoPlayer player) {
        getContainerAt(player, NONE).render();
        getContainerAt(player, TOP).render();
        getContainerAt(player, CENTER).render();
        getContainerAt(player, BOTTOM).render();
    }

    private Container getContainerAt(JZVideoPlayer player, ContainerLocation location) {
        switch (location) {
            case NONE: return player.findViewById(R.id.none);
            case TOP: return player.findViewById(R.id.top);
            case CENTER: return player.findViewById(R.id.center);
            case BOTTOM: return player.findViewById(R.id.bottom);
            default: throw new IllegalArgumentException("Container doesn't have a valid location: should be TOP, CENTER or BOTTOM");
        }
    }
}
