package com.darkaddons.client.renderer;

import com.darkaddons.block.entity.DisplayBaseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class DisplayBaseEntityRenderer implements BlockEntityRenderer<DisplayBaseEntity, DisplayBaseEntityRenderer.DisplayRenderState> {

    @SuppressWarnings("unused")
    public DisplayBaseEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public @NotNull DisplayBaseEntityRenderer.DisplayRenderState createRenderState() {
        return new DisplayRenderState();
    }

    @Override
    public void extractRenderState(DisplayBaseEntity entity, DisplayRenderState state, float partialTick,
                                   Vec3 cameraPos, CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPos, crumblingOverlay);

        ItemStack stack = entity.getDisplayedStack();

        state.rotation = entity.getRotation();

        state.itemRenderState.clear();

        if (!stack.isEmpty()) {
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                    state.itemRenderState, stack, ItemDisplayContext.FIXED, entity.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(DisplayRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState cameraRenderState) {
        if (state.itemRenderState.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.63, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation));
        poseStack.scale(30f, 30f, 30f); // it was 1.2f
        state.itemRenderState.submit(poseStack, collector, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static class DisplayRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
        public float rotation;
    }
}