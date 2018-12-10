package cn.jzvd.plugin;

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

    private Map<String, JZUiPlugin> registeredUiPlugins = new HashMap<>();
    private Map<String, JZUiControlPlugin> registeredControlPlugins = new HashMap<>();

    public void registerControlPlugins(JZVideoPlayer player) {
        List<JZUiControlPlugin> controlPlugins =
                Arrays.asList(new TopContainer(player),
                              new BottomContainer(player),
                              new ThumbPlugin(player),
                              new RetryPlugin(player),
                              new StartButtonPlugin(player),
                              new ProgressPlugin(player),
                              new BottomProgressPlugin(player));

        for (JZUiControlPlugin plugin : controlPlugins) {
            if(registeredControlPlugins.get(plugin.getName()) == null) {
                register(plugin);
            }
        }
        assignToContainers(player, getRegisteredControlPlugins());
    }

    public void registerUiPlugins(JZVideoPlayerStandard player) {
        List<JZUiPlugin> uiPlugins =
                Arrays.asList(new BatteryPlugin(player),
                              new TitlePlugin(player),
                              new FullscreenPlugin(player),
                              new TinyBackPlugin(player),
                              new BackButtonPlugin(player),
                              new ClarityPlugin(player));

        for (JZUiPlugin plugin : uiPlugins) {
            if(registeredUiPlugins.get(plugin.getName()) == null) {
                register(plugin);
            }
        }
        assignToContainers(player, getRegisteredUiPlugins());
    }

    public void register(JZUiPlugin... plugins) {
        for (JZUiPlugin plugin : plugins) {
            registeredUiPlugins.put(plugin.getName(), plugin);
        }
    }

    public void register(JZUiControlPlugin... plugins) {
        for (JZUiControlPlugin plugin : plugins) {
            registeredControlPlugins.put(plugin.getName(), plugin);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends JZUiPlugin> T getUiPluginNamed(Class<T> _class) {
        return (T) registeredUiPlugins.get(_class.getSimpleName());
    }
    
    @SuppressWarnings("unchecked")
    public <T extends JZUiControlPlugin> T getControlPluginNamed(Class<T> _class) {
        return (T) registeredControlPlugins.get(_class.getSimpleName());
    }

    public List<JZCorePlugin> getAllRegisteredPlugins() {
        ArrayList<JZCorePlugin> all = new ArrayList<>();
        all.addAll(registeredUiPlugins.values());
        all.addAll(registeredControlPlugins.values());
        return all;
    }

    public List<JZUiPlugin> getRegisteredUiPlugins() {
        ArrayList<JZUiPlugin> plugins = new ArrayList<>(registeredUiPlugins.values());
        Collections.sort(plugins);
        return plugins;
    }

    public List<JZUiControlPlugin> getRegisteredControlPlugins() {
        ArrayList<JZUiControlPlugin> plugins = new ArrayList<>(registeredControlPlugins.values());
        Collections.sort(plugins);
        return plugins;
    }

    private void assignToContainers(JZVideoPlayer player, List<? extends JZCorePlugin> plugins) {
        for (JZCorePlugin plugin : plugins) {
            ContainerLocation containerLocation = plugin.getContainer();
            if(containerLocation != null) {
                Container container = getContainerAt(player, containerLocation);
                container.register(plugin);
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
