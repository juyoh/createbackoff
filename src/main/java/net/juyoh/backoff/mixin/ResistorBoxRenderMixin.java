package net.juyoh.backoff.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.createmod.catnip.outliner.Outliner;
import net.juyoh.backoff.CreateBackOff;
import net.juyoh.backoff.block.ResistorBlockEntity;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class ResistorBoxRenderMixin {

    @Shadow @Final private RenderBuffers renderBuffers;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getModelViewStack()Lorg/joml/Matrix4fStack;", shift = At.Shift.BEFORE))
    private void renderLevel(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci){
        ResourceLocation BORDER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/forcefield.png");
        Player player = Minecraft.getInstance().player;
        VertexConsumer vertexConsumer = this.renderBuffers.bufferSource().getBuffer(RenderType.debugFilledBox());
        Matrix4f matrix4f = RenderSystem.getModelViewMatrix();
        if (player.level() == null || camera == null) {
            return;
        }
        for (BlockPos pos1 : CreateBackOff.resistors.keySet()) {
            if (CreateBackOff.resistors.get(pos1) != player.level().dimension()) {
                continue;
            }
            BlockEntity entity = player.level().getBlockEntity(pos1);
            if (!(entity instanceof ResistorBlockEntity)) {
                continue;
            }
            float size = ((ResistorBlockEntity) entity).calculateSize();
            if (size == 0) {
                continue;
            }

            float ms = Util.getMillis();
            float red = 0.9f;
            float green = 0.3f;
            float blue = 0.4f;
            float alpha = (float) (0.4f + (Math.sin(ms / 2400d) / 6f));

            Outliner.getInstance().showAABB("resistorBox" + pos1.toShortString(), AABB.ofSize(pos1.getCenter().add(-0.5d, -0.5d, -0.5d), size * 2, size * 2, size * 2))
                    .colored(0x7f1f0c)
                    .lineWidth(1 / 16f);

            if (((ResistorBlockEntity) entity).isInside(camera.getPosition()) || ((ResistorBlockEntity) entity).isInsideWall(camera.getPosition())) {
                Outliner.getInstance().showAABB("resistorBoxInside" + pos1.toShortString(), AABB.ofSize(pos1.getCenter().add(-0.5d, -0.5d, -0.5d), (size * 2) - 2, (size * 2) - 2, (size * 2) - 2))
                        .colored(0x7f1f0c)
                        .lineWidth(1 / 16f);
            }

            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderTexture(0, BORDER_TEXTURE);
            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            RenderSystem.setShaderColor(red, green, blue, alpha);
            RenderSystem.setShader(GameRenderer::getRendertypeTranslucentShader);
            RenderSystem.polygonOffset(-3.0F, -3.0F);
            RenderSystem.enablePolygonOffset();
            RenderSystem.disableCull();

            float x = (float)(pos1.getBottomCenter().x() - camera.getPosition().x());
            float y = (float)(pos1.getBottomCenter().y() - camera.getPosition().y());
            float z = (float)(pos1.getBottomCenter().z() - camera.getPosition().z());

            float minX = x - (size + 0.5f);
            float maxX = x + (size - 0.5f);
            float minY = y - size;
            float maxY = y + size;
            float minZ = z - (size + 0.5f);
            float maxZ = z + (size - 0.5f);

            float uv = (ms % 1000) / 1000;

            vertexConsumer.addVertex(matrix4f, minX, minY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, minY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, minY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, minY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, maxY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, maxY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, maxY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, minY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, maxY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, minY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, minY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, minY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, maxY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, maxY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, maxY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, minY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, maxY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, minY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, minY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, minY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, minY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, minY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, minY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, maxY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, maxY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, minX, maxY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, maxY, minZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, maxY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, maxY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);
            vertexConsumer.addVertex(matrix4f, maxX, maxY, maxZ).setColor(red, green, blue, alpha).setUv(uv, uv);

            RenderSystem.enableCull();
            RenderSystem.polygonOffset(0.0F, 0.0F);
            RenderSystem.disablePolygonOffset();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);

        }

    }

}