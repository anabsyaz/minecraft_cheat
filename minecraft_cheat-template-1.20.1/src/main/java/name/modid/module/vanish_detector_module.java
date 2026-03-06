package com.example.utilmod.module;

import com.example.utilmod.UtilityMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Vanish Detector Module
 * Detects players who are invisible or vanished using packet analysis.
 */
public class VanishDetectorModule extends Module {
    
    // Track players who we know are vanished/invisible
    private final Set<UUID> vanishedPlayers = new HashSet<>();
    private final Set<Integer> knownEntities = new HashSet<>();
    
    public VanishDetectorModule() {
        super("VanishDetector", "Detects invisible and vanished players", -1);
    }
    
    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.world == null) return;
        
        // Check for players who became visible/invisible since last tick
        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            UUID uuid = player.getUuid();
            boolean isInvisible = player.isInvisible();
            
            if (isInvisible && !vanishedPlayers.contains(uuid)) {
                // Player just became invisible
                vanishedPlayers.add(uuid);
                if (UtilityMod.getInstance().getConfig().isVanishAlertInChat()) {
                    alertPlayerVanish(player, true);
                }
            } else if (!isInvisible && vanishedPlayers.contains(uuid)) {
                // Player became visible again
                vanishedPlayers.remove(uuid);
                if (UtilityMod.getInstance().getConfig().isVanishAlertInChat()) {
                    alertPlayerVanish(player, false);
                }
            }
        }
        
        // Clean up disconnected players
        vanishedPlayers.removeIf(uuid -> client.world.getPlayerByUuid(uuid) == null);
    }
    
    @Override
    public void onEnable() {
        UtilityMod.getInstance().getConfig().setVanishDetectorEnabled(true);
        vanishedPlayers.clear();
        knownEntities.clear();
    }
    
    @Override
    public void onDisable() {
        UtilityMod.getInstance().getConfig().setVanishDetectorEnabled(false);
        vanishedPlayers.clear();
    }
    
    /**
     * Called when an entity spawn packet is received
     * Some vanish plugins use spawn/despawn mechanics
     */
    public void onEntitySpawn(EntitySpawnS2CPacket packet) {
        // Track entity spawns - if we see a spawn for a player we couldn't see before,
        // they may have just unvanished
        knownEntities.add(packet.getId());
    }
    
    /**
     * Called when entity metadata is updated
     * Invisibility is controlled via metadata flags
     */
    public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;
        
        Entity entity = client.world.getEntityById(packet.id());
        if (!(entity instanceof PlayerEntity player)) return;
        
        // Check for invisibility flag in metadata (index 0 is the bitmask in 1.20.1)
        for (DataTracker.SerializedEntry<?> entry : packet.trackedValues()) {
            if (entry.handler() == net.minecraft.entity.data.TrackedDataHandlerRegistry.BYTE && entry.index() == 0) {
                byte bitmask = (Byte) entry.value();
                boolean invisible = (bitmask & 0x20) != 0; // 0x20 is the invisible flag
                
                UUID uuid = player.getUuid();
                if (invisible && !vanishedPlayers.contains(uuid)) {
                    vanishedPlayers.add(uuid);
                    if (UtilityMod.getInstance().getConfig().isVanishAlertInChat()) {
                        alertPlayerVanish(player, true);
                    }
                }
            }
        }
    }
    
    /**
     * Send chat alert about vanish status change
     */
    private void alertPlayerVanish(PlayerEntity player, boolean vanished) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        String message;
        if (vanished) {
            message = String.format("§8[§9Utility§8] §e%s §7is now §cVANISHED/Invisible!", player.getName().getString());
        } else {
            message = String.format("§8[§9Utility§8] §e%s §7is now §aVisible!", player.getName().getString());
        }
        
        client.player.sendMessage(Text.literal(message), false);
    }
    
    /**
     * Check if a specific player is tracked as vanished
     */
    public boolean isPlayerVanished(UUID uuid) {
        return vanishedPlayers.contains(uuid);
    }
    
    public Set<UUID> getVanishedPlayers() {
        return new HashSet<>(vanishedPlayers);
    }
}