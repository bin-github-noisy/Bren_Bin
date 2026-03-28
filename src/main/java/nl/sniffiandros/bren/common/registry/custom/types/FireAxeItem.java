package nl.sniffiandros.bren.common.registry.custom.types;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class FireAxeItem extends AxeItem {

    public FireAxeItem(ToolMaterial toolMaterial, float f, float g, Item.Properties properties) {
        super(toolMaterial, f, g, properties);
    }

    public FireAxeItem(Item.Properties properties) {
        super(VanillaToolMaterials.NETHERITE, 6.0F, -3.0F, properties);
    }

}
