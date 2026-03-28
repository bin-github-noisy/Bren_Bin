package nl.sniffiandros.bren.common.registry.custom.types;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import nl.sniffiandros.bren.common.registry.custom.PoseType;

public class FlareGunItem extends ShotgunItem  {
    public FlareGunItem(Item.Properties settings) {
        super(settings);
    }
    @Override
    public int reloadSpeed() {
        return 6;
    }

    @Override
    public int getMaxCapacity(ItemStack stack) {
        // 为霰弹枪设置合适的容量，通常霰弹枪有6-8发容量
        // 这里设置为8发，可以根据实际需求调整
        return 1; // 修复：直接返回int值，而不是Optional.of(8)
    }

    @Override
    public PoseType holdingPose() {
        return PoseType.REVOLVER;
    }

}
