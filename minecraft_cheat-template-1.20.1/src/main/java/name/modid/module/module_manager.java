package com.example.utilmod.module;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Central registry and manager for all modules.
 * Handles initialization, ticking, and cross-module communication.
 */
public class ModuleManager {
    
    private final List<Module> modules = new ArrayList<>();
    
    // Specific module references for easy access
    private final ESPModule espModule;
    private final AimbotModule aimbotModule;
    private final VanishDetectorModule vanishModule;
    
    public ModuleManager() {
        // Initialize modules
        this.espModule = new ESPModule();
        this.aimbotModule = new AimbotModule();
        this.vanishModule = new VanishDetectorModule();
        
        // Register modules
        modules.add(espModule);
        modules.add(aimbotModule);
        modules.add(vanishModule);
    }
    
    /**
     * Called every client tick to update all enabled modules
     */
    public void onTick(MinecraftClient client) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick(client);
            }
        }
    }
    
    /**
     * Get all registered modules
     */
    public List<Module> getModules() {
        return modules;
    }
    
    /**
     * Get module by name (case-insensitive)
     */
    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }
    
    // Specific getters for direct access
    
    public ESPModule getEspModule() {
        return espModule;
    }
    
    public AimbotModule getAimbotModule() {
        return aimbotModule;
    }
    
    public VanishDetectorModule getVanishModule() {
        return vanishModule;
    }
}