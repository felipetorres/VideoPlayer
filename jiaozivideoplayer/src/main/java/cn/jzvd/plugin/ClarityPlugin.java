package cn.jzvd.plugin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.jzvd.JZDataSource;
import cn.jzvd.R;
import cn.jzvd.ui.ContainerLocation;
import cn.jzvd.ui.PluginLocation;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;

public class ClarityPlugin extends JZUiPlugin {

    private TextView clarity;
    private PopupWindow clarityPopWindow;

    public ClarityPlugin() {
        super.container = ContainerLocation.BOTTOM;
        super.location = PluginLocation.RIGHT;
        super.orderIfSameLocation = 0;
    }

    @Override
    public String getName() {
        return ClarityPlugin.class.getSimpleName();
    }

    @Override
    public void init(ViewGroup parent) {
        super.init(parent);

        clarity = parent.findViewById(R.id.clarity);
        clarity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClarityPlugin.this.onClick();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.plugin_clarity;
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if (player.currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            if (dataSource.getMap().size() == 1) {
                clarity.setVisibility(View.GONE);
            } else {
                clarity.setText(dataSource.getKey(player.currentUrlMapIndex));
                clarity.setVisibility(View.VISIBLE);
            }
        } else {
            clarity.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClickUiToggle() {
        if (player.getBottomContainerVisibility() != View.VISIBLE) {
            clarity.setText(player.dataSource.getKey(player.currentUrlMapIndex));
        }
    }

    @Override
    public void onDismissControlView() {
        if (clarityPopWindow != null) {
            clarityPopWindow.dismiss();
        }
    }

    private void onClick() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.jz_layout_clarity, null);

        View.OnClickListener mQualityListener = new View.OnClickListener() {
            public void onClick(View v) {
                int index = (int) v.getTag();
                player.getStateMachine().setPreparingChangingUrl(index, player.getCurrentPositionWhenPlaying());
                clarity.setText(player.dataSource.getKey(player.currentUrlMapIndex));
                for (int j = 0; j < layout.getChildCount(); j++) {//设置点击之后的颜色
                    if (j == player.currentUrlMapIndex) {
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

        for (int j = 0; j < player.dataSource.getMap().size(); j++) {
            String key = player.dataSource.getKey(j);
            TextView clarityItem = (TextView) View.inflate(context, R.layout.jz_layout_clarity_item, null);
            clarityItem.setText(key);
            clarityItem.setTag(j);
            layout.addView(clarityItem, j);
            clarityItem.setOnClickListener(mQualityListener);
            if (j == player.currentUrlMapIndex) {
                clarityItem.setTextColor(Color.parseColor("#fff85959"));
            }
        }

        clarityPopWindow = new PopupWindow(layout, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        clarityPopWindow.setContentView(layout);
        clarityPopWindow.showAsDropDown(clarity);
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int offsetX = clarity.getMeasuredWidth() / 3;
        int offsetY = clarity.getMeasuredHeight() / 3;
        clarityPopWindow.update(clarity, -offsetX, -offsetY, Math.round(layout.getMeasuredWidth() * 2), layout.getMeasuredHeight());
    }
}
