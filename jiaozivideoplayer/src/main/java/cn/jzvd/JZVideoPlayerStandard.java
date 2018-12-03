package cn.jzvd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nathen
 * On 2016/04/18 16:15
 */
public class JZVideoPlayerStandard extends JZVideoPlayer implements View.OnClickListener, View.OnTouchListener {

    public ImageView startButton;
    private SeekBar progressBar;
    protected ImageView fullscreenButton;
    private TextView currentTimeTextView, totalTimeTextView;
    public ViewGroup topContainer, bottomContainer;
    private ViewGroup textureViewContainer;

    public ImageView backButton;
    public ProgressBar bottomProgressBar, loadingProgressBar;
    public TextView titleTextView;
    public ImageView thumbImageView;
    public ImageView tinyBackImageView;
    public LinearLayout batteryTimeLayout;
    public ImageView batteryLevel;
    public TextView videoCurrentTime;
    public TextView replayTextView;
    public TextView clarity;
    public PopupWindow clarityPopWindow;
    public TextView mRetryBtn;
    public LinearLayout mRetryLayout;

    protected Dialog mProgressDialog;
    protected ProgressBar mDialogProgressBar;
    protected TextView mDialogSeekTime;
    protected TextView mDialogTotalTime;
    protected ImageView mDialogIcon;
    protected Dialog mVolumeDialog;
    protected ProgressBar mDialogVolumeProgressBar;
    protected TextView mDialogVolumeTextView;
    protected ImageView mDialogVolumeImageView;
    protected Dialog mBrightnessDialog;
    protected ProgressBar mDialogBrightnessProgressBar;
    protected TextView mDialogBrightnessTextView;
    public static long LAST_GET_BATTERYLEVEL_TIME = 0;
    public static int LAST_GET_BATTERYLEVEL_PERCENT = 70;

