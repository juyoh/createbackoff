package net.juyoh.backoff.network;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.juyoh.backoff.CreateBackOff;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record ResistorConfigPacket(int x, int y, int z, String filter) implements ServerboundPacketPayload {
    public static final Type<ResistorConfigPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CreateBackOff.MODID, "resistor_config"));

    public static final StreamCodec<ByteBuf, ResistorConfigPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ResistorConfigPacket::x,
            ByteBufCodecs.INT,
            ResistorConfigPacket::y,
            ByteBufCodecs.INT,
            ResistorConfigPacket::z,
            ByteBufCodecs.STRING_UTF8,
            ResistorConfigPacket::filter,
            ResistorConfigPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return Packets.RESISTOR_CONFIG;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    @Override
    public void handle(ServerPlayer player) {

    }
}
