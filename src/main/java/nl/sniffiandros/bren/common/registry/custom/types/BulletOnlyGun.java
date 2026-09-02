package nl.sniffiandros.bren.common.registry.custom.types;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.ItemReg;
import nl.sniffiandros.bren.common.utils.GunHelper;

public abstract class BulletOnlyGun extends GunItem {

    private static final String BULLET_COUNT_KEY = "BulletCount";

    public BulletOnlyGun(Item.Properties settings) {
        super(settings);
    }

    @Override
    public int getMaxCapacity(ItemStack stack) {
        // 为霰弹枪设置合适的容量，通常霰弹枪有6-8发容量
        // 这里设置为6发，可以根据实际需求调整
        return 6; // 修复：直接返回int值，而不是Optional.of(6)
    }

    @Override
    public int getContents(ItemStack stack) {
        // 修复：返回int类型而不是Optional<Integer>
        var nbtComponent = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);

        if (nbtComponent != null) {
            return nbtComponent.copyTag().getInt(BULLET_COUNT_KEY).orElse(0);
        } else {
            return 0; // 默认值，直接返回int
        }
    }

    public void addContent(ItemStack stack) {
        int currentCount = getContents(stack); // 修复：直接使用int值，不需要.orElse(0)
        int newCount = Math.min(currentCount + 1, getMaxCapacity(stack)); // 修复：直接使用int值，不需要.orElse(0)

        var nbt = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        nbt.putInt(BULLET_COUNT_KEY, newCount);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(nbt));
    }

    @Override
    public void useBullet(ItemStack stack) {
        int currentCount = getContents(stack);
        int newCount = Math.max(currentCount - 1, 0);

        var nbt = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        nbt.putInt(BULLET_COUNT_KEY, newCount);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(nbt));
    }

    @Override
    public boolean isEmpty(ItemStack stack) {
        return getContents(stack) <= 0;
    }

    @Override
    public void onReload(Player player) {
        ItemStack stack = player.getMainHandItem();
        ItemCooldowns cooldownManager = player.getCooldowns();

        if (player instanceof IGunUser gunUser && !cooldownManager.isOnCooldown(stack)) {
            ItemStack bullets = Bren.getItemFromPlayer(player, compatibleBullet(player));

            // 修复：使用更通用的弹药术语
            // 修改：允许在枪械未满且有弹药时继续装填，即使之前已经装填过
            if (bullets.isEmpty() || getContents(stack) >= getMaxCapacity(stack)) {
                return;
            }

            if (!gunUser.bren_1_21_1$canReload()) {
                return;
            }

            gunUser.bren_1_21_1$setCanReload(false);
            gunUser.bren_1_21_1$setGunState(GunHelper.GunStates.RELOADING);
            gunUser.bren_1_21_1$setReloadingGun(stack);
            // 修复：在Minecraft 1.21.4中，set方法需要Identifier而不是Item
            var itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
            cooldownManager.addCooldown(itemId, this.reloadSpeed());
            onInsert(stack, player);
        }
    }

    protected void onInsert(ItemStack stack, LivingEntity player) {
    }

    protected void afterInserted(ItemStack stack, LivingEntity player) {
    }

    protected void onFullyLoaded(ItemStack stack, LivingEntity player) {
    }

    public Item compatibleBullet(Player Player) {
        return ItemReg.BULLET;
    }

    @Override
    public void reloadTick(ItemStack stack, Level world, Player player, IGunUser gunUser) {
        ItemCooldowns cooldownManager = player.getCooldowns();
        float cooldownProgress = cooldownManager.getCooldownPercent(stack, 1.0F);
        
        // 关键修复：确保只有在装弹状态下才执行装弹逻辑
        if (!cooldownManager.isOnCooldown(stack) && 
            gunUser.bren_1_21_1$getGunState().equals(GunHelper.GunStates.RELOADING)) {
            
            // 关键修复：使用当前枪械实例的compatibleBullet方法，确保调用子类重写的方法
            Item compatibleBulletItem = this.compatibleBullet(player);
            
            // 检查玩家是否有弹药
            ItemStack bullets = Bren.getItemFromPlayer(player, compatibleBulletItem);
            if (bullets.isEmpty()) {
                // 重置状态
                gunUser.bren_1_21_1$setGunState(GunHelper.GunStates.NORMAL);
                gunUser.bren_1_21_1$setCanReload(true);
                gunUser.bren_1_21_1$setReloadingGun(ItemStack.EMPTY);
                return;
            }
            
            // 检查枪械是否已满
            if (getContents(stack) >= getMaxCapacity(stack)) {
                // 重置状态
                gunUser.bren_1_21_1$setGunState(GunHelper.GunStates.NORMAL);
                gunUser.bren_1_21_1$setCanReload(true);
                gunUser.bren_1_21_1$setReloadingGun(ItemStack.EMPTY);
                return;
            }
            
            // 执行装弹逻辑
            addContent(stack);
            bullets.shrink(1);
            afterInserted(stack, player);
            
            // 关键修改：每次只装填一发，然后重置状态，等待玩家再次按下R键
            gunUser.bren_1_21_1$setGunState(GunHelper.GunStates.NORMAL);
            gunUser.bren_1_21_1$setCanReload(true);
            gunUser.bren_1_21_1$setReloadingGun(ItemStack.EMPTY);
            
            // 移除自动连续装填的逻辑
            // 不再设置新的冷却时间，让玩家可以立即进行下一次装填
            }
    }

    public boolean applyCustomMatrix(LivingEntity entity, GunHelper.GunStates state, PoseStack matrices, ItemStack stack, float cooldownProgress, boolean leftHanded) {
        return false;
    }

}