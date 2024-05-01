package com.pandapulsestudios.pulsecommands;

import com.pandapulsestudios.api.Enum.PlayerCommandError;
import com.pandapulsestudios.api.Interface.CustomPlayerMethodNMS;
import com.pandapulsestudios.api.Interface.PCMethod;
import com.pandapulsestudios.api.Interface.PCMethodData;
import com.pandapulsestudios.pulsecore.ChatAPI.API.MessageAPI;
import com.pandapulsestudios.pulsecore.ChatAPI.Enum.MessageType;
import com.pandapulsestudios.pulsecore.ChatAPI.Object.ChatBuilderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class PlayerCommand extends BukkitCommand {

    private final List<CustomPlayerMethodNMS> customPlayerMethods = new ArrayList<>();
    private final HashMap<String, Method> liveData = new HashMap<>();
    private final String commandName;
    private final boolean debugErrors;

    public PlayerCommand(JavaPlugin javaPlugin, String commandName, boolean debugErrors, String... alias) {
        super(commandName.toLowerCase());
        for(var method : this.getClass().getMethods()){
            try {
                if(method.isAnnotationPresent(PCMethod.class)) customPlayerMethods.add((CustomPlayerMethodNMS) PulseCommands.customPlayerMethodNMS.getConstructor(JavaPlugin.class, Object.class, Method.class).newInstance(javaPlugin, this, method));
                if(method.isAnnotationPresent(PCMethodData.class)) liveData.put(method.getName(), method);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        setAliases(Arrays.stream(alias).toList());
        this.commandName = commandName;
        this.debugErrors = debugErrors;
    }


    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if(!(commandSender instanceof Player)){
            if(debugErrors) ChatBuilderAPI.chatBuilder().SendMessage(PlayerCommandError.MustBePlayerTOUseCommand.error, true);
            return false;
        }

        var player = (Player) commandSender;
        for(var cm : customPlayerMethods){
            var invoke = cm.TryAndInvokeMethod(player, args);
            if(invoke == null) return true;
            else if(debugErrors) ChatBuilderAPI.chatBuilder().SendMessage(invoke.error, true);
        }

        if(debugErrors) ChatBuilderAPI.chatBuilder().SendMessage(PlayerCommandError.NoMethodOrCommandFound.error, true);
        NoMethodFound(player, s, args);
        DisplayHelpMenu(player);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String s, String[] args) throws IllegalArgumentException {
        if(!(commandSender instanceof Player)) new ArrayList<String>();
        var player = (Player) commandSender;
        var data = new ArrayList<String>();
        for(var customMethod : customPlayerMethods){
            try {data.addAll(customMethod.ReturnTabComplete(player, args));}
            catch ( Exception e) { throw new RuntimeException(e);}
        }
        return data;
    }

    public void DisplayHelpMenu(Player player){
        if(helpMenuPrefix(player).isEmpty() || helpMenuSuffix(player).isEmpty()) return;
        ChatBuilderAPI.chatBuilder().messageType(MessageType.Player).playerToo(player).SendMessage(helpMenuPrefix(player), true);
        for(var customPlayerMethod : customPlayerMethods){
            if(!customPlayerMethod.CanPlayerUseCommand(player)) return;
            var stringBuilder = new StringBuilder();
            stringBuilder.append("/").append("#7a7a7a").append(commandName).append(" ");
            for(var value : helpMenuFormat(player,  customPlayerMethod.ReturnHelpMenu()).values()) stringBuilder.append("#adacac(").append(value).append("#adacac)").append(" ");

            var textComp = new TextComponent(MessageAPI.FormatMessage(stringBuilder.toString(), true, true, player));
            textComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click To Copy!")));
            textComp.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, customPlayerMethod.ReturnClipboard("/" + commandName)));
            player.spigot().sendMessage(textComp);
        }
        ChatBuilderAPI.chatBuilder().messageType(MessageType.Player).playerToo(player).SendMessage(helpMenuSuffix(player), true);
    }

    public Method ReturnMethodByName(String methodName){
       return liveData.getOrDefault(methodName, null);
    }
    public abstract void NoMethodFound(Player player, String s, String[] args);
    public abstract String helpMenuPrefix(Player player);
    public abstract LinkedHashMap<String, String> helpMenuFormat(Player player, LinkedHashMap<String, String> params);
    public abstract String helpMenuSuffix(Player player);
}