package nl.sniffiandros.bren.common.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import nl.sniffiandros.bren.common.registry.custom.types.GrapplingHookItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 钩索的Tick处理器，负责每刻更新钩索的物理效果和视觉效果
 */
public class GrapplingHookHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrapplingHookHandler.class);
    
    public static void register() {
        // 服务器端tick事件 - 处理物理效果
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            // 为每个在线玩家更新钩索
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                updatePlayerHook(player);
            }
        });
        
        // 客户端tick事件 - 仅在客户端运行
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            try {
                // 使用反射调用客户端tick，避免服务器端加载客户端类
                Class<?> clientTickEventsClass = Class.forName("net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents");
                Object endClientTick = clientTickEventsClass.getField("END_CLIENT_TICK").get(null);
                // 通过反射注册客户端tick事件
                java.lang.reflect.Method registerMethod = endClientTick.getClass().getMethod("register", Object.class);
                registerMethod.invoke(endClientTick, (net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick) client -> {
                    if (client.player != null && client.level != null) {
                        GrapplingHookItem.clientTick();
                    }
                });
            } catch (Exception e) {
                LOGGER.warn("Failed to register client tick events: {}", e.getMessage());
            }
        }
        
        LOGGER.info("GrapplingHookHandler registered successfully");
    }
    
    private static void updatePlayerHook(ServerPlayer player) {
        // 检查主手和副手是否持有钩索
        ItemStack mainHandStack = player.getMainHandItem();
        ItemStack offHandStack = player.getOffhandItem();
        
        if (mainHandStack.getItem() instanceof GrapplingHookItem) {
            GrapplingHookItem.tickHook(player, mainHandStack);
        } else if (offHandStack.getItem() instanceof GrapplingHookItem) {
            GrapplingHookItem.tickHook(player, offHandStack);
        }
    }
}