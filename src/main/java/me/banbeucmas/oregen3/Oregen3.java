package me.banbeucmas.oregen3;

import me.banbeucmas.oregen3.commands.Commands;
import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.listeners.BlockListener;
import me.banbeucmas.oregen3.listeners.GUIListener;
import me.banbeucmas.oregen3.utils.StringUtils;
import me.banbeucmas.oregen3.utils.hooks.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class Oregen3 extends JavaPlugin implements Listener {
    private static Oregen3 plugin;
    private static SkyblockHook hook;
    public static boolean DEBUG = false;


    //TODO Seperate these into methods
    @Override
    public void onEnable() {
        plugin = this;
        Metrics metrics = new Metrics(this);

        saveDefaultConfig();
        updateConfig();
        hookInit();

        CommandSender sender = Bukkit.getConsoleSender();
        //Send Message
        sender.sendMessage(StringUtils.getColoredString("&7&m-------------&f[Oregen3&f]&7-------------"));
        sender.sendMessage("");
        sender.sendMessage(StringUtils.getColoredString("       &fPlugin made by &e&oBanbeucmas"));
        sender.sendMessage(StringUtils.getColoredString("       &f&oVersion: &e" + getDescription().getVersion()));
        sender.sendMessage("");
        sender.sendMessage(StringUtils.getColoredString("------------------------------------"));

        DataManager.loadData();
        getCommand("oregen3").setExecutor(new Commands());
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
    }

    public void onDisable() {
        plugin = null;
        DataManager.unregisterAll();
    }

    private void hookInit(){
        boolean asbHook = Bukkit.getServer().getPluginManager().isPluginEnabled("ASkyBlock");
        boolean acidHook = Bukkit.getServer().getPluginManager().isPluginEnabled("AcidIsland");
        boolean bentoHook = Bukkit.getServer().getPluginManager().isPluginEnabled("BentoBox");
        if(asbHook){
            hook = new ASkyblockHook();
        }
        else if(acidHook){
            hook = new AcidIslandHook();
        }
        else if(bentoHook){
            hook = new BentoboxHook();
            BentoBox bento = (BentoBox) Bukkit.getPluginManager().getPlugin("BentoBox");
            Optional<Addon> addon = bento.getAddonsManager().getAddonByName("Level");

            //TODO Fix redundency
            if(addon.isPresent()){
                hook = new BentoLevelHook();
            }
        }
        else {
            hook = new VanillaHook();
        }

    }

    public static SkyblockHook getHook() {
        return hook;
    }

    public static Oregen3 getPlugin() {
        return plugin;
    }

    public static void updateConfig(){
        if(!getPlugin().getConfig().isSet("version")){
            getPlugin().getConfig().set("version", "1.1.0");

            List<String> l = new ArrayList<>();
            l.add("FENCE");
            l.add("ACACIA_FENCE");
            l.add("BIRCH_FENCE");
            l.add("DARK_OAK_FENCE");
            l.add("IRON_FENCE");
            getPlugin().getConfig().set("blocks", l);
            getPlugin().getConfig().set("mode.lavaBlock", false);
            getPlugin().getConfig().set("mode.waterBlock", true);
            getPlugin().getConfig().set("mode.lavaFence", null);
            getPlugin().getConfig().set("mode.waterFence", null);
            getPlugin().saveConfig();
        }

        String version = getPlugin().getConfig().getString("version");

        if(version.equals("1.1.0")){
            getPlugin().getConfig().set("version", "1.2.0");
            getPlugin().getConfig().set("enableDependency", true);
            getPlugin().saveConfig();
            updateConfig();
        }
        if(version.equals("1.2.0")){
            getPlugin().getConfig().set("version", "1.3.0");
            getPlugin().getConfig().set("messages.gui.title", "&eChances");
            getPlugin().getConfig().set("messages.gui.block.displayName", "&6%name%");
            getPlugin().getConfig().set("messages.gui.block.lore",
                    Arrays.asList("&6Chances: &e%chance%&6%"));
            getPlugin().saveConfig();
        }
        if(version.equals("1.3.0")){
            getPlugin().getConfig().set("version", "1.3.1");
            getPlugin().getConfig().set("enablePermission", false);
            getPlugin().saveConfig();
        }
    }
}
