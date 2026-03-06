package com.example.utilmod.module;

import net.minecraft.client.MinecraftClient;

/**
 * Abstract base class for all modules.
 * Provides common functionality for enable/disable states and ticking.
 */
public abstract class Module {
    
    protected final String name;
    protected final String description;
    protected boolean enabled;
    protected final int keybind;
    
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    
    public Module(String name, String description, int keybind) {
        this.name = name;
        this.description = description;
        this.keybind = keybind;
        this.enabled = false;
    }
    
    /**
     * Called every client tick when the module is enabled
     */
    public abstract void onTick(MinecraftClient client);
    
    /**
     * Called when the module is enabled
     */
    public void onEnable() {
        // Override in subclasses if needed
    }
    
    /**
     * Called when the module is disabled
     */
    public void onDisable() {
        // Override in subclasses if needed
    }
    
    /**
     * Toggle the module on/off
     */
    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }
    
    /**
     * Set the module state directly
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            toggle();
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public int getKeybind() {
        return keybind;
    }
}