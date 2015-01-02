package net.hmksq.dhw;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class DeprecatedHostWarning extends Plugin implements Listener {

    Configuration config = null;

    @Override
    public void onEnable() {
        getLogger().info("DeprecatedHostWarning v" + getDescription().getVersion() + " has been enabled!");

        getProxy().getPluginManager().registerListener(this, this);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            getLogger().info("Config does not exist, creating config file!");
            try {
                Files.copy(getResourceAsStream("config.yml"), file.toPath());
            } catch (IOException e) {
                getLogger().warning("Failed to create config file!");
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().warning("Error loading config!");
        }
    }


    @EventHandler
    public void onLogin(PostLoginEvent event) {
        String loginHost = event.getPlayer().getPendingConnection().getVirtualHost().getHostString();

        List<String> list = config.getStringList("OldHosts");

        for (String configHost : list) {
            if (configHost.equalsIgnoreCase(loginHost)) {
                for (String newline : config.getStringList("OldHostMessage")) {
                    event.getPlayer().sendMessage(new ComponentBuilder(newline.replace('&', '§')).create());
                }
            }
        }
    }
}