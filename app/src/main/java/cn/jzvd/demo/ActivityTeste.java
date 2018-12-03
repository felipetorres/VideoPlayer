package cn.jzvd.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cn.jzvd.ContainerLocation;
import cn.jzvd.JZDataSource;
import cn.jzvd.Player;
import cn.jzvd.core.Core;
import cn.jzvd.plugins.PluginLocation;
import cn.jzvd.plugins.RetryPlugin;

public class ActivityTeste extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teste_activity);

        Player player = findViewById(R.id.teste_player);
        Core core = new Core(this, new JZDataSource(null));


        player.add(ContainerLocation.TOP, new RetryPlugin(PluginLocation.LEFT), new RetryPlugin(PluginLocation.LEFT));
        player.add(ContainerLocation.CENTER, new RetryPlugin(PluginLocation.CENTER));
        player.add(ContainerLocation.BOTTOM, new RetryPlugin(PluginLocation.RIGHT));
        player.render();
    }
}
