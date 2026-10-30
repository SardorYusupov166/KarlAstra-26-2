/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.render.TextureSetup
 *  net.minecraft.client.renderer.RenderPipelines
 */
package astra.render;

import astra.gui.components.UiDraw;
import astra.render.ColorUtil;
import astra.render.Icon;
import astra.render.Mesh;
import astra.render.Paint;
import astra.render.Render2D;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;

public final class Icons {
    private static final Mesh PEN_MESH = new Mesh(false);

    private Icons() {
    }

    public static void draw(GuiGraphics ctx, Icon icon, float x, float y, float size, int color) {
        Icons.draw(ctx, icon, x, y, size, color, 1.0f);
    }

    public static void drawNeon(GuiGraphics ctx, Icon icon, float x, float y, float size, int color, int glow, float strength) {
        float gs = UiDraw.glowStrength();
        if (gs > 0.0f && strength > 0.0f) {
            float g = strength * Math.min(1.5f, gs);
            Icons.draw(ctx, icon, x, y, size, ColorUtil.withAlpha(glow, 0.13f * g), 3.6f);
            Icons.draw(ctx, icon, x, y, size, ColorUtil.withAlpha(glow, 0.28f * g), 2.1f);
        }
        Icons.draw(ctx, icon, x, y, size, color, 1.0f);
    }

    private static void draw(GuiGraphics ctx, Icon icon, float x, float y, float size, int color, float strokeMul) {
        Pen p = new Pen(x, y, size, ColorUtil.mulAlpha(color, 1.0f), Render2D.pixel(ctx), strokeMul);
        switch (icon) {
            case HOME: {
                p.poly(false, 3.0f, 10.5f, 12.0f, 3.0f, 21.0f, 10.5f);
                p.poly(false, 5.0f, 9.0f, 5.0f, 21.0f, 19.0f, 21.0f, 19.0f, 9.0f);
                p.poly(false, 10.0f, 21.0f, 10.0f, 15.0f, 14.0f, 15.0f, 14.0f, 21.0f);
                break;
            }
            case EYE: {
                p.arc(12.0f, 19.0f, 12.5f, 214.0f, 112.0f);
                p.arc(12.0f, 5.0f, 12.5f, 34.0f, 112.0f);
                p.circle(12.0f, 12.0f, 3.2f);
                break;
            }
            case SWORD: {
                p.poly(false, 14.5f, 17.5f, 3.0f, 6.0f, 3.0f, 3.0f, 6.0f, 3.0f, 17.5f, 14.5f);
                p.line(13.0f, 19.0f, 19.0f, 13.0f);
                p.line(16.0f, 16.0f, 20.0f, 20.0f);
                p.line(19.0f, 21.0f, 21.0f, 19.0f);
                break;
            }
            case RUN: {
                p.circle(15.0f, 4.5f, 2.0f);
                p.poly(false, 6.0f, 11.0f, 9.5f, 8.0f, 14.0f, 9.0f, 12.0f, 14.0f, 16.0f, 17.0f, 15.0f, 21.5f);
                p.poly(false, 12.0f, 14.0f, 9.0f, 17.0f, 4.0f, 17.5f);
                p.poly(false, 14.0f, 9.0f, 17.0f, 12.0f, 20.0f, 12.0f);
                break;
            }
            case SHIRT: {
                p.poly(true, 8.0f, 3.0f, 4.0f, 5.0f, 2.0f, 9.5f, 5.5f, 11.0f, 5.5f, 21.0f, 18.5f, 21.0f, 18.5f, 11.0f, 22.0f, 9.5f, 20.0f, 5.0f, 16.0f, 3.0f, 14.5f, 5.0f, 9.5f, 5.0f);
                break;
            }
            case GEAR: {
                p.circle(12.0f, 12.0f, 3.0f);
                p.circle(12.0f, 12.0f, 7.0f);
                for (int i = 0; i < 8; ++i) {
                    double a = Math.toRadians(i * 45);
                    p.line(12.0f + (float)Math.cos(a) * 7.5f, 12.0f + (float)Math.sin(a) * 7.5f, 12.0f + (float)Math.cos(a) * 10.0f, 12.0f + (float)Math.sin(a) * 10.0f);
                }
                break;
            }
            case MAP: {
                p.poly(true, 3.0f, 6.0f, 9.0f, 3.0f, 15.0f, 6.0f, 21.0f, 3.0f, 21.0f, 18.0f, 15.0f, 21.0f, 9.0f, 18.0f, 3.0f, 21.0f);
                p.line(9.0f, 3.0f, 9.0f, 18.0f);
                p.line(15.0f, 6.0f, 15.0f, 21.0f);
                break;
            }
            case PLAY: {
                p.circle(12.0f, 12.0f, 9.5f);
                p.poly(true, 10.0f, 8.0f, 16.0f, 12.0f, 10.0f, 16.0f);
                break;
            }
            case USER: {
                p.circle(12.0f, 8.0f, 4.0f);
                p.arc(12.0f, 21.0f, 7.5f, 180.0f, 180.0f);
                break;
            }
            case PALETTE: {
                p.arc(12.0f, 12.0f, 9.5f, 60.0f, 300.0f);
                p.poly(false, 16.8f, 20.2f, 15.0f, 17.0f, 17.0f, 14.5f, 21.5f, 12.0f);
                p.dot(7.5f, 10.5f, 1.4f);
                p.dot(11.0f, 7.0f, 1.4f);
                p.dot(15.5f, 8.0f, 1.4f);
                break;
            }
            case DROPLET: {
                p.poly(false, 12.0f, 2.5f, 6.4f, 10.4f);
                p.poly(false, 12.0f, 2.5f, 17.6f, 10.4f);
                p.arc(12.0f, 14.5f, 6.5f, -30.0f, 240.0f);
                break;
            }
            case SUN: {
                p.circle(12.0f, 12.0f, 4.0f);
                for (int i = 0; i < 8; ++i) {
                    double a = Math.toRadians(i * 45);
                    p.line(12.0f + (float)Math.cos(a) * 7.0f, 12.0f + (float)Math.sin(a) * 7.0f, 12.0f + (float)Math.cos(a) * 9.5f, 12.0f + (float)Math.sin(a) * 9.5f);
                }
                break;
            }
            case STAR: {
                p.poly(true, 12.0f, 2.5f, 14.8f, 8.6f, 21.5f, 9.3f, 16.5f, 13.8f, 17.9f, 20.5f, 12.0f, 17.1f, 6.1f, 20.5f, 7.5f, 13.8f, 2.5f, 9.3f, 9.2f, 8.6f);
                break;
            }
            case LEAF: {
                p.arc(19.0f, 19.0f, 15.0f, 180.0f, 90.0f);
                p.arc(5.0f, 5.0f, 15.0f, 0.0f, 90.0f);
                p.line(5.0f, 19.0f, 13.0f, 11.0f);
                break;
            }
            case BULB: {
                p.arc(12.0f, 9.0f, 6.5f, 150.0f, 240.0f);
                p.poly(false, 6.4f, 12.3f, 9.0f, 15.5f, 9.0f, 17.0f);
                p.poly(false, 17.6f, 12.3f, 15.0f, 15.5f, 15.0f, 17.0f);
                p.line(9.0f, 17.0f, 15.0f, 17.0f);
                p.line(10.0f, 20.5f, 14.0f, 20.5f);
                break;
            }
            case SPARKLE: {
                p.sparkle(10.0f, 13.0f, 8.5f);
                p.sparkle(18.5f, 5.5f, 3.6f);
                break;
            }
            case CLOUD: {
                p.arc(8.0f, 15.0f, 4.5f, 90.0f, 180.0f);
                p.arc(13.0f, 11.0f, 6.0f, 190.0f, 160.0f);
                p.arc(17.5f, 15.0f, 4.5f, 270.0f, 180.0f);
                p.line(8.0f, 19.5f, 17.5f, 19.5f);
                break;
            }
            case KEYBOARD: {
                p.rect(2.5f, 6.0f, 19.0f, 12.0f, 2.0f);
                p.dot(6.5f, 10.0f, 1.0f);
                p.dot(10.0f, 10.0f, 1.0f);
                p.dot(13.5f, 10.0f, 1.0f);
                p.dot(17.5f, 10.0f, 1.0f);
                p.line(8.0f, 14.5f, 16.0f, 14.5f);
                break;
            }
            case MOUSE: {
                p.rect(6.0f, 3.0f, 12.0f, 18.0f, 6.0f);
                p.line(12.0f, 6.5f, 12.0f, 10.0f);
                break;
            }
            case CROSSHAIR: {
                p.circle(12.0f, 12.0f, 8.0f);
                p.line(12.0f, 2.0f, 12.0f, 7.0f);
                p.line(12.0f, 17.0f, 12.0f, 22.0f);
                p.line(2.0f, 12.0f, 7.0f, 12.0f);
                p.line(17.0f, 12.0f, 22.0f, 12.0f);
                break;
            }
            case SHIELD: {
                p.poly(true, 12.0f, 2.5f, 20.0f, 5.5f, 20.0f, 12.0f, 12.0f, 21.5f, 4.0f, 12.0f, 4.0f, 5.5f);
                break;
            }
            case POTION: {
                p.poly(false, 10.0f, 3.0f, 10.0f, 9.0f, 5.0f, 18.0f, 6.5f, 21.0f, 17.5f, 21.0f, 19.0f, 18.0f, 14.0f, 9.0f, 14.0f, 3.0f);
                p.line(8.5f, 3.0f, 15.5f, 3.0f);
                p.line(7.0f, 15.0f, 17.0f, 15.0f);
                break;
            }
            case HEART: {
                p.arc(8.0f, 9.0f, 4.5f, 150.0f, 210.0f);
                p.arc(16.0f, 9.0f, 4.5f, 180.0f, 210.0f);
                p.poly(false, 4.2f, 11.4f, 12.0f, 20.0f, 19.8f, 11.4f);
                break;
            }
            case TARGET: {
                p.circle(12.0f, 12.0f, 9.5f);
                p.circle(12.0f, 12.0f, 5.5f);
                p.dot(12.0f, 12.0f, 1.6f);
                break;
            }
            case CLOCK: {
                p.circle(12.0f, 12.0f, 9.5f);
                p.poly(false, 12.0f, 6.5f, 12.0f, 12.0f, 16.0f, 14.0f);
                break;
            }
            case FLAME: {
                p.arc(12.0f, 14.5f, 6.5f, -20.0f, 220.0f);
                p.poly(false, 6.1f, 12.3f, 9.0f, 9.5f, 12.0f, 2.5f, 15.0f, 7.0f, 17.4f, 10.6f);
                break;
            }
            case ZOOM: {
                p.circle(10.5f, 10.5f, 7.0f);
                p.line(15.5f, 15.5f, 21.0f, 21.0f);
                p.line(7.5f, 10.5f, 13.5f, 10.5f);
                p.line(10.5f, 7.5f, 10.5f, 13.5f);
                break;
            }
            case CAMERA: {
                p.rect(2.5f, 7.0f, 19.0f, 13.0f, 2.5f);
                p.poly(false, 8.0f, 7.0f, 9.5f, 4.0f, 14.5f, 4.0f, 16.0f, 7.0f);
                p.circle(12.0f, 13.5f, 3.5f);
                break;
            }
            case COMPASS: {
                p.circle(12.0f, 12.0f, 9.5f);
                p.poly(true, 15.5f, 8.5f, 13.5f, 13.5f, 8.5f, 15.5f, 10.5f, 10.5f);
                break;
            }
            case PIN: {
                p.arc(12.0f, 10.0f, 7.0f, 150.0f, 240.0f);
                p.poly(false, 5.9f, 13.5f, 12.0f, 21.5f, 18.1f, 13.5f);
                p.circle(12.0f, 10.0f, 2.5f);
                break;
            }
            case FOLDER: {
                p.poly(true, 3.0f, 6.0f, 3.0f, 19.0f, 21.0f, 19.0f, 21.0f, 8.0f, 12.0f, 8.0f, 10.0f, 5.0f, 3.0f, 5.0f);
                break;
            }
            case SAVE: {
                p.poly(true, 4.0f, 4.0f, 4.0f, 20.0f, 20.0f, 20.0f, 20.0f, 8.0f, 16.0f, 4.0f);
                p.poly(false, 7.5f, 4.0f, 7.5f, 8.5f, 15.0f, 8.5f);
                p.rect(7.5f, 13.0f, 9.0f, 7.0f, 0.5f);
                break;
            }
            case PLUS: {
                p.line(12.0f, 5.0f, 12.0f, 19.0f);
                p.line(5.0f, 12.0f, 19.0f, 12.0f);
                break;
            }
            case TRASH: {
                p.line(3.5f, 6.0f, 20.5f, 6.0f);
                p.poly(false, 9.0f, 6.0f, 9.0f, 3.5f, 15.0f, 3.5f, 15.0f, 6.0f);
                p.poly(false, 5.5f, 6.0f, 6.5f, 20.5f, 17.5f, 20.5f, 18.5f, 6.0f);
                p.line(10.0f, 10.0f, 10.0f, 16.5f);
                p.line(14.0f, 10.0f, 14.0f, 16.5f);
                break;
            }
            case CLOSE: {
                p.line(6.0f, 6.0f, 18.0f, 18.0f);
                p.line(18.0f, 6.0f, 6.0f, 18.0f);
                break;
            }
            case CHEVRON_DOWN: {
                p.poly(false, 6.0f, 9.0f, 12.0f, 15.0f, 18.0f, 9.0f);
                break;
            }
            case CHEVRON_RIGHT: {
                p.poly(false, 9.0f, 6.0f, 15.0f, 12.0f, 9.0f, 18.0f);
                break;
            }
            case RESET: {
                p.arc(12.0f, 12.0f, 8.0f, -60.0f, 290.0f);
                p.poly(false, 16.0f, 2.5f, 16.0f, 6.5f, 20.0f, 6.5f);
                break;
            }
            case DOWNLOAD: {
                p.line(12.0f, 3.0f, 12.0f, 15.0f);
                p.poly(false, 7.0f, 10.5f, 12.0f, 15.5f, 17.0f, 10.5f);
                p.poly(false, 3.5f, 15.0f, 3.5f, 20.0f, 20.5f, 20.0f, 20.5f, 15.0f);
                break;
            }
            case MONITOR: {
                p.rect(2.5f, 3.5f, 19.0f, 13.0f, 2.0f);
                p.line(8.0f, 21.0f, 16.0f, 21.0f);
                p.line(12.0f, 16.5f, 12.0f, 21.0f);
                break;
            }
            case CHIP: {
                p.rect(5.5f, 5.5f, 13.0f, 13.0f, 2.0f);
                p.rect(9.5f, 9.5f, 5.0f, 5.0f, 0.5f);
                for (int i = 0; i < 3; ++i) {
                    float c = 8.5f + (float)i * 3.5f;
                    p.line(c, 2.0f, c, 5.0f);
                    p.line(c, 19.0f, c, 22.0f);
                    p.line(2.0f, c, 5.0f, c);
                    p.line(19.0f, c, 22.0f, c);
                }
                break;
            }
            case CHART: {
                p.poly(false, 3.0f, 3.0f, 3.0f, 21.0f, 21.0f, 21.0f);
                p.line(8.0f, 17.0f, 8.0f, 13.0f);
                p.line(12.5f, 17.0f, 12.5f, 8.0f);
                p.line(17.0f, 17.0f, 17.0f, 11.0f);
                break;
            }
            case BOLT: {
                p.poly(true, 13.0f, 2.5f, 4.5f, 13.5f, 11.5f, 13.5f, 10.5f, 21.5f, 19.5f, 10.5f, 12.5f, 10.5f);
                break;
            }
            case LAYERS: {
                p.poly(true, 12.0f, 3.0f, 21.5f, 8.0f, 12.0f, 13.0f, 2.5f, 8.0f);
                p.poly(false, 2.5f, 12.5f, 12.0f, 17.5f, 21.5f, 12.5f);
                p.poly(false, 2.5f, 16.5f, 12.0f, 21.5f, 21.5f, 16.5f);
                break;
            }
            case GRID: {
                p.rect(3.0f, 3.0f, 7.0f, 7.0f, 1.0f);
                p.rect(14.0f, 3.0f, 7.0f, 7.0f, 1.0f);
                p.rect(3.0f, 14.0f, 7.0f, 7.0f, 1.0f);
                p.rect(14.0f, 14.0f, 7.0f, 7.0f, 1.0f);
                break;
            }
            case IMAGE: {
                p.rect(3.0f, 3.0f, 18.0f, 18.0f, 2.5f);
                p.circle(9.0f, 9.0f, 2.0f);
                p.poly(false, 21.0f, 15.0f, 16.0f, 10.0f, 5.0f, 21.0f);
                break;
            }
            case WINGS: {
                p.arc(5.0f, 12.0f, 7.0f, -80.0f, 160.0f);
                p.arc(19.0f, 12.0f, 7.0f, 100.0f, 160.0f);
                p.poly(false, 12.0f, 6.0f, 2.0f, 4.0f, 3.5f, 13.0f, 9.0f, 18.0f);
                p.poly(false, 12.0f, 6.0f, 22.0f, 4.0f, 20.5f, 13.0f, 15.0f, 18.0f);
                break;
            }
            case HALO: {
                p.arc(12.0f, 6.0f, 8.0f, 0.0f, 360.0f);
                p.circle(12.0f, 16.0f, 4.0f);
                break;
            }
            case CAPE: {
                p.poly(true, 7.0f, 3.0f, 17.0f, 3.0f, 20.0f, 21.0f, 12.0f, 18.5f, 4.0f, 21.0f);
                p.line(9.5f, 3.0f, 12.0f, 7.0f);
                p.line(14.5f, 3.0f, 12.0f, 7.0f);
                break;
            }
            case SMILE: {
                p.circle(12.0f, 12.0f, 9.5f);
                p.arc(12.0f, 12.0f, 5.0f, 30.0f, 120.0f);
                p.dot(9.0f, 9.5f, 1.3f);
                p.dot(15.0f, 9.5f, 1.3f);
                break;
            }
            case CROWN: {
                p.poly(true, 3.0f, 7.0f, 7.5f, 12.0f, 12.0f, 4.0f, 16.5f, 12.0f, 21.0f, 7.0f, 19.0f, 18.0f, 5.0f, 18.0f);
                p.line(5.0f, 21.0f, 19.0f, 21.0f);
                break;
            }
            case MEDAL: {
                p.circle(12.0f, 15.0f, 6.0f);
                p.poly(false, 8.0f, 10.5f, 5.0f, 3.0f, 10.0f, 3.0f, 12.0f, 9.0f);
                p.poly(false, 16.0f, 10.5f, 19.0f, 3.0f, 14.0f, 3.0f, 12.0f, 9.0f);
                p.star(12.0f, 15.2f, 2.8f);
                break;
            }
            case TAG: {
                p.poly(true, 3.0f, 3.0f, 12.0f, 3.0f, 21.0f, 12.0f, 12.0f, 21.0f, 3.0f, 12.0f);
                p.dot(8.0f, 8.0f, 1.6f);
                break;
            }
            case FRAME: {
                p.rect(3.0f, 3.0f, 18.0f, 18.0f, 9.0f);
                p.circle(12.0f, 10.0f, 3.0f);
                p.arc(12.0f, 18.5f, 5.0f, 200.0f, 140.0f);
                break;
            }
            case ARROW_UP: {
                p.line(12.0f, 20.0f, 12.0f, 4.0f);
                p.poly(false, 6.0f, 10.0f, 12.0f, 4.0f, 18.0f, 10.0f);
                break;
            }
            case WIFI: {
                p.arc(12.0f, 19.0f, 15.0f, 225.0f, 90.0f);
                p.arc(12.0f, 19.0f, 10.0f, 225.0f, 90.0f);
                p.arc(12.0f, 19.0f, 5.0f, 225.0f, 90.0f);
                p.dot(12.0f, 19.0f, 1.6f);
                break;
            }
            case TROPHY: {
                p.poly(false, 7.0f, 3.0f, 17.0f, 3.0f, 17.0f, 10.0f);
                p.arc(12.0f, 10.0f, 5.0f, 0.0f, 180.0f);
                p.line(7.0f, 3.0f, 7.0f, 10.0f);
                p.arc(7.0f, 7.0f, 3.0f, 90.0f, 180.0f);
                p.arc(17.0f, 7.0f, 3.0f, 270.0f, 180.0f);
                p.line(12.0f, 15.0f, 12.0f, 19.0f);
                p.line(7.5f, 20.5f, 16.5f, 20.5f);
                break;
            }
            case HAND: {
                p.poly(false, 8.0f, 13.0f, 8.0f, 5.0f, 10.5f, 5.0f, 10.5f, 12.0f);
                p.poly(false, 10.5f, 11.0f, 10.5f, 3.5f, 13.0f, 3.5f, 13.0f, 11.0f);
                p.poly(false, 13.0f, 11.0f, 13.0f, 5.0f, 15.5f, 5.0f, 15.5f, 12.0f);
                p.poly(false, 15.5f, 12.0f, 15.5f, 8.0f, 18.0f, 8.0f, 18.0f, 15.0f);
                p.arc(12.0f, 15.0f, 6.0f, 0.0f, 180.0f);
                p.poly(false, 8.0f, 13.0f, 5.5f, 10.5f, 4.5f, 12.0f, 6.0f, 15.0f);
                break;
            }
            case SNEAK: {
                p.circle(12.0f, 4.5f, 2.0f);
                p.poly(false, 12.0f, 7.0f, 10.0f, 13.0f, 14.0f, 16.0f, 14.0f, 21.0f);
                p.poly(false, 10.0f, 13.0f, 6.0f, 17.0f, 6.0f, 21.0f);
                p.poly(false, 11.5f, 9.0f, 16.5f, 11.0f);
                break;
            }
            case MOVE: {
                p.line(12.0f, 2.5f, 12.0f, 21.5f);
                p.line(2.5f, 12.0f, 21.5f, 12.0f);
                p.poly(false, 9.0f, 5.5f, 12.0f, 2.5f, 15.0f, 5.5f);
                p.poly(false, 9.0f, 18.5f, 12.0f, 21.5f, 15.0f, 18.5f);
                p.poly(false, 5.5f, 9.0f, 2.5f, 12.0f, 5.5f, 15.0f);
                p.poly(false, 18.5f, 9.0f, 21.5f, 12.0f, 18.5f, 15.0f);
                break;
            }
            case EDIT: {
                p.poly(true, 15.5f, 4.0f, 20.0f, 8.5f, 8.5f, 20.0f, 4.0f, 20.0f, 4.0f, 15.5f);
                p.line(13.0f, 6.5f, 17.5f, 11.0f);
                break;
            }
            case CHECK: {
                p.poly(false, 5.0f, 12.5f, 10.0f, 17.5f, 19.0f, 7.0f);
                break;
            }
            case COPY: {
                p.rect(8.0f, 8.0f, 13.0f, 13.0f, 2.0f);
                p.poly(false, 4.0f, 16.0f, 3.0f, 16.0f, 3.0f, 3.0f, 16.0f, 3.0f, 16.0f, 4.0f);
                break;
            }
            case FILM: {
                p.rect(3.0f, 3.0f, 18.0f, 18.0f, 2.0f);
                p.line(7.5f, 3.0f, 7.5f, 21.0f);
                p.line(16.5f, 3.0f, 16.5f, 21.0f);
                p.line(3.0f, 12.0f, 21.0f, 12.0f);
                break;
            }
            case SPEED: {
                p.arc(12.0f, 14.0f, 9.0f, 180.0f, 180.0f);
                p.line(12.0f, 14.0f, 16.5f, 8.5f);
                p.dot(12.0f, 14.0f, 1.6f);
                break;
            }
            case WAVE: {
                p.arc(6.0f, 9.0f, 3.0f, 180.0f, 180.0f);
                p.arc(12.0f, 9.0f, 3.0f, 0.0f, 180.0f);
                p.arc(18.0f, 9.0f, 3.0f, 180.0f, 180.0f);
                p.arc(6.0f, 16.0f, 3.0f, 180.0f, 180.0f);
                p.arc(12.0f, 16.0f, 3.0f, 0.0f, 180.0f);
                p.arc(18.0f, 16.0f, 3.0f, 180.0f, 180.0f);
                break;
            }
            case LOGO: {
                p.star(12.0f, 12.0f, 10.0f);
            }
        }
        Render2D.submit(ctx, p.m, RenderPipelines.GUI, TextureSetup.noTexture());
    }

