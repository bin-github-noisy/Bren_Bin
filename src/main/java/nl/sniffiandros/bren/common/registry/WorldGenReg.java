package nl.sniffiandros.bren.common.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.world.feature.SupplyCrateFeature;

public class WorldGenReg {
    
    public static final ResourceKey<Feature> SUPPLY_CRATE_FEATURE_KEY =
        ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Bren.MODID, "supply_crate"));
    
    public static Holder.Reference<Feature> SUPPLY_CRATE_FEATURE_HOLDER;
    public static SupplyCrateFeature SUPPLY_CRATE_FEATURE;
    
    public static final ResourceKey<PlacedFeature> SUPPLY_CRATE_PLACED_KEY = 
        ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Bren.MODID, "supply_crate"));

    private static boolean featuresRegistered = false;

    public static void registerFeatures() {
        if (featuresRegistered) return;
        
        try {
            // TODO: Re-enable after fixing registration issue
            // Registry.register(BuiltInRegistries.FEATURE_TYPE, Identifier.fromNamespaceAndPath(Bren.MODID, "supply_crate"), SupplyCrateFeature.CODEC);
            
            SupplyCrateFeature.initPools();
            featuresRegistered = true;
        } catch (Exception e) {
        }
    }
    
    public static void registerPlacements() {
        try {
        } catch (Exception e) {
        }
    }
    
    public static void addBiomeModifications() {
        
        // TODO: Re-enable after fixing placed_feature registration
        /*
        try {
            BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_OVERWORLD),
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                SUPPLY_CRATE_PLACED_KEY
            );
        } catch (Exception e) {
        }
        */
    }
    
    public static void init() {
        registerFeatures();
        registerPlacements();
        addBiomeModifications();
    }
}