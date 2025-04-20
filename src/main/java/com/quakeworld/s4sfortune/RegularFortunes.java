package com.quakeworld.s4sfortune;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RegularFortunes {
    private static List<Fortune> regularFortunes = new ArrayList<>();
    
    /**
     * Loads regular fortunes from config and hardcoded special cases
     */
    public static List<Fortune> loadRegularFortunesFromConfig(Main plugin) {
        regularFortunes.clear();
        
        // Add hardcoded bad luck fortunes
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
                player.addPotionEffect((new PotionEffect(PotionEffectType.BLINDNESS, 25*20, 0)));
            } else {
                player.addPotionEffect((new PotionEffect(PotionEffectType.SLOW, 2 * 60 * 20, 0)));
            }
        }));
        
        // Load regular fortunes from config
        List<Map<?, ?>> configFortunes = plugin.getConfig().getMapList("regularFortunes");
        
        for (Map<?, ?> fortuneMap : configFortunes) {
            String color = (String) fortuneMap.get("color");
            String text = (String) fortuneMap.get("text");
            regularFortunes.add(new Fortune(color, text));
        }
        
        plugin.getLogger().info("Loaded " + regularFortunes.size() + " fortunes (including " + configFortunes.size() + " from config)");
        
        return regularFortunes;
    }
    
    /**
     * Returns the list of regular fortunes
     */
    public static List<Fortune> getRegularFortunes() {
        return regularFortunes;
    }
}