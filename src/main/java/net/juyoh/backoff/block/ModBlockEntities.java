package net.juyoh.backoff.block;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.juyoh.backoff.CreateBackOff;
import net.minecraft.resources.ResourceLocation;

public class ModBlockEntities {
    public static final BlockEntityEntry<ResistorBlockEntity> LOOT_COLLECTOR = CreateBackOff.REGISTRATE
            .blockEntity("resistor", ResistorBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual.of(PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateBackOff.MODID, "shaft_nub_vertical"))))
            .renderer(() -> KineticBlockEntityRenderer::new)
            .validBlocks(CreateBackOff.RESISTOR)
            .register();

    public static void register() {
    }
}