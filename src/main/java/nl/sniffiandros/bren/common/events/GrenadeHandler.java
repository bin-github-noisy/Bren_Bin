package nl.sniffiandros.bren.common.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import nl.sniffiandros.bren.common.registry.custom.types.GrenadeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrenadeHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrenadeHandler.class);
    
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                updatePlayerGrenade(player);
            }
        });
        
        MEvents.ITEM_LEFT_CLICK_EVENT.register((player, stack) -> {
            if (stack.getItem() instanceof GrenadeItem) {
                GrenadeItem.onLeftClick(player, stack);
            }
        });
        
        LOGGER.info("GrenadeHandler registered successfully");
    }
    
    private static void updatePlayerGrenade(ServerPlayer player) {
        ItemStack mainHandStack = player.getMainHandItem();
        ItemStack offHandStack = player.getOffhandItem();
        
        if (mainHandStack.getItem() instanceof GrenadeItem) {
            GrenadeItem.tickGrenade(player, mainHandStack);
        } else if (offHandStack.getItem() instanceof GrenadeItem) {
            GrenadeItem.tickGrenade(player, offHandStack);
        }
    }
}