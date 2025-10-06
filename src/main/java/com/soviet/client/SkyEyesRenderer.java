package com.soviet.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkyEyesRenderer {
    private final Random random = new Random();
    private final List<Eye> eyes = new ArrayList<>();
    private long lastSpawnTime = 0;

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastSpawnTime > 60000 && random.nextFloat() < 0.005f) {
            spawnEye();
            lastSpawnTime = currentTime;
        }
        
        eyes.removeIf(eye -> {
            eye.lifetime--;
            return eye.lifetime <= 0;
        });
        
        if (!eyes.isEmpty()) {
            renderEyes(event.getPoseStack());
        }
    }
    
    private void spawnEye() {
        Eye eye = new Eye();
        eye.x = random.nextFloat() * 2.0f - 1.0f;
        eye.y = random.nextFloat() * 0.5f + 0.3f;
        eye.z = random.nextFloat() * 2.0f - 1.0f;
        eye.size = random.nextFloat() * 0.3f + 0.2f;
        eye.lifetime = random.nextInt(200) + 100;
        eye.blinkTimer = random.nextInt(40);
        eyes.add(eye);
    }
    
    private void renderEyes(PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.depthMask(false);
        
        poseStack.pushPose();
        
        for (Eye eye : eyes) {
            eye.blinkTimer--;
            if (eye.blinkTimer <= 0) {
                eye.blinkTimer = random.nextInt(60) + 40;
            }
            
            boolean isBlinking = eye.blinkTimer < 5;
            if (!isBlinking) {
                renderEye(poseStack, eye);
            }
        }
        
        poseStack.popPose();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
    
    private void renderEye(PoseStack poseStack, Eye eye) {
        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        
        float x = eye.x * 50.0f;
        float y = eye.y * 50.0f + 20.0f;
        float z = eye.z * 50.0f;
        float size = eye.size * 2.0f;
        
        int alpha = Math.min(255, eye.lifetime * 2);
        
        bufferBuilder.vertex(matrix, x - size, y - size, z).color(255, 255, 255, alpha).endVertex();
        bufferBuilder.vertex(matrix, x + size, y - size, z).color(255, 255, 255, alpha).endVertex();
        bufferBuilder.vertex(matrix, x + size, y + size, z).color(255, 255, 255, alpha).endVertex();
        bufferBuilder.vertex(matrix, x - size, y + size, z).color(255, 255, 255, alpha).endVertex();
        
        float pupilSize = size * 0.4f;
        bufferBuilder.vertex(matrix, x - pupilSize, y - pupilSize, z - 0.1f).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.vertex(matrix, x + pupilSize, y - pupilSize, z - 0.1f).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.vertex(matrix, x + pupilSize, y + pupilSize, z - 0.1f).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.vertex(matrix, x - pupilSize, y + pupilSize, z - 0.1f).color(0, 0, 0, alpha).endVertex();
        
        BufferUploader.drawWithShader(bufferBuilder.end());
    }
    
    private static class Eye {
        float x, y, z;
        float size;
        int lifetime;
        int blinkTimer;
    }
}
