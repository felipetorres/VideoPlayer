package cn.jzvd;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.jzvd.component.ClarityComponent;
import cn.jzvd.component.JZCoreComponent;
import cn.jzvd.component.JZUIComponent;
import cn.jzvd.component.JZUIControlComponent;
import cn.jzvd.component.TextureViewContainer;
import cn.jzvd.dialog.JZDialogs;
import cn.jzvd.task.DismissControlViewTimerTask;
import cn.jzvd.task.ProgressTimerTask;

/**
 * Created by Nathen
 * On 2016/04/18 16:15
 */
public class JZVideoPlayerStandard extends JZVideoPlayer implements View.OnClickListener, View.OnTouchListener {

    public SeekBar progressBar;
    private TextView currentTimeTextView, totalTimeTextView;
    public ViewGroup topContainer, bottomContainer;
    public TextureViewContainer textureViewContainer;

    public ProgressBar bottomProgressBar, loadingProgressBar;
    public TextView replayTextView;

    public boolean mTouchingProgressBar;

    private JZDialogs dialogs;

    public JZVideoPlayerStandard(Context context) {
        super(context);
    }

    public JZVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);

        super.loader.registerComponents(this);
        this.dialogs = new JZDialogs(this);
        textureViewContainer = new TextureViewContainer(this, this.dialogs);

        progressBar = findViewById(R.id.bottom_seek_progress);
        currentTimeTextView = findViewById(R.id.current);
        totalTimeTextView = findViewById(R.id.total);
        bottomContainer = findViewById(R.id.layout_bottom);
        topContainer = findViewById(R.id.layout_top);

        progressBar.setOnSeekBarChangeListener(this);

        bottomProgressBar = findViewById(R.id.bottom_progress);

        loadingProgressBar = findViewById(R.id.loading);
        replayTextView = findViewById(R.id.replay_text);
    }

    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        super.setUp(dataSource, defaultUrlMapIndex, screen, objects);

        for (JZCoreComponent component : loader.getAllRegisteredComponents()) {
            component.setUp(dataSource, defaultUrlMapIndex, screen, objects);
        }

        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            changeStartButtonSize((int) getResources().getDimension(R.dimen.jz_start_button_w_h_fullscreen));
            textureViewContainer.initTextureView();
        } else if (currentScreen == SCREEN_WINDOW_NORMAL
                || currentScreen == SCREEN_WINDOW_LIST) {
            changeStartButtonSize((int) getResources().getDimension(R.dimen.jz_start_button_w_h_normal));
        } else if (currentScreen == SCREEN_WINDOW_TINY) {
            setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE,
                    View.INVISIBLE, View.INVISIBLE);
            textureViewContainer.addTextureView();
        }


        if (tmp_test_back) {
            tmp_test_back = false;
            JZVideoPlayerManager.setFirstFloor(this);
            backPress();
        }
    }

    public void changeStartButtonSize(int size) {
        ViewGroup.LayoutParams lp = loadingProgressBar.getLayoutParams();
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
    public void onStatePreparingChangingUrl(int urlMapIndex) {
        super.onStatePreparingChangingUrl(urlMapIndex);
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPreparingChangingUrl();
        }
        loadingProgressBar.setVisibility(VISIBLE);
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

    @Deprecated
    public void onClick(View v) {
        //It will be removed in the next commits. Click behaviour must be managed by the components themselves.
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
        if (!getStateMachine().currentStatePlaying() && !getStateMachine().currentStatePause()) return;

        long time = seekBar.getProgress() * getDuration() / 100;
        JZMediaManager.seekTo(time);
        Log.i(TAG, "seekTo " + time + " [" + this.hashCode() + "] ");

        ProgressTimerTask.start(this);
        if (getStateMachine().currentStatePlaying()) {
            DismissControlViewTimerTask.oneShot();
        } else {
            DismissControlViewTimerTask.start(this);
        }
    }

    public void onClickUiToggle() {
        for (JZUIComponent component : loader.getRegisteredUIComponents()) {
            component.onClickUiToggle();
        }

        if (getStateMachine().currentStatePreparing()) {
            changeUiToPreparing();
            if (bottomContainer.getVisibility() == View.VISIBLE) {
            } else {
            }
        } else if (getStateMachine().currentStatePlaying()) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPlayingClear();
            } else {
                changeUiToPlayingShow();
            }
        } else if (getStateMachine().currentStatePause()) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPauseClear();
            } else {
                changeUiToPauseShow();
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
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onNormal(currentScreen);
        }

        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToPreparing() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPreparing(currentScreen);
        }

        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    private void changeUiToPlayingShow() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPlayingShow(currentScreen);
        }

        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    public void changeUiToPlayingClear() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPlayingClear(currentScreen);
        }

        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.VISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.VISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    private void changeUiToPauseShow() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPauseShow(currentScreen);
        }

        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToPauseClear() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPauseClear(currentScreen);
        }

        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.VISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.VISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    public void changeUiToComplete() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onComplete(currentScreen);
        }

        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    private void changeUiToError() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onError(currentScreen);
        }

        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    public void setAllControlsVisiblity(int topCon, int bottomCon, int loadingPro,
                                        int bottomPro) {
        topContainer.setVisibility(topCon);
        bottomContainer.setVisibility(bottomCon);
        loadingProgressBar.setVisibility(loadingPro);
        bottomProgressBar.setVisibility(bottomPro);
    }

    public void updateStartImage() {
        if (getStateMachine().currentStatePlaying()) {
            replayTextView.setVisibility(INVISIBLE);
        } else if (getStateMachine().currentStateError()) {
            replayTextView.setVisibility(INVISIBLE);
        } else if (getStateMachine().currentStateAutoComplete()) {
            replayTextView.setVisibility(VISIBLE);
        } else {
            replayTextView.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        dialogs.dismiss();
        DismissControlViewTimerTask.finish();
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        JZAudioManager.getInstance(this).abandonAudioFocus();
        dialogs.dismiss();
        textureViewContainer.removeView(JZMediaManager.textureView);
        ProgressTimerTask.finish();
        DismissControlViewTimerTask.finish();
        ClarityComponent clarityComponent = loader.getUIComponent(ClarityComponent.class);
        clarityComponent.onDismissControlView();
    }

    public void dismissRegisteredComponents() {
        for (JZCoreComponent component : loader.getAllRegisteredComponents()) {
            component.onDismissControlView();
        }
    }

    @Override
    public void startVideo() {
        JZVideoPlayerManager.completeAll();
        Log.d(TAG, "startVideo [" + this.hashCode() + "] ");
        textureViewContainer.initTextureView();
        JZAudioManager.getInstance(this).requestAudioFocus();
        super.startVideo();
    }

    @Override
    public void playOnThisJzvd() {
        super.playOnThisJzvd();
        onQuitFullscreenOrTinyWindow();
        textureViewContainer.addTextureView();
    }

    @Override
    public void startWindowFullscreen() {
        textureViewContainer.removeView(JZMediaManager.textureView);
        textureViewContainer.addTextureView();
        super.startWindowFullscreen();
    }

    @Override
    public void startWindowTiny() {
        textureViewContainer.removeView(JZMediaManager.textureView);
        super.startWindowTiny();
    }

    @Override
    public void onQuitFullscreenOrTinyWindow() {
        super.onQuitFullscreenOrTinyWindow();
        textureViewContainer.clearFloatScreen();
    }

    @Override
    public void onVideoSizeChanged() {
        textureViewContainer.onVideoSizeChanged();
    }

    public void setVideoImageDisplayType(int type) {
        textureViewContainer.setVideoImageDisplayType(type);
    }

    public void rotateTo(int degrees) {
        textureViewContainer.rotateTo(degrees);
    }
}
