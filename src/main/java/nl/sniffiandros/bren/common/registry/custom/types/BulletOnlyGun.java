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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BulletOnlyGun extends GunItem {

    private static final Logger LOGGER = LoggerFactory.getLogger("Bren/BulletOnlyGun");
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

        LOGGER.info("Adding bullet to gun: current={}, new={}, max={}",
                currentCount, newCount, getMaxCapacity(stack));

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

        LOGGER.info("Using bullet from gun: current={}, new={}", currentCount, newCount);

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

        LOGGER.info("onReload called for player: {}, item: {}, cooling down: {}",
                player.getName().getString(), stack.getItem().toString(), cooldownManager.isOnCooldown(stack));

        if (player instanceof IGunUser gunUser && !cooldownManager.isOnCooldown(stack)) {
            ItemStack bullets = Bren.getItemFromPlayer(player, compatibleBullet(player));

            // 修复：使用更通用的弹药术语
            LOGGER.info("Player {} has ammunition: {}, current ammo count: {}, max capacity: {}",
                    player.getName().getString(), !bullets.isEmpty(), getContents(stack), getMaxCapacity(stack));

            // 修改：允许在枪械未满且有弹药时继续装填，即使之前已经装填过
            if (bullets.isEmpty() || getContents(stack) >= getMaxCapacity(stack)) {
                LOGGER.info("Cannot reload: ammunition empty={}, gun full={}",
                        bullets.isEmpty(), getContents(stack) >= getMaxCapacity(stack));
                return;
            }

            if (!gunUser.bren_1_21_1$canReload()) {
                LOGGER.info("Player {} cannot reload (canReload=false)", player.getName().getString());
                return;
            }

            LOGGER.info("Starting single bullet reload process for player {}, gun state: {}",
                    player.getName().getString(), gunUser.bren_1_21_1$getGunState());

            gunUser.bren_1_21_1$setCanReload(false);
            gunUser.bren_1_21_1$setGunState(GunHelper.GunStates.RELOADING);
            gunUser.bren_1_21_1$setReloadingGun(stack);
            // 修复：在Minecraft 1.21.4中，set方法需要Identifier而不是Item
            var itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
            cooldownManager.addCooldown(itemId, this.reloadSpeed());
            onInsert(stack, player);

            LOGGER.info("Single bullet reload started for player {}, cooldown set for item: {}, speed: {}",
                    player.getName().getString(), itemId, this.reloadSpeed());
        } else {
            LOGGER.info("Player {} cannot reload: is IGunUser: {}, cooling down: {}",
                    player.getName().getString(), player instanceof IGunUser, cooldownManager.isOnCooldown(stack));
        }
    }

    protected void onInsert(ItemStack stack, LivingEntity player) {
        LOGGER.debug("onInsert called for player {}", player.getName().getString());
    }

    protected void afterInserted(ItemStack stack, LivingEntity player) {
        LOGGER.debug("afterInserted called for player {}", player.getName().getString());
    }

    protected void onFullyLoaded(ItemStack stack, LivingEntity player) {
        LOGGER.info("Gun fully loaded for player {}", player.getName().getString());
    }

    public Item compatibleBullet(Player Player) {
        return ItemReg.BULLET;
    }

    @Override
    public void reloadTick(ItemStack stack, Level world, Player player, IGunUser gunUser) {
        ItemCooldowns cooldownManager = player.getCooldowns();
        float cooldownProgress = cooldownManager.getCooldownPercent(stack, 1.0F);
        
        LOGGER.debug("BulletOnlyGun reloadTick called: player={}, state={}, cooling down={}, progress={}", 
            player.getName().getString(), gunUser.bren_1_21_1$getGunState(), 
            cooldownManager.isOnCooldown(stack), cooldownProgress);
        
        // 关键修复：确保只有在装弹状态下才执行装弹逻辑
        if (!cooldownManager.isOnCooldown(stack) && 
            gunUser.bren_1_21_1$getGunState().equals(GunHelper.GunStates.RELOADING)) {
            
            LOGGER.info("BulletOnlyGun reload tick processing for player {}", player.getName().getString());
            
            // 关键修复：使用当前枪械实例的compatibleBullet方法，确保调用子类重写的方法
            Item compatibleBulletItem = this.compatibleBullet(player);
            LOGGER.info("Using compatible bullet item: {}", compatibleBulletItem.toString());
            
            // 检查玩家是否有弹药
            ItemStack bullets = Bren.getItemFromPlayer(player, compatibleBulletItem);
            if (bullets.isEmpty()) {
                LOGGER.info("Player {} has no ammunition, stopping reload", player.getName().getString());
                // 重置状态
                gunUser.bren_1_21_1$setGunState(GunHelper.GunStates.NORMAL);
                gunUser.bren_1_21_1$setCanReload(true);
                gunUser.bren_1_21_1$setReloadingGun(ItemStack.EMPTY);
                return;
            }
            
            // 检查枪械是否已满
            if (getContents(stack) >= getMaxCapacity(stack)) {
                LOGGER.info("Gun is already full for player {}, stopping reload", player.getName().getString());
                // 重置状态
                gunUser.bren_1_21_1$setGunState(GunHelper.GunStates.NORMAL);
                gunUser.bren_1_21_1$setCanReload(true);
                gunUser.bren_1_21_1$setReloadingGun(ItemStack.EMPTY);
                return;
            }
            
            // 执行装弹逻辑
            LOGGER.info("Adding bullet to gun for player {}", player.getName().getString());
            addContent(stack);
            bullets.shrink(1);
            afterInserted(stack, player);
            
            // 关键修改：每次只装填一发，然后重置状态，等待玩家再次按下R键
            LOGGER.info("Single bullet loaded for player {}, resetting state to allow manual reload", player.getName().getString());
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