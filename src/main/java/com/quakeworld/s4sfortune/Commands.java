package com.quakeworld.s4sfortune;

import org.bukkit.Bukkit;
import org.bukkit.BanList.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Commands implements CommandExecutor {
    private final Main plugin;
    private HashMap<String, Date> nextRolls;
    private List<Fortune> regularFortunes;
    final int SPECIAL_PROBABILITY = 20;
    
    public Commands(Main plugin, HashMap<String, Date> nextRolls, List<Fortune> regularFortunes) {
        this.plugin = plugin;
        this.nextRolls = nextRolls;
        this.regularFortunes = regularFortunes;
    }
    
    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        // allowed to roll?
        Date allowedTime = nextRolls.get(sender.getName());

        if (command.getName().equalsIgnoreCase("roll")) {
            if (allowedTime != null) {
                if (allowedTime.after(new Date(System.currentTimeMillis()))) {
                    int timeLeft = (int) ((allowedTime.getTime() - System.currentTimeMillis()) / 1000);
                    sender.sendMessage("You can roll again in " + timeLeft + " seconds.");
                    return true;
                }
            }
            int special = new Random().nextInt(SPECIAL_PROBABILITY); 

            if (special == 1) {
                // do a special roll
                announceRoll(pickRandomFromList(SpecialFortunes.fortunes), (Player) sender);
            } else {
                // normal roll - use RegularFortunes class to get the current list
                announceRoll(pickRandomFromList(RegularFortunes.getRegularFortunes()), (Player) sender);
            }

            // debounce
            Date nextAllowedRollTime = new Date(System.currentTimeMillis() + 30*1000);
            this.nextRolls.put(sender.getName(), nextAllowedRollTime);
        }

        if (command.getName().equalsIgnoreCase("checkRRolls") && sender.isOp()) {
            StringBuilder RfoutuneNames = new StringBuilder();
            int i = 0;
            // Use RegularFortunes to get the current list
            for (Fortune rf : RegularFortunes.getRegularFortunes()) {
                RfoutuneNames.append("|§n§3").append(i).append("§r-").append(rf.getFortune());
                ++i;
            }
            sender.sendMessage(RfoutuneNames.toString());
        }

        if (command.getName().equalsIgnoreCase("checkSRolls") && sender.isOp()) {
            StringBuilder foutuneNames = new StringBuilder();
            int j = 0;
            for (Fortune f : SpecialFortunes.fortunes) {
                foutuneNames.append("|§n§3").append(j).append("§r-").append(f.getFortune());
                ++j;
            }
            sender.sendMessage(foutuneNames.toString());
        }
        
        if (command.getName().equalsIgnoreCase("funnyban") && sender.isOp()) {
            if (args.length < 1) {
                sender.sendMessage("Usage: /funnyban <player> [reason]");
                return true;
            }
            
            String recipient = args[0];
            String banReason;
            
            if (args.length == 1) {
                banReason = "USER WAS BANNED FOR THIS PEKO";
            } else {
                String[] actualMessageNouns = Arrays.copyOfRange(args, 1, args.length);
                banReason = String.join(" ", actualMessageNouns);
            }

            TextComponent component = new TextComponent("Banned " + recipient + " ");
            component.setColor(ChatColor.RED);

            TextComponent reason = new TextComponent("(" + banReason + ")");
            reason.setColor(ChatColor.of("#FF0000"));
            reason.setBold(true);

            plugin.getServer().spigot().broadcast(component, reason);
            return true;
        }

        if (command.getName().equalsIgnoreCase("rrolltest") && sender.isOp()) {
            if (args.length < 1) {
                sender.sendMessage("Usage: /rrolltest <index>");
                return true;
            }
            try {
                int index = Integer.parseInt(args[0]);
                // Use RegularFortunes to get the current list
                List<Fortune> fortunes = RegularFortunes.getRegularFortunes();
                if (index >= 0 && index < fortunes.size()) {
                    announceRoll(fortunes.get(index), (Player) sender);
                } else {
                    sender.sendMessage("Index out of bounds. Use /checkRRolls to see available indices.");
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Please provide a valid number.");
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("srolltest") && sender.isOp()) {
            if (args.length < 1) {
                sender.sendMessage("Usage: /srolltest <index>");
                return true;
            }
            try {
                int index = Integer.parseInt(args[0]);
                if (index >= 0 && index < SpecialFortunes.fortunes.length) {
                    announceRoll(SpecialFortunes.fortunes[index], (Player) sender);
                } else {
                    sender.sendMessage("Index out of bounds. Use /checkSRolls to see available indices.");
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Please provide a valid number.");
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("unfortunate") && sender.isOp()) {
            if (args.length < 1) {
                sender.sendMessage("Usage: /unfortunate <player>");
                return true;
            }
            
            String playerName = plugin.getServer().getOfflinePlayer(args[0]).getName();
            if (Bukkit.getBanList(Type.NAME).isBanned(playerName)) {
                Bukkit.getBanList(Type.NAME).pardon(playerName);
                TextComponent pardonMessage = new TextComponent(playerName+"'s bad luck has been PARDONED!");
                pardonMessage.setColor(ChatColor.of("#00FF00"));
                pardonMessage.setBold(true);
                plugin.getServer().spigot().broadcast(pardonMessage);
            }
            else {
                sender.sendMessage(playerName + " is not banned or doesn't exist");
            }
            return true;
        }

        return true;
    }
    
    private void announceRoll(Fortune fortune, Player roller) {
        TextComponent toRoller = new TextComponent("Your fortune: " + fortune.fortune);
        toRoller.setBold(true);
        toRoller.setColor(ChatColor.of(fortune.color));
        
        TextComponent toEveryoneElse = new TextComponent(roller.getName() + "'s fortune: " + fortune.fortune);
        toEveryoneElse.setBold(true);
        toEveryoneElse.setColor(ChatColor.of(fortune.color));

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.equals(roller)) {
                player.spigot().sendMessage(toRoller);
            } else {
                player.spigot().sendMessage(toEveryoneElse);
            }
        }

        fortune.sideEffect.run(roller);
        plugin.getLogger().info(roller.getName() + "rolled " + fortune.fortune);
    }
    
    private <T> T pickRandomFromList(T[] list) {
        return list[new Random().nextInt(list.length)];
    }

    private <T> T pickRandomFromList(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }
}