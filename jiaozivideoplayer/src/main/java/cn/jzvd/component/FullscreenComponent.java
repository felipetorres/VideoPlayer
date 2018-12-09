package cn.jzvd.component;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZUserAction;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;
import cn.jzvd.ui.ContainerLocation;
import cn.jzvd.ui.PluginLocation;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;
import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_LIST;
import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_NORMAL;
import static cn.jzvd.JZVideoPlayer.backPress;

public class FullscreenComponent extends JZUIComponent {

    private static final String TAG = "FullscreenComponent";
    private ImageView fullscreenButton;

    public FullscreenComponent(JZVideoPlayerStandard player) {
        super(player);
        super.container = ContainerLocation.BOTTOM;
        super.location = PluginLocation.RIGHT;
        super.orderIfSameLocation = 1;
    }

    @Override
    public String getName() {
        return FullscreenComponent.class.getSimpleName();
    }

    @Override
    public void init(ViewGroup parent) {
        super.init(parent);
        fullscreenButton = parent.findViewById(R.id.fullscreen);
        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullscreenComponent.this.onClick();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.plugin_fullscreen;
    }

    private void onClick() {
        Log.i(TAG, "onClick fullscreen [" + this.hashCode() + "] ");
        if (player.getStateMachine().currentStateAutoComplete()) return;
        if (player.currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            //quit fullscreen
            backPress();
        } else {
            Log.d(TAG, "toFullscreenActivity [" + this.hashCode() + "] ");
            player.onEvent(JZUserAction.ON_ENTER_FULLSCREEN);
            player.startWindowFullscreen();
        }
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object...
            objects) {
        if (player.currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            fullscreenButton.setImageResource(R.drawable.jz_shrink);
        } else if (player.currentScreen == SCREEN_WINDOW_NORMAL || player.currentScreen == SCREEN_WINDOW_LIST) {
            fullscreenButton.setImageResource(R.drawable.jz_enlarge);
        }
    }
}
