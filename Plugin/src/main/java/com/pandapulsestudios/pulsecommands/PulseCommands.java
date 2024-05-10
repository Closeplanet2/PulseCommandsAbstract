package com.pandapulsestudios.pulsecommands;

import com.pandapulsestudios.api.Interface.CustomPlayerMethodNMS;
import com.pandapulsestudios.pulsecore.ChatAPI.Object.ChatBuilderAPI;
import com.pandapulsestudios.pulsecore.JavaAPI.API.JavaAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
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
            ChatBuilderAPI.chatBuilder().SendMessage(ChatColor.RED + "Loading support for " + version, true);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void RegisterRaw(JavaPlugin javaPlugin) {
        try {
            Register(javaPlugin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void Register(JavaPlugin javaPlugin) throws Exception {
        for(var autoRegisterClass : JavaAPI.ReturnAllAutoRegisterClasses(javaPlugin)){
            if(PlayerCommand.class.isAssignableFrom(autoRegisterClass)) RegisterCommand(autoRegisterClass, javaPlugin);
        }
    }

    public static void RegisterCommand(Class<?> clazz, JavaPlugin plugin) throws Exception{
        var commandMap = (CommandMap) getField(Bukkit.getServer().getClass(), "commandMap").get(Bukkit.getServer());
        var commandClass = (Class<? extends PlayerCommand>) clazz;
        PlayerCommand command;
        try {
            command = commandClass.getConstructor(plugin.getClass()).newInstance(plugin);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            try {
                command = commandClass.getConstructor(JavaPlugin.class).newInstance(plugin);
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                try {
                    command = commandClass.getConstructor().newInstance();
                } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex2) {
                    ex2.printStackTrace();
                    System.err.println("Error instantiating " + commandClass.getName() + ": " + ex2.getMessage());
                    return;
                }
            }
        }

        commandMap.register(plugin.getName(), command);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + plugin.getDescription().getFullName() + ": Registered command " + command.getName());
    }

    private static Field getField(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}
