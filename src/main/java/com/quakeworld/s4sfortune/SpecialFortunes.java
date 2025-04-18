package com.quakeworld.s4sfortune;

import org.bukkit.*;
import org.bukkit.BanList.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public class SpecialFortunes {
    
    public static final Fortune[] fortunes = {
        new Fortune("#ff0000", "(YOU ARE BANNED)", (player) -> {
        //random lenght of time 10mins to 8hrs
        int onehr = 60*60*1000;
        int time = (Math.random() < 0.1) ? ((Math.random() < 0.1) ? (onehr) : 10 * 60 * 1000) : new Random().nextInt(1800000) + 600000;
        Date banEnd = new Date(System.currentTimeMillis() + time);
        Bukkit.getBanList(Type.NAME).addBan(player.getName(),
                org.bukkit.ChatColor.BOLD + "" + org.bukkit.ChatColor.RED + "Your fortune: (YOU ARE BANNED)" + org.bukkit.ChatColor.RESET, banEnd, "s4sfortune");
        player.kickPlayer(org.bukkit.ChatColor.BOLD + "" + org.bukkit.ChatColor.RED + "Your fortune: (YOU ARE BANNED)");
    }),
        new Fortune("#f7dc6f", "Your Shitty Luck Has Angered ZEUSCHAMA", (player) -> {
            Location loc = player.getLocation();
            loc.getWorld().strikeLightningEffect(loc);
    }),

        new Fortune("#fff451", "THEY GLOW IN THE DARK", (player) -> {
            player.addPotionEffect((new PotionEffect(PotionEffectType.GLOWING, 300*20,1)));
            player.addPotionEffect((new PotionEffect(PotionEffectType.INVISIBILITY, 300*20,0)));
    }),
        new Fortune("#396a24", "*BRAAAAAAP* You Feel a Strange Smell around you ", (player) -> {
        player.addPotionEffect((new PotionEffect(PotionEffectType.CONFUSION, 2*20,1)));
    }),
        new Fortune("#141414", "§kEND §rYou meet a dark handsome stranger §kEND", (player) -> {
            Location locc =player.getLocation();
            locc.getWorld().spawnEntity(locc.add(0,1,0), EntityType.ENDERMAN);
    }),
        new Fortune("#ffd700", "Oy Vey", (player) -> {
        Location locc =player.getLocation();
        locc.getWorld().spawnEntity(locc.add(0,2,5), EntityType.WANDERING_TRADER);
    }),
        new Fortune("#00d7ff", "Sixtee foour diamonds ebin :DD", (player) -> {
         Location loc = player.getLocation();
         Location centerOfBlock = loc.add(0.5, 0, 0.5);
         loc.getWorld().dropItemNaturally(centerOfBlock,new ItemStack(Material.DIAMOND, 64));
    }),
        new Fortune("#b48905", "Berries :D", (player) -> {
        Location loc = player.getLocation();
        Location centerOfBlock = loc.add(0, 0.5, 0);
            ItemStack item = new ItemStack(Material.SWEET_BERRIES,64);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(org.bukkit.ChatColor.RED + "Berry");
            List<String> loreList = new ArrayList<String>();
            loreList.add(org.bukkit.ChatColor.BOLD + "Berry");//This is the first line of lore
            loreList.add(org.bukkit.ChatColor.GRAY + "Its a berry");//This is the second line of lore
            im.setLore(loreList);
            item.setItemMeta(im);
        loc.getWorld().dropItemNaturally(centerOfBlock,item);
    }),
        new Fortune("#68923a", "I Yearned For The Mines", (player) -> {
        player.addPotionEffect((new PotionEffect(PotionEffectType.SLOW_DIGGING, 30*20,0)));
        Location loc = player.getLocation();
        Location centerOfBlock = loc.add(0, 0.5, 0);
            ItemStack item = new ItemStack(Material.IRON_PICKAXE,1);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(org.bukkit.ChatColor.GOLD + "Pickaxe");
            List<String> loreList = new ArrayList<String>();
            loreList.add(org.bukkit.ChatColor.BOLD + "Signed by Jack Black");
            im.setLore(loreList);
            item.setItemMeta(im);
            loc.getWorld().dropItemNaturally(centerOfBlock,item);

    }),
        new Fortune("#4a6f28", "You are blessed with OP's speed",(player) -> {
            player.addPotionEffect((new PotionEffect(PotionEffectType.SPEED, 60*20,2)));
        }), //[Changed] you are blessed with op's speed (speed effect)

        new Fortune("#b48905", "Pippa Feet Reveal", (player) -> {
        Location loc = player.getLocation();
        Location centerOfBlock = loc.add(0, 0.5, 0);
        ItemStack item = new ItemStack(Material.RABBIT_FOOT,1);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(org.bukkit.ChatColor.LIGHT_PURPLE + "Pippa's FOOT");
        List<String> loreList = new ArrayList<String>();
        loreList.add(org.bukkit.ChatColor.BOLD + "Feet");//This is the first line of lore
        loreList.add(org.bukkit.ChatColor.GRAY + "A cute bunny's feet");//This is the second line of lore
        im.setLore(loreList);
        item.setItemMeta(im);
        loc.getWorld().dropItemNaturally(centerOfBlock,item);
    }), //[Changed] pippa's feet item

        new Fortune("#ec44e3", "ayy lmao", (player) -> {
        player.addPotionEffect((new PotionEffect(PotionEffectType.LEVITATION, 25*20,0)));
    }), //[Changed]levitating effect

        new Fortune("#d302a7", "§lGodly Luck", (player) -> {
        player.addPotionEffect((new PotionEffect(PotionEffectType.LUCK, 300*20,0)));
        double s = new Random().nextFloat();
        //Good luck reroll
        Location locoG = player.getLocation();
        if (s >=0.9){
            ItemStack pcake = new ItemStack(Material.CAKE,1);
            ItemMeta meta = pcake.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.LIGHT_PURPLE + "Pogu's Cake");
            pcake.setItemMeta(meta);
            locoG.getWorld().dropItemNaturally(locoG,pcake);
        }else if(s>=0.8){
            ItemStack pegg = new ItemStack(Material.VILLAGER_SPAWN_EGG,2);
            ItemMeta meta = pegg.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.MAGIC + "SEGGS EGGS");
            pegg.setItemMeta(meta);
            locoG.getWorld().dropItemNaturally(locoG,pegg);

        }else if(s>=0.5) {
            ItemStack gN = new ItemStack(Material.GOLD_NUGGET,1);
            ItemMeta meta = gN.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.GOLD + "Rabi's Blessing");
            gN.setItemMeta(meta);
            locoG.getWorld().dropItemNaturally(locoG,gN);
        }
        else{
            int amount = (int)(s* 10);
            ItemStack gN = new ItemStack(Material.IRON_NUGGET,amount);
            ItemMeta meta = gN.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.STRIKETHROUGH + "Pocket Change");
            gN.setItemMeta(meta);
            locoG.getWorld().dropItemNaturally(locoG,gN);
        }
    }),
        new Fortune("#006994", "§lYou caught a fish!", (player) -> {
            List<Material> fishlist = Arrays.asList(Material.TROPICAL_FISH_BUCKET,Material.PUFFERFISH_BUCKET,Material.COD_BUCKET,Material.SALMON_BUCKET);
            Material Fish = fishlist.get(new Random().nextInt(fishlist.size()));
            Location loc = player.getLocation();
            ItemStack item = new ItemStack(Fish,1);
            loc.getWorld().dropItemNaturally(loc,item);
        }),//fish

        new Fortune("#ffd700","§lCHICKEN JOCKEY!!!", (player) -> {
            globalSoundPlayer("vtcraft:chat.chicken");
            //player.playSound(player.getLocation(), "vtcraft:chat.chicken" , SoundCategory.VOICE, 6 ,1);
            //player.chat("chicken");
            Location locc = player.getLocation();
            org.bukkit.entity.Chicken chicken = locc.getWorld().spawn(locc.add(0,1,0), org.bukkit.entity.Chicken.class);
            org.bukkit.entity.Zombie zombie = locc.getWorld().spawn(locc, org.bukkit.entity.Zombie.class);
            zombie.setBaby(true);
            chicken.addPassenger(zombie);

        }),

            new Fortune("#aa00aa","§lTHE NETHER!", (player)->{
                //globalSoundPlayer("vtcraft:chat.nether");
                Location nether = new Location(Bukkit.getWorld("world_nether"),23,100,-16);
                player.teleport(nether);
            }),
            new Fortune("#ffa500","§lFLINT AND STEEL", (player)->{
                //globalSoundPlayer("vtcraft:chat.flintNsteel");
                player.setFireTicks(1000);
                Location loc = player.getLocation();
                Location centerOfBlock = loc.add(0, 0.5, 0);
                ItemStack item = new ItemStack(Material.FLINT_AND_STEEL,1);
                ItemMeta im = item.getItemMeta();
                im.setDisplayName(ChatColor.GOLD + "Flint and §kSteel");
                List<String> loreList = new ArrayList<String>();
                loreList.add(org.bukkit.ChatColor.BOLD + "Signed by Jack Black");
                im.setLore(loreList);
                item.setItemMeta(im);
                loc.getWorld().dropItemNaturally(centerOfBlock,item);
            }),

    };
    public static void globalSoundPlayer(String sound) {
        for (Player p : getServer().getOnlinePlayers()) {
            Location pos = p.getLocation();
            p.playSound(pos, sound, SoundCategory.VOICE, 6 ,1);

        }
    }
}