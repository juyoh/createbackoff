package net.juyoh.backoff.ponder;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.juyoh.backoff.CreateBackOff;
import net.minecraft.resources.ResourceLocation;

import static com.simibubi.create.infrastructure.ponder.AllCreatePonderTags.KINETIC_APPLIANCES;

public class ModPonders implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateBackOff.MODID;
    }
    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?,?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(CreateBackOff.RESISTOR)
                .addStoryBoard("resistor_intro", ResistorScenes::intro)
                .addStoryBoard("resistor_speed", ResistorScenes::speed)
                .addStoryBoard("resistor_filter", ResistorScenes::filter);
        HELPER.forComponents(CreateBackOff.LEGAL_PAPER).addStoryBoard("resistor_filter", ResistorScenes::filter);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?,?>> TAG_HELPER = helper.withKeyFunction(RegistryEntry::getId);
        TAG_HELPER.addToTag(KINETIC_APPLIANCES).add(CreateBackOff.RESISTOR);
    }
}
