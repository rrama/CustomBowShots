package me.rrama.CustomBowShots;

/**
 *
 * @authors rrama
 * All rights reserved to rrama.
 * No copying/stealing any part of the code (Exceptions; You are from the Bukkit team, you have written consent from rrama).
 * No copying/stealing ideas from the code (Exceptions; You are from the Bukkit team, you have written consent from rrama).
 * 
 * @credits Credit goes to rrama (author)
 */

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class CustomBowShots extends JavaPlugin implements Listener {
    
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("CoolBow").setExecutor(this);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.isCancelled()) return;
        try {
            ItemMeta IM = event.getBow().getItemMeta();
            List<String> Lore = IM.getLore();
            if (!Lore.get(0).equals("CustomBowShots")) return;
            LivingEntity LE = event.getEntity();
            boolean infinite = IM.hasEnchant(Enchantment.ARROW_INFINITE);
            Entity p;
            switch (Lore.get(1)) {
                case "Snowball":
                    if (!infinite) {
                        if (!take(LE, 332)) return;
                    }
                    p = LE.launchProjectile(Snowball.class);
                    break;
                    
                case "Egg":
                    if (!infinite) {
                        if (!take(LE, 344)) return;
                    }
                    p = LE.launchProjectile(Egg.class);
                    break;
                    
                case "EnderPearl":
                    if (!infinite) {
                        if (!take(LE, 368)) return;
                    }
                    EnderPearl EP = (EnderPearl) LE.launchProjectile(EnderPearl.class);
                    EP.setShooter(LE);
                    p = EP;
                    break;
                    
                case "Entity":
                    p = LE.getWorld().spawnEntity(LE.getEyeLocation(), EntityType.valueOf(Lore.get(2).toUpperCase()));
                    p.setVelocity(LE.getLocation().getDirection());
                    break;
                    
                case "FallingBlock":
                    String[] L2S = Lore.get(2).split(" - ");
                    p = LE.getWorld().spawnFallingBlock(LE.getEyeLocation(), Integer.parseInt(L2S[0]), Byte.parseByte(L2S[1]));
                    p.setVelocity(LE.getLocation().getDirection());
                    break;
                    
                case "Potion":
                    L2S = Lore.get(2).split(" - ");
                    Potion P = new Potion(PotionType.valueOf(L2S[0].toUpperCase()), Integer.valueOf(L2S[1])).splash();
                    try {
                        P.setHasExtendedDuration(L2S[3].equalsIgnoreCase("Extended"));
                    } catch (Exception ex) {}
                    ThrownPotion TP = (ThrownPotion) LE.launchProjectile(ThrownPotion.class);
                    ItemStack iss = P.toItemStack(1);
                    if (!infinite) {
                        if (!take(LE, iss)) return;
                    }
                    TP.setItem(iss);
                    p = TP;
                    p.setVelocity(p.getVelocity().multiply(2));
                    break;
                    
                default:
                    return;
                    
            }
            int mul = IM.getEnchantLevel(Enchantment.ARROW_DAMAGE) + 1;
            p.setVelocity(p.getVelocity().multiply(mul));
            p.setVelocity(p.getVelocity().multiply(event.getForce()));
            event.setCancelled(true);
        } catch (Exception ex) {}
    }
    
    public static boolean take(LivingEntity LE, int id) {
        return take(LE, new ItemStack(id));
    }
    
    public static boolean take(LivingEntity LE, ItemStack iss) {
        if (LE instanceof HumanEntity) return true;
        HumanEntity HE = (HumanEntity) LE;
        return HE.getInventory().removeItem(iss).isEmpty();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        try {
            if (event.getItem().getItemMeta().getLore().get(0).equals("CustomBowShots")) {
                event.setCancelled(true);
            }
        } catch (Exception ex) {}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantItem(EnchantItemEvent event) {
        try {
            if (event.getItem().getItemMeta().getLore().get(0).equals("CustomBowShots")) {
                event.setCancelled(true);
            }
        } catch (Exception ex) {}
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        ItemStack Bow = new ItemStack(261);
        ItemMeta IM = Bow.getItemMeta();
        switch (args[0]) {
            case "en":
                IM.setLore(Arrays.asList("CustomBowShots", "Entity", "Pig"));
                break;
            case "fb":
                IM.setLore(Arrays.asList("CustomBowShots", "FallingBlock", "35 - 0"));
                break;
            case "p":
                IM.setLore(Arrays.asList("CustomBowShots", "Potion", "Speed - 1 - extended"));
                break;
            case "EnderPearl":
                IM.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
            default:
                IM.setLore(Arrays.asList("CustomBowShots", args[0]));
        }
        IM.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        Bow.setItemMeta(IM);
        Player P = (Player) sender;
        if (args.length == 2) {
            Skeleton S = (Skeleton) P.getWorld().spawnEntity(P.getLocation(), EntityType.SKELETON);
            S.setCustomName(args[0]);
            S.getEquipment().setItemInHand(Bow); //Does not work. They still shot normal arrows :(
        } else {
            P.getInventory().addItem(Bow);
        }
        return true;
    }
    
}