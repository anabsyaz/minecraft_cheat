package com.example.utilmod.mixin;

import com.example.utilmod.UtilityMod;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into ClientPlayerEntity to hook into player movement and input.
 * Used for aimbot rotation application and movement correction.
 */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    
    @Shadow
    public Input input;
    
    /**
     * Inject at the beginning of tick() to apply aimbot rotations
     * before movement calculations
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // The aimbot rotation is applied in the module's onTick via UtilityMod
        // This hook can be used for additional movement modifications if needed
    }
    
    /**
     * Inject into sendMovementPackets to ensure our rotations are synced to server
     */
    @Inject(
        method = "sendMovementPackets",
        at = @At("HEAD")
    )
    private void onSendMovementPackets(CallbackInfo ci) {
        // Ensure aimbot rotations are properly sent to server
        // The rotation is already applied to the player entity in AimbotModule
    }
    
    /**
     * Inject at the end of tick to clean up or post-process
     */
    @Inject(method = "tick", at = @At("RETURN"))
    private void onTickEnd(CallbackInfo ci) {
        // Post-tick cleanup if needed
    }
}