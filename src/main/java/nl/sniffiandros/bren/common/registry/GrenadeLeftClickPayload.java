package nl.sniffiandros.bren.common.registry;

import net.minecraft.network.RegistryFriendlyByteBuf;

public interface GrenadeLeftClickPayload {
    void write(RegistryFriendlyByteBuf buf);
}
