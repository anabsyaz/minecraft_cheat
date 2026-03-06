package com.example.utilmod.mixin;

import com.example.utilmod.UtilityMod;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into GameRenderer to hook into the render loop.
 * This allows us to render ESP boxes on top of the world.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    
    /**
     * Inject after the world has been rendered but before the HUD/hand.
     * This ensures ESP renders on top of the world but under UI elements.
     */
    @Inject(
        method = "renderWorld",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;)V",
            shift = At.Shift.AFTER
        )
    )
    private void onRenderWorld(
        float tickDelta, 
        long limitTime, 
        MatrixStack matrices,
        CallbackInfo ci
    ) {
        // Get the GameRenderer instance
        GameRenderer renderer = (GameRenderer) (Object) this;
        
        // Get camera and projection matrix
        Camera camera = renderer.getCamera();
        Matrix4f projectionMatrix = renderer.getBasicProjectionMatrix(camera, tickDelta, false);
        
        // Call ESP render
        UtilityMod.getInstance().getModuleManager().getEspModule().onRender(
            matrices, tickDelta, limitTime, false, camera, renderer, projectionMatrix
        );
    }
}