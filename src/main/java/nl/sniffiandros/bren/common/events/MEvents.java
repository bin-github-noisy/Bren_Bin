package nl.sniffiandros.bren.common.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MEvents {

    /**
     * An event that is called when a gun is fired.
     *
     * This is fired from {@link nl.sniffiandros.bren.common.mixin.PlayerEntityMixin#handleShooting)}
     *
     */
    public static final Event<MEvents.GunFired> GUN_FIRED_EVENT = EventFactory.createArrayBacked(MEvents.GunFired.class, callbacks -> (player, stack) -> {
        for (MEvents.GunFired callback : callbacks) {
            callback.gunFired(player, stack);
        }
    });

    @FunctionalInterface
    public interface GunFired {
        /**
         * Called when a player shoots a gun.
         *
         * @param player the player that fired the gun
         * @param stack the gun item stack
         */
        void gunFired(Player player, ItemStack stack);
    }

    /**
     * An event that is called when a player left-clicks (attacks) with an item.
     *
     * This is fired from {@link nl.sniffiandros.bren.common.mixin.PlayerEntityMixin#attack}
     *
     */
    public static final Event<MEvents.ItemLeftClick> ITEM_LEFT_CLICK_EVENT = EventFactory.createArrayBacked(MEvents.ItemLeftClick.class, callbacks -> (player, stack) -> {
        for (MEvents.ItemLeftClick callback : callbacks) {
            callback.onLeftClick(player, stack);
        }
    });

    @FunctionalInterface
    public interface ItemLeftClick {
        /**
         * Called when a player left-clicks with an item.
         *
         * @param player the player that left-clicked
         * @param stack the item stack being held
         */
        void onLeftClick(Player player, ItemStack stack);
    }
}