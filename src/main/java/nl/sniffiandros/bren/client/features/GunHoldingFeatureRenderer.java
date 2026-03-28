package nl.sniffiandros.bren.client.features;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import nl.sniffiandros.bren.common.registry.custom.PoseType;
import nl.sniffiandros.bren.common.registry.custom.types.GunItem;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class GunHoldingFeatureRenderer<S extends HumanoidRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
    public GunHoldingFeatureRenderer(LivingEntityRenderer context) {
        super(context);
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, int i, S entityRenderState, float f, float g) {

    }

    public void render(PoseStack matrices, SubmitNodeCollector queue, int light, S state, float limbAngle, float limbDistance) {
        // 现在我们可以访问BipedEntityRenderState中的mainArm字段了
        double target_degree = state.mainArm == HumanoidArm.LEFT ? 135D : 45D;

        // 使用反射来安全地访问可能的mainHandItem字段
        ItemStack s = getMainHandItem(state);

        if (s != null && !s.isEmpty()) {
            if (s.getItem() instanceof GunItem gunItem) {
                if (gunItem.holdingPose() == PoseType.TWO_ARMS) {
                    // 应用变换之前保存矩阵状态
                    matrices.pushPose();
                    matrices.mulPose(Axis.YN.rotation((float) Math.toRadians(target_degree - 90)));
                    // 恢复矩阵状态
                    matrices.popPose();
                }
            }
        }
    }
    /**
     * 通过反射安全地获取主手物品
     */
    private ItemStack getMainHandItem(HumanoidRenderState state) {
        try {
            // 尝试访问不同的可能字段名
            Class<?> clazz = state.getClass();
            while (clazz != null) {
                try {
                    // 尝试mainHandItem字段
                    java.lang.reflect.Field mainHandItemField = clazz.getDeclaredField("mainHandItem");
                    mainHandItemField.setAccessible(true);
                    return (ItemStack) mainHandItemField.get(state);
                } catch (NoSuchFieldException e1) {
                    try {
                        // 尝试mainHandItemState字段
                        java.lang.reflect.Field mainHandItemStateField = clazz.getDeclaredField("mainHandItemState");
                        mainHandItemStateField.setAccessible(true);
                        return (ItemStack) mainHandItemStateField.get(state);
                    } catch (NoSuchFieldException e2) {
                        // 继续检查父类
                        clazz = clazz.getSuperclass();
                    }
                }
            }
        } catch (Exception ignored) {
            // 如果所有尝试都失败，返回空的ItemStack
        }
        return ItemStack.EMPTY;
    }
}