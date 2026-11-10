package nl.sniffiandros.bren.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.FabricOrderedSubmitNodeCollector;
import net.fabricmc.fabric.api.client.rendering.v1.FeatureRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.SubmitRenderPhases;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import nl.sniffiandros.bren.common.registry.custom.types.GrapplingHookItem;

/**
 * 钩索绳索渲染：通过 RenderPearl 提交式管线在真实的 3D 世界中绘制绳索。
 * 复用内置的 {@link SubmitRenderPhases#AFTER_TERRAIN} 阶段，因此绳索会随地形进行正确的深度遮蔽。
 */
public class GrappleRopeRenderer extends RenderTypeFeatureRenderer<GrappleRopeRenderer.RopeSubmit> {

    private static final int SEGMENTS = 24;
    private static final float SAG_FACTOR = 0.12f;

    private static final FeatureRendererType<RopeSubmit> ROPE_TYPE =
            FeatureRendererType.create("bren:grapple_rope");

    /**
     * 客户端初始化时注册：将自定义渲染器绑定到类型，并订阅世界收集阶段。
     */
    public static void init() {
        FeatureRendererRegistry.register(ROPE_TYPE, GrappleRopeRenderer::new);
        LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
            RopeSubmit submit = RopeSubmit.create();
            if (submit != null) {
                ((FabricOrderedSubmitNodeCollector) context.submitNodeCollector())
                        .submitCustom(SubmitRenderPhases.AFTER_TERRAIN, submit);
            }
        });
    }

    @Override
    protected void buildGroup(FeatureFrameContext frameInfo, java.util.List<RopeSubmit> submits) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.mainCamera().position();
        VertexConsumer builder = getVertexBuilder(RenderTypes.lines());

        for (RopeSubmit submit : submits) {
            Vec3 start = submit.start;
            Vec3 end = submit.end;

            double sag = start.distanceTo(end) * SAG_FACTOR;
            Vec3 mid = start.add(end).scale(0.5).add(0.0, -sag, 0.0);

            for (int i = 0; i < SEGMENTS; i++) {
                double t1 = (double) i / SEGMENTS;
                double t2 = (double) (i + 1) / SEGMENTS;
                Vec3 p1 = bezier(start, mid, end, t1);
                Vec3 p2 = bezier(start, mid, end, t2);

                builder.addVertex(
                                (float) (p1.x - cameraPos.x), (float) (p1.y - cameraPos.y), (float) (p1.z - cameraPos.z))
                        .setColor(0.35f, 0.30f, 0.28f, 1.0f)
                        .setNormal(0.0f, 0.0f, 1.0f)
                        .setLineWidth(6.0f);
                builder.addVertex(
                                (float) (p2.x - cameraPos.x), (float) (p2.y - cameraPos.y), (float) (p2.z - cameraPos.z))
                        .setColor(0.35f, 0.30f, 0.28f, 1.0f)
                        .setNormal(0.0f, 0.0f, 1.0f)
                        .setLineWidth(6.0f);
            }
        }
    }

    private static Vec3 bezier(Vec3 p0, Vec3 p1, Vec3 p2, double t) {
        double u = 1.0 - t;
        return p0.scale(u * u)
                .add(p1.scale(2.0 * u * t))
                .add(p2.scale(t * t));
    }

    /**
     * 录制阶段创建的提交节点：携带绳索两端的世界坐标。
     */
    record RopeSubmit(Vec3 start, Vec3 end) implements SubmitNode {
        @Override
        public FeatureRendererType<RopeSubmit> featureType() {
            return ROPE_TYPE;
        }

        /**
         * 若玩家当前持钩且已命中目标，则创建一个绘制节点，否则返回 null。
         */
        static RopeSubmit create() {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return null;

            ItemStack stack = getHeldHook(player);
            if (stack == null || !GrapplingHookItem.isHooked(stack)) return null;

            Vec3 anchor = GrapplingHookItem.getHookedPos(stack);
            if (anchor == null) return null;

            return new RopeSubmit(player.getEyePosition(1.0f), anchor);
        }

        private static ItemStack getHeldHook(LocalPlayer player) {
            ItemStack main = player.getMainHandItem();
            if (main.getItem() instanceof GrapplingHookItem) return main;
            ItemStack off = player.getOffhandItem();
            if (off.getItem() instanceof GrapplingHookItem) return off;
            return null;
        }
    }
}