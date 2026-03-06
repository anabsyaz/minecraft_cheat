package com.example.utilmod.module;

import com.example.utilmod.UtilityMod;
import com.example.utilmod.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

/**
 * ESP (Extra Sensory Perception) Module
 * Renders bounding boxes around players visible through walls.
 */
public class ESPModule extends Module {
    
    public ESPModule() {
        super("ESP", "Renders bounding boxes around players", -1);
    }
    
    @Override
    public void onTick(MinecraftClient client) {
        // ESP is render-based, logic handled in GameRendererMixin
    }
    
    @Override
    public void onEnable() {
        UtilityMod.getInstance().getConfig().setEspEnabled(true);
    }
    
    @Override
    public void onDisable() {
        UtilityMod.getInstance().getConfig().setEspEnabled(false);
    }
    
    /**
     * Main render method called from GameRendererMixin
     * This performs the actual ESP rendering
     */
    public void onRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, 
                        Camera camera, GameRenderer gameRenderer, Matrix4f projectionMatrix) {
        
        if (!isEnabled() || mc.world == null || mc.player == null) return;
        
        // Save GL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        
        // Setup render state for ESP (visible through walls)
        GL11.glDisable(GL11.GL_DEPTH_TEST);     // Disable depth test to see through walls
        GL11.glDisable(GL11.GL_TEXTURE_2D);     // Disable textures for solid color
        GL11.glEnable(GL11.GL_BLEND);           // Enable blending for transparency
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(UtilityMod.getInstance().getConfig().getEspLineWidth());
        
        // Get camera position for relative rendering
        Vec3d cameraPos = camera.getPos();
        
        // Iterate through all players
        for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            // Skip local player if in first person (optional, but usually desired)
            if (player == mc.player && mc.options.getPerspective().isFirstPerson()) continue;
            
            // Skip if player is dead or invalid
            if (!player.isAlive()) continue;
            
            // Check if we should render this player (vanish integration)
            if (player.isInvisible() && !UtilityMod.getInstance().getConfig().isAimbotTargetInvisible()) {
                continue;
            }
            
            // Calculate interpolated position for smooth rendering
            double x = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX()) - cameraPos.x;
            double y = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY()) - cameraPos.y;
            double z = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ()) - cameraPos.z;
            
            // Get player bounding box and offset to current position
            Box bb = player.getBoundingBox();
            Box offsetBox = new Box(
                bb.minX - player.getX() + x,
                bb.minY - player.getY() + y,
                bb.minZ - player.getZ() + z,
                bb.maxX - player.getX() + x,
                bb.maxY - player.getY() + y,
                bb.maxZ - player.getZ() + z
            );
            
            // Determine color based on vanish status
            int color;
            if (player.isInvisible() && UtilityMod.getInstance().getModuleManager().getVanishModule().isEnabled()) {
                color = UtilityMod.getInstance().getConfig().getVanishHighlightColor();
            } else {
                color = UtilityMod.getInstance().getConfig().getEspColor();
            }
            
            // Render the box
            if (UtilityMod.getInstance().getConfig().isEsp3D()) {
                RenderUtil.draw3DBox(matrices, offsetBox, color);
            } else {
                RenderUtil.draw2DBox(matrices, offsetBox, color, projectionMatrix, tickDelta);
            }
        }
        
        // Restore GL state
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}