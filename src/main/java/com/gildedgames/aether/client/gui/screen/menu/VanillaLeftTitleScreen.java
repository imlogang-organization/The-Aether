package com.gildedgames.aether.client.gui.screen.menu;

import com.gildedgames.aether.client.gui.component.DynamicMenuButton;
import com.gildedgames.aether.mixin.mixins.client.accessor.TitleScreenAccessor;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.TitleScreenModUpdateIndicator;
import net.minecraftforge.internal.BrandingControl;

import javax.annotation.Nonnull;

public class VanillaLeftTitleScreen extends TitleScreen {
    private final PanoramaRenderer panorama = new PanoramaRenderer(CUBE_MAP);
    private static final ResourceLocation PANORAMA_OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation MINECRAFT_EDITION = new ResourceLocation("textures/gui/title/edition.png");

    public boolean fading;
    public long fadeInStart;

    private TitleScreenModUpdateIndicator modUpdateNotification;

    public VanillaLeftTitleScreen() {
        this.fading = true;
    }

    @Override
    protected void init() {
        super.init();
        this.setupButtons();
    }

    public void setupButtons() {
        int buttonCount = 0;
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof Button button) {
                Component buttonText = button.getMessage();
                if (isButtonLeft(buttonText)) {
                    button.setX(47);
                    button.setY(80 + buttonCount * 25);
                    button.setWidth(200);
                    buttonCount++;
                    if (button.getMessage().equals(Component.translatable("fml.menu.mods"))) {
                        this.modUpdateNotification = TitleScreenModUpdateIndicator.init(this, button);
                    }
                }
            }
        }
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        TitleScreenAccessor titleScreenAccessor = (TitleScreenAccessor) this;
        if (this.minecraft != null) {
            if (this.fadeInStart == 0L && this.fading) {
                this.fadeInStart = Util.getMillis();
            }
            float f = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
            this.panorama.render(partialTicks, Mth.clamp(f, 0.0F, 1.0F));
            int j = 10;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.fading ? (float)Mth.ceil(Mth.clamp(f, 0.0F, 1.0F)) : 1.0F);
            blit(poseStack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
            float f1 = this.fading ? Mth.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
            int l = Mth.ceil(f1 * 255.0F) << 24;
            if ((l & -67108864) != 0) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, MINECRAFT_LOGO);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f1);
                if (titleScreenAccessor.aether$getMinceraftEasterEgg()) {
                    this.blitOutlineBlack(j, 15, (x, y) -> {
                        this.blit(poseStack, x, y, 0, 0, 99, 44);
                        this.blit(poseStack, x + 99, y, 129, 0, 27, 44);
                        this.blit(poseStack, x + 99 + 26, y, 126, 0, 3, 44);
                        this.blit(poseStack, x + 99 + 26 + 3, y, 99, 0, 26, 44);
                        this.blit(poseStack, x + 155, y, 0, 45, 155, 44);
                    });
                } else {
                    this.blitOutlineBlack(j, 15, (x, y) -> {
                        this.blit(poseStack, x, y, 0, 0, 155, 44);
                        this.blit(poseStack, x + 155, y, 0, 45, 155, 44);
                    });
                }

                RenderSystem.setShaderTexture(0, MINECRAFT_EDITION);
                blit(poseStack, j + 88, 52, 0.0F, 0.0F, 98, 14, 128, 16);

                net.minecraftforge.client.ForgeHooksClient.renderMainMenu(this, poseStack, this.font, this.width, this.height, l);

                if (titleScreenAccessor.aether$getSplash() != null) {
                    poseStack.pushPose();
                    poseStack.translate(250.0F, 50.0F, 0.0F);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(-20.0F));
                    float f2 = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2F)) * 0.1F);
                    f2 = f2 * 100.0F / (float) (this.font.width(titleScreenAccessor.aether$getSplash()) + 32); poseStack.scale(f2, f2, f2);
                    drawCenteredString(poseStack, this.font, titleScreenAccessor.aether$getSplash(), 0, -8, 16776960 | l);
                    poseStack.popPose();
                }

                BrandingControl.forEachLine(true, true, (brdline, brd) ->
                        drawString(poseStack, this.font, brd, width - this.font.width(brd) - 1, this.height - (10 + (brdline + 1) * (this.font.lineHeight + 1)), 16777215 | l)
                );
                BrandingControl.forEachAboveCopyrightLine((brdline, brd) ->
                        drawString(poseStack, this.font, brd, 1, this.height - (brdline + 1) * (this.font.lineHeight + 1), 16777215 | l)
                );
            }

            for (GuiEventListener guieventlistener : this.children()) {
                if (guieventlistener instanceof AbstractWidget abstractWidget) {
                    if (f1 > 0.02F) {
                        abstractWidget.setAlpha(f1);
                        abstractWidget.visible = true;
                    } else {
                        abstractWidget.visible = false;
                    }
                }
            }

            int offset = 0;
            for (Renderable renderable : this.renderables) {
                renderable.render(poseStack, mouseX, mouseY, partialTicks);
                if (renderable instanceof DynamicMenuButton dynamicMenuButton) {
                    if (dynamicMenuButton.enabled) {
                        offset -= 24;
                    }
                }
            }
            for (Renderable renderable : this.renderables) {
                if (renderable instanceof Button button) {
                    Component buttonText = button.getMessage();
                    if (buttonText.equals(Component.translatable("narrator.button.accessibility"))) {
                        button.setX(this.width - 48 + offset);
                        button.setY(4);
                    } else if (buttonText.equals(Component.translatable("narrator.button.language"))) {
                        button.setX(this.width - 24 + offset);
                        button.setY(4);
                    }
                }
            }

            if (f1 >= 1.0f) {
                this.modUpdateNotification.render(poseStack, mouseX, mouseY, partialTicks);
            }
        }
    }

    public boolean isButtonLeft(Component buttonText) {
        return buttonText.equals(Component.translatable("menu.singleplayer"))
                || buttonText.equals(Component.translatable("menu.multiplayer"))
                || buttonText.equals(Component.translatable("menu.online"))
                || buttonText.equals(Component.translatable("fml.menu.mods"))
                || buttonText.equals(Component.translatable("menu.options"))
                || buttonText.equals(Component.translatable("menu.quit"));
    }
}
