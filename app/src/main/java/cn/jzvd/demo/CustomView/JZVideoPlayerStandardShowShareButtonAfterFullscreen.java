package cn.jzvd.demo.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import cn.jzvd.JZDataSource;
import cn.jzvd.demo.R;
import cn.jzvd.plugin.BatteryPlugin;
import cn.jzvd.plugin.JZUiPlugin;
import cn.jzvd.ui.ContainerLocation;
import cn.jzvd.ui.PluginLocation;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;

/**
 * Created by Nathen
 * On 2016/04/22 00:54
 */
public class JZVideoPlayerStandardShowShareButtonAfterFullscreen extends JZVideoPlayerStandardGlide {

    public JZVideoPlayerStandardShowShareButtonAfterFullscreen(Context context) {
        super(context);
    }

    public JZVideoPlayerStandardShowShareButtonAfterFullscreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.loader.register(this, new CustomBatteryPlugin(),
                                    new SharePlugin());

        super.init(context);
    }
}

class CustomBatteryPlugin extends BatteryPlugin {

    public CustomBatteryPlugin() {
        super.orderIfSameLocation = 1;
    }
}

class SharePlugin extends JZUiPlugin {

    private ImageView shareButton;

    public SharePlugin() {
        super.container = ContainerLocation.TOP;
        super.location = PluginLocation.RIGHT;
        super.orderIfSameLocation = 0;
    }

    @Override
    public String getName() {
        return SharePlugin.class.getSimpleName();
    }

    @Override
    public int getLayoutId() {
        return R.layout.plugin_share_button;
    }

    @Override
    public void init(ViewGroup parent) {
        super.init(parent);
        shareButton = parent.findViewById(R.id.share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(player.getContext(), "Whatever the icon means", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if (player.currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            shareButton.setVisibility(View.VISIBLE);
        } else {
            shareButton.setVisibility(View.INVISIBLE);
        }
    }
}
