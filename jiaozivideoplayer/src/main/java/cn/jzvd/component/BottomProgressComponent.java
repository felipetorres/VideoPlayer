package cn.jzvd.component;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.R;
import cn.jzvd.ui.ContainerLocation;
import cn.jzvd.ui.PluginLocation;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_TINY;

public class BottomProgressComponent extends JZUIControlComponent {

    private ProgressBar bottomProgressBar;

    public BottomProgressComponent(JZVideoPlayer player) {
        super(player);
        super.container = ContainerLocation.NONE;
        super.location = PluginLocation.CENTER;
    }

    @Override
    public String getName() {
        return BottomProgressComponent.class.getSimpleName();
    }

    @Override
    public int getLayoutId() {
        return R.layout.plugin_bottom_progress;
    }

    @Override
    public void init(ViewGroup parent) {
        super.init(parent);
        bottomProgressBar = parent.findViewById(R.id.bottom_progress);
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if (player.currentScreen == SCREEN_WINDOW_TINY) {
            bottomProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNormal(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            bottomProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPreparing(int currentScreen) {
        resetProgressAndTime();
        if (currentScreen != SCREEN_WINDOW_TINY) {
            bottomProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPlayingShow(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            bottomProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPlayingClear(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            bottomProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPauseShow(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            bottomProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPauseClear(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            bottomProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onComplete(int currentScreen) {
        bottomProgressBar.setProgress(100);

        if (currentScreen != SCREEN_WINDOW_TINY) {
            bottomProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onError(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            bottomProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDismissControlView() {
        if (player.currentScreen != SCREEN_WINDOW_TINY) {
            bottomProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void resetProgressAndTime() {
        bottomProgressBar.setProgress(0);
        bottomProgressBar.setSecondaryProgress(0);
    }

    public void setProgress(int progress) {
        if (progress != 0) bottomProgressBar.setProgress(progress);
    }

    public void setBufferProgress(int bufferProgress) {
        if (bufferProgress != 0) {
            bottomProgressBar.setSecondaryProgress(bufferProgress);
        }
    }
}
