package com.soviet.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

import java.util.Random;

public class GlitchRenderer {
    private final Random random = new Random();
    private long lastGlitchTime = 0;
    private boolean isGlitching = false;
    private int glitchDuration = 0;

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        long currentTime = System.currentTimeMillis();
        
        if (!isGlitching && currentTime - lastGlitchTime > 30000) {
            if (random.nextFloat() < 0.01f) {
                isGlitching = true;
                glitchDuration = random.nextInt(100) + 20;
                lastGlitchTime = currentTime;
            }
        }
        
        if (isGlitching) {
            renderGlitch(event.getGuiGraphics().pose());
            glitchDuration--;
            if (glitchDuration <= 0) {
                isGlitching = false;
            }
        }
    }
    
    private void renderGlitch(PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        
        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int w = random.nextInt(width / 4);
            int h = random.nextInt(10) + 1;
            
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            int a = random.nextInt(128) + 64;
            
            bufferBuilder.vertex(matrix, x, y + h, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + w, y + h, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + w, y, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x, y, 0).color(r, g, b, a).endVertex();
        }
        
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
    }
}
