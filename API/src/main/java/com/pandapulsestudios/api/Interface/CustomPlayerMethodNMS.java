package com.pandapulsestudios.api.Interface;

import com.pandapulsestudios.api.Enum.PlayerCommandError;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface CustomPlayerMethodNMS {
    public void AcceptLiveData(HashMap<String, Method> liveData);
    public PlayerCommandError TryAndInvokeMethod(Player player, String[] player_args);
    public <T> T[] convertArray(Object[] array, Class<?> targetClass);
    public List<String> ReturnTabComplete(Player player, String[] args) throws InvocationTargetException, IllegalAccessException;
    public LinkedHashMap<String, String> ReturnHelpMenu();
    public String ReturnClipboard(String commandName);
    public boolean CanPlayerUseCommand(Player player);
}
