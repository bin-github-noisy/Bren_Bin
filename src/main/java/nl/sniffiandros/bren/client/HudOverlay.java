package nl.sniffiandros.bren.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.resources.Identifier;
import nl.sniffiandros.bren.common.Bren;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HudOverlay {
    private static final Logger LOGGER = LoggerFactory.getLogger("Bren/HudOverlay");
    private static final Identifier BULLET_ICONS = Identifier.fromNamespaceAndPath(Bren.MODID,
            "textures/gui/bullet_icons.png");

    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, int currentAmmo, int maxAmmo, int uOffset) {
        LOGGER.info("render called - currentAmmo: {}, maxAmmo: {}, uOffset: {}", currentAmmo, maxAmmo, uOffset);

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        LOGGER.info("Screen size: {}x{}", width, height);

        int rows = 2;
        LOGGER.info("Rendering {} bullet icons", maxAmmo);

        for (int n = 0; n < maxAmmo; ++n) {
            LOGGER.info("Loop iteration n = {}", n);

            int ri = rows * 10;
            LOGGER.info("ri = {}", ri);

            int row = (int) Math.floor((double) n /ri);
            LOGGER.info("row = {}", row);

            int y1 = height - 30 - (n * 6 - row*ri*6);
            int x1 = width - 30 - (15*row);
            LOGGER.info("Position: ({}, {})", x1, y1);

            int u1 = n < currentAmmo ? 0 : 24;
            LOGGER.info("u1 = {}, u = {}", u1, uOffset);

            int color = n < currentAmmo ? 0xFFFFFFFF : 0xFF666666;
            graphics.fill(x1, y1, x1 + 12, y1 + 12, color);
            LOGGER.info("Filled rectangle for bullet {}", n);
        }

        LOGGER.info("HUD render completed");
    }

    public void renderWithTexture(GuiGraphicsExtractor graphics, GuiRenderState guiRenderState, DeltaTracker deltaTracker, int currentAmmo, int maxAmmo, int uOffset) {
        LOGGER.info("renderWithTexture called - currentAmmo: {}, maxAmmo: {}, uOffset: {}", currentAmmo, maxAmmo, uOffset);

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        LOGGER.info("Screen size: {}x{}", width, height);

        // 根据屏幕比例动态调整图标大小
        int baseIconSize = 12; // 基础图标大小
        int screenScale = Math.max(1, width / 800); // 根据屏幕宽度计算缩放比例
        int iconSize = baseIconSize * screenScale; // 动态图标大小
        int rows = 2; // 2行布局
        
        LOGGER.info("Rendering {} bullet icons with {} rows, screenScale: {}, iconSize: {}", maxAmmo, rows, screenScale, iconSize);

        for (int n = 0; n < maxAmmo; ++n) {
            LOGGER.info("Loop iteration n = {}", n);

            // 根据屏幕比例动态调整间距
            int ri = rows * 10; // 每列最多20个图标
            int row = (int) Math.floor((double) n / ri); // 当前列号
            int y1 = n * (6 * screenScale) - row * ri * (6 * screenScale); // 垂直位置计算
            int x1 = (15 * screenScale) * row + (15 * screenScale); // 水平位置计算
            
            LOGGER.info("Position: ({}, {}), row: {}, ri: {}", x1, y1, row, ri);

            // 精确还原1.20.4版本的纹理选择逻辑
            int u1 = n < currentAmmo ? 0 : 24; // 弹药状态偏移
            int textureOffset = uOffset + u1; // 加上枪械类型偏移
            
            LOGGER.info("Texture offset: {}, uOffset: {}, u1: {}, hasAmmo: {}", textureOffset, uOffset, u1, n < currentAmmo);

            // 根据屏幕比例动态调整纹理坐标
            // 纹理图集大小为48x12，每个图标区域为12x12
            int textureWidth = 48; // 纹理总宽度
            int textureHeight = 12; // 纹理总高度
            
            // 计算纹理UV坐标，根据屏幕比例缩放
            float u0 = (float) textureOffset / textureWidth;
            float u1_uv = (float) (textureOffset + baseIconSize) / textureWidth;
            float v0 = 0.0f;
            float v1 = (float) baseIconSize / textureHeight;
            
            LOGGER.info("UV coordinates: u0={}, u1={}, v0={}, v1={}", u0, u1_uv, v0, v1);

            // 使用正确的blit方法参数格式，使用动态UV坐标
            graphics.blit(BULLET_ICONS, x1, y1, x1 + iconSize, y1 + iconSize, 
                         u0, u1_uv, v0, v1);
            LOGGER.info("Blitted sprite for bullet {}", n);
        }

        LOGGER.info("HUD render completed");
    }
}