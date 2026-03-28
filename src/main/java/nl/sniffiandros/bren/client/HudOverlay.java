package nl.sniffiandros.bren.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.registry.custom.types.GunItem;
import nl.sniffiandros.bren.common.registry.custom.types.GunWithMagItem;

public class HudOverlay implements HudElementRegistry {
    private static final Identifier BULLET_ICONS = Identifier.fromNamespaceAndPath(Bren.MODID,
            "textures/gui/bullet_icons.png");

    public void onHudRender(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
        Player player = null;

        int width = context.guiWidth();
        int height = context.guiHeight();

        int x = width / 2;
        int y = height/ 2;

        Minecraft client = Minecraft.getInstance();
        if (client != null) {
            player = client.player;
        }

        if (player == null) {
            return;
        }

        ItemStack gun = player.getMainHandItem();
        int i;
        int u;
        int max;
        if (gun.getItem() instanceof GunItem gunItem) {
            i = gunItem.getContents(gun);
            u = gunItem.bulletAmount() > 1 ? 12 : 0;
            max = gunItem.getMaxCapacity(gun);

            if (gunItem instanceof GunWithMagItem) {
                if (!GunWithMagItem.hasMagazine(gun)) {
                    return;
                }
            }

        } else {
            return;
        }

        // 移除对getProfiler()的调用，因为在Minecraft 1.21.4中已不存在
        // client.getProfiler().push("machine_gun_bullets");

        // 在Minecraft 1.21.6中，RenderSystem的渲染方法已被移除
        // 使用DrawContext的渲染方法替代
        // RenderSystem.enableBlend();
        // RenderSystem.defaultBlendFunc();
        // RenderSystem.disableDepthTest();

        int rows = 2;

        for (int n = 0; n < max; ++n) {

            int ri = rows * 10;

            int row = (int) Math.floor((double) n /ri);

            // 调整位置，使其显示在屏幕右下角
            int y1 = height - 30 - (n * 6 - row*ri*6);
            int x1 = width - 30 - (15*row);

            int u1 = n < i ? 0 : 24;

            addBulletIcon(context, x1, y1, u + u1, 0);
        }

        // 在Minecraft 1.21.6中，RenderSystem的渲染方法已被移除
        // RenderSystem.enableDepthTest();
        // RenderSystem.disableBlend();

        // 移除对getProfiler()的调用，因为在Minecraft 1.21.4中已不存在
        // client.getProfiler().pop();
    }

    public void addBulletIcon(GuiGraphicsExtractor context, int x, int y, int u, int v) {
        // 在Minecraft 1.21.6中，使用简化的drawTexture方法
        // 方法签名：drawTexture(Identifier texture, int x, int y, int u, int v, int width, int height)
//        context.drawTexture(BULLET_ICONS, x, y, u, v, 12, 12);
    }
}