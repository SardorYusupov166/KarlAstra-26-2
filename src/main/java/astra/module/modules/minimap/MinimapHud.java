/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.resources.Identifier
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.MapColor
 *  org.joml.Matrix3x2fStack
 */
package astra.module.modules.minimap;

import astra.gui.UiContext;
import astra.gui.components.CardPreview;
import astra.hud.HudModule;
import astra.module.Category;
import astra.module.modules.hud.CoordinatesHud;
import astra.module.modules.minimap.WaypointsModule;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Mesh;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.theme.Theme;
import astra.waypoint.Waypoints;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import org.joml.Matrix3x2fStack;

public class MinimapHud
extends HudModule {
    private static final int TEX = 256;
    private static final Identifier TEXTURE_ID = Identifier.fromNamespaceAndPath((String)"astra", (String)"minimap");
    private final ModeSetting shape = this.add(new ModeSetting("\u0424\u043e\u0440\u043c\u0430", "\u041a\u0440\u0443\u0433", "\u041a\u0440\u0443\u0433", "\u041a\u0432\u0430\u0434\u0440\u0430\u0442"));
    private final NumberSetting zoom = this.add(new NumberSetting("\u041c\u0430\u0441\u0448\u0442\u0430\u0431", 100.0, 50.0, 400.0, 25.0).unit("%"));
    private final NumberSetting mapOpacity = this.add(new NumberSetting("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u043a\u0430\u0440\u0442\u044b", 95.0, 20.0, 100.0, 5.0).unit("%"));
    private final BooleanSetting rotate = this.add(new BooleanSetting("\u0412\u0440\u0430\u0449\u0430\u0442\u044c \u0441 \u0438\u0433\u0440\u043e\u043a\u043e\u043c", true));
    private final BooleanSetting compass = this.add(new BooleanSetting("\u041a\u043e\u043c\u043f\u0430\u0441", true));
    private final BooleanSetting coords = this.add(new BooleanSetting("\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b", true));
    private final BooleanSetting biome = this.add(new BooleanSetting("\u0411\u0438\u043e\u043c", true));
    private final BooleanSetting explored = this.add((BooleanSetting)new BooleanSetting("\u041f\u043e\u0441\u0435\u0449\u0451\u043d\u043d\u0430\u044f \u043c\u0435\u0441\u0442\u043d\u043e\u0441\u0442\u044c", true).desc("\u041f\u043e\u043c\u043d\u0438\u0442\u044c \u0443\u0436\u0435 \u0443\u0432\u0438\u0434\u0435\u043d\u043d\u044b\u0435 \u0447\u0430\u043d\u043a\u0438 \u0434\u043e \u0432\u044b\u0445\u043e\u0434\u0430 \u0438\u0437 \u043c\u0438\u0440\u0430"));
    private final Map<Long, int[]> chunks = new HashMap<Long, int[]>();
    private final Map<Long, Long> updatedAt = new HashMap<Long, Long>();
    private ClientLevel cachedWorld;
    private DynamicTexture texture;
    private int texCenterX;
    private int texCenterZ;
    private int ticks;
    private int lastBuiltX = Integer.MIN_VALUE;
    private int lastBuiltZ = Integer.MIN_VALUE;
    private static final float SIZE = 90.0f;

    public MinimapHud() {
        super("minimap", "\u041c\u0438\u043d\u0438\u043a\u0430\u0440\u0442\u0430", "\u041a\u0430\u0440\u0442\u0430 \u043c\u0435\u0441\u0442\u043d\u043e\u0441\u0442\u0438 \u0432\u043e\u043a\u0440\u0443\u0433 \u0432\u0430\u0441 \u0441 \u043a\u043e\u043c\u043f\u0430\u0441\u043e\u043c.", Category.MINIMAP, Icon.MAP, 0.92f, 0.16f);
        this.setDefaultEnabled(true);
    }

    @Override
    public void onTick() {
        ClientLevel world = MinimapHud.mc.level;
        if (world == null || MinimapHud.mc.player == null) {
            return;
        }
        if (world != this.cachedWorld) {
            this.chunks.clear();
            this.updatedAt.clear();
            this.cachedWorld = world;
        }
        int pcx = MinimapHud.mc.player.getBlockX() >> 4;
        int pcz = MinimapHud.mc.player.getBlockZ() >> 4;
        int radius = Math.min(MinimapHud.mc.options.getEffectiveRenderDistance(), 8);
        long now = System.currentTimeMillis();
        int budget = 6;
        for (int r = 0; r <= radius && budget > 0; ++r) {
            for (int dx = -r; dx <= r && budget > 0; ++dx) {
                for (int dz = -r; dz <= r && budget > 0; ++dz) {
                    long maxAge;
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != r) continue;
                    int cx = pcx + dx;
                    int cz = pcz + dz;
                    if (!world.getChunkSource().hasChunk(cx, cz)) continue;
                    long key2 = ChunkPos.pack((int)cx, (int)cz);
                    Long last = this.updatedAt.get(key2);
                    long l = maxAge = r <= 1 ? 1000L : 4000L;
                    if (last != null && now - last < maxAge) continue;
                    this.chunks.put(key2, this.scanChunk(world, cx, cz));
                    this.updatedAt.put(key2, now);
                    --budget;
                }
            }
        }
        if (!((Boolean)this.explored.get()).booleanValue()) {
            this.chunks.keySet().removeIf(key -> !world.getChunkSource().hasChunk(ChunkPos.getX((long)key), ChunkPos.getZ((long)key)));
        }
        if (this.chunks.size() > 6000) {
            Iterator<Map.Entry<Long, Long>> it = this.updatedAt.entrySet().iterator();
            while (it.hasNext() && this.chunks.size() > 5000) {
                Map.Entry<Long, Long> e = it.next();
                if (now - e.getValue() <= 60000L) continue;
                this.chunks.remove(e.getKey());
                it.remove();
            }
        }
        if (++this.ticks % 2 == 0) {
            int bx = MinimapHud.mc.player.getBlockX();
            int bz = MinimapHud.mc.player.getBlockZ();
            if (budget < 6 || bx != this.lastBuiltX || bz != this.lastBuiltZ || this.texture == null) {
                this.lastBuiltX = bx;
                this.lastBuiltZ = bz;
                this.rebuildTexture();
            }
        }
    }

    private int[] scanChunk(ClientLevel world, int cx, int cz) {
        int x;
        int z;
        int[] colors = new int[256];
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int baseX = cx << 4;
        int baseZ = cz << 4;
        int[] heights = new int[289];
        for (z = -1; z < 16; ++z) {
            for (x = 0; x < 16; ++x) {
                heights[(z + 1) * 17 + x] = world.getHeight(Heightmap.Types.WORLD_SURFACE, baseX + x, baseZ + z) - 1;
            }
        }
        for (z = 0; z < 16; ++z) {
            for (x = 0; x < 16; ++x) {
                int north;
                MapColor mapColor;
                int depth;
                int y = heights[(z + 1) * 17 + x];
                pos.set(baseX + x, y, baseZ + z);
                BlockState state = world.getBlockState((BlockPos)pos);
                FluidState fluid = state.getFluidState();
                if (fluid.is(FluidTags.WATER)) {
                    BlockPos.MutableBlockPos p = pos.mutable();
                    for (depth = 0; depth < 12 && world.getFluidState((BlockPos)p).is(FluidTags.WATER); ++depth) {
                        p.move(0, -1, 0);
                    }
                }
                if ((mapColor = state.getMapColor((BlockGetter)world, (BlockPos)pos)) == MapColor.NONE) {
                    colors[z * 16 + x] = 0;
                    continue;
                }
                int rgb = mapColor.col;
                float shade = depth > 0 ? 1.0f - Math.min(0.45f, (float)depth * 0.04f) : (y > (north = heights[z * 17 + x]) ? 1.08f : (y < north ? 0.84f : 0.96f));
                int r = Math.min(255, Math.round((float)(rgb >> 16 & 0xFF) * shade));
                int g = Math.min(255, Math.round((float)(rgb >> 8 & 0xFF) * shade));
                int b = Math.min(255, Math.round((float)(rgb & 0xFF) * shade));
                colors[z * 16 + x] = 0xFF000000 | r << 16 | g << 8 | b;
            }
        }
        return colors;
    }

    private void rebuildTexture() {
        NativeImage img;
        if (this.texture == null) {
            this.texture = new DynamicTexture(() -> "astra_minimap", 256, 256, true);
            mc.getTextureManager().register(TEXTURE_ID, (AbstractTexture)this.texture);
        }
        if ((img = this.texture.getPixels()) == null || MinimapHud.mc.player == null) {
            return;
        }
        this.texCenterX = MinimapHud.mc.player.getBlockX();
        this.texCenterZ = MinimapHud.mc.player.getBlockZ();
        int half = 128;
        int empty = -15857126;
        for (int tz = 0; tz < 256; ++tz) {
            int wz = this.texCenterZ - half + tz;
            int cz = wz >> 4;
            int row = (wz & 0xF) * 16;
            int lastCx = Integer.MIN_VALUE;
            int[] c = null;
            for (int tx = 0; tx < 256; ++tx) {
                int wx = this.texCenterX - half + tx;
                int cx = wx >> 4;
                if (cx != lastCx) {
                    lastCx = cx;
                    c = this.chunks.get(ChunkPos.pack((int)cx, (int)cz));
                }
                int col = c == null ? empty : c[row + (wx & 0xF)];
                img.setPixel(tx, tz, col == 0 ? empty : col);
            }
        }
        this.texture.upload();
    }

    @Override
    public float width() {
        return 90.0f;
    }

    @Override
    public float height() {
        float h = 90.0f;
        if (((Boolean)this.coords.get()).booleanValue()) {
            h += 11.0f;
        }
        if (((Boolean)this.biome.get()).booleanValue()) {
            h += 9.0f;
        }
        return h;
    }

    @Override
    protected boolean drawsPanel() {
        return false;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        this.drawMap(ctx, MinimapHud.theme(), 0.0f, 0.0f, 90.0f, this.accentColor(), editor);
        float y = 92.0f;
        Theme t = MinimapHud.theme();
        if (((Boolean)this.coords.get()).booleanValue()) {
            String s = MinimapHud.mc.player != null ? "X: " + MinimapHud.mc.player.getBlockX() + "  Y: " + MinimapHud.mc.player.getBlockY() + "  Z: " + MinimapHud.mc.player.getBlockZ() : "X: 1236  Y: 98  Z: -452";
            Fonts.drawCentered(ctx, s, w / 2.0f, y, 6.5f, t.text(), true);
            y += 10.0f;
        }
        if (((Boolean)this.biome.get()).booleanValue()) {
            Fonts.drawCentered(ctx, "\u0411\u0438\u043e\u043c: " + CoordinatesHud.biomeName(), w / 2.0f, y, 6.0f, t.textDim(), false);
        }
    }

    private void drawMap(GuiGraphicsExtractor ctx, Theme t, float x, float y, float size, int accent, boolean editor) {
        WaypointsModule wps;
        int col;
        float rotation;
        float glow;
        boolean circle = this.shape.is("\u041a\u0440\u0443\u0433");
        float r = size / 2.0f;
        float cx = x + r;
        float cy = y + r;
        float f = glow = "Astra Glow".equals(this.frame.get()) ? 1.0f : 0.0f;
        if (glow > 0.0f) {
            Render2D.glow(ctx, x, y, size, size, circle ? r : 8.0f, 8.0f, ColorUtil.withAlpha(accent, 0.35f));
        }
        Render2D.roundRect(ctx, x, y, size, size, circle ? r : 8.0f, ColorUtil.withAlpha(t.bg(), 0.9f));
        float inner = r - 3.0f;
        float yaw = MinimapHud.mc.player != null ? MinimapHud.mc.player.getYRot() : 0.0f;
        float f2 = rotation = (Boolean)this.rotate.get() != false ? -(yaw + 180.0f) : 0.0f;
        if (this.texture != null && MinimapHud.mc.player != null) {
            float blocksRadius = inner / (this.zoom.getFloat() / 100.0f) * 0.75f;
            float offX = (float)(MinimapHud.mc.player.getX() - ((double)this.texCenterX + 0.5));
            float offZ = (float)(MinimapHud.mc.player.getZ() - ((double)this.texCenterZ + 0.5));
            float u = (128.5f + offX) / 256.0f;
            float v = (128.5f + offZ) / 256.0f;
            float uvR = blocksRadius / 256.0f;
            col = ColorUtil.withAlpha(-1, this.mapOpacity.getFloat() / 100.0f);
            if (circle) {
                Render2D.texturedCircle(ctx, this.texture.getTextureView(), cx, cy, inner, u, v, uvR, -rotation, col);
            } else {
                Render2D.texturedSquare(ctx, this.texture.getTextureView(), cx, cy, inner, u, v, uvR, -rotation, col);
            }
        } else {
            Render2D.roundRect(ctx, cx - inner, cy - inner, inner * 2.0f, inner * 2.0f, circle ? inner : 6.0f, Paint.linear(cx - inner, cy - inner, cx + inner, cy + inner, -12875190, ColorUtil.lerp(-13738072, t.accent2(), 0.3f)));
            for (int i = 0; i < 7; ++i) {
                float a = (float)i * 0.9f;
                Render2D.petal(ctx, cx + (float)Math.cos(a) * inner * 0.55f, cy + (float)Math.sin(a * 1.3f) * inner * 0.5f, 5.0f, i * 50, Paint.solid(ColorUtil.withAlpha(-28720, 0.85f)));
            }
        }
        Render2D.outline(ctx, x + 1.0f, y + 1.0f, size - 2.0f, size - 2.0f, circle ? r - 1.0f : 7.0f, 1.6f, Paint.linear(x, y, x + size, y + size, accent, t.accent2()));
        if (((Boolean)this.compass.get()).booleanValue()) {
            String[] dirs = new String[]{"N", "E", "S", "W"};
            for (int i = 0; i < 4; ++i) {
                double ang = Math.toRadians((float)(i * 90 - 90) + ((Boolean)this.rotate.get() != false ? rotation : 0.0f));
                float dx = (float)Math.cos(ang) * (inner - 6.0f);
                float dy = (float)Math.sin(ang) * (inner - 6.0f);
                col = i == 0 ? accent : -1;
                Render2D.circle(ctx, cx + dx, cy + dy, 4.5f, ColorUtil.withAlpha(t.bg(), 0.7f));
                Fonts.drawCentered(ctx, dirs[i], cx + dx, cy + dy - 3.2f, 6.0f, col, true);
            }
        }
        if ((wps = WaypointsModule.get()) != null && wps.showOnMinimap() && MinimapHud.mc.player != null && !editor) {
            float blocksRadius = inner / (this.zoom.getFloat() / 100.0f) * 0.75f;
            float pxPerBlock = inner / blocksRadius;
            for (Waypoints.Waypoint w : Waypoints.here()) {
                double dx = (double)w.x + 0.5 - MinimapHud.mc.player.getX();
                double dz = (double)w.z + 0.5 - MinimapHud.mc.player.getZ();
                double ang = Math.atan2(dz, dx) + Math.toRadians(rotation);
                float dist = (float)Math.sqrt(dx * dx + dz * dz) * pxPerBlock;
                boolean edge = dist > inner - 5.0f;
                float d = Math.min(dist, inner - 5.0f);
                float mx = cx + (float)Math.cos(ang) * d;
                float my = cy + (float)Math.sin(ang) * d;
                Render2D.circle(ctx, mx, my, edge ? 2.6f : 3.4f, ColorUtil.withAlpha(-15464420, 0.8f));
                Render2D.circle(ctx, mx, my, edge ? 1.8f : 2.5f, w.color | 0xFF000000);
            }
        }
        float arrowRot = (Boolean)this.rotate.get() != false ? 0.0f : yaw + 180.0f;
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(cx, cy);
        m.rotate((float)Math.toRadians(arrowRot));
        Render2D.glow(ctx, -3.0f, -3.0f, 6.0f, 6.0f, 3.0f, 5.0f, ColorUtil.withAlpha(accent, 0.6f));
        Mesh mesh = Render2D.begin();
        float px = Render2D.pixel(ctx);
        Render2D.addPolygon(mesh, new float[]{0.0f, -5.5f, 4.0f, 4.0f, 0.0f, 2.0f, -4.0f, 4.0f}, 4, 0.0f, 0.0f, Paint.solid(-1), px);
        Render2D.end(ctx);
        m.popMatrix();
    }

    @Override
    public CardPreview getPreview() {
        return new CardPreview(){

            @Override
            public float height(float width) {
                return 100.0f;
            }

            @Override
            public void render(UiContext ui, float x, float y, float w, float h) {
                MinimapHud.this.drawMap(ui.ctx, ui.theme, x + (w - 96.0f) / 2.0f, y + 2.0f, 96.0f, MinimapHud.this.accentColor(), true);
            }
        };
    }

    @Override
    protected void onDisable() {
        this.chunks.clear();
        this.updatedAt.clear();
    }

    private static float wrap(float deg) {
        return Mth.wrapDegrees((float)deg);
    }
}

