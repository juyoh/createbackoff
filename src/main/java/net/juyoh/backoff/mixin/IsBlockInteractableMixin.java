package net.juyoh.backoff.mixin;

import net.juyoh.backoff.CreateBackOff;
import net.juyoh.backoff.block.ResistorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ConcurrentModificationException;

@Mixin(Player.class)
public abstract class IsBlockInteractableMixin {

    @Inject(method = "blockActionRestricted", at = @At(value = "HEAD"), cancellable = true)
    private void blockActionRestricted(Level level, BlockPos pos, GameType gameMode, CallbackInfoReturnable<Boolean> cir){
        try {
            for (BlockPos entityPos : CreateBackOff.resistors.keySet()) {

                BlockEntity entity = CreateBackOff.getBlockEntityAt(entityPos, level);
                Player player = (Player)((Object) this);
                if (!(entity instanceof ResistorBlockEntity)) {
                    continue;
                }
                if (!((ResistorBlockEntity) entity).isInside(pos.getCenter())) {
                    continue;
                }
                if (((ResistorBlockEntity) entity).isPlayerOwner(player.getUUID())) {
                    continue;
                }
                cir.setReturnValue(true);
                cir.cancel();
            }
        } catch (ConcurrentModificationException e) {
            CreateBackOff.LOGGER.warn("Tried to iterate over Resistors map while it was being changed");
        }
    }
    @Inject(method = "canInteractWithBlock", at = @At(value = "HEAD"), cancellable = true)
    private void canInteractWithBlock(BlockPos pos, double distance, CallbackInfoReturnable<Boolean> cir){
        try {
            for (BlockPos entityPos : CreateBackOff.resistors.keySet()) {
                Player player = (Player)((Object) this);
                BlockEntity entity = CreateBackOff.getBlockEntityAt(entityPos, player.level());

                if (!(entity instanceof ResistorBlockEntity)) {
                    continue;
                }
                if (!((ResistorBlockEntity) entity).isInside(pos.getCenter())) {
                    continue;
                }
                if (((ResistorBlockEntity) entity).isPlayerOwner(player.getUUID())) {
                    continue;
                }
                cir.setReturnValue(false);
                cir.cancel();
            }
        } catch (ConcurrentModificationException e) {
            CreateBackOff.LOGGER.warn("Tried to iterate over Resistors map while it was being changed");
        }
    }
    @Inject(method = "canInteractWithEntity(Lnet/minecraft/world/entity/Entity;D)Z", at = @At(value = "HEAD"), cancellable = true)
    private void canInteractWithEntity(Entity entity, double distance, CallbackInfoReturnable<Boolean> cir){
        try {
            for (BlockPos entityPos : CreateBackOff.resistors.keySet()) {
                Player player = (Player)((Object) this);
                BlockEntity blockEntity = CreateBackOff.getBlockEntityAt(entityPos, player.level());

                if (!(blockEntity instanceof ResistorBlockEntity)) {
                    continue;
                }
                if (!((ResistorBlockEntity) blockEntity).isInside(entity.getPosition(0))) {
                    continue;
                }
                if (((ResistorBlockEntity) blockEntity).isPlayerOwner(player.getUUID())) {
                    continue;
                }
                cir.setReturnValue(false);
                cir.cancel();
            }
        } catch (ConcurrentModificationException e) {
            CreateBackOff.LOGGER.warn("Tried to iterate over Resistors map while it was being changed");
        }
    }
}