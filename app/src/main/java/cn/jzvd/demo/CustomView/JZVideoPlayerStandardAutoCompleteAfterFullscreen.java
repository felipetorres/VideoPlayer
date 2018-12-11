package cn.jzvd.demo.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;

import cn.jzvd.JZAudioManager;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUtils;

/**
 * 全屏状态播放完成，不退出全屏
 * Created by Nathen on 2016/11/26.
 */
public class JZVideoPlayerStandardAutoCompleteAfterFullscreen extends JZVideoPlayerStandardGlide {
    public JZVideoPlayerStandardAutoCompleteAfterFullscreen(Context context) {
        super(context);
    }

    public JZVideoPlayerStandardAutoCompleteAfterFullscreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void startVideo() {
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            Log.d(TAG, "startVideo [" + this.hashCode() + "] ");
            textureViewContainer.initTextureView();
            textureViewContainer.addTextureView();
            JZAudioManager.getInstance(this).requestAudioFocus();
            JZUtils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            JZMediaManager.setDataSource(dataSource);
            JZMediaManager.setCurrentPath(dataSource.getCurrentPath(currentUrlMapIndex));
            JZMediaManager.instance().positionInList = positionInList;
            onStatePreparing();
        } else {
            super.startVideo();
        }
    }

    @Override
    public void onAutoCompletion() {
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            getStateMachine().setAutoComplete();
        } else {
            super.onAutoCompletion();
        }

    }
}
