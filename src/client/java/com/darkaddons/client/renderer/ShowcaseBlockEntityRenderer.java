package com.darkaddons.client.renderer;

import com.darkaddons.block.entity.ShowcaseBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class ShowcaseBlockEntityRenderer implements BlockEntityRenderer<ShowcaseBlockEntity, ShowcaseBlockEntityRenderer.ShowcaseRenderState> {

    @SuppressWarnings("unused")
    public ShowcaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public @NotNull ShowcaseRenderState createRenderState() {
        return new ShowcaseRenderState();
    }

    @Override
    public void extractRenderState(ShowcaseBlockEntity entity, ShowcaseRenderState state, float partialTick, Vec3 cameraPos, CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPos, crumblingOverlay);

        ItemStack stack = entity.getDisplayedStack();
        state.light = entity.getLevel() != null ? LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos()) : LightTexture.FULL_BRIGHT;

        long time = entity.getLevel() != null ? entity.getLevel().getGameTime() : 0;
        state.yOffset = (float) Math.sin((time + partialTick) / 8.0) / 10.0f;
        state.rotation = (time + partialTick) * 4;

        state.itemRenderState.clear();

        if (!stack.isEmpty()) {
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.itemRenderState, stack, ItemDisplayContext.GROUND, entity.getLevel(), null, 0);

            if (Vec3.atCenterOf(entity.getBlockPos()).closerThan(cameraPos, 64)) {
                state.label = stack.getHoverName();
            } else {
                state.label = null;
            }
        } else {
            state.label = null;
        }
    }

    @Override
    public void submit(ShowcaseRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (state.itemRenderState.isEmpty()) return;

        poseStack.pushPose();

        poseStack.translate(0.5, 0.4 + state.yOffset, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation));
        poseStack.scale(1.3f, 1.3f, 1.3f);

        state.itemRenderState.submit(poseStack, submitNodeCollector, state.light, OverlayTexture.NO_OVERLAY, 0
        );

        poseStack.popPose();

        if (state.label != null) {
            renderLabel(state, poseStack, Minecraft.getInstance().renderBuffers().bufferSource());
        }
    }

    //Errors here
    private void renderLabel(ShowcaseRenderState state, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.pushPose();

        // 1. Position and Rotation
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        poseStack.scale(-0.025f, -0.025f, 0.025f);

        Matrix4f matrix4f = poseStack.last().pose();
        float x = -Minecraft.getInstance().font.width(state.label) / 2.0f;

        // Calculate vanilla background opacity
        int opacity = (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;

        Font font = Minecraft.getInstance().font;
        MultiBufferSource.BufferSource source = (buffer instanceof MultiBufferSource.BufferSource s) ? s : null;

        // --- PASS 1: SEE-THROUGH LAYER (Visible through walls) ---
        // This draws the text "behind" walls. We use a transparent color so it doesn't look weird.
        font.drawInBatch(
                state.label,
                x,
                0,
                0xFF00FF00, // Transparent White text
                true,      // No shadow
                matrix4f,
                buffer,
                Font.DisplayMode.SEE_THROUGH,
                opacity,    // Background
                LightTexture.FULL_BRIGHT
        );
        // FORCE FLUSH 1
        if (source != null) source.endBatch();

        // --- PASS 2: NORMAL LAYER (Crisp text in front) ---
        // This draws the standard text on top. If there is no wall, this covers the see-through layer.
        font.drawInBatch(
                state.label,
                x,
                0,
                0xFFFFFFFF, // Full White text
                false,      // No shadow
                matrix4f,
                buffer,
                Font.DisplayMode.NORMAL,
                0,          // No background (already drawn by Pass 1)
                LightTexture.FULL_BRIGHT
        );
        // FORCE FLUSH 2
        if (source != null) source.endBatch();

        poseStack.popPose();
    }

    public static class ShowcaseRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState itemRenderState = new ItemStackRenderState();

        public float yOffset;
        public float rotation;
        public int light;
        public Component label;
    }
}