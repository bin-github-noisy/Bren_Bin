package nl.sniffiandros.bren.common.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import nl.sniffiandros.bren.client.GunAnimationSystem;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.custom.types.GunItem;
import nl.sniffiandros.bren.common.utils.GunHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value= EnvType.CLIENT)
@Mixin(HumanoidModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> implements ArmedModel, HeadedModel {

    @Shadow public abstract ModelPart getHead();

    @Shadow @Final public ModelPart leftArm;

    @Shadow @Final public ModelPart rightArm;

    @Shadow @Final public ModelPart hat;

    @Shadow @Final public ModelPart leftLeg;

    @Shadow @Final public ModelPart rightLeg;

    @Shadow @Final public ModelPart body;

    // 修复注入点：使用新的渲染状态参数
    @Inject(at = @At("RETURN"), method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V")
    private void angles(HumanoidRenderState state, CallbackInfo info) {
        // 通过客户端获取当前渲染的实体
        Minecraft client = Minecraft.getInstance();
        
        // 简化实体检测逻辑
        if (client.getCameraEntity() instanceof Player player) {
            ItemStack mainHandItem = player.getMainHandItem();

            // 检查是否持有枪械
            if (!mainHandItem.isEmpty() && mainHandItem.getItem() instanceof GunItem gunItem) {
                // 获取冷却进度管理器
                ItemCooldowns cooldownManager = player.getCooldowns();

                // 修复：getCooldownProgress需要2个参数（物品栈和部分刻）
                float cooldownProgress = cooldownManager.getCooldownPercent(mainHandItem, 0.0F);

                // 检查实体是否实现了IGunUser接口
                if (player instanceof IGunUser gunUser) {
                    // 获取枪械状态和计时器
                    GunHelper.GunStates gunState = gunUser.bren_1_21_1$getGunState();
                    int gunTicks = gunUser.bren_1_21_1$getGunTicks();

                    // 使用新的动画系统应用动画，同时同步头部和帽子
                    GunAnimationSystem.applyGunAnimation(
                        this.leftArm, this.rightArm, this.getHead(), this.hat,
                        state, player
                    );
                }
            }
        }
    }
}