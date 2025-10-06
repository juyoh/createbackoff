package net.juyoh.backoff.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.juyoh.backoff.CreateBackOff;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItemComponents {
    public static final Codec<EntityTypeComponent> ENTITY_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(EntityTypeComponent::name)
            ).apply(instance, EntityTypeComponent::new)
    );

    public static final StreamCodec<ByteBuf, EntityTypeComponent> ENTITY_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, EntityTypeComponent::name,
            EntityTypeComponent::new
    );

    // In another class
// The specialized DeferredRegister.DataComponents simplifies data component registration and avoids some generic inference issues with the `DataComponentType.Builder` within a `Supplier`
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateBackOff.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EntityTypeComponent>> ENTITY_COMPONENT = REGISTRAR.registerComponentType(
            "entity_codec",
            builder -> builder
                    .persistent(ENTITY_CODEC)
                    // Note we use a unit stream codec here
                    .networkSynchronized(ENTITY_STREAM_CODEC)
    );
    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }
}
