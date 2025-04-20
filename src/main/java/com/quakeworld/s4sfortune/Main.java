package com.quakeworld.s4sfortune;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.BanList.Type;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Main extends JavaPlugin implements Listener
{
    // one in n
    final int SPECIAL_PROBABILITY = 20;
    
    // List to store adverts loaded from config
    private List<String> adverts;
    private List<Fortune> regularFortunes = new ArrayList<>();

    private HashMap<String, Date> nextRolls = new HashMap<String, Date>();

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        loadAdvertsFromConfig();
        loadRegularFortunesFromConfig();
        getServer().getPluginManager().registerEvents(this, this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                TextComponent advertprefix = new TextComponent("⭐ " + pickRandomFromList(adverts) + " ");
                advertprefix.setColor(ChatColor.of("#0dbd8b"));
                getServer().spigot().broadcast(advertprefix); // advertlink, advertspace,advertlink2
            }
        }
        , 0L
        , 20L * 60 * 60 // 60 minutes
        );
    }
    
    /**
     * Loads adverts from config.yml
     */
    private void loadAdvertsFromConfig() {
        adverts = new ArrayList<>();
        List<String> configAdverts = getConfig().getStringList("adverts");
        
        for (String advert : configAdverts) {
            // Replace "future year" with current year for HoloEN3 entry
            if (advert.contains("future year")) {
                advert = advert.replace("future year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            }
            adverts.add(advert);
        }
        
        getLogger().info("Loaded " + adverts.size() + " adverts from config");
    }
    
    /**
     * Loads regular fortunes from config.yml
     */
    private void loadRegularFortunesFromConfig() {
        regularFortunes.clear();
        
        regularFortunes.add(new Fortune("#a205df", "Very Bad Luck", (player) -> {
            player.addPotionEffect((new PotionEffect(PotionEffectType.UNLUCK, 2 * 60 * 20, 0)));
            int s = new Random().nextInt(10); 
            Location loco = player.getLocation();
            switch (s) {
                case 0:
                    Location centerOfBlock = loco.add(0, 0.5, 0);
                    loco.getWorld().dropItemNaturally(centerOfBlock, new ItemStack(Material.COBBLESTONE, 920));
                    break;
                case 1:
                    player.addPotionEffect((new PotionEffect(PotionEffectType.POISON, 25 * 20, 0)));
                    break;
                case 2:
                    player.teleport(loco.add(0, 164, 0));
                    break;
                case 3:
                    player.addPotionEffect((new PotionEffect(PotionEffectType.WITHER, 25 * 20, 0)));
                    break;
                case 4:
                    player.setHealth(2);
                    break;
                default:
                    player.setFireTicks(200);
                    break;
            }
        }));
        
        regularFortunes.add(new Fortune("#7fec11", "Bad Luck", (player) -> {
            player.addPotionEffect((new PotionEffect(PotionEffectType.UNLUCK, 2 * 60 * 20, 0)));
            double k = Math.random();
            if (k >= 0.5){
                player.addPotionEffect((new PotionEffect(PotionEffectType.BLINDNESS,25*20 , 0)));
            }else{
                player.addPotionEffect((new PotionEffect(PotionEffectType.SLOW,2 * 60 * 20 , 0)));
            }
        }));
        
        // Load regular fortunes from config
        List<Map<?, ?>> configFortunes = getConfig().getMapList("regularFortunes");
        
        for (Map<?, ?> fortuneMap : configFortunes) {
            String color = (String) fortuneMap.get("color");
            String text = (String) fortuneMap.get("text");
            regularFortunes.add(new Fortune(color, text));
        }
        
        getLogger().info("Loaded " + regularFortunes.size() + " fortunes (including " + configFortunes.size() + " from config)");
    }

    private void announceRoll(Fortune fortune, Player roller) {
        TextComponent toRoller = new TextComponent("Your fortune: " + fortune.fortune);
        toRoller.setBold(true);
        toRoller.setColor(ChatColor.of(fortune.color));
        
        TextComponent toEveryoneElse = new TextComponent(roller.getName() + "'s fortune: " + fortune.fortune);
        toEveryoneElse.setBold(true);
        toEveryoneElse.setColor(ChatColor.of(fortune.color));

        for (Player player : getServer().getOnlinePlayers()) {
            if (player.equals(roller)) {
                player.spigot().sendMessage(toRoller);
            } else {
                player.spigot().sendMessage(toEveryoneElse);
            }
        }

        fortune.sideEffect.run(roller);
        getLogger().info(roller.getName() + "rolled " + fortune.fortune);
    }

    private <T> T pickRandomFromList(T[] list) {
        return list[new Random().nextInt(list.length)];
    }

    private <T> T pickRandomFromList(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    // FIXME break out into separate plugin
    @EventHandler
    public void AsyncChatEvent(AsyncPlayerChatEvent e) {
        urlHandler(e.getMessage(), e.getRecipients());
        if (e.getMessage().startsWith(">")) {
            e.setMessage(ChatColor.GREEN + e.getMessage());
        }
    }


    @EventHandler
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        String[] commandNouns = e.getMessage().split(" ");

        try {
            if (commandNouns[0].equals("/msg") || commandNouns[0].equals("/tell") || commandNouns[0].equals("/w") ) {
                Player recipient = getServer().getPlayerExact(commandNouns[1]);

                if (recipient != null) {
                    String[] actualMessageNouns = Arrays.copyOfRange(commandNouns, 2, commandNouns.length);
                    String actualMessage = String.join(" ", actualMessageNouns);

                    getLogger().info(actualMessage);

                    urlHandler(actualMessage, new HashSet<Player>(Arrays.asList(recipient)));
                }
            }

            if (commandNouns[0].equals("/funnyban") && e.getPlayer().isOp())  {
                //Player recipient = getServer().getPlayerExact(commandNouns[1]);
                //if (recipient != null) { 
                String recipient = commandNouns[1];
                if (true) {
                    String banReason;
                    if (commandNouns.length == 2) {
                        banReason = "USER WAS BANNED FOR THIS PEKO";
                    } else {
                        String[] actualMessageNouns = Arrays.copyOfRange(commandNouns, 2, commandNouns.length);
                        banReason = String.join(" ", actualMessageNouns);
                    }

                    TextComponent component = new TextComponent("Banned " + recipient + " ");
                    component.setColor(ChatColor.RED);

                    TextComponent reason = new TextComponent("(" + banReason + ")");
                    reason.setColor(ChatColor.of("#FF0000"));
                    reason.setBold(true);

                    getServer().spigot().broadcast(component, reason);
                }
            }

            if (commandNouns[0].equals("/rrolltest") && e.getPlayer().isOp())  {
                announceRoll(regularFortunes.get(Integer.parseInt(commandNouns[1])), e.getPlayer());
            }

            if (commandNouns[0].equals("/srolltest") && e.getPlayer().isOp())  {
                announceRoll(SpecialFortunes.fortunes[Integer.parseInt(commandNouns[1])], e.getPlayer());
            }

            if (commandNouns[0].equals("/unfortunate") && e.getPlayer().isOp()) {

                String playerName = getServer().getOfflinePlayer(commandNouns[1]).getName();
                if (Bukkit.getBanList(Type.NAME).isBanned(playerName)) {
                    Bukkit.getBanList(Type.NAME).pardon(playerName);
                    TextComponent pardonMessage = new TextComponent(playerName+"'s bad luck has been PARDONED!");
                    pardonMessage.setColor(ChatColor.of("#00FF00"));
                    pardonMessage.setBold(true);
                    getServer().spigot().broadcast(pardonMessage);
                }
                else {
                    e.setMessage(playerName + "is not banned or doesn't exist");
                }


            }

            
        } catch (IndexOutOfBoundsException excpt) {
            
        }
    }

    public void urlHandler(String message, Set<Player> recipients) {
        String[] nouns = message.split(" ");
       
        for (String noun : nouns) {
            try {
                URL url = new URL(noun);

                TextComponent component = new TextComponent("[Go To " + url.getHost() + "]");
                component.setUnderlined(true);
                component.setBold(true);
                component.setColor(ChatColor.of("#00FF00"));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, noun));

                for (Player receiver : recipients) {
                    getServer().getScheduler().runTask(this, new Runnable() {

                        @Override
                        public void run() {
                            receiver.spigot().sendMessage(component);
                        }
                    });
                }
            } catch (MalformedURLException excpt) {
                continue;
            }
        }
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
                // normal roll
                announceRoll(pickRandomFromList(regularFortunes), (Player) sender);
            }

            // debounce
            Date nextAllowedRollTime = new Date(System.currentTimeMillis() + 30*1000);
            this.nextRolls.put(sender.getName(), nextAllowedRollTime);
        }

        if (command.getName().equalsIgnoreCase("checkRRolls") && sender.isOp()) {
            StringBuilder RfoutuneNames = new StringBuilder();
            int i = 0;
            for (Fortune rf : regularFortunes) {
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

        return true;
    }
}
