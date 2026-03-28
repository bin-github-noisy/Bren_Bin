package nl.sniffiandros.bren.common.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    private GameRenderState gameRenderState;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void bren$modifyHudFov(DeltaTracker deltaTracker, CallbackInfo ci) {
        // 修改 hudFov
    }

    public GameRenderState getGameRenderState() {
        return gameRenderState;
    }
}