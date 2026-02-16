package com.darkaddons.client.renderer;

import com.darkaddons.block.entity.ShowcaseBlockEntity;
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
public class ShowcaseBlockEntityRenderer implements BlockEntityRenderer<ShowcaseBlockEntity, ShowcaseBlockEntityRenderer.ShowcaseRenderState> {

    @SuppressWarnings("unused")
    public ShowcaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public @NotNull ShowcaseRenderState createRenderState() {
        return new ShowcaseRenderState();
    }

    @Override
    public void extractRenderState(ShowcaseBlockEntity entity, ShowcaseRenderState state, float partialTick,
                                   Vec3 cameraPos, CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPos, crumblingOverlay);

        ItemStack stack = entity.getDisplayedStack();

        long time = entity.getLevel() != null ? entity.getLevel().getGameTime() : 0;
        state.yOffset = (float) Math.sin((time + partialTick) / 8.0) / 10.0f;
        state.rotation = (time + partialTick) * 4;

        state.itemRenderState.clear();

        if (!stack.isEmpty()) {
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                    state.itemRenderState, stack, ItemDisplayContext.GROUND, entity.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(ShowcaseRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState cameraRenderState) {
        if (state.itemRenderState.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.35 + state.yOffset, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation));
        poseStack.scale(1.2f, 1.2f, 1.2f);
        state.itemRenderState.submit(poseStack, collector, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static class ShowcaseRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
        public float yOffset;
        public float rotation;
    }
}