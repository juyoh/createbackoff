package net.juyoh.backoff;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateBackOff.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BACK_OFF = REGISTER.register("back_off",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("Create: Back Off!"))
                    .icon(CreateBackOff.RESISTOR::asStack)
                    .displayItems(List.of(
                            CreateBackOff.RESISTOR_ITEM,
                            CreateBackOff.LEGAL_PAPER
                    ))
                    .build());

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
