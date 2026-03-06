package com.example.utilmod.config;

import net.minecraft.util.math.Vec3d;

/**
 * Configuration class for all mod settings.
 * In a production mod, this would be serialized to JSON.
 */
public class ModConfig {
    
    // ESP Settings
    private boolean espEnabled = false;
    private int espColor = 0xFF0000FF; // ARGB format (Blue default)
    private boolean esp3D = true;      // true = 3D box, false = 2D box
    private boolean espThroughWalls = true;
    private float espLineWidth = 2.0f;
    
    // Aimbot Settings
    private boolean aimbotEnabled = false;
    private float aimbotSpeed = 0.15f;  // Rotation smoothness (0.0 - 1.0)
    private double aimbotRange = 64.0;  // Maximum target range
    private boolean aimbotTargetInvisible = false;
    private String aimbotTargetBone = "head"; // head, body, feet
    
    // Vanish Detector Settings
    private boolean vanishDetectorEnabled = false;
    private boolean vanishAlertInChat = true;
    private boolean vanishHighlightInvisible = true;
    private int vanishHighlightColor = 0xFFFF0000; // Red for vanished players
    
    // Getters and Setters
    
    public boolean isEspEnabled() { return espEnabled; }
    public void setEspEnabled(boolean enabled) { this.espEnabled = enabled; }
    
    public int getEspColor() { return espColor; }
    public void setEspColor(int color) { this.espColor = color; }
    
    public boolean isEsp3D() { return esp3D; }
    public void setEsp3D(boolean esp3D) { this.esp3D = esp3D; }
    
    public boolean isEspThroughWalls() { return espThroughWalls; }
    public void setEspThroughWalls(boolean throughWalls) { this.espThroughWalls = throughWalls; }
    
    public float getEspLineWidth() { return espLineWidth; }
    public void setEspLineWidth(float width) { this.espLineWidth = width; }
    
    public boolean isAimbotEnabled() { return aimbotEnabled; }
    public void setAimbotEnabled(boolean enabled) { this.aimbotEnabled = enabled; }
    
    public float getAimbotSpeed() { return aimbotSpeed; }
    public void setAimbotSpeed(float speed) { this.aimbotSpeed = Math.max(0.0f, Math.min(1.0f, speed)); }
    
    public double getAimbotRange() { return aimbotRange; }
    public void setAimbotRange(double range) { this.aimbotRange = range; }
    
    public boolean isAimbotTargetInvisible() { return aimbotTargetInvisible; }
    public void setAimbotTargetInvisible(boolean targetInvisible) { this.aimbotTargetInvisible = targetInvisible; }
    
    public String getAimbotTargetBone() { return aimbotTargetBone; }
    public void setAimbotTargetBone(String bone) { this.aimbotTargetBone = bone; }
    
    public boolean isVanishDetectorEnabled() { return vanishDetectorEnabled; }
    public void setVanishDetectorEnabled(boolean enabled) { this.vanishDetectorEnabled = enabled; }
    
    public boolean isVanishAlertInChat() { return vanishAlertInChat; }
    public void setVanishAlertInChat(boolean alert) { this.vanishAlertInChat = alert; }
    
    public boolean isVanishHighlightInvisible() { return vanishHighlightInvisible; }
    public void setVanishHighlightInvisible(boolean highlight) { this.vanishHighlightInvisible = highlight; }
    
    public int getVanishHighlightColor() { return vanishHighlightColor; }
    public void setVanishHighlightColor(int color) { this.vanishHighlightColor = color; }
}