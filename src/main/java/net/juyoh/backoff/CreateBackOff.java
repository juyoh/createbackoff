package net.juyoh.backoff;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.createmod.catnip.lang.FontHelper;
import net.juyoh.backoff.block.ModBlockEntities;
import net.juyoh.backoff.block.ResistorBlock;
import net.juyoh.backoff.block.ResistorBlockEntity;
import net.juyoh.backoff.config.ModConfigs;
import net.juyoh.backoff.config.ModStress;
import net.juyoh.backoff.item.LegalPaperItem;
import net.juyoh.backoff.item.ModItemComponents;
import net.juyoh.backoff.item.RestrainingOrderItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateBackOff.MODID)
public class CreateBackOff {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "createbackoff";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "createbackoff" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "createbackoff" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID).defaultCreativeTab(ModTabs.BACK_OFF.getKey());
    static {
        REGISTRATE.setTooltipModifierFactory(item ->
                new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
        );
    }
    public static final BlockEntry<ResistorBlock> RESISTOR = REGISTRATE.block("resistor", ResistorBlock::new)
            .transform(ModStress.setImpact(8))
            .addLayer(() -> RenderType::cutoutMipped)
            .register();

    public static final DeferredItem<BlockItem> RESISTOR_ITEM = ITEMS.registerSimpleBlockItem("resistor", RESISTOR);

    public static final DeferredItem<Item> LEGAL_PAPER = ITEMS.registerItem("legal_paper", LegalPaperItem::new, new Item.Properties().stacksTo(16));
    public static final DeferredItem<RestrainingOrderItem> RESTRAINING_ORDER = ITEMS.registerItem("restraining_order", RestrainingOrderItem::new, new Item.Properties().stacksTo(16));

    public static Map<BlockPos, ResourceKey<Level>> resistors = new HashMap<>();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CreateBackOff(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        ModBlockEntities.register();
        ModTabs.register(modEventBus);
        ModItemComponents.register(modEventBus);

        ModLoadingContext loadingContext = ModLoadingContext.get();
        ModConfigs.register(loadingContext, modContainer);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (CreateBackOff) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
    @SubscribeEvent
    public void onWorldLeave(LevelEvent.Unload event) {
        resistors = new HashMap<>();
    }
    //@SubscribeEvent
    //public void onMobSpawn(MobSpawnEvent.SpawnPlacementCheck event) {
    //    BlockPos startPos = event.getPos();
    //    int maxSize = ResistorBlockEntity.getMaxSize();
    //    for (int x = -maxSize; x < maxSize; x++) {
    //        for (int z = -maxSize; z < maxSize; z++) {
    //            for (int y = -maxSize; y < maxSize; y++) {
    //                BlockEntity entity = event.getLevel().getBlockEntity(startPos.offset(x, y, z));
    //                if (entity instanceof ResistorBlockEntity) {
    //                    if (((ResistorBlockEntity) entity).isInside(startPos.getCenter())) {
    //                        event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
    //                        break;
    //                    }
    //                }
    //            }
    //        }
    //    }
//
    //}
}
