package com.pandapulsestudios.pulsecommands;

import com.pandapulsestudios.api.Interface.CustomPlayerMethodNMS;
import com.pandapulsestudios.pulsecore.ChatAPI.Object.ChatBuilderAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public final class PulseCommands extends JavaPlugin {
    public static PulseCommands PulseCommands;
    public static Class<?> customPlayerMethodNMS;

    @Override
    public void onEnable() {
        PulseCommands = this;
        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf(".") + 1).toLowerCase();
        try{
            final Class<?> clazz = Class.forName("com.pandapulsestudios." + version + ".Objects.CustomPlayerMethod");
            if (CustomPlayerMethodNMS.class.isAssignableFrom(clazz)) customPlayerMethodNMS = clazz;
        } catch (ClassNotFoundException e) {
            ChatBuilderAPI.chatBuilder().SendMessage("com.pandapulsestudios." + version + ".Objects.CustomPlayerMethod", true);
            throw new RuntimeException(e);
        }
        this.getLogger().info("Loading support for " + version);
    }
}
