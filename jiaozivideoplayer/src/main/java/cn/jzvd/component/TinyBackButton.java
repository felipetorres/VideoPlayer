package cn.jzvd.component;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;
import cn.jzvd.ui.ContainerLocation;
import cn.jzvd.ui.PluginLocation;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_TINY;
import static cn.jzvd.JZVideoPlayer.backPress;
import static cn.jzvd.JZVideoPlayer.quitFullscreenOrTinyWindow;

public class TinyBackButton extends JZUIComponent {

    private ImageView tinyBackImageView;

    public TinyBackButton(JZVideoPlayerStandard player) {
        super(player);
        super.container = ContainerLocation.NONE;
        super.location = PluginLocation.LEFT;
    }

    @Override
    public String getName() {
        return TinyBackButton.class.getSimpleName();
    }

    @Override
    public void init(ViewGroup parent) {
        super.init(parent);
        tinyBackImageView = parent.findViewById(R.id.back_tiny);
        tinyBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TinyBackButton.this.onClick();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.plugin_back_tiny;
    }

    private void onClick() {
        if (JZVideoPlayerManager.getFirstFloor().currentScreen == JZVideoPlayer.SCREEN_WINDOW_LIST) {
            quitFullscreenOrTinyWindow();
        } else {
            backPress();
        }
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if (player.currentScreen == SCREEN_WINDOW_TINY) {
            tinyBackImageView.setVisibility(View.VISIBLE);
        } else {
            tinyBackImageView.setVisibility(View.GONE);
        }
    }
}
