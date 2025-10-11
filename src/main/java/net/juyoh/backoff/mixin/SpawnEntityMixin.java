package net.juyoh.backoff.mixin;

import net.juyoh.backoff.CreateBackOff;
import net.juyoh.backoff.block.ResistorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class SpawnEntityMixin {
    @Inject(method = "checkMobSpawnRules", at = @At(value = "HEAD"), cancellable = true)
    private static void spawn(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir){
        for (BlockPos pos1 : CreateBackOff.resistors.keySet()) {
            if (CreateBackOff.resistors.get(pos1) != ((Level) level).dimension()) {
                return;
            }
            BlockEntity entity = CreateBackOff.getBlockEntityAt(pos1, (Level) level);
            if (!(entity instanceof ResistorBlockEntity)) {
                return;
            }
            if (!((ResistorBlockEntity) entity).isInside(pos.getCenter())) {
                return;
            }
            if (((ResistorBlockEntity) entity).filterEquals(Component.translatable(type.getDescriptionId()).getString())) {
                cir.setReturnValue(false);
                cir.cancel();
                return;
            }
        }

    }

}