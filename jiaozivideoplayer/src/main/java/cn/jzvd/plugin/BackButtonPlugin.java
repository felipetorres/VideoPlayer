package cn.jzvd.plugin;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;
import cn.jzvd.ui.ContainerLocation;
import cn.jzvd.ui.PluginLocation;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;
import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_LIST;
import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_NORMAL;
import static cn.jzvd.JZVideoPlayer.backPress;

public class BackButtonPlugin extends JZUiPlugin {

    private ImageView backButton;

    public BackButtonPlugin(JZVideoPlayerStandard player) {
        super(player);
        super.container = ContainerLocation.TOP;
        super.location = PluginLocation.LEFT;
    }

    @Override
    public String getName() {
        return BackButtonPlugin.class.getSimpleName();
    }

    @Override
    public void init(ViewGroup parent) {
        super.init(parent);

        backButton = parent.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackButtonPlugin.this.onClick();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.plugin_back_button;
    }

    private void onClick() {
        backPress();
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if (player.currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            backButton.setVisibility(View.VISIBLE);
        } else if (player.currentScreen == SCREEN_WINDOW_NORMAL || player.currentScreen == SCREEN_WINDOW_LIST) {
            backButton.setVisibility(View.GONE);
        }
    }
}
