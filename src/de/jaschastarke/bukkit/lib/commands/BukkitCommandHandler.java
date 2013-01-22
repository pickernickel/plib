package de.jaschastarke.bukkit.lib.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;

import de.jaschastarke.bukkit.lib.Core;
import de.jaschastarke.bukkit.lib.chat.ConsoleFormatter;
import de.jaschastarke.bukkit.lib.chat.IFormatter;
import de.jaschastarke.bukkit.lib.chat.InGameFormatter;

public class BukkitCommandHandler implements CommandExecutor, ICommandListing {
    protected Core plugin;
    protected Map<Command, ICommand> commands = new HashMap<Command, ICommand>();
    public BukkitCommandHandler(Core plugin) {
        this.plugin = plugin;
    }
    
    public void registerCommand(ICommand cmd) {
        PluginCommand bcmd = plugin.getCommand(cmd.getName());
        if (bcmd == null)
            throw new IllegalArgumentException("Command "+cmd.getName()+" isn't registered for this plugin. Check plugin.yml");
        commands.put(bcmd, cmd);
        if (cmd instanceof BukkitCommand)
            ((BukkitCommand) cmd).setBukkitCommand(bcmd);
        bcmd.setExecutor(this);
    }
    public void removeCommand(ICommand cmd) {
        PluginCommand bcmd = plugin.getCommand(cmd.getName());
        if (bcmd != null) {
            commands.remove(bcmd);
            bcmd.setExecutor(null);
        }
    }
    public void removeCommand(String cmd) {
        PluginCommand bcmd = plugin.getCommand(cmd);
        if (bcmd != null) {
            commands.remove(bcmd);
            bcmd.setExecutor(null);
        }
    }
    @Override
    public Collection<ICommand> getCommandList() {
        return commands.values();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ICommand cmd = commands.get(command);
        if (cmd == null)
            throw new IllegalArgumentException("Not to handler registered command fired: "+command.getName());
        CommandContext context = createContext(sender);
        try {
            context.addHandledCommand(cmd, label);
            return cmd.execute(context, args);
        } catch (MissingPermissionCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return true;
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return true;
        }
    }
    
    public CommandContext createContext(CommandSender sender) {
        CommandContext context = new CommandContext(sender);
        context.setPlugin(plugin);
        context.setPermissinManager(plugin.getPermManager());
        context.setFormatter(getFormatter(sender));
        
        return context;
    }
    
    private Map<Object, IFormatter> formatter = new HashMap<Object, IFormatter>();
    protected IFormatter getFormatter(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            if (!formatter.containsKey(ConsoleCommandSender.class))
                formatter.put(ConsoleCommandSender.class, new ConsoleFormatter(plugin.getLang()));
            return formatter.get(ConsoleCommandSender.class);
        } else {
            if (!formatter.containsKey(null))
                formatter.put(null, new InGameFormatter(plugin.getLang()));
            return formatter.get(null);
        }
    }
}