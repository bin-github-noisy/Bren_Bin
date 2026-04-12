package nl.sniffiandros.bren.common.registry.custom.types;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class GrenadeItem extends Item {

    private static final String FUSE_LIT_KEY = "grenade_fuse_lit";
    private static final String FUSE_START_TIME_KEY = "grenade_fuse_start_time";
    private static final int FUSE_TIME_TICKS = 100; // 5秒 (100 ticks)

    private final float explosionPower;

    public GrenadeItem(float explosionPower, int fuseTime, Properties properties) {
        super(properties);
        this.explosionPower = explosionPower;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        if (!isFuseLit(itemstack)) {
            if (level.isClientSide()) {
                player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§c先拉引信！(左键点击)"));
            }
            return InteractionResult.PASS;
        }
        
        if (!level.isClientSide()) {
            GrenadeProjectile grenade = new GrenadeProjectile(level, player, explosionPower, getRemainingFuse(itemstack, level)) {
                @Override
                protected Item getDefaultItem() {
                    return null;
                }
            };
            grenade.setItem(itemstack);
            grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(grenade);
            
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }
        
        clearFuse(itemstack);
        return InteractionResult.SUCCESS;
    }

    public static void onLeftClick(Player player, ItemStack stack) {
        if (stack.getItem() instanceof GrenadeItem && !isFuseLit(stack)) {
            lightFuse(stack, player.level().getGameTime());
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§e引信已点燃！5秒后爆炸！"));
        }
    }

    public static void tickGrenade(Player player, ItemStack stack) {
        if (stack.getItem() instanceof GrenadeItem && isFuseLit(stack)) {
            long currentTime = player.level().getGameTime();
            long startTime = getFuseStartTime(stack);
            long elapsed = currentTime - startTime;
            
            if (elapsed >= FUSE_TIME_TICKS) {
                if (!player.level().isClientSide()) {
                    explodeInHand(player, stack);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    // 清除引信状态
                    clearFuse(stack);
                }
            } else {
                if (player.level().isClientSide() && player.level().getGameTime() % 5 == 0) {
                    player.level().addParticle(ParticleTypes.SMOKE, 
                            player.getX(), player.getY() + 1.5, player.getZ(), 
                            0.0, 0.05, 0.0);
                }
            }
        }
    }

    private static void explodeInHand(Player player, ItemStack stack) {
        Level level = player.level();
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 4.0F,
                (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);

        level.explode(player,
                player.damageSources().explosion(player, player),
                null,
                player.getX(), player.getY(), player.getZ(),
                3.0f, false, Level.ExplosionInteraction.TNT);

        for (int i = 0; i < 20; i++) {
            double offsetX = (level.getRandom().nextDouble() - 0.5) * 2.0;
            double offsetY = (level.getRandom().nextDouble() - 0.5) * 2.0;
            double offsetZ = (level.getRandom().nextDouble() - 0.5) * 2.0;

            level.addParticle(ParticleTypes.EXPLOSION,
                    player.getX() + offsetX,
                    player.getY() + offsetY,
                    player.getZ() + offsetZ,
                    0, 0, 0);
        }
    }

    private static void lightFuse(ItemStack stack, long gameTime) {
        var customData = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY);
        var nbt = customData.copyTag();
        nbt.putBoolean(FUSE_LIT_KEY, true);
        nbt.putLong(FUSE_START_TIME_KEY, gameTime);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(nbt));
    }

    private static boolean isFuseLit(ItemStack stack) {
        var customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }
        var nbt = customData.copyTag();
        return nbt.getBoolean(FUSE_LIT_KEY).orElse(false);
    }

    private static long getFuseStartTime(ItemStack stack) {
        var customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return 0;
        }
        var nbt = customData.copyTag();
        return nbt.getLong(FUSE_START_TIME_KEY).orElse(0L);
    }

    private static void clearFuse(ItemStack stack) {
        var customData = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY);
        var nbt = customData.copyTag();
        nbt.putBoolean(FUSE_LIT_KEY, false);
        nbt.putLong(FUSE_START_TIME_KEY, 0L);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(nbt));
    }

    private static int getRemainingFuse(ItemStack stack, Level level) {
        long currentTime = level.getGameTime();
        long startTime = getFuseStartTime(stack);
        long elapsed = currentTime - startTime;
        return Math.max(0, FUSE_TIME_TICKS - (int) elapsed);
    }

    public static abstract class GrenadeProjectile extends ThrowableItemProjectile {
        private float explosionPower;
        private int fuse;
        private int fuseTime;

        public GrenadeProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
            super(entityType, level);
            this.explosionPower = 0.0f;
            this.fuse = 0;
            this.fuseTime = 0;
        }

        public GrenadeProjectile(Level level, LivingEntity thrower, float explosionPower, int fuseTime) {
            this(EntityType.SNOWBALL, level);
            this.explosionPower = explosionPower;
            this.fuse = fuseTime;
            this.fuseTime = fuseTime;
            this.setOwner(thrower);
        }



        @Override
        public void tick() {
            super.tick();
            
            if (--fuse <= 0) {
                if (!level().isClientSide()) {
                    triggerExplosion();
                }
                discard();
            }

            if (level().isClientSide()) {
                level().addParticle(ParticleTypes.SMOKE, getX(), getY() + 0.5, getZ(), 0.0, 0.0, 0.0);
            }
        }

        private void triggerExplosion() {
            if (level().isClientSide()) return;

            level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 4.0F,
                    (1.0F + (level().getRandom().nextFloat() - level().getRandom().nextFloat()) * 0.2F) * 0.7F);

            level().explode(getOwner(),
                    getOwner() != null ? getOwner().damageSources().explosion(getOwner(), getOwner()) : null,
                    null,
                    getX(), getY(), getZ(),
                    explosionPower, false, Level.ExplosionInteraction.TNT);

            for (int i = 0; i < 20; i++) {
                double offsetX = (level().getRandom().nextDouble() - 0.5) * 2.0;
                double offsetY = (level().getRandom().nextDouble() - 0.5) * 2.0;
                double offsetZ = (level().getRandom().nextDouble() - 0.5) * 2.0;

                level().addParticle(ParticleTypes.EXPLOSION,
                        getX() + offsetX,
                        getY() + offsetY,
                        getZ() + offsetZ,
                        0, 0, 0);
            }
        }

        @Override
        protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
            super.onHitEntity(entityHitResult);
            triggerExplosion();
            discard();
        }

        @Override
        protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
            super.onHitBlock(blockHitResult);
        }

        public int getFuseTime() {
            return fuseTime;
        }
    }
}