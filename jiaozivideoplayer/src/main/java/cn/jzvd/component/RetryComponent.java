package cn.jzvd.component;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUserAction;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_TINY;

public class RetryComponent extends JZUIControlComponent {

    private TextView mRetryBtn;
    private LinearLayout mRetryLayout;

    public RetryComponent(JZVideoPlayerStandard player) {
        super(player);
    }

    @Override
    public String getName() {
        return RetryComponent.class.getSimpleName();
    }

    @Override
    protected void init(FrameLayout frameLayout) {
        mRetryBtn = frameLayout.findViewById(R.id.retry_btn);
        mRetryLayout = frameLayout.findViewById(R.id.retry_layout);

        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetryComponent.this.onClick();
            }
        });
    }

    private void onClick() {
        if (player.dataSource == null || player.dataSource.getCurrentPath(player.currentUrlMapIndex) == null) {
            Toast.makeText(context, player.getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!player.dataSource.getCurrentPath(player.currentUrlMapIndex).toString().startsWith("file") && !
                player.dataSource.getCurrentPath(player.currentUrlMapIndex).toString().startsWith("/") &&
                !JZUtils.isWifiConnected(context) && !player.wifiDialog.showed()) {
            player.wifiDialog.show();
            return;
        }
        player.textureViewContainer.initTextureView();//和开始播放的代码重复
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
