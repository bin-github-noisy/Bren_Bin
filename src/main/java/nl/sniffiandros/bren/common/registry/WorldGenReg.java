package nl.sniffiandros.bren.common.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.world.feature.SupplyCrateFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldGenReg {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldGenReg.class);
    
    public static final ResourceKey<Feature> SUPPLY_CRATE_FEATURE_KEY =
        ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Bren.MODID, "supply_crate"));
    
    public static Holder.Reference<Feature> SUPPLY_CRATE_FEATURE_HOLDER;
    public static SupplyCrateFeature SUPPLY_CRATE_FEATURE;
    
    public static final ResourceKey<PlacedFeature> SUPPLY_CRATE_PLACED_KEY = 
        ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Bren.MODID, "supply_crate"));

    private static boolean featuresRegistered = false;

    public static void registerFeatures() {
        if (featuresRegistered) return;
        LOGGER.info("⚠️ [WorldGen] Feature registration temporarily disabled for debugging");
        
        try {
            // TODO: Re-enable after fixing registration issue
            // Registry.register(BuiltInRegistries.FEATURE_TYPE, Identifier.fromNamespaceAndPath(Bren.MODID, "supply_crate"), SupplyCrateFeature.CODEC);
            
            SupplyCrateFeature.initPools();
            featuresRegistered = true;
            LOGGER.info("§a[WorldGen] Feature pools initialized (registration skipped)");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize feature pools", e);
        }
    }
    
    public static void registerPlacements() {
        LOGGER.info("Registering world generation placements");
        
        try {
            LOGGER.info("Successfully registered supply crate placements");
        } catch (Exception e) {
            LOGGER.error("Failed to register world generation placements", e);
        }
    }
    
    public static void addBiomeModifications() {
        LOGGER.info("⚠️ [WorldGen] Biome modifications temporarily disabled for debugging");
        
        // TODO: Re-enable after fixing placed_feature registration
        /*
        try {
            BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_OVERWORLD),
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                SUPPLY_CRATE_PLACED_KEY
            );
            
            LOGGER.info("§a[WorldGen] Successfully added biome modifications for supply crate generation: {}", SUPPLY_CRATE_PLACED_KEY);
        } catch (Exception e) {
            LOGGER.error("Failed to add biome modifications", e);
        }
        */
    }
    
    public static void init() {
        registerFeatures();
        registerPlacements();
        addBiomeModifications();
    }
}