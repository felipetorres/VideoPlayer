package cn.jzvd.component;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUserAction;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.R;
import cn.jzvd.task.DismissControlViewTimerTask;
import cn.jzvd.task.ProgressTimerTask;
import cn.jzvd.ui.ContainerLocation;
import cn.jzvd.ui.PluginLocation;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_TINY;

public class ProgressComponent extends JZUIControlComponent implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "ProgressComponent";

    private SeekBar progressBar;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private ProgressBar bottomProgressBar;

    public ProgressComponent(JZVideoPlayer player) {
        super(player);
        super.container = ContainerLocation.BOTTOM;
        super.location = PluginLocation.LEFT;
    }

    @Override
    public String getName() {
        return ProgressComponent.class.getSimpleName();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void init(ViewGroup parent) {
        super.init(parent);
        progressBar = parent.findViewById(R.id.bottom_seek_progress);
        currentTimeTextView = parent.findViewById(R.id.current);
        totalTimeTextView = parent.findViewById(R.id.total);

        bottomProgressBar = parent.findViewById(R.id.bottom_progress);

        progressBar.setOnSeekBarChangeListener(this);
        progressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DismissControlViewTimerTask.finish();
                        break;
                    case MotionEvent.ACTION_UP:
                        DismissControlViewTimerTask.start(player);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.plugin_progress;
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if (player.currentScreen == SCREEN_WINDOW_TINY) {
            bottomProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            //设置这个progres对应的时间，给textview
            long duration = player.getDuration();
            currentTimeTextView.setText(JZUtils.stringForTime(progress * duration / 100));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStartTrackingTouch [" + this.hashCode() + "] ");
        ProgressTimerTask.finish();
        DismissControlViewTimerTask.finish();

        ViewParent vpdown = player.getParent();
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true);
            vpdown = vpdown.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStopTrackingTouch [" + this.hashCode() + "] ");
        player.onEvent(JZUserAction.ON_SEEK_POSITION);
        ViewParent vpup = player.getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if (!player.getStateMachine().currentStatePlaying() && !player.getStateMachine().currentStatePause())
            return;

        long time = seekBar.getProgress() * player.getDuration() / 100;
        JZMediaManager.seekTo(time);
        Log.i(TAG, "seekTo " + time + " [" + this.hashCode() + "] ");

        ProgressTimerTask.start(player);
        if (player.getStateMachine().currentStatePlaying()) {
            DismissControlViewTimerTask.oneShot();
        } else {
            DismissControlViewTimerTask.start(player);
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
        progressBar.setProgress(100);
        currentTimeTextView.setText(totalTimeTextView.getText());
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
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
        currentTimeTextView.setText(JZUtils.stringForTime(0));
        totalTimeTextView.setText(JZUtils.stringForTime(0));

        bottomProgressBar.setProgress(0);
        bottomProgressBar.setSecondaryProgress(0);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void setProgressAndText(boolean mTouchingProgressBar, int progress, long position, long duration) {
        Log.d(TAG, "setProgressAndText: progress=" + progress + " position=" + position + " duration=" + duration);
        if (!mTouchingProgressBar) {
            if (progress != 0) setProgress(progress);
        }
        if (position != 0) currentTimeTextView.setText(JZUtils.stringForTime(position));
        totalTimeTextView.setText(JZUtils.stringForTime(duration));

        if (progress != 0) bottomProgressBar.setProgress(progress);
    }

    public void setBufferProgress(int bufferProgress) {
        if (bufferProgress != 0) {
            progressBar.setSecondaryProgress(bufferProgress);
            bottomProgressBar.setSecondaryProgress(bufferProgress);
        }
    }

    public void copySecondaryProgressFrom(Loader loader) {
        ProgressComponent progressComponent = loader.getControlComponent(ProgressComponent.class);
        int secondaryProgress = progressComponent.progressBar.getSecondaryProgress();

        progressBar.setSecondaryProgress(secondaryProgress);
    }
}