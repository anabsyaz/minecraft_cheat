package com.example.utilmod;

import com.example.utilmod.config.ModConfig;
import com.example.utilmod.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Utility Mod.
 * Initializes all modules, keybinds, and networking hooks.
 */
public class UtilityMod implements ClientModInitializer {
    public static final String MOD_ID = "utilitymod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // Singleton instance for easy access
    private static UtilityMod INSTANCE;
    
    private ModuleManager moduleManager;
    private ModConfig config;
    
    // Keybindings for toggling modules
    private KeyBinding toggleEspKey;
    private KeyBinding toggleAimbotKey;
    private KeyBinding toggleVanishKey;
    private KeyBinding guiKey;
    
    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        
        LOGGER.info("Initializing Utility Mod for Minecraft 1.20.1");
        
        // Initialize configuration
        this.config = new ModConfig();
        
        // Initialize module manager
        this.moduleManager = new ModuleManager();
        
        // Register keybindings
        registerKeybindings();
        
        // Register tick events for module updates
        registerTickEvents();
        
        // Register network packet listeners for vanish detection
        registerNetworkListeners();
        
        LOGGER.info("Utility Mod initialized successfully!");
    }
    
    /**
     * Registers all keybindings for module toggles
     */
    private void registerKeybindings() {
        // ESP Toggle (default: R)
        toggleEspKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.utilitymod.toggle_esp",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.utilitymod.modules"
        ));
        
        // Aimbot Toggle (default: X)
        toggleAimbotKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.utilitymod.toggle_aimbot",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "category.utilitymod.modules"
        ));
        
        // Vanish Detector Toggle (default: V)
        toggleVanishKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.utilitymod.toggle_vanish",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.utilitymod.modules"
        ));
        
        // GUI Key (default: Right Shift)
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.utilitymod.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.utilitymod.modules"
        ));
    }
    
    /**
     * Registers client tick events for updating modules and checking keybinds
     */
    private void registerTickEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            
            // Handle keybind presses
            while (toggleEspKey.wasPressed()) {
                moduleManager.getEspModule().toggle();
                client.player.sendMessage(
                    net.minecraft.text.Text.literal("§8[§9Utility§8] §7ESP: " + 
                        (moduleManager.getEspModule().isEnabled() ? "§aEnabled" : "§cDisabled")), 
                    true
                );
            }
            
            while (toggleAimbotKey.wasPressed()) {
                moduleManager.getAimbotModule().toggle();
                client.player.sendMessage(
                    net.minecraft.text.Text.literal("§8[§9Utility§8] §7Aimbot: " + 
                        (moduleManager.getAimbotModule().isEnabled() ? "§aEnabled" : "§cDisabled")), 
                    true
                );
            }
            
            while (toggleVanishKey.wasPressed()) {
                moduleManager.getVanishModule().toggle();
                client.player.sendMessage(
                    net.minecraft.text.Text.literal("§8[§9Utility§8] §7Vanish Detector: " + 
                        (moduleManager.getVanishModule().isEnabled() ? "§aEnabled" : "§cDisabled")), 
                    true
                );
            }
            
            // Update all active modules
            moduleManager.onTick(client);
        });
    }
    
    /**
     * Registers network packet listeners for vanish detection
     * This captures entity spawn packets and metadata updates server->client
     */
    private void registerNetworkListeners() {
        // Listen for entity spawn packets (vanish detection)
        ClientPlayNetworking.registerGlobalReceiver(
            EntitySpawnS2CPacket.class, 
            this::onEntitySpawn
        );
        
        // Listen for entity metadata updates (vanish state changes)
        ClientPlayNetworking.registerGlobalReceiver(
            EntityTrackerUpdateS2CPacket.class,
            this::onEntityTrackerUpdate
        );
    }
    
    /**
     * Called when an entity spawns in the world
     * Used by vanish detector to track entity appearances
     */
    private void onEntitySpawn(EntitySpawnS2CPacket packet, PacketSender sender) {
        if (moduleManager != null && moduleManager.getVanishModule().isEnabled()) {
            moduleManager.getVanishModule().onEntitySpawn(packet);
        }
    }
    
    /**
     * Called when entity metadata is updated
     * Used to detect invisibility status changes
     */
    private void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet, PacketSender sender) {
        if (moduleManager != null && moduleManager.getVanishModule().isEnabled()) {
            moduleManager.getVanishModule().onEntityTrackerUpdate(packet);
        }
    }
    
    public static UtilityMod getInstance() {
        return INSTANCE;
    }
    
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
    
    public ModConfig getConfig() {
        return config;
    }
}