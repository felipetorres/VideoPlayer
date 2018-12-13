package cn.jzvd.plugin;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUserAction;
import cn.jzvd.JZUtils;
import cn.jzvd.R;
import cn.jzvd.ui.ContainerLocation;
import cn.jzvd.ui.PluginLocation;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_TINY;

public class RetryPlugin extends JZUiControlPlugin {

    private LinearLayout mRetryLayout;

    public RetryPlugin() {
        super.container = ContainerLocation.CENTER;
        super.location = PluginLocation.CENTER;
    }

    @Override
    public String getName() {
        return RetryPlugin.class.getSimpleName();
    }

    @Override
    public void init(ViewGroup parent) {
        super.init(parent);
        mRetryLayout = parent.findViewById(R.id.retry_layout);

        TextView mRetryBtn = parent.findViewById(R.id.retry_btn);
        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetryPlugin.this.onClick();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.plugin_retry;
    }

    private void onClick() {
        if (player.dataSource == null || player.dataSource.getCurrentPath(player.currentUrlMapIndex) == null) {
            Toast.makeText(context, player.getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (withWifiDialog()
                && !player.dataSource.getCurrentPath(player.currentUrlMapIndex).toString().startsWith("file")
                && !player.dataSource.getCurrentPath(player.currentUrlMapIndex).toString().startsWith("/")
                && !JZUtils.isWifiConnected(context)
                && !wifiDialog.showed()) {
            wifiDialog.show();
            return;
        }
        //TODO FELIPE: ARRUMAR AQUI
        //player.textureViewContainer.initTextureView();//和开始播放的代码重复
        JZMediaManager.setDataSource(player.dataSource);
        JZMediaManager.setCurrentPath(player.dataSource.getCurrentPath(player.currentUrlMapIndex));
        player.getStateMachine().setPreparing();
        player.onEvent(JZUserAction.ON_CLICK_START_ERROR);
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if (player.currentScreen == SCREEN_WINDOW_TINY) {
            mRetryLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNormal(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            mRetryLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPreparing(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            mRetryLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPlayingShow(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            mRetryLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPlayingClear(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            mRetryLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPauseShow(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            mRetryLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPauseClear(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            mRetryLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onComplete(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            mRetryLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onError(int currentScreen) {
        if (currentScreen != SCREEN_WINDOW_TINY) {
            mRetryLayout.setVisibility(View.VISIBLE);
        }
    }
}
