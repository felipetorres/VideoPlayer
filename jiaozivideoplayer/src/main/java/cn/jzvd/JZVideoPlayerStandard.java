package cn.jzvd;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import cn.jzvd.plugin.BottomContainer;
import cn.jzvd.plugin.BottomProgressPlugin;
import cn.jzvd.plugin.ClarityPlugin;
import cn.jzvd.plugin.JZCorePlugin;
import cn.jzvd.plugin.JZUiPlugin;
import cn.jzvd.plugin.JZUiControlPlugin;
import cn.jzvd.plugin.ProgressPlugin;
import cn.jzvd.plugin.TextureViewContainer;
import cn.jzvd.dialog.JZDialogs;
import cn.jzvd.task.DismissControlViewTimerTask;
import cn.jzvd.task.ProgressTimerTask;

/**
 * Created by Nathen
 * On 2016/04/18 16:15
 */
public class JZVideoPlayerStandard extends JZVideoPlayer {

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

        super.loader.registerUiPlugins(this);
    }

    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        super.setUp(dataSource, defaultUrlMapIndex, screen, objects);

        for (JZCorePlugin plugin : loader.getAllRegisteredPlugins()) {
            plugin.setUp(dataSource, defaultUrlMapIndex, screen, objects);
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
        for (JZUiControlPlugin plugin : loader.getRegisteredControlPlugins()) {
            plugin.onPreparingChangingUrl();
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

    public void onClickUiToggle() {
        for (JZUiPlugin plugin : loader.getRegisteredUiPlugins()) {
            plugin.onClickUiToggle();
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
        BottomContainer bottomContainer = loader.getControlPlugin(BottomContainer.class);
        return bottomContainer.getVisibility();
    }

    public void setProgress(int progress) {
        ProgressPlugin progressPlugin = loader.getControlPlugin(ProgressPlugin.class);
        progressPlugin.setProgress(progress);
    }

    @Override
    public void setProgressAndText() {
        long position = getCurrentPositionWhenPlaying();
        long duration = getDuration();
        int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));

        ProgressPlugin progressPlugin = loader.getControlPlugin(ProgressPlugin.class);
        progressPlugin.setProgressAndText(mTouchingProgressBar, progress, position, duration);

        BottomProgressPlugin bottomProgressPlugin = loader.getControlPlugin(BottomProgressPlugin.class);
        bottomProgressPlugin.setProgress(progress);
    }

    @Override
    public void setBufferProgress(int bufferProgress) {
        ProgressPlugin progressPlugin = loader.getControlPlugin(ProgressPlugin.class);
        BottomProgressPlugin bottomProgressPlugin = loader.getControlPlugin(BottomProgressPlugin.class);
        progressPlugin.setBufferProgress(bufferProgress);
        bottomProgressPlugin.setBufferProgress(bufferProgress);
    }

    private void changeUiToNormal() {
        for (JZUiControlPlugin plugin : loader.getRegisteredControlPlugins()) {
            plugin.onNormal(currentScreen);
        }
    }

    public void changeUiToPreparing() {
        for (JZUiControlPlugin plugin : loader.getRegisteredControlPlugins()) {
            plugin.onPreparing(currentScreen);
        }
    }

    private void changeUiToPlayingShow() {
        for (JZUiControlPlugin plugin : loader.getRegisteredControlPlugins()) {
            plugin.onPlayingShow(currentScreen);
        }
    }

    public void changeUiToPlayingClear() {
        for (JZUiControlPlugin plugin : loader.getRegisteredControlPlugins()) {
            plugin.onPlayingClear(currentScreen);
        }
    }

    private void changeUiToPauseShow() {
        for (JZUiControlPlugin plugin : loader.getRegisteredControlPlugins()) {
            plugin.onPauseShow(currentScreen);
        }
    }

    public void changeUiToPauseClear() {
        for (JZUiControlPlugin plugin : loader.getRegisteredControlPlugins()) {
            plugin.onPauseClear(currentScreen);
        }
    }

    public void changeUiToComplete() {
        for (JZUiControlPlugin plugin : loader.getRegisteredControlPlugins()) {
            plugin.onComplete(currentScreen);
        }
    }

    private void changeUiToError() {
        for (JZUiControlPlugin plugin : loader.getRegisteredControlPlugins()) {
            plugin.onError(currentScreen);
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
        ClarityPlugin clarityPlugin = loader.getUiPluginNamed(ClarityPlugin.class);
        clarityPlugin.onDismissControlView();
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
