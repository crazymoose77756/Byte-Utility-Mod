package me.danny125.byteutilitymod.mixin.client;

import me.danny125.byteutilitymod.Initialize;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.danny125.byteutilitymod.modules.Module;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    private PostEffectProcessor postProcessor;

    @Shadow
    public abstract void loadPostProcessor(Identifier id);

    @Shadow
    public abstract void disablePostProcessor();

    // Inject into the render method or any other method you need to check the state
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && isLSDActive()) {
            if(Initialize.INSTANCE.loadPostProcessor) {
                Initialize.INSTANCE.loadPostProcessor = false;// Assuming 'isLSDActive()' checks if the module is enabled
                if (this.postProcessor != null) {
                    this.disablePostProcessor();
                }
                Identifier shader = IdentifierAccessor.createIdentifier("minecraft", "shaders/post/lsd.json");
                this.loadPostProcessor(shader);
            }
        } else {
            if (this.postProcessor != null) {
                this.disablePostProcessor();
            }
        }
    }

    // You can create a helper method to check if your LSD module is active
    private boolean isLSDActive() {
        return Initialize.INSTANCE.isModuleToggled("LSD");// Adjust according to your mod structure
    }
}
