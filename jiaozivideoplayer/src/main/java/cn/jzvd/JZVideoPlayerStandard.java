package cn.jzvd;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import cn.jzvd.component.BottomContainer;
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
public class JZVideoPlayerStandard extends JZVideoPlayer implements View.OnClickListener {

    private JZDialogs dialogs;
    public TextureViewContainer textureViewContainer;
    public boolean mTouchingProgressBar;

    public JZVideoPlayerStandard(Context context) {
        super(context);
    }

    public JZVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);

        this.dialogs = new JZDialogs(this);
        textureViewContainer = new TextureViewContainer(this, this.dialogs);

        super.loader.registerComponents(this);
    }

    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        super.setUp(dataSource, defaultUrlMapIndex, screen, objects);

        for (JZCoreComponent component : loader.getAllRegisteredComponents()) {
            component.setUp(dataSource, defaultUrlMapIndex, screen, objects);
        }

        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            textureViewContainer.initTextureView();
        } else if (currentScreen == SCREEN_WINDOW_TINY) {
            textureViewContainer.addTextureView();
        }

        if (tmp_test_back) {
            tmp_test_back = false;
            JZVideoPlayerManager.setFirstFloor(this);
            backPress();
        }
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
        changeUiToPreparing();
    }

    @Override
    public void onStatePreparingChangingUrl(int urlMapIndex) {
        super.onStatePreparingChangingUrl(urlMapIndex);
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPreparingChangingUrl();
        }
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

        changeUiToComplete();
    }

    @Deprecated
    public void onClick(View v) {
        //It will be removed in the next commits. Click behaviour must be managed by the components themselves.
    }

    public void onClickUiToggle() {
        for (JZUIComponent component : loader.getRegisteredUIComponents()) {
            component.onClickUiToggle();
        }

        if (getStateMachine().currentStatePreparing()) {
            changeUiToPreparing();
        } else if (getStateMachine().currentStatePlaying()) {
            if (getBottomContainerVisibility() == View.VISIBLE) {
                changeUiToPlayingClear();
            } else {
                changeUiToPlayingShow();
            }
        } else if (getStateMachine().currentStatePause()) {
            if (getBottomContainerVisibility() == View.VISIBLE) {
                changeUiToPauseClear();
            } else {
                changeUiToPauseShow();
            }
        }
    }

    public int getBottomContainerVisibility() {
        BottomContainer bottomContainer = loader.getControlComponent(BottomContainer.class);
        return bottomContainer.getVisibility();
    }

    public void setProgress(int progress) {
        //TODO FELIPE
//        ProgressComponent progressComponent = loader.getControlComponent(ProgressComponent.class);
//        progressComponent.setProgress(progress);
    }

    @Override
    public void setProgressAndText() {
        long position = getCurrentPositionWhenPlaying();
        long duration = getDuration();
        int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));

        //TODO FELIPE
//        ProgressComponent progressComponent = loader.getControlComponent(ProgressComponent.class);
//        progressComponent.setProgressAndText(mTouchingProgressBar, progress, position, duration);
    }

    @Override
    public void setBufferProgress(int bufferProgress) {
        //TODO FELIPE
//        ProgressComponent progressComponent = loader.getControlComponent(ProgressComponent.class);
//        progressComponent.setBufferProgress(bufferProgress);
    }

    private void changeUiToNormal() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onNormal(currentScreen);
        }
    }

    public void changeUiToPreparing() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPreparing(currentScreen);
        }
    }

    private void changeUiToPlayingShow() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPlayingShow(currentScreen);
        }
    }

    public void changeUiToPlayingClear() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPlayingClear(currentScreen);
        }
    }

    private void changeUiToPauseShow() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPauseShow(currentScreen);
        }
    }

    public void changeUiToPauseClear() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onPauseClear(currentScreen);
        }
    }

    public void changeUiToComplete() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onComplete(currentScreen);
        }
    }

    private void changeUiToError() {
        for (JZUIControlComponent component : loader.getRegisteredControlComponents()) {
            component.onError(currentScreen);
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
