package net.juyoh.backoff.mixin;

import net.juyoh.backoff.CreateBackOff;
import net.juyoh.backoff.block.ResistorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.Logger;

@Mixin(BlockBehaviour.class)
public abstract class IsBlockCollidableMixin {

    @Inject(method = "getCollisionShape", at = @At(value = "HEAD"), cancellable = true)
    private void getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir){
        for (BlockPos entityPos : CreateBackOff.resistors.keySet()) {
            BlockEntity entity = level.getBlockEntity(entityPos);
            if (!(entity instanceof ResistorBlockEntity)) {
                continue;
            }
            if (!((ResistorBlockEntity) entity).isInsideWall(pos.getCenter())) {
                continue;
            }
            if (context instanceof EntityCollisionContext && ((EntityCollisionContext) context).getEntity() != null) {
                Entity collidingEntity = ((EntityCollisionContext) context).getEntity();
                if (collidingEntity instanceof Player) {
                    if (!((ResistorBlockEntity) entity).filterEqualsPlayer((collidingEntity.getUUID())) && !(((ResistorBlockEntity) entity).isPlayerOwner(collidingEntity.getUUID()))) {
                        cir.setReturnValue(Block.box(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F));
                        cir.cancel();
                        break;
                    }
                } else if (((ResistorBlockEntity) entity).filterEquals(collidingEntity.getName().getString())) {
                    cir.setReturnValue(Block.box(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F));
                    cir.cancel();
                    break;
                }
            } else {
                cir.setReturnValue(Block.box(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F));
                cir.cancel();
                break;
            }

        }
    }
}