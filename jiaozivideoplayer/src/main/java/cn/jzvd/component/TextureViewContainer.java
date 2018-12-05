package cn.jzvd.component;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import cn.jzvd.JZActionBarManager;
import cn.jzvd.task.DismissControlViewTimerTask;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZResizeTextureView;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.task.ProgressTimerTask;
import cn.jzvd.R;
import cn.jzvd.dialog.JZDialogs;

import static cn.jzvd.JZVideoPlayer.NORMAL_ORIENTATION;
import static cn.jzvd.JZVideoPlayer.TAG;

public class TextureViewContainer extends JZComponent implements View.OnClickListener, View.OnTouchListener {

    private final JZDialogs dialogs;
    private final ViewGroup view;
    private int degrees = 0;

    public TextureViewContainer(JZVideoPlayerStandard player, JZDialogs dialogs) {
        super(player);
        this.dialogs = dialogs;

        this.view = player.findViewById(R.id.surface_container);
        this.view.setOnClickListener(this);
        this.view.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
        DismissControlViewTimerTask.start(getPlayer());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();

        if (id == R.id.surface_container) {
            dialogs.onTouch(event);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(TAG, "onTouch surfaceContainer actionDown [" + this.hashCode() + "] ");
                    getPlayer().mTouchingProgressBar = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(TAG, "onTouch surfaceContainer actionMove [" + this.hashCode() + "] ");
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(TAG, "onTouch surfaceContainer actionUp [" + this.hashCode() + "] ");
                    getPlayer().mTouchingProgressBar = false;

                    ProgressTimerTask.start(getPlayer());
                    DismissControlViewTimerTask.start(getPlayer());
                    break;
            }
        }
        return false;
    }

    public void removeView(JZResizeTextureView textureView) {
        this.view.removeView(textureView);
    }

    private void removeTextureView() {
        JZMediaManager.savedSurfaceTexture = null;
        if (JZMediaManager.textureView != null && JZMediaManager.textureView.getParent() != null) {
            ((ViewGroup) JZMediaManager.textureView.getParent()).removeView(JZMediaManager.textureView);
        }
    }

    public void initTextureView() {
        removeTextureView();
        JZMediaManager.textureView = new JZResizeTextureView(getPlayer().getContext());
        JZMediaManager.textureView.setSurfaceTextureListener(JZMediaManager.instance());
        addTextureView();
    }

    public void addTextureView() {
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ");
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        this.view.addView(JZMediaManager.textureView, layoutParams);
    }

    public void clearFloatScreen() {
        Context context = getPlayer().getContext();

        JZUtils.setRequestedOrientation(context, NORMAL_ORIENTATION);
        JZActionBarManager.showSupportActionBar(context);
        ViewGroup vp = (JZUtils.scanForActivity(context))//.getWindow().getDecorView();
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
    }

    public void onVideoSizeChanged() {
        Log.i(TAG, "onVideoSizeChanged " + " [" + this.hashCode() + "] ");
        if (JZMediaManager.textureView != null) {
            if (degrees != 0) {
                JZMediaManager.textureView.setRotation(degrees);
            }
            JZMediaManager instance = JZMediaManager.instance();
            JZMediaManager.textureView.setVideoSize(instance.currentVideoWidth, instance.currentVideoHeight);
        }
    }


    public void rotateTo(int degrees) {
        this.degrees = degrees;
        if (JZMediaManager.textureView != null) {
            JZMediaManager.textureView.setRotation(degrees);
        }
    }

    public void setVideoImageDisplayType(int type) {
        if (JZMediaManager.textureView != null) {
            JZMediaManager.textureView.setResizeTextureViewType(type);
            JZMediaManager.textureView.requestLayout();
        }
    }
}
