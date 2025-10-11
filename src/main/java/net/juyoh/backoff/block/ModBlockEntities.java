package net.juyoh.backoff.block;

import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.juyoh.backoff.CreateBackOff;

public class ModBlockEntities {
    public static final BlockEntityEntry<ResistorBlockEntity> RESISTOR_BE = CreateBackOff.REGISTRATE
            .blockEntity("resistor", ResistorBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual.of(ResistorRenderer.NUB_PARTIAL))
            .renderer(() -> ResistorRenderer::new)
            .validBlocks(CreateBackOff.RESISTOR)
            .register();

    public static void register() {

    }
}