package net.juyoh.backoff.mixin;

import net.juyoh.backoff.CreateBackOff;
import net.juyoh.backoff.block.ResistorBlockEntity;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ConcurrentModificationException;

@Mixin(MultiPlayerGameMode.class)
public abstract class IsBlockUseableMixin {

    @Inject(method = "useItemOn", at = @At(value = "HEAD"), cancellable = true)
    private void useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir){
        try {
            for (BlockPos entityPos : CreateBackOff.resistors.keySet()) {
                BlockEntity entity = CreateBackOff.getBlockEntityAt(entityPos, player.level());

                if (!(entity instanceof ResistorBlockEntity)) {
                    continue;
                }
                if (!((ResistorBlockEntity) entity).isInside(result.getBlockPos().getCenter())) {
                    continue;
                }
                if (((ResistorBlockEntity) entity).isPlayerOwner(player.getUUID())) {
                    continue;
                }
                cir.setReturnValue(InteractionResult.FAIL);
                cir.cancel();
            }
        } catch (ConcurrentModificationException e) {
            CreateBackOff.LOGGER.warn("Tried to iterate over Resistors map while it was being changed");
        }
    }
}