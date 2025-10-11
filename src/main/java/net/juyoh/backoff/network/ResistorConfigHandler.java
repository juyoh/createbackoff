package net.juyoh.backoff.network;

import net.juyoh.backoff.block.ResistorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class ResistorConfigHandler implements IPayloadHandler<ResistorConfigPacket> {
    @Override
    public void handle(ResistorConfigPacket resizerConfigSyncPayload, IPayloadContext iPayloadContext) {
        BlockEntity blockEntity = iPayloadContext.player().level().getBlockEntity(new BlockPos(resizerConfigSyncPayload.x(), resizerConfigSyncPayload.y(), resizerConfigSyncPayload.z()));
        if (blockEntity instanceof ResistorBlockEntity) {
            ((ResistorBlockEntity) blockEntity).setFilter(resizerConfigSyncPayload.filter());
        }
    }
}
