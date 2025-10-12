package net.juyoh.backoff;

import net.createmod.ponder.foundation.PonderIndex;
import net.juyoh.backoff.ponder.ModPonders;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateBackOff.MODID, dist = Dist.CLIENT)
public class CreateBackOffClient {

    public CreateBackOffClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

    }

    public static void clientSetup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new ModPonders());
    }
}