    private BroadcastReceiver battertReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                int percent = level * 100 / scale;
                LAST_GET_BATTERYLEVEL_PERCENT = percent;
                setBatteryLevel();
                getContext().unregisterReceiver(battertReceiver);
            }
        }
    };

    public JZVideoPlayerStandard(Context context) {
        super(context);
    }

    public JZVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);

        textureViewContainer = findViewById(R.id.surface_container);

        textureViewContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onTextureViewContainerClick();
            }
        });
        textureViewContainer.setOnTouchListener(new TextureViewTouchListener());

        startButton = findViewById(R.id.start);
        fullscreenButton = findViewById(R.id.fullscreen);
        progressBar = findViewById(R.id.bottom_seek_progress);
        currentTimeTextView = findViewById(R.id.current);
        totalTimeTextView = findViewById(R.id.total);
        bottomContainer = findViewById(R.id.layout_bottom);
        topContainer = findViewById(R.id.layout_top);

        startButton.setOnClickListener(this);
        fullscreenButton.setOnClickListener(this);
        progressBar.setOnSeekBarChangeListener(this);
        bottomContainer.setOnClickListener(this);


        batteryTimeLayout = findViewById(R.id.battery_time_layout);
        bottomProgressBar = findViewById(R.id.bottom_progress);
        titleTextView = findViewById(R.id.title);
        backButton = findViewById(R.id.back);
        thumbImageView = findViewById(R.id.thumb);
        loadingProgressBar = findViewById(R.id.loading);
        tinyBackImageView = findViewById(R.id.back_tiny);
        batteryLevel = findViewById(R.id.battery_level);
        videoCurrentTime = findViewById(R.id.video_current_time);
        replayTextView = findViewById(R.id.replay_text);
        clarity = findViewById(R.id.clarity);
        mRetryBtn = findViewById(R.id.retry_btn);
        mRetryLayout = findViewById(R.id.retry_layout);

        thumbImageView.setOnClickListener(this);
        backButton.setOnClickListener(this);
        tinyBackImageView.setOnClickListener(this);
        clarity.setOnClickListener(this);
        mRetryBtn.setOnClickListener(this);
    }

    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        super.setUp(dataSource, defaultUrlMapIndex, screen, objects);
        if (objects.length != 0) titleTextView.setText(objects[0].toString());
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            fullscreenButton.setImageResource(R.drawable.jz_shrink);
            backButton.setVisibility(View.VISIBLE);
            tinyBackImageView.setVisibility(View.INVISIBLE);
            batteryTimeLayout.setVisibility(View.VISIBLE);
            if (dataSource.getMap().size() == 1) {
                clarity.setVisibility(GONE);
            } else {
                clarity.setText(dataSource.getKey(currentUrlMapIndex));
                clarity.setVisibility(View.VISIBLE);
            }
            changeStartButtonSize((int) getResources().getDimension(R.dimen.jz_start_button_w_h_fullscreen));
        } else if (currentScreen == SCREEN_WINDOW_NORMAL
                || currentScreen == SCREEN_WINDOW_LIST) {
            fullscreenButton.setImageResource(R.drawable.jz_enlarge);
            backButton.setVisibility(View.GONE);
            tinyBackImageView.setVisibility(View.INVISIBLE);
            changeStartButtonSize((int) getResources().getDimension(R.dimen.jz_start_button_w_h_normal));
            batteryTimeLayout.setVisibility(View.GONE);
            clarity.setVisibility(View.GONE);
        } else if (currentScreen == SCREEN_WINDOW_TINY) {
            tinyBackImageView.setVisibility(View.VISIBLE);
            setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                    View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
            batteryTimeLayout.setVisibility(View.GONE);
            clarity.setVisibility(View.GONE);
        }
        setSystemTimeAndBattery();


        if (tmp_test_back) {
            tmp_test_back = false;
            JZVideoPlayerManager.setFirstFloor(this);
            backPress();
        }
    }

    public void changeStartButtonSize(int size) {
        ViewGroup.LayoutParams lp = startButton.getLayoutParams();
        lp.height = size;
        lp.width = size;
        lp = loadingProgressBar.getLayoutParams();
        lp.height = size;
        lp.width = size;
    }

    @Override
    public int getLayoutId() {
        return R.layout.jz_layout_standard;
    }

    @Override
    public void onStateNormal() {
        super.onStateNormal();
        ProgressTimerTask.finish();
        changeUiToNormal();
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
        resetProgressAndTime();
        changeUiToPreparing();
    }

    @Override
    public void onStatePreparingChangingUrl(int urlMapIndex, long seekToInAdvance) {
        super.onStatePreparingChangingUrl(urlMapIndex, seekToInAdvance);
        loadingProgressBar.setVisibility(VISIBLE);
        startButton.setVisibility(INVISIBLE);
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        ProgressTimerTask.start(this);
        changeUiToPlayingClear();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        ProgressTimerTask.start(this);
        DismissControlViewTimerTask.finish();
        changeUiToPauseShow();
    }

    @Override
    public void onStateError() {
        super.onStateError();
        ProgressTimerTask.finish();
        changeUiToError();
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();

        ProgressTimerTask.finish();
        DismissControlViewTimerTask.finish();

        progressBar.setProgress(100);
        currentTimeTextView.setText(totalTimeTextView.getText());

        changeUiToComplete();
        bottomProgressBar.setProgress(100);
    }

    @Override
    public void onTextureViewContainerClick() {
        super.onTextureViewContainerClick();
        DismissControlViewTimerTask.start(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.bottom_seek_progress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    DismissControlViewTimerTask.finish();
                    break;
                case MotionEvent.ACTION_UP:
                    DismissControlViewTimerTask.start(this);
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.start) {
            Log.i(TAG, "onClick start [" + this.hashCode() + "] ");
            if (dataSource == null || dataSource.getCurrentPath(currentUrlMapIndex) == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentState == CURRENT_STATE_NORMAL) {
                if (!dataSource.getCurrentPath(currentUrlMapIndex).toString().startsWith("file") && !
                        dataSource.getCurrentPath(currentUrlMapIndex).toString().startsWith("/") &&
                        !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                    showWifiDialog();
                    return;
                }
                startVideo();
                onEvent(JZUserAction.ON_CLICK_START_ICON);
            } else if (currentState == CURRENT_STATE_PLAYING) {
                onEvent(JZUserAction.ON_CLICK_PAUSE);
                Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ");
                JZMediaManager.pause();
                onStatePause();
            } else if (currentState == CURRENT_STATE_PAUSE) {
                onEvent(JZUserAction.ON_CLICK_RESUME);
                JZMediaManager.start();
                onStatePlaying();
            } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                onEvent(JZUserAction.ON_CLICK_START_AUTO_COMPLETE);
                startVideo();
            }
        } else if (i == R.id.fullscreen) {
            Log.i(TAG, "onClick fullscreen [" + this.hashCode() + "] ");
            if (currentState == CURRENT_STATE_AUTO_COMPLETE) return;
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                //quit fullscreen
                backPress();
            } else {
                Log.d(TAG, "toFullscreenActivity [" + this.hashCode() + "] ");
                onEvent(JZUserAction.ON_ENTER_FULLSCREEN);
                startWindowFullscreen();
            }
        } else if (i == R.id.thumb) {
            if (dataSource == null || dataSource.getCurrentPath(currentUrlMapIndex) == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentState == CURRENT_STATE_NORMAL) {
                if (!dataSource.getCurrentPath(currentUrlMapIndex).toString().startsWith("file") &&
                        !dataSource.getCurrentPath(currentUrlMapIndex).toString().startsWith("/") &&
                        !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                    showWifiDialog();
                    return;
                }
                onEvent(JZUserActionStandard.ON_CLICK_START_THUMB);
                startVideo();
            } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                onClickUiToggle();
            }
        } else if (i == R.id.back) {
            backPress();
        } else if (i == R.id.back_tiny) {
            if (JZVideoPlayerManager.getFirstFloor().currentScreen == JZVideoPlayer.SCREEN_WINDOW_LIST) {
                quitFullscreenOrTinyWindow();
            } else {
                backPress();
            }
        } else if (i == R.id.clarity) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.jz_layout_clarity, null);

            OnClickListener mQualityListener = new OnClickListener() {
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    onStatePreparingChangingUrl(index, getCurrentPositionWhenPlaying());
                    clarity.setText(dataSource.getKey(currentUrlMapIndex));
                    for (int j = 0; j < layout.getChildCount(); j++) {//设置点击之后的颜色
                        if (j == currentUrlMapIndex) {
                            ((TextView) layout.getChildAt(j)).setTextColor(Color.parseColor("#fff85959"));
                        } else {
                            ((TextView) layout.getChildAt(j)).setTextColor(Color.parseColor("#ffffff"));
                        }
                    }
                    if (clarityPopWindow != null) {
                        clarityPopWindow.dismiss();
                    }
                }
            };

            for (int j = 0; j < dataSource.getMap().size(); j++) {
                String key = dataSource.getKey(j);
                TextView clarityItem = (TextView) View.inflate(getContext(), R.layout.jz_layout_clarity_item, null);
                clarityItem.setText(key);
                clarityItem.setTag(j);
                layout.addView(clarityItem, j);
                clarityItem.setOnClickListener(mQualityListener);
                if (j == currentUrlMapIndex) {
                    clarityItem.setTextColor(Color.parseColor("#fff85959"));
                }
            }

            clarityPopWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
            clarityPopWindow.setContentView(layout);
            clarityPopWindow.showAsDropDown(clarity);
            layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int offsetX = clarity.getMeasuredWidth() / 3;
            int offsetY = clarity.getMeasuredHeight() / 3;
            clarityPopWindow.update(clarity, -offsetX, -offsetY, Math.round(layout.getMeasuredWidth() * 2), layout.getMeasuredHeight());
        } else if (i == R.id.retry_btn) {
            if (dataSource == null || dataSource.getCurrentPath(currentUrlMapIndex) == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!dataSource.getCurrentPath(currentUrlMapIndex).toString().startsWith("file") && !
                    dataSource.getCurrentPath(currentUrlMapIndex).toString().startsWith("/") &&
                    !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                showWifiDialog();
                return;
            }
            initTextureView();//和开始播放的代码重复
            addTextureView();
            JZMediaManager.setDataSource(dataSource);
            JZMediaManager.setCurrentPath(dataSource.getCurrentPath(currentUrlMapIndex));
            onStatePreparing();
            onEvent(JZUserAction.ON_CLICK_START_ERROR);
        }
    }

    @Override
    public void showWifiDialog() {
        super.showWifiDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(getResources().getString(R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onEvent(JZUserActionStandard.ON_CLICK_START_WIFIDIALOG);
                startVideo();
                WIFI_TIP_DIALOG_SHOWED = true;
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clearFloatScreen();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            //设置这个progres对应的时间，给textview
            long duration = getDuration();
            currentTimeTextView.setText(JZUtils.stringForTime(progress * duration / 100));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStartTrackingTouch [" + this.hashCode() + "] ");
        ProgressTimerTask.finish();
        DismissControlViewTimerTask.finish();

        ViewParent vpdown = getParent();
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true);
            vpdown = vpdown.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStopTrackingTouch [" + this.hashCode() + "] ");
        onEvent(JZUserAction.ON_SEEK_POSITION);
        ViewParent vpup = getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if (currentState != CURRENT_STATE_PLAYING &&
                currentState != CURRENT_STATE_PAUSE) return;

        long time = seekBar.getProgress() * getDuration() / 100;
        JZMediaManager.seekTo(time);
        Log.i(TAG, "seekTo " + time + " [" + this.hashCode() + "] ");

        ProgressTimerTask.start(this);
        if (currentState == CURRENT_STATE_PLAYING) {
            DismissControlViewTimerTask.oneShot();
        } else {
            DismissControlViewTimerTask.start(this);
        }
    }

    protected void onClickUiToggle() {
        if (bottomContainer.getVisibility() != View.VISIBLE) {
            setSystemTimeAndBattery();
            clarity.setText(dataSource.getKey(currentUrlMapIndex));
        }
        if (currentState == CURRENT_STATE_PREPARING) {
            changeUiToPreparing();
            if (bottomContainer.getVisibility() == View.VISIBLE) {
            } else {
                setSystemTimeAndBattery();
            }
        } else if (currentState == CURRENT_STATE_PLAYING) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPlayingClear();
            } else {
                changeUiToPlayingShow();
            }
        } else if (currentState == CURRENT_STATE_PAUSE) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPauseClear();
            } else {
                changeUiToPauseShow();
            }
        }
    }

    private void setSystemTimeAndBattery() {
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        videoCurrentTime.setText(dateFormater.format(date));
        if ((System.currentTimeMillis() - LAST_GET_BATTERYLEVEL_TIME) > 30000) {
            LAST_GET_BATTERYLEVEL_TIME = System.currentTimeMillis();
            getContext().registerReceiver(
                    battertReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            );
        } else {
            setBatteryLevel();
        }
    }

    private void setBatteryLevel() {
        int percent = LAST_GET_BATTERYLEVEL_PERCENT;
        if (percent < 15) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_10);
        } else if (percent < 40) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_30);
        } else if (percent < 60) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_50);
        } else if (percent < 80) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_70);
        } else if (percent < 95) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_90);
        } else if (percent <= 100) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_100);
        }
    }

    public void onCLickUiToggleToClear() {
        if (currentState == CURRENT_STATE_PREPARING) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPreparing();
            } else {
            }
        } else if (currentState == CURRENT_STATE_PLAYING) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPlayingClear();
            } else {
            }
        } else if (currentState == CURRENT_STATE_PAUSE) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPauseClear();
            } else {
            }
        } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToComplete();
            } else {
            }
        }
    }

    public void setProgressAndText(int progress, long position, long duration) {
//        Log.d(TAG, "setProgressAndText: progress=" + progress + " position=" + position + " duration=" + duration);
        if (!mTouchingProgressBar) {
            if (progress != 0) progressBar.setProgress(progress);
        }
        if (position != 0) currentTimeTextView.setText(JZUtils.stringForTime(position));
        totalTimeTextView.setText(JZUtils.stringForTime(duration));

        if (progress != 0) bottomProgressBar.setProgress(progress);
    }

    public void setBufferProgress(int bufferProgress) {
        if (bufferProgress != 0) progressBar.setSecondaryProgress(bufferProgress);
        if (bufferProgress != 0) bottomProgressBar.setSecondaryProgress(bufferProgress);
    }

    private void resetProgressAndTime() {
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
        currentTimeTextView.setText(JZUtils.stringForTime(0));
        totalTimeTextView.setText(JZUtils.stringForTime(0));

        bottomProgressBar.setProgress(0);
        bottomProgressBar.setSecondaryProgress(0);
    }

    private void changeUiToNormal() {
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    private void changeUiToPreparing() {
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    private void changeUiToPlayingShow() {
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    private void changeUiToPlayingClear() {
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    private void changeUiToPauseShow() {
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    private void changeUiToPauseClear() {
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    private void changeUiToComplete() {
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    private void changeUiToError() {
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    public void setAllControlsVisiblity(int topCon, int bottomCon, int startBtn, int loadingPro,
                                        int thumbImg, int bottomPro, int retryLayout) {
        topContainer.setVisibility(topCon);
        bottomContainer.setVisibility(bottomCon);
        startButton.setVisibility(startBtn);
        loadingProgressBar.setVisibility(loadingPro);
        thumbImageView.setVisibility(thumbImg);
        bottomProgressBar.setVisibility(bottomPro);
        mRetryLayout.setVisibility(retryLayout);
    }

    public void updateStartImage() {
        if (currentState == CURRENT_STATE_PLAYING) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.drawable.jz_click_pause_selector);
            replayTextView.setVisibility(INVISIBLE);
        } else if (currentState == CURRENT_STATE_ERROR) {
            startButton.setVisibility(INVISIBLE);
            replayTextView.setVisibility(INVISIBLE);
        } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.drawable.jz_click_replay_selector);
            replayTextView.setVisibility(VISIBLE);
        } else {
            startButton.setImageResource(R.drawable.jz_click_play_selector);
            replayTextView.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void showProgressDialog(float deltaX, String seekTime, long seekTimePosition, String totalTime, long totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
        if (mProgressDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jz_dialog_progress, null);
            mDialogProgressBar = localView.findViewById(R.id.duration_progressbar);
            mDialogSeekTime = localView.findViewById(R.id.tv_current);
            mDialogTotalTime = localView.findViewById(R.id.tv_duration);
            mDialogIcon = localView.findViewById(R.id.duration_image_tip);
            mProgressDialog = createDialogWithView(localView);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        mDialogSeekTime.setText(seekTime);
        mDialogTotalTime.setText(" / " + totalTime);
        mDialogProgressBar.setProgress(totalTimeDuration <= 0 ? 0 : (int) (seekTimePosition * 100 / totalTimeDuration));
        if (deltaX > 0) {
            mDialogIcon.setBackgroundResource(R.drawable.jz_forward_icon);
        } else {
            mDialogIcon.setBackgroundResource(R.drawable.jz_backward_icon);
        }
        onCLickUiToggleToClear();
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showVolumeDialog(float deltaY, int volumePercent) {
        super.showVolumeDialog(deltaY, volumePercent);
        if (mVolumeDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jz_dialog_volume, null);
            mDialogVolumeImageView = localView.findViewById(R.id.volume_image_tip);
            mDialogVolumeTextView = localView.findViewById(R.id.tv_volume);
            mDialogVolumeProgressBar = localView.findViewById(R.id.volume_progressbar);
            mVolumeDialog = createDialogWithView(localView);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        if (volumePercent <= 0) {
            mDialogVolumeImageView.setBackgroundResource(R.drawable.jz_close_volume);
        } else {
            mDialogVolumeImageView.setBackgroundResource(R.drawable.jz_add_volume);
        }
        if (volumePercent > 100) {
            volumePercent = 100;
        } else if (volumePercent < 0) {
            volumePercent = 0;
        }
        mDialogVolumeTextView.setText(volumePercent + "%");
        mDialogVolumeProgressBar.setProgress(volumePercent);
        onCLickUiToggleToClear();
    }

    @Override
    public void dismissVolumeDialog() {
        super.dismissVolumeDialog();
        if (mVolumeDialog != null) {
            mVolumeDialog.dismiss();
        }
    }

    @Override
    public void showBrightnessDialog(int brightnessPercent) {
        super.showBrightnessDialog(brightnessPercent);
        if (mBrightnessDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jz_dialog_brightness, null);
            mDialogBrightnessTextView = localView.findViewById(R.id.tv_brightness);
            mDialogBrightnessProgressBar = localView.findViewById(R.id.brightness_progressbar);
            mBrightnessDialog = createDialogWithView(localView);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }
        if (brightnessPercent > 100) {
            brightnessPercent = 100;
        } else if (brightnessPercent < 0) {
            brightnessPercent = 0;
        }
        mDialogBrightnessTextView.setText(brightnessPercent + "%");
        mDialogBrightnessProgressBar.setProgress(brightnessPercent);
        onCLickUiToggleToClear();
    }

    @Override
    public void dismissBrightnessDialog() {
        super.dismissBrightnessDialog();
        if (mBrightnessDialog != null) {
            mBrightnessDialog.dismiss();
        }
    }

    public Dialog createDialogWithView(View localView) {
        Dialog dialog = new Dialog(getContext(), R.style.jz_style_dialog_progress);
        dialog.setContentView(localView);
        Window window = dialog.getWindow();
        window.addFlags(Window.FEATURE_ACTION_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        window.setLayout(-2, -2);
        WindowManager.LayoutParams localLayoutParams = window.getAttributes();
        localLayoutParams.gravity = Gravity.CENTER;
        window.setAttributes(localLayoutParams);
        return dialog;
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        DismissControlViewTimerTask.finish();
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        textureViewContainer.removeView(JZMediaManager.textureView);
        ProgressTimerTask.finish();
        DismissControlViewTimerTask.finish();

        if (clarityPopWindow != null) {
            clarityPopWindow.dismiss();
        }
    }

    @Override
    public void startVideo() {
        JZVideoPlayerManager.completeAll();
        Log.d(TAG, "startVideo [" + this.hashCode() + "] ");
        initTextureView();
        addTextureView();
        super.startVideo();
    }

    @Override
    public void playOnThisJzvd() {
        super.playOnThisJzvd();
        addTextureView();
    }

    @Override
    public void startWindowFullscreen() {
        textureViewContainer.removeView(JZMediaManager.textureView);
        super.startWindowFullscreen();
    }

    @Override
    public void startWindowTiny() {
        textureViewContainer.removeView(JZMediaManager.textureView);
        super.startWindowTiny();
    }

    @Override
    public void clearFloatScreen() {
        super.clearFloatScreen();
        JZUtils.setRequestedOrientation(getContext(), NORMAL_ORIENTATION);
        showSupportActionBar(getContext());
        ViewGroup vp = (JZUtils.scanForActivity(getContext()))//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        JZVideoPlayerStandard fullJzvd = vp.findViewById(R.id.jz_fullscreen_id);
        JZVideoPlayerStandard tinyJzvd = vp.findViewById(R.id.jz_tiny_id);

        if (fullJzvd != null) {
            vp.removeView(fullJzvd);
            if (fullJzvd.textureViewContainer != null)
                fullJzvd.textureViewContainer.removeView(JZMediaManager.textureView);
        }
        if (tinyJzvd != null) {
            vp.removeView(tinyJzvd);
            if (tinyJzvd.textureViewContainer != null)
                tinyJzvd.textureViewContainer.removeView(JZMediaManager.textureView);
        }
        JZVideoPlayerManager.setSecondFloor(null);
    }

    public void initTextureView() {
        removeTextureView();
        JZMediaManager.textureView = new JZResizeTextureView(getContext());
        JZMediaManager.textureView.setSurfaceTextureListener(JZMediaManager.instance());
    }

    public void addTextureView() {
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ");
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        textureViewContainer.addView(JZMediaManager.textureView, layoutParams);
    }

    public void removeTextureView() {
        JZMediaManager.savedSurfaceTexture = null;
        if (JZMediaManager.textureView != null && JZMediaManager.textureView.getParent() != null) {
            ((ViewGroup) JZMediaManager.textureView.getParent()).removeView(JZMediaManager.textureView);
        }
    }

    private class TextureViewTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            int id = v.getId();

            if (id == R.id.surface_container) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "onTouch surfaceContainer actionDown [" + this.hashCode() + "] ");
                        mTouchingProgressBar = true;

                        mDownX = x;
                        mDownY = y;
                        mChangeVolume = false;
                        mChangePosition = false;
                        mChangeBrightness = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i(TAG, "onTouch surfaceContainer actionMove [" + this.hashCode() + "] ");
                        float deltaX = x - mDownX;
                        float deltaY = y - mDownY;
                        float absDeltaX = Math.abs(deltaX);
                        float absDeltaY = Math.abs(deltaY);
                        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                            if (!mChangePosition && !mChangeVolume && !mChangeBrightness) {
                                if (absDeltaX > THRESHOLD || absDeltaY > THRESHOLD) {
                                    ProgressTimerTask.finish();
                                    if (absDeltaX >= THRESHOLD) {
                                        // 全屏模式下的CURRENT_STATE_ERROR状态下,不响应进度拖动事件.
                                        // 否则会因为mediaplayer的状态非法导致App Crash
                                        if (currentState != CURRENT_STATE_ERROR) {
                                            mChangePosition = true;
                                            mGestureDownPosition = getCurrentPositionWhenPlaying();
                                        }
                                    } else {
                                        //如果y轴滑动距离超过设置的处理范围，那么进行滑动事件处理
                                        if (mDownX < mScreenWidth * 0.5f) {//左侧改变亮度
                                            mChangeBrightness = true;
                                            WindowManager.LayoutParams lp = JZUtils.getWindow(getContext()).getAttributes();
                                            if (lp.screenBrightness < 0) {
                                                try {
                                                    mGestureDownBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                                                    Log.i(TAG, "current system brightness: " + mGestureDownBrightness);
                                                } catch (Settings.SettingNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                mGestureDownBrightness = lp.screenBrightness * 255;
                                                Log.i(TAG, "current activity brightness: " + mGestureDownBrightness);
                                            }
                                        } else {//右侧改变声音
                                            mChangeVolume = true;
                                            mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                        }
                                    }
                                }
                            }
                        }
                        if (mChangePosition) {
                            long totalTimeDuration = getDuration();
                            mSeekTimePosition = (int) (mGestureDownPosition + deltaX * totalTimeDuration / mScreenWidth);
                            if (mSeekTimePosition > totalTimeDuration)
                                mSeekTimePosition = totalTimeDuration;
                            String seekTime = JZUtils.stringForTime(mSeekTimePosition);
                            String totalTime = JZUtils.stringForTime(totalTimeDuration);

                            showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);
                        }
                        if (mChangeVolume) {
                            deltaY = -deltaY;
                            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                            int deltaV = (int) (max * deltaY * 3 / mScreenHeight);
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
                            //dialog中显示百分比
                            int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / mScreenHeight);
                            showVolumeDialog(-deltaY, volumePercent);
                        }

                        if (mChangeBrightness) {
                            deltaY = -deltaY;
                            int deltaV = (int) (255 * deltaY * 3 / mScreenHeight);
                            WindowManager.LayoutParams params = JZUtils.getWindow(getContext()).getAttributes();
                            if (((mGestureDownBrightness + deltaV) / 255) >= 1) {//这和声音有区别，必须自己过滤一下负值
                                params.screenBrightness = 1;
                            } else if (((mGestureDownBrightness + deltaV) / 255) <= 0) {
                                params.screenBrightness = 0.01f;
                            } else {
                                params.screenBrightness = (mGestureDownBrightness + deltaV) / 255;
                            }
                            JZUtils.getWindow(getContext()).setAttributes(params);
                            //dialog中显示百分比
                            int brightnessPercent = (int) (mGestureDownBrightness * 100 / 255 + deltaY * 3 * 100 / mScreenHeight);
                            showBrightnessDialog(brightnessPercent);
//                        mDownY = y;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "onTouch surfaceContainer actionUp [" + this.hashCode() + "] ");
                        mTouchingProgressBar = false;
                        dismissProgressDialog();
                        dismissVolumeDialog();
                        dismissBrightnessDialog();
                        if (mChangePosition) {
                            onEvent(JZUserAction.ON_TOUCH_SCREEN_SEEK_POSITION);
                            JZMediaManager.seekTo(mSeekTimePosition);
                            long duration = getDuration();
                            int progress = (int) (mSeekTimePosition * 100 / (duration == 0 ? 1 : duration));
                            progressBar.setProgress(progress);
                        }
                        if (mChangeVolume) {
                            onEvent(JZUserAction.ON_TOUCH_SCREEN_SEEK_VOLUME);
                        }
                        ProgressTimerTask.start(JZVideoPlayerStandard.this);
                        DismissControlViewTimerTask.start(JZVideoPlayerStandard.this);

                        if (mChangePosition) {
                            long duration = getDuration();
                            int progress = (int) (mSeekTimePosition * 100 / (duration == 0 ? 1 : duration));
                            bottomProgressBar.setProgress(progress);
                        }
                        if (!mChangePosition && !mChangeVolume) {
                            onEvent(JZUserActionStandard.ON_CLICK_BLANK);
                            onClickUiToggle();
                        }
                        break;
                }
            }
            return false;
        }
    }
}
