package com.quakeworld.s4sfortune;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.sun.istack.internal.NotNull;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.BanList.Type;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

    public class LastRoll {
        String name;
        String time;
    }

    private HashMap<String, Date> nextRolls = new HashMap<String, Date>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                TextComponent advertprefix = new TextComponent("⭐ " + pickRandomFromList(adverts) + " ");
        //        TextComponent advertlink = new TextComponent("#yubicraft:matrix.org");
        //        TextComponent advertspace = new TextComponent(" / ");
        //        TextComponent advertlink2 = new TextComponent("discord.gg/zus9dcvfjU");

        //        advertprefix.setItalic(true);
        //        advertspace.setItalic(true);
        //        advertlink.setItalic(true);
        //        advertlink2.setItalic(true);

                advertprefix.setColor(ChatColor.of("#0dbd8b"));
        //        advertspace.setColor(ChatColor.of("#0dbd8b"));
        //        advertlink.setColor(ChatColor.of("#0dbd8b"));
        //        advertlink2.setColor(ChatColor.of("#0dbd8b"));

        //        advertlink.setUnderlined(true);
        //        advertlink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://matrix.to/#yubicraft:matrix.org"));

        //        advertlink2.setUnderlined(true);
        //        advertlink2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/zus9dcvfjU"));

                getServer().spigot().broadcast(advertprefix); // advertlink, advertspace,advertlink2
            }
        }
        , 0L
        , 20L * 60 * 60 // 60 minutes
        );
    }
    @Override
    public void onDisable() {
    }

    private final String[] adverts = {
            "secret erp channel",
            "chatroom where you say ogey rrat",
            "ONGOING RAID (jk)",
            "be nice!",
            "gura in the dark room",
            "HoloEN3 " + Calendar.getInstance().get(Calendar.YEAR),
            "kiara fan club",
            "server chat yah!",
            "fumo posting area ᗜˬᗜ",
            "ඞ",
            "0 days in last yab",
            "\"\"\"appeal\"\"\" your ban ;) ;) ",
            "PIPPA NOTICE ME",
            "yeah, me",
            "virtual youtuber enthusiasts group",
            "おはよううううううううう！！！",
            "SEXXXXX",
            "do your reps!",
            "rigger Hate!",
            "cum",
            "Dnot let this ERP distract you from the fact that Pekora bought a fucking MONKEEEE",
            "I LOVE WHOOOOOOOOOOOOOOOOOOOOOOOOOORES",
            "YOU WILL NOT PIRATE JAVS",
            "reminder to hide your discord overlay!",
    };

    private final Fortune[] regularFortunes = {
        new Fortune("#f51c6a", "Reply hazy, try again"),
        new Fortune("#e7890c", "Good Luck"),
        new Fortune("#bac200", "Average Luck"),
        new Fortune("#7fec11", "Bad Luck", (player) -> {
        player.addPotionEffect((new PotionEffect(PotionEffectType.UNLUCK, 2*60*20,0)));
        double s = Math.random();
        //Bad luck reroll
        Location loco = player.getLocation();
        if (s >=0.9){
            Location centerOfBlock = loco.add(0, 0.5, 0);
            loco.getWorld().dropItemNaturally(centerOfBlock,new ItemStack(Material.COBBLESTONE, 640));
        }else if(s>=0.5){
            player.addPotionEffect((new PotionEffect(PotionEffectType.POISON, 400,0)));
        }else if(s>=0.3) {
            player.teleport(loco.add(0,103,0));
        }else if(s>=0.2) {
            player.setHealth(1);
        }
        else{
            player.setFireTicks(500);
        }
    }),
        new Fortune("#43fd3b", "Good news will come to you by mail"),
        new Fortune("#16f174", "（ ´_ゝ`）ﾌｰﾝ"),
        new Fortune("#00cbb0", "ｷﾀ━━━━━━(ﾟ∀ﾟ)━━━━━━ !!!!"),
        new Fortune("#0893e1", "You will meet a dark handsome stranger"),
        new Fortune("#2a56fb", "Better not tell you now"),
        new Fortune("#6023f8", "Outlook good"),
        //Godly luck moved to special fortune
    };


    private final Fortune[] specialFortunes = {
        new Fortune("#ff0000", "(YOU ARE BANNED)", (player) -> {
        //random lenght of time 10mins to 8hrs
        int onehr = 60*60*1000;
        int time = (int) Math.floor(Math.random() < 0.1 ? Math.random() < 0.1 ? 8 * onehr : onehr : 10 * 60 * 1000);
        // int time = (int) Math.floor(ye);
        Date banEnd = new Date(System.currentTimeMillis() + time);
        Bukkit.getBanList(Type.NAME).addBan(player.getName(),
                ChatColor.BOLD + "" + ChatColor.RED + "Your fortune: (YOU ARE BANNED)" + ChatColor.RESET, banEnd, "s4sfortune");
        player.kickPlayer(ChatColor.BOLD + "" + ChatColor.RED + "Your fortune: (YOU ARE BANNED)");
    }),
        new Fortune("#f7dc6f", "Your Shitty Luck Has Angered ZESUCHAMA", (player) -> {
            Location loc = player.getLocation();
            loc.getWorld().strikeLightningEffect(loc);
    }),

      /*  new Fortune("#00d7ff", "sixtee four diamonds ebin :DD", (player) -> {
            player.getInventory().addItem(new ItemStack(Material.DIAMOND, 64));
        }), */

        new Fortune("#fff451", "THEY GLOW IN THE DARK", (player) -> {
            player.addPotionEffect((new PotionEffect(PotionEffectType.GLOWING, 300,1)));
    }),
        new Fortune("#396a24", "*BRAAAAAAP* You Feel a Strange Smell around you ", (player) -> {
        player.addPotionEffect((new PotionEffect(PotionEffectType.CONFUSION, 20,1)));
    }),
        new Fortune("#141414", "You meet a dark handsome stranger ", (player) -> {
            Location locc =player.getLocation();
            locc.getWorld().spawnEntity(locc.add(0,1,0), EntityType.ENDERMAN);
    }),
        new Fortune("#ffd700", "Oy Vey", (player) -> {
        Location locc =player.getLocation();
        locc.getWorld().spawnEntity(locc.add(0,2,5), EntityType.WANDERING_TRADER);
    }),
        new Fortune("#00d7ff", "trudy too diamonds ebin :DD", (player) -> {
         Location loc = player.getLocation();
         Location centerOfBlock = loc.add(0.5, 0.5, 0.5);
         loc.getWorld().dropItemNaturally(centerOfBlock,new ItemStack(Material.DIAMOND, 32));
    }),
        new Fortune("#b48905", "Berries :D", (player) -> {
        Location loc = player.getLocation();
        Location centerOfBlock = loc.add(0.5, 0.5, 0.5);
            ItemStack item = new ItemStack(Material.SWEET_BERRIES,64);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(ChatColor.RED + "displayName");
            List<String> loreList = new ArrayList<String>();
            loreList.add(ChatColor.BOLD + "Berry");//This is the first line of lore
            loreList.add(ChatColor.GRAY + "Its a berry");//This is the second line of lore
            im.setLore(loreList);
            item.setItemMeta(im);
        loc.getWorld().dropItemNaturally(centerOfBlock,item);
    }),
        new Fortune("#68923a", "Get Shrekt", (player) -> {
        player.addPotionEffect((new PotionEffect(PotionEffectType.SLOW_DIGGING, 30*20,0)));
    }),
        new Fortune("#4a6f28", "You are blessed with OP's speed",(player) -> {
            player.addPotionEffect((new PotionEffect(PotionEffectType.SPEED, 60*5,2)));
        }), //[Changed] you are blessed with op's speed (speed effect)

        new Fortune("#b48905", "Pippa Feet Reveal", (player) -> {
        Location loc = player.getLocation();
        Location centerOfBlock = loc.add(0.5, 0.5, 0.5);
        ItemStack item = new ItemStack(Material.RABBIT_FOOT,1);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.LIGHT_PURPLE + "Pippa's FOOT");
        List<String> loreList = new ArrayList<String>();
        loreList.add(ChatColor.BOLD + "Feet");//This is the first line of lore
        loreList.add(ChatColor.GRAY + "A cute bunny's feet");//This is the second line of lore
        im.setLore(loreList);
        item.setItemMeta(im);
        loc.getWorld().dropItemNaturally(centerOfBlock,item);
    }), //[Changed] pippa's feet item

        new Fortune("#ec44e3", "ayy lmao", (player) -> {
        player.addPotionEffect((new PotionEffect(PotionEffectType.LEVITATION, 60,0)));
    }), //[Changed]levitating effect

        new Fortune("#d302a7", "Godly Luck", (player) -> {
        player.addPotionEffect((new PotionEffect(PotionEffectType.LUCK, 300*20,0)));
        double s = Math.random();
        //Good luck reroll
        Location locoG = player.getLocation();
        if (s >=0.9){
            ItemStack pcake = new ItemStack(Material.CAKE,1);
            //ItemMeta im = item.getItemMeta();
            pcake.getItemMeta().setDisplayName(ChatColor.LIGHT_PURPLE + "Pogu's Cake");
            locoG.getWorld().dropItemNaturally(locoG,pcake);
        }else if(s>=0.8){
            ItemStack pegg = new ItemStack(Material.VILLAGER_SPAWN_EGG,2);
            pegg.getItemMeta().setDisplayName(ChatColor.YELLOW + "SEGGS EGGS");
            locoG.getWorld().dropItemNaturally(locoG,pegg);

        }else if(s>=0.5) {
            ItemStack gN = new ItemStack(Material.GOLD_NUGGET,1);
            gN.getItemMeta().setDisplayName(ChatColor.YELLOW + "Rabi's Blessing");
            locoG.getWorld().dropItemNaturally(locoG,gN);
        }
        else{
            int amount = (int)(s* 10);
            ItemStack gN = new ItemStack(Material.IRON_INGOT,amount);
            gN.getItemMeta().setDisplayName(ChatColor.STRIKETHROUGH + "Pocket Change");
            locoG.getWorld().dropItemNaturally(locoG,gN);
        }
    }),
        new Fortune("#a922ca", "You caught a fish!", (player) -> {
            List<Material> fishlist = Arrays.asList(Material.TROPICAL_FISH_BUCKET,Material.PUFFERFISH_BUCKET,Material.COD_BUCKET,Material.SALMON_BUCKET);
            Material Fish = fishlist.get(RandomUtils.nextInt(fishlist.size()));
            Location loc = player.getLocation();
            ItemStack item = new ItemStack(Fish,1);
            item.getItemMeta().setDisplayName(ChatColor.BLUE + "displayName");
            loc.getWorld().dropItemNaturally(loc,item);
        }),//fish
    };

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
        return list[(int) Math.floor(list.length * Math.random())];
    }

    // FIXME break out into separate plugin
    @EventHandler
    public void AsyncChatEvent(AsyncPlayerChatEvent e) {
        urlHandler(e.getMessage(), e.getRecipients());
    }

    @EventHandler
    public boolean PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
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
        } catch (IndexOutOfBoundsException excpt) {
            return true;
        }

        return true;
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

        if (allowedTime != null) {
            if (allowedTime.after(new Date(System.currentTimeMillis()))) {
                int timeLeft = (int) ((allowedTime.getTime() - System.currentTimeMillis()) / 1000);
                sender.sendMessage("You can roll again in " + timeLeft + " seconds.");
                return true;
            }
        }
                                 
        if (command.getName().equalsIgnoreCase("roll")) {
            int special = (int) Math.floor(SPECIAL_PROBABILITY * Math.random()); 

            if (special == 1) {
                // do a special roll
                announceRoll(pickRandomFromList(specialFortunes), (Player) sender);
            } else {
                // normal roll
                announceRoll(pickRandomFromList(regularFortunes), (Player) sender);
            }

            // debounce
            Date nextAllowedRollTime = new Date(System.currentTimeMillis() + 30*1000);
            this.nextRolls.put(sender.getName(), nextAllowedRollTime);
        }

        return true;
    }
}