    private static final class Pen {
        final Mesh m = PEN_MESH;
        final float ox;
        final float oy;
        final float k;
        final float stroke;
        final float px;
        final int color;

        Pen(float x, float y, float size, int color, float px, float strokeMul) {
            this.ox = x;
            this.oy = y;
            this.k = size / 24.0f;
            this.color = color;
            this.px = px;
            this.stroke = Math.max(px, 2.0f * this.k * strokeMul);
            this.m.reset();
        }

        float X(float v) {
            return this.ox + v * this.k;
        }

        float Y(float v) {
            return this.oy + v * this.k;
        }

        void line(float x1, float y1, float x2, float y2) {
            Render2D.addLine(this.m, this.X(x1), this.Y(y1), this.X(x2), this.Y(y2), this.stroke, this.color, true, this.px);
        }

        void poly(boolean closed, float ... p) {
            int i = 0;
            while (i + 3 < p.length) {
                this.line(p[i], p[i + 1], p[i + 2], p[i + 3]);
                i += 2;
            }
            if (closed && p.length >= 6) {
                this.line(p[p.length - 2], p[p.length - 1], p[0], p[1]);
            }
        }

        void circle(float cx, float cy, float r) {
            Render2D.addArc(this.m, this.X(cx), this.Y(cy), r * this.k, this.stroke, 0.0f, 360.0f, this.color, this.px);
        }

