package nl.sniffiandros.bren.common.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // 为每个在线玩家更新钩索
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                updatePlayerHook(player);
            }
        });
        
        // 客户端tick事件 - 处理视觉效果
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.level != null) {
                GrapplingHookItem.clientTick();
            }
        });
        
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