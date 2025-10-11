package net.juyoh.backoff.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.juyoh.backoff.CreateBackOff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ResistorRenderer extends KineticBlockEntityRenderer<ResistorBlockEntity> {
    public static final PartialModel NUB_PARTIAL = PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateBackOff.MODID, "shaft_nub_vertical"));
    public static final PartialModel CUBE_PARTIAL = PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateBackOff.MODID, "cube"));
    public ResistorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ResistorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        SuperByteBuffer cubeRender1 = CachedBuffers.partial(CUBE_PARTIAL, CreateBackOff.RESISTOR.getDefaultState());
        SuperByteBuffer cubeRender2 = CachedBuffers.partial(CUBE_PARTIAL, CreateBackOff.RESISTOR.getDefaultState());
        if (be.getSpeed() == 0) {
            cubeRender1.light(1);
            cubeRender2.light(1);
            cubeRender1.color(40, 20, 8, 40);
            cubeRender2.color(40, 20, 8, 40);
        } else {
            float speed = be.calculateSize() / 128f;
            float sin1 = (float) (Math.sin(Minecraft.getInstance().levelRenderer.getTicks() * 0.7f) * (64f + speed));
            float sin2 = (float) (Math.sin(Minecraft.getInstance().levelRenderer.getTicks() * 0.5f) * (48f + speed));
            cubeRender1.light(255);
            cubeRender1.color(255, 255, 255, (int) (128 + sin1));
            cubeRender2.scale(1.4f);
            cubeRender2.translate(-0.15f, -0.15f, -0.15f);
            cubeRender2.light(255);
            cubeRender2.color(255, 255, 255, (int) (128 + sin2));
        }
        cubeRender1.renderInto(ms, buffer.getBuffer(RenderType.TRANSLUCENT));
        cubeRender2.renderInto(ms, buffer.getBuffer(RenderType.TRANSLUCENT));
    }

    @Override
    public boolean shouldRenderOffScreen(ResistorBlockEntity blockEntity) {
        return true;
    }
    @Override
    protected BlockState getRenderedBlockState(ResistorBlockEntity be) {
        return Blocks.AIR.defaultBlockState();
    }
}