        void arc(float cx, float cy, float r, float start, float sweep) {
            Render2D.addArc(this.m, this.X(cx), this.Y(cy), r * this.k, this.stroke, start, sweep, this.color, this.px);
        }

        void dot(float cx, float cy, float r) {
            Render2D.addCircle(this.m, this.X(cx), this.Y(cy), r * this.k, Paint.solid(this.color), this.px);
        }

        void rect(float x, float y, float w, float h, float r) {
            Render2D.addOutline(this.m, this.X(x) - this.stroke / 2.0f, this.Y(y) - this.stroke / 2.0f, w * this.k + this.stroke, h * this.k + this.stroke, r * this.k + this.stroke / 2.0f, r * this.k + this.stroke / 2.0f, r * this.k + this.stroke / 2.0f, r * this.k + this.stroke / 2.0f, this.stroke, Paint.solid(this.color), this.px);
        }

        void fillRect(float x, float y, float w, float h, float r) {
            float rr = r * this.k;
            Render2D.addRoundRect(this.m, this.X(x), this.Y(y), w * this.k, h * this.k, rr, rr, rr, rr, Paint.solid(this.color), this.px);
        }

        void sparkle(float cx, float cy, float s) {
            Render2D.addSparkle(this.m, this.X(cx), this.Y(cy), s * this.k, 0.0f, this.color, this.px);
        }

        void star(float cx, float cy, float s) {
            Render2D.addStar(this.m, this.X(cx), this.Y(cy), s * this.k, 0.0f, this.color, this.px);
        }

        void heart(float cx, float cy, float s) {
            Render2D.addHeart(this.m, this.X(cx), this.Y(cy), s * this.k, this.color, this.px);
        }
    }
}

