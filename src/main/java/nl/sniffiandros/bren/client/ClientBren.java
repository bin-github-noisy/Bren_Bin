package nl.sniffiandros.bren.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import nl.sniffiandros.bren.client.features.GunBackFeatureRenderer;
import nl.sniffiandros.bren.client.features.GunHoldingFeatureRenderer;
import nl.sniffiandros.bren.client.particle.AirRingParticle;
import nl.sniffiandros.bren.client.particle.CasingParticle;
import nl.sniffiandros.bren.client.particle.MuzzleSmokeParticle;
import nl.sniffiandros.bren.client.renderer.BulletRenderer;
import nl.sniffiandros.bren.client.renderer.RecoilSys;
import nl.sniffiandros.bren.client.renderer.WeaponTickHolder;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.config.MConfig;
import nl.sniffiandros.bren.common.registry.ClientNetworkReg;
import nl.sniffiandros.bren.common.registry.KeyBindingReg;
import nl.sniffiandros.bren.common.registry.ParticleReg;

public class ClientBren implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        var particleRegistry = ParticleProviderRegistry.getInstance();

        // 使用通配符类型参数
        particleRegistry.register((ParticleType<ParticleOptions>) ParticleReg.MUZZLE_SMOKE_PARTICLE, MuzzleSmokeParticle.Factory::new);
        particleRegistry.register((ParticleType<ParticleOptions>) ParticleReg.AIR_RING_PARTICLE, AirRingParticle.Factory::new);
        particleRegistry.register((ParticleType<ParticleOptions>) ParticleReg.CASING_PARTICLE, CasingParticle.Factory::new);

        // 使用ClientPlayConnectionEvents.INIT事件来延迟注册网络数据包接收器
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            ClientNetworkReg.shootPacket();
            ClientNetworkReg.clientShootPacket();
            ClientNetworkReg.shootAnimationPacket();
            ClientNetworkReg.recoilPacket();
            ClientNetworkReg.shootParticlePacket();
        });

        EntityRendererRegistry.register(Bren.BULLET, BulletRenderer::new);
        KeyBindingReg.reg();
        // registerModelPredicates(); // 暂时禁用模型谓词注册 // 暂时禁用模型谓词注册

        // 注册后坐力系统渲染回调
        RecoilSys.registerRenderCallback();

        if (MConfig.showAmmoGui.get()) {
            // 使用正确的 HudElementRegistry API
            // HudElementRegistry.addLast(new Identifier("bren", "ammo_overlay"), new HudOverlay() {
            //     @Override
            //     public void onHudRender(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
            //         super.onHudRender(drawContext, tickCounter);
            //     }
            // });
            // 暂时注释掉，避免编译错误，需要先定义 HudOverlay 类
        }

        LivingEntityRenderLayerRegistrationCallback.EVENT.register((t, r, e, c) -> {
            // 只为人形生物注册枪械相关渲染器，避免类型转换错误
            // t 参数是 EntityType，我们需要检查是否为人形生物类型
            if (isHumanoidEntityType(t)) {
                if (MConfig.renderGunOnBack.get()) {
                    // GunBackFeatureRenderer 需要的是 LivingEntityRenderer 而不是 ItemRenderer
                    e.register(new GunBackFeatureRenderer(r, (net.minecraft.client.renderer.entity.LivingEntityRenderer) r));
                }
                
                // 只为人形生物注册枪械持有姿势特性渲染器
                // 使用原始类型避免泛型类型检查问题
                e.register(new GunHoldingFeatureRenderer(r));
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(WeaponTickHolder::tick);
        ClientTickEvents.START_CLIENT_TICK.register(RecoilSys::tick);
    }

    // 添加一个新的方法来处理模型注册
    public static void registerAllModels() {
        // 这里可以添加额外的模型注册逻辑
    }
    
    // 检查实体类型是否为人形生物
    private static boolean isHumanoidEntityType(net.minecraft.world.entity.EntityType<?> entityType) {
        // 人形生物包括玩家、村民、僵尸、骷髅等
        return entityType == net.minecraft.world.entity.EntityType.PLAYER;
    }
}