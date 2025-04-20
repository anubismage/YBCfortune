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
    private Commands commandExecutor;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        loadAdvertsFromConfig();
        
        // Load fortunes from RegularFortunes class
        regularFortunes = RegularFortunes.loadRegularFortunesFromConfig(this);
        
        // Register command executor
        commandExecutor = new Commands(this, nextRolls, regularFortunes);
        getCommand("roll").setExecutor(commandExecutor);
        getCommand("checkRRolls").setExecutor(commandExecutor);
        getCommand("checkSRolls").setExecutor(commandExecutor);
        getCommand("funnyban").setExecutor(commandExecutor);
        getCommand("rrolltest").setExecutor(commandExecutor);
        getCommand("srolltest").setExecutor(commandExecutor);
        getCommand("unfortunate").setExecutor(commandExecutor);
        
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
}
