package com.example.utilmod.module;

import com.example.utilmod.UtilityMod;
import com.example.utilmod.util.RotationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Aimbot Module
 * Automatically aims at the nearest player with smooth rotation.
 */
public class AimbotModule extends Module {
    
    private AbstractClientPlayerEntity currentTarget = null;
    
    public AimbotModule() {
        super("Aimbot", "Automatically aims at nearest player", -1);
    }
    
    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        
        // Find nearest target
        AbstractClientPlayerEntity target = findNearestTarget(client);
        
        if (target == null) {
            currentTarget = null;
            return;
        }
        
        currentTarget = target;
        
        // Calculate aim position based on target bone setting
        Vec3d targetPos = getTargetPosition(target);
        
        // Calculate required rotation
        float[] rotations = RotationUtil.calculateRotations(client.player.getEyePos(), targetPos);
        
        // Apply smooth rotation
        smoothRotate(client, rotations[0], rotations[1]);
    }
    
    @Override
    public void onEnable() {
        UtilityMod.getInstance().getConfig().setAimbotEnabled(true);
    }
    
    @Override
    public void onDisable() {
        UtilityMod.getInstance().getConfig().setAimbotEnabled(false);
        currentTarget = null;
    }
    
    /**
     * Finds the nearest valid target within range
     */
    private AbstractClientPlayerEntity findNearestTarget(MinecraftClient client) {
        AbstractClientPlayerEntity nearest = null;
        double nearestDistance = UtilityMod.getInstance().getConfig().getAimbotRange();
        
        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            // Skip self
            if (player == client.player) continue;
            
            // Skip dead players
            if (!player.isAlive()) continue;
            
            // Skip invisible players if configured
            if (player.isInvisible() && !UtilityMod.getInstance().getConfig().isAimbotTargetInvisible()) {
                continue;
            }
            
            // Calculate distance
            double distance = client.player.getPos().distanceTo(player.getPos());
            if (distance > nearestDistance) continue;
            
            // Check line of sight (optional, can be disabled for wall-aim)
            // if (!client.player.canSee(player)) continue;
            
            nearest = player;
            nearestDistance = distance;
        }
        
        return nearest;
    }
    
    /**
     * Gets the target position based on configured bone (head, body, feet)
     */
    private Vec3d getTargetPosition(PlayerEntity target) {
        String bone = UtilityMod.getInstance().getConfig().getAimbotTargetBone();
        Vec3d pos = target.getPos();
        
        switch (bone.toLowerCase()) {
            case "head":
                // Head position (eye height)
                return pos.add(0, target.getStandingEyeHeight(), 0);
            case "body":
                // Center of body
                return pos.add(0, target.getHeight() / 2, 0);
            case "feet":
            default:
                // Feet position (already at pos)
                return pos;
        }
    }
    
    /**
     * Smoothly rotates the player towards target yaw and pitch
     */
    private void smoothRotate(MinecraftClient client, float targetYaw, float targetPitch) {
        float speed = UtilityMod.getInstance().getConfig().getAimbotSpeed();
        
        // Get current rotation
        float currentYaw = client.player.getYaw();
        float currentPitch = client.player.getPitch();
        
        // Calculate difference
        float deltaYaw = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float deltaPitch = targetPitch - currentPitch;
        
        // Apply smoothing
        float newYaw = currentYaw + (deltaYaw * speed);
        float newPitch = currentPitch + (deltaPitch * speed);
        
        // Clamp pitch to valid range
        newPitch = MathHelper.clamp(newPitch, -90.0f, 90.0f);
        
        // Apply rotation
        client.player.setYaw(newYaw);
        client.player.setPitch(newPitch);
        
        // Also update prev rotation for smooth rendering
        client.player.prevYaw = newYaw;
        client.player.prevPitch = newPitch;
    }
    
    public AbstractClientPlayerEntity getCurrentTarget() {
        return currentTarget;
    }
}