package nl.sniffiandros.bren.common.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import nl.sniffiandros.bren.common.registry.custom.types.GunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 简单的动画测试Mixin
 * 用于调试和验证动画系统是否正常工作
 */
@SuppressWarnings("ALL")
@Environment(value= EnvType.CLIENT)
@Mixin(SpecialModelWrapper.class)
public class SimpleAnimationMixin {

    @Inject(at = @At("HEAD"), method = "update")
    private void bren$simpleAnimationTest(
            ItemStackRenderState output, 
            ItemStack item, 
            net.minecraft.client.renderer.item.ItemModelResolver resolver, 
            ItemDisplayContext displayContext, 
            net.minecraft.client.multiplayer.ClientLevel level, 
            net.minecraft.world.entity.ItemOwner owner, 
            int seed, 
            CallbackInfo ci) {
        
        Minecraft client = Minecraft.getInstance();

        // 只在客户端且玩家存在时处理
        if (client.player != null && client.getCameraEntity() instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
            ItemStack mainHandItem = client.player.getMainHandItem();
            ItemStack offHandItem = client.player.getOffhandItem();

            // 检查是否为枪械物品
            boolean isMainHandGun = !mainHandItem.isEmpty() && mainHandItem.getItem() instanceof GunItem;
            boolean isOffHandGun = !offHandItem.isEmpty() && offHandItem.getItem() instanceof GunItem;

            // 调试信息已移除
        }
    }
}