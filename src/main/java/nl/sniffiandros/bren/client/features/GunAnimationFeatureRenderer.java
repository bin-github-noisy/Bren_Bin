package nl.sniffiandros.bren.client.features;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.LivingEntity;
import nl.sniffiandros.bren.client.GunAnimationSystem;

/**
 * 在新的渲染系统中实现枪械动画的FeatureRenderer
 */
@Environment(EnvType.CLIENT)
public class GunAnimationFeatureRenderer<T extends HumanoidRenderState, M extends HumanoidModel<T>> 
        extends RenderLayer<T, M> {
    
    private final Minecraft client;
    
    public GunAnimationFeatureRenderer(RenderLayerParent<T, M> context) {
        super(context);
        this.client = Minecraft.getInstance();
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, T entityRenderState, float f, float g) {

    }

    public void render(PoseStack matrices, SubmitNodeCollector commandQueue, int light,
                      T state, float limbAngle, float limbDistance) {
        // 在新的渲染系统中，我们需要通过其他方式获取实体信息
        // 通过客户端获取当前渲染的实体
        if (client.getCameraEntity() instanceof LivingEntity livingEntity) {
            // 应用枪械动画
            // 注意：这里可能需要根据具体的state类型进行调整
        }
    }
    
    /**
     * 静态方法，供外部调用应用枪械动画
     */
    public static <T extends LivingEntity> void applyGunAnimationToModel(HumanoidModel<?> model, T entity) {
        if (entity != null && model != null) {
            GunAnimationSystem.applyGunAnimation(
                model.leftArm, model.rightArm, model.head, 
                null, entity // 暂时传递null作为state，因为需要重构动画系统
            );
        }
    }

    public Minecraft getClient() {
        return client;
    }
}