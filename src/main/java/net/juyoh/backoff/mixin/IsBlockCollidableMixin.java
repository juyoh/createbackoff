package net.juyoh.backoff.mixin;

import net.juyoh.backoff.CreateBackOff;
import net.juyoh.backoff.block.ModBlockEntities;
import net.juyoh.backoff.block.ResistorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ConcurrentModificationException;
import java.util.Optional;

@Mixin(BlockBehaviour.class)
public abstract class IsBlockCollidableMixin {

    @Inject(method = "getCollisionShape", at = @At(value = "HEAD"), cancellable = true)
    private void getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir){

        int maxSize = ResistorBlockEntity.getMaxSize();

        try {
            for (BlockPos entityPos : CreateBackOff.resistors.keySet()) {
                AABB maxBox = AABB.ofSize(entityPos.getCenter().add(-0.5d, -0.5d, -0.5d), maxSize * 2, maxSize * 2, maxSize * 2);
                if (!maxBox.contains(pos.getCenter())) {
                    continue;
                }
                Optional<ResistorBlockEntity> entity = null;
                try {
                    if (!level.getBlockState(entityPos).hasBlockEntity()) {
                        continue;
                    }
                    entity = level.getBlockEntity(entityPos, ModBlockEntities.RESISTOR_BE.get());
                    if (entity.isEmpty()) {
                        continue;
                    }
                } catch (Exception e) {
                    CreateBackOff.LOGGER.warn("Tried to access block entities in ungenerated chunk");
                }
                if (entity == null) {
                    continue;
                }
                if (entity.isEmpty()) {
                    continue;
                }

                if (!entity.get().isInsideWall(pos.getCenter())) {
                    continue;
                }
                entity.get().isColliding = false;
                if (context instanceof EntityCollisionContext && ((EntityCollisionContext) context).getEntity() != null) {
                    Entity collidingEntity = ((EntityCollisionContext) context).getEntity();
                    if (entity.get().shouldResistEntity(collidingEntity)) {
                        cir.setReturnValue(Block.box(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F));
                        cir.cancel();
                        //only output redstone if an entity collides (rather than a particle or smth)
                        entity.get().isColliding = true;
                        break;
                    }
                } else {
                    cir.setReturnValue(Block.box(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F));
                    cir.cancel();
                    break;
                }

            }
        } catch (ConcurrentModificationException e) {
            CreateBackOff.LOGGER.warn("Tried to iterate over Resistors map while it was being changed");
        }
    }
}