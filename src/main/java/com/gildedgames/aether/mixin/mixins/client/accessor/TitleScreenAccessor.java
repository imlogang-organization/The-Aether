package com.gildedgames.aether.mixin.mixins.client.accessor;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TitleScreen.class)
public interface TitleScreenAccessor {
    @Accessor("splash")
    String aether$getSplash();

    @Accessor("splash")
    void aether$setSplash(String splash);

    @Accessor("minceraftEasterEgg")
    boolean aether$getMinceraftEasterEgg();

    @Mutable
    @Accessor("fading")
    void aether$setFading(boolean fading);

    @Accessor("fadeInStart")
    void aether$setFadeInStart(long fadeInStart);
}