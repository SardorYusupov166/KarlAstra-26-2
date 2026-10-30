/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 */
package astra.module.modules.cosmetic;

import astra.animation.AnimationUtils;
import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.module.modules.cosmetic.ProfileStyle;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.KeybindSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.sound.AstraSounds;
import astra.sound.SoundSlot;
import astra.sound.user.SoundPacks;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SoundsModule
extends Module {
    private static final Set<String> EXPLICIT = Set.of("join_3", "hurt_3", "hurt_4", "death_1", "kill_8", "board_1", "board_2", "totem_3");
    private final NumberSetting master = this.add(new NumberSetting("\u041e\u0431\u0449\u0430\u044f \u0433\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 80.0, 0.0, 100.0, 5.0).unit("%"));
    private final BooleanSetting adult = this.add((BooleanSetting)new BooleanSetting("\u0424\u0440\u0430\u0437\u044b 18+", true).desc("\u0412\u044b\u043a\u043b\u044e\u0447\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u043d\u0435 \u0437\u0432\u0443\u0447\u0430\u043b\u0438 \u0444\u0440\u0430\u0437\u044b \u0441 \u043c\u0430\u0442\u043e\u043c \u0438 \u043f\u043e\u0448\u043b\u043e\u0441\u0442\u044f\u043c\u0438"));
    private final BooleanSetting noOverlap = this.add((BooleanSetting)((Setting)new BooleanSetting("\u041d\u0435 \u043d\u0430\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u0442\u044c \u0444\u0440\u0430\u0437\u044b", true).desc("\u041e\u0434\u043d\u043e\u0432\u0440\u0435\u043c\u0435\u043d\u043d\u043e \u0437\u0432\u0443\u0447\u0438\u0442 \u043e\u0434\u043d\u0430 \u0444\u0440\u0430\u0437\u0430: \u0432\u0430\u0436\u043d\u044b\u0435 (\u0441\u043c\u0435\u0440\u0442\u044c, \u0442\u043e\u0442\u0435\u043c, \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u043e) \u043f\u0435\u0440\u0435\u0431\u0438\u0432\u0430\u044e\u0442 \u043e\u0441\u0442\u0430\u043b\u044c\u043d\u044b\u0435")).onChange(v -> {
        AstraSounds.singleVoice = v;
    }));
    private static final String NO_PACK = "\u0411\u0435\u0437 \u043f\u0430\u043a\u0430";
    private static final String ALL_PACKS = "\u0412\u0441\u0435 \u043f\u0430\u043a\u0438";
    private final ModeSetting pack = this.add((ModeSetting)new ModeSetting("\u0417\u0432\u0443\u043a\u043e\u0432\u043e\u0439 \u043f\u0430\u043a", "\u0412\u0441\u0435 \u043f\u0430\u043a\u0438", "\u0411\u0435\u0437 \u043f\u0430\u043a\u0430", "\u0412\u0441\u0435 \u043f\u0430\u043a\u0438").dynamic(SoundsModule::packModes).desc("\u0427\u044c\u0438 \u0437\u0432\u0443\u043a\u0438 \u0438\u0433\u0440\u0430\u044e\u0442 \u043d\u0430 \u0441\u043e\u0431\u044b\u0442\u0438\u044f: \u0444\u0430\u0439\u043b\u044b \u0438\u0437 \u043f\u0430\u043f\u043e\u043a \u0441\u043e\u0431\u044b\u0442\u0438\u0439 \u043f\u0430\u043a\u0430 (\u00ab\u0423\u0434\u0430\u0440\u00bb, \u00ab\u0423\u0431\u0438\u0439\u0441\u0442\u0432\u043e\u00bb\u2026)"));
    private final ModeSetting packMode = this.add((ModeSetting)((Setting)new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u043f\u0430\u043a\u0430", "\u0417\u0430\u043c\u0435\u043d\u044f\u0442\u044c", "\u0417\u0430\u043c\u0435\u043d\u044f\u0442\u044c", "\u0421\u043c\u0435\u0448\u0438\u0432\u0430\u0442\u044c").desc("\u0417\u0430\u043c\u0435\u043d\u044f\u0442\u044c \u2014 \u0435\u0441\u043b\u0438 \u0432 \u043f\u0430\u043a\u0435 \u0435\u0441\u0442\u044c \u0437\u0432\u0443\u043a \u0434\u043b\u044f \u0441\u043e\u0431\u044b\u0442\u0438\u044f, \u0437\u0432\u0443\u0447\u0438\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u043e\u043d. \u0421\u043c\u0435\u0448\u0438\u0432\u0430\u0442\u044c \u2014 \u0441\u043b\u0443\u0447\u0430\u0439\u043d\u043e \u0438\u0437 \u043e\u0431\u043e\u0438\u0445")).visibleWhen(() -> !this.pack.is(NO_PACK)));
    private final ButtonSetting openPacks = this.add(new ButtonSetting("\u0421\u0432\u043e\u0438 \u0437\u0432\u0443\u043a\u0438", "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 \u043f\u0430\u043a\u043e\u0432", () -> SoundPacks.open(SoundPacks.root())).label(() -> "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 \u043f\u0430\u043a\u043e\u0432 (" + SoundPacks.packs().size() + ")"));
    private final ButtonSetting newPack = this.add(new ButtonSetting("\u041d\u043e\u0432\u044b\u0439 \u043f\u0430\u043a", "\uff0b \u0421\u043e\u0437\u0434\u0430\u0442\u044c \u043f\u0430\u043a \u0441 \u043f\u0430\u043f\u043a\u0430\u043c\u0438 \u0441\u043e\u0431\u044b\u0442\u0438\u0439", () -> {
        Path dir = SoundPacks.createPack();
        if (dir != null) {
            SoundPacks.open(dir);
        }
    }));
    private final ButtonSetting rescan = this.add(new ButtonSetting("\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c \u043f\u0430\u043a\u0438", "\u27f3 \u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c", () -> SoundPacks.rescanAsync(true)).label(() -> {
        int sounds = 0;
        for (SoundPacks.Pack p : SoundPacks.packs()) {
            sounds += p.soundCount();
        }
        return "\u27f3 \u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c (\u0437\u0432\u0443\u043a\u043e\u0432: " + sounds + ", \u043f\u0435\u0441\u0435\u043d: " + SoundPacks.songs().size() + ")";
    }));
    private final ButtonSetting export = this.add((ButtonSetting)new ButtonSetting("\u041f\u043e\u0434\u0435\u043b\u0438\u0442\u044c\u0441\u044f \u043f\u0430\u043a\u043e\u043c", "\u042d\u043a\u0441\u043f\u043e\u0440\u0442 \u043f\u0430\u043a\u0430 \u0432 .zip", () -> {
        SoundPacks.Pack p = SoundPacks.pack((String)this.pack.get());
        if (p == null) {
            return;
        }
        Path zip = SoundPacks.exportPack(p);
        if (zip != null) {
            SoundPacks.open(zip.getParent());
        }
    }).visibleWhen(() -> SoundPacks.pack((String)this.pack.get()) != null));
    private final SoundSlot hit = this.slot(new SoundSlot("\u0423\u0434\u0430\u0440", "hit", new String[]{"hit_1", "hit_2", "hit_3", "hit_5", "hit_6", "hit_7", "hit_8"}, 60, this::masterVolume));
    private final SoundSlot crit = this.slot(new SoundSlot("\u041a\u0440\u0438\u0442", "hit", new String[]{"crit_1", "crit_2", "crit_3", "crit_4", "crit_5"}, 70, this::masterVolume));
    private final SoundSlot combo = this.slot(new SoundSlot("\u041a\u043e\u043c\u0431\u043e", "hit", new String[]{"combo_1", "combo_2", "combo_3"}, 80, this::masterVolume));
    private final NumberSetting comboEvery = this.add((NumberSetting)new NumberSetting("\u041a\u043e\u043c\u0431\u043e \u043a\u0430\u0436\u0434\u044b\u0435", 5.0, 2.0, 20.0, 1.0).unit(" \u0443\u0434\u0430\u0440\u043e\u0432").visibleWhen(() -> (Boolean)this.combo.enabled.get()));
    private final SoundSlot kill = this.slot(new SoundSlot("\u0423\u0431\u0438\u0439\u0441\u0442\u0432\u043e", "kill", new String[]{"kill_1", "kill_2", "kill_3", "kill_4", "kill_5", "kill_6", "kill_7", "kill_8", "kill_9"}, 80, this::masterVolume));
    private final SoundSlot killNoWeapon = this.slot(new SoundSlot("\u0423\u0431\u0438\u0439\u0441\u0442\u0432\u043e \u0431\u0435\u0437 \u043e\u0440\u0443\u0436\u0438\u044f", "kill", new String[]{"kill_noweapon_1"}, 80, this::masterVolume));
    private final SoundSlot targetFound = this.slot(new SoundSlot("Target HUD \u043d\u0430\u0448\u0451\u043b \u0438\u0433\u0440\u043e\u043a\u0430", "target", new String[]{"target_1"}, 75, this::masterVolume));
    private final SoundSlot fight = this.slot(new SoundSlot("\u041d\u0430\u0447\u0430\u043b\u043e \u0431\u043e\u044f", "fight", new String[]{"fight_1"}, 70, this::masterVolume));
    private final SoundSlot miss = this.slot(new SoundSlot("\u041f\u0440\u043e\u043c\u0430\u0445", "fight", new String[]{"miss_1"}, 60, this::masterVolume));
    private final NumberSetting missChance = this.add((NumberSetting)new NumberSetting("\u0428\u0430\u043d\u0441 \u0444\u0440\u0430\u0437\u044b \u043f\u0440\u0438 \u043f\u0440\u043e\u043c\u0430\u0445\u0435", 30.0, 5.0, 100.0, 5.0).unit("%").visibleWhen(() -> (Boolean)this.miss.enabled.get()));
    private final SoundSlot join = this.slot(new SoundSlot("\u0412\u0445\u043e\u0434 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440", "join", new String[]{"join_1", "join_2", "join_3", "join_4"}, 80, this::masterVolume));
    private final BooleanSetting joinCaption = this.add((BooleanSetting)new BooleanSetting("\u041d\u0430\u0434\u043f\u0438\u0441\u044c \u043f\u0440\u0438 \u0432\u0445\u043e\u0434\u0435", true).desc("\u041f\u043b\u0430\u0448\u043a\u0430 \u0441 \u0444\u0440\u0430\u0437\u043e\u0439 \u0441\u0432\u0435\u0440\u0445\u0443 \u044d\u043a\u0440\u0430\u043d\u0430"));
    private final SoundSlot menu = this.slot(new SoundSlot("\u041e\u0442\u043a\u0440\u044b\u0442\u0438\u0435 \u043c\u0435\u043d\u044e", "menu", new String[]{"menu_1", "menu_2", "menu_3"}, 50, this::masterVolume));
    private final SoundSlot hurt = this.slot(new SoundSlot("\u041f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u0435 \u0443\u0440\u043e\u043d\u0430", "hurt", new String[]{"hurt_1", "hurt_2", "hurt_3", "hurt_4"}, 70, this::masterVolume));
    private final NumberSetting hurtCooldown = this.add((NumberSetting)new NumberSetting("\u0423\u0440\u043e\u043d: \u043d\u0435 \u0447\u0430\u0449\u0435 \u0447\u0435\u043c \u0440\u0430\u0437 \u0432", 1.5, 0.3, 5.0, 0.1).unit(" \u0441").visibleWhen(() -> (Boolean)this.hurt.enabled.get()));
    private final SoundSlot death = this.slot(new SoundSlot("\u0421\u0432\u043e\u044f \u0441\u043c\u0435\u0440\u0442\u044c", "death", new String[]{"death_1", "death_2", "death_3"}, 80, this::masterVolume));
    private final SoundSlot leave = this.slot(new SoundSlot("\u0412\u044b\u0445\u043e\u0434 \u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430", "join", new String[]{"leave_1"}, 80, this::masterVolume));
    private final SoundSlot toggleOn = this.slot(new SoundSlot("\u0412\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u0444\u0443\u043d\u043a\u0446\u0438\u0438", "toggle", new String[]{"toggle_on_1"}, 60, this::masterVolume));
    private final SoundSlot toggleOff = this.slot(new SoundSlot("\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u0444\u0443\u043d\u043a\u0446\u0438\u0438", "toggle", new String[]{"toggle_off_1"}, 60, this::masterVolume));
    private final SoundSlot close = this.slot(new SoundSlot("\u0417\u0430\u043a\u0440\u044b\u0442\u0438\u0435 \u043c\u0435\u043d\u044e", "menu", new String[]{"close_1", "close_2", "close_3"}, 60, this::masterVolume));
    private final SoundSlot editor = this.slot(new SoundSlot("\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 HUD", "menu", new String[]{"editor_1"}, 60, this::masterVolume));
    private final SoundSlot zoom = this.slot(new SoundSlot("\u0417\u0443\u043c", "zoom", new String[]{"zoom_1", "zoom_2", "zoom_3"}, 60, this::masterVolume));
    private final SoundSlot respawn = this.slot(new SoundSlot("\u0412\u043e\u0437\u0440\u043e\u0436\u0434\u0435\u043d\u0438\u0435", "death", new String[]{"respawn_1", "respawn_2"}, 80, this::masterVolume));
    private final SoundSlot totem = this.slot(new SoundSlot("\u0422\u043e\u0442\u0435\u043c \u0443 \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430", "totem", new String[]{"totem_1", "totem_2", "totem_3"}, 85, this::masterVolume));
    private final BooleanSetting totemSelf = this.add((BooleanSetting)((Setting)new BooleanSetting("\u0422\u043e\u0442\u0435\u043c: \u0438 \u043d\u0430 \u0441\u0432\u043e\u0439 \u0442\u043e\u0436\u0435", false).desc("\u0424\u0440\u0430\u0437\u0430 \u0437\u0432\u0443\u0447\u0438\u0442, \u0434\u0430\u0436\u0435 \u043a\u043e\u0433\u0434\u0430 \u0442\u043e\u0442\u0435\u043c \u0441\u0440\u0430\u0431\u043e\u0442\u0430\u043b \u0443 \u0432\u0430\u0441")).visibleWhen(() -> (Boolean)this.totem.enabled.get()));
    private final SoundSlot itemBreak = this.slot(new SoundSlot("\u041f\u043e\u043b\u043e\u043c\u043a\u0430 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430", "break", new String[]{"break_1"}, 80, this::masterVolume));
    private final SoundSlot weaponLow = this.slot(new SoundSlot("\u041c\u0435\u0447 \u0441\u043a\u043e\u0440\u043e \u0441\u043b\u043e\u043c\u0430\u0435\u0442\u0441\u044f", "break", new String[]{"weapon_low_1"}, 80, this::masterVolume));
    private final NumberSetting weaponThreshold = this.add((NumberSetting)new NumberSetting("\u041f\u043e\u0440\u043e\u0433 \u043f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u0438 \u043c\u0435\u0447\u0430", 10.0, 3.0, 30.0, 1.0).unit("%").visibleWhen(() -> (Boolean)this.weaponLow.enabled.get()));
    private final SoundSlot armorLow = this.slot(new SoundSlot("\u0411\u0440\u043e\u043d\u044f \u0441\u043a\u043e\u0440\u043e \u0441\u043b\u043e\u043c\u0430\u0435\u0442\u0441\u044f", "break", new String[]{"armor_low_1"}, 80, this::masterVolume));
    private final NumberSetting armorThresholdSetting = this.add((NumberSetting)new NumberSetting("\u041f\u043e\u0440\u043e\u0433 \u043f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u0438 \u0431\u0440\u043e\u043d\u0438", 10.0, 3.0, 30.0, 1.0).unit("%").visibleWhen(() -> (Boolean)this.armorLow.enabled.get()));
    private final BooleanSetting durabilityCaption = this.add((BooleanSetting)new BooleanSetting("\u041d\u0430\u0434\u043f\u0438\u0441\u0438 \u043e \u043f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u0438", true).desc("\u041f\u043b\u0430\u0448\u043a\u0430 \u0441\u0432\u0435\u0440\u0445\u0443 \u044d\u043a\u0440\u0430\u043d\u0430 \u0432\u043c\u0435\u0441\u0442\u0435 \u0441\u043e \u0437\u0432\u0443\u043a\u043e\u043c"));
    private final SoundSlot idle = this.slot(new SoundSlot("\u0421\u043a\u0443\u043a\u0430 (\u0410\u0424\u041a)", "idle", new String[]{"idle_1"}, 70, this::masterVolume));
    private final NumberSetting idleSeconds = this.add((NumberSetting)new NumberSetting("\u0410\u0424\u041a \u0447\u0435\u0440\u0435\u0437", 90.0, 20.0, 600.0, 10.0).unit(" \u0441").visibleWhen(() -> (Boolean)this.idle.enabled.get()));
    private final SoundSlot board = this.slot(new SoundSlot("\u0421\u0430\u0443\u043d\u0434\u0431\u043e\u0440\u0434", "board", new String[]{"board_1", "board_2", "board_3", "fight_1", "idle_1", "kill_6", "kill_7", "kill_9", "menu_3", "crit_5", "kill_noweapon_1"}, 80, this::masterVolume));
    private final KeybindSetting boardKey = this.add((KeybindSetting)new KeybindSetting("\u041a\u043b\u0430\u0432\u0438\u0448\u0430 \u0441\u0430\u0443\u043d\u0434\u0431\u043e\u0440\u0434\u0430", 66).visibleWhen(() -> (Boolean)this.board.enabled.get()));
    private final SoundSlot lowHp = this.slot(new SoundSlot("\u041c\u0430\u043b\u043e \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f", "lowhp", new String[]{"lowhp_1"}, 80, this::masterVolume));
    private final NumberSetting lowHpThreshold = this.add((NumberSetting)new NumberSetting("\u041f\u043e\u0440\u043e\u0433 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f", 30.0, 10.0, 60.0, 5.0).unit("%").visibleWhen(() -> (Boolean)this.lowHp.enabled.get()));
    private long lastHit;
    private boolean lowHpArmed = true;
    private int lastHurtTime;
    private boolean deadPlayed;
    private long lastZoomSound;
    private long lastFightSound;
    private long lastTargetSound;
    private Entity lastFound;
    private long lastMissSound;
    private boolean boardWasDown;
    private double idleX;
    private double idleY;
    private double idleZ;
    private float idleYaw;
    private float idlePitch;
    private long idleSince;
    private boolean idlePlayed;
    private String[] caption;
    private long captionAt;
    private long lastHurtSound;
    private int joinDelay = -1;
    private int leaveDelay = -1;
    private int warnedSlot = -1;
    private Item warnedItem;
    private final Item[] armorWarnedItem = new Item[4];
    private final Item[] slotItems = new Item[64];
    private final int[] slotLeft = new int[64];

    private static List<String> packModes() {
        ArrayList<String> list = new ArrayList<String>(List.of(NO_PACK, ALL_PACKS));
        for (SoundPacks.Pack p : SoundPacks.packs()) {
            list.add(p.name());
        }
        return list;
    }

    private List<SoundPacks.Pack> activePacks() {
        if (this.pack.is(NO_PACK)) {
            return List.of();
        }
        if (this.pack.is(ALL_PACKS)) {
            return SoundPacks.packs();
        }
        SoundPacks.Pack p = SoundPacks.pack((String)this.pack.get());
        return p == null ? List.of() : List.of(p);
    }

    private static String[] captionFor(String id) {
        String[] stringArray;
        if (id == null) {
            return new String[]{"\u0421 \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0435\u043d\u0438\u0435\u043c!", "Astra Anime Client"};
        }
        switch (id) {
            case "join_1": {
                String[] stringArray2 = new String[2];
                stringArray2[0] = "\u041e\u0445\u0430\u0451\u0448\u0435\u0447\u043a\u0438 \u0434\u0430 \u0442\u0430\u0431\u0430\u0451\u0448\u0435\u0447\u043a\u0438,";
                stringArray = stringArray2;
                stringArray2[1] = "\u0441\u0435\u043c\u043f\u0430\u0439!";
                break;
            }
            case "join_2": {
                String[] stringArray3 = new String[2];
                stringArray3[0] = "\u041e\u0445\u0430\u0451\u0448\u0435\u0447\u043a\u0438 \u0434\u0430 \u0442\u0430\u0431\u0430\u0451\u0448\u0435\u0447\u043a\u0438,";
                stringArray = stringArray3;
                stringArray3[1] = "\u043f\u043e\u0430\u043d\u0438\u043c\u0435\u0448\u0438\u043c\u0441\u044f?";
                break;
            }
            case "join_3": {
                String[] stringArray4 = new String[2];
                stringArray4[0] = "\u0422\u044b \u0442\u0430\u043a\u043e\u0439 \u043f\u0440\u0435\u043a\u0440\u0430\u0441\u043d\u044b\u0439 \u0441\u0435\u0433\u043e\u0434\u043d\u044f\u2026";
                stringArray = stringArray4;
                stringArray4[1] = "\u044f \u0431\u044b \u0442\u0435\u0431\u044f \u0442\u0440\u0430\u0445\u043d\u0443\u043b\u0430 \u2661";
                break;
            }
            case "join_4": {
                String[] stringArray5 = new String[2];
                stringArray5[0] = "\u0421\u043a\u043e\u043b\u044c\u043a\u043e \u043b\u0435\u0442,";
                stringArray = stringArray5;
                stringArray5[1] = "\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0437\u0438\u043c!";
                break;
            }
            default: {
                String[] stringArray6 = new String[2];
                stringArray6[0] = "\u0421 \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0435\u043d\u0438\u0435\u043c!";
                stringArray = stringArray6;
                stringArray6[1] = "Astra Anime Client";
            }
        }
        return stringArray;
    }

    public SoundsModule() {
        super("anime_sounds", "\u0410\u043d\u0438\u043c\u0435-\u0437\u0432\u0443\u043a\u0438", "\u041d\u044f\u0448\u043d\u044b\u0435 \u0437\u0432\u0443\u043a\u0438 \u043d\u0430 \u0443\u0434\u0430\u0440, \u043a\u0440\u0438\u0442, \u043a\u043e\u043c\u0431\u043e, \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u043e, \u0443\u0440\u043e\u043d, \u0441\u043c\u0435\u0440\u0442\u044c, \u043c\u0430\u043b\u043e \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f, \u0432\u0445\u043e\u0434 \u0438 \u043c\u0435\u043d\u044e.", Category.SOUNDS, Icon.WAVE);
        SoundSlot.activePacks = this::activePacks;
        AstraSounds.singleVoice = true;
        SoundSlot.replaceBuiltin = () -> this.packMode.is("\u0417\u0430\u043c\u0435\u043d\u044f\u0442\u044c");
        this.setDefaultEnabled(true);
    }

    private SoundSlot slot(SoundSlot slot) {
        for (Setting<?> s : slot.settings()) {
            this.add(s);
        }
        return slot;
    }

    private float masterVolume() {
        return this.master.getFloat() / 100.0f;
    }

    public static SoundsModule get() {
        SoundsModule m = ModuleManager.get(SoundsModule.class);
        return m != null && m.isEnabled() ? m : null;
    }

    public void onHit(boolean critical, int comboCount) {
        long now = System.currentTimeMillis();
        if (((Boolean)this.combo.enabled.get()).booleanValue() && comboCount > 0 && comboCount % this.comboEvery.getInt() == 0) {
            this.lastHit = now;
            this.combo.play(this::masterVolume);
            return;
        }
        if (now - this.lastHit < 90L) {
            return;
        }
        this.lastHit = now;
        if (critical && ((Boolean)this.crit.enabled.get()).booleanValue()) {
            this.crit.play(this::masterVolume);
        } else {
            this.hit.play(this::masterVolume);
        }
    }

    private void checkLowHealth() {
        if (SoundsModule.mc.player == null || SoundsModule.mc.player.isDeadOrDying() || SoundsModule.mc.player.isCreative() || SoundsModule.mc.player.isSpectator()) {
            this.lowHpArmed = true;
            return;
        }
        float ratio = SoundsModule.mc.player.getHealth() / Math.max(1.0f, SoundsModule.mc.player.getMaxHealth());
        float threshold = this.lowHpThreshold.getFloat() / 100.0f;
        if (this.lowHpArmed && ratio < threshold) {
            this.lowHp.play(this::masterVolume);
            this.lowHpArmed = false;
        } else if (!this.lowHpArmed && ratio > threshold + 0.1f) {
            this.lowHpArmed = true;
        }
    }

    private void checkWeapon() {
        boolean sword;
        if (SoundsModule.mc.player == null) {
            return;
        }
        Inventory inv = SoundsModule.mc.player.getInventory();
        int sel = inv.getSelectedSlot();
        ItemStack hand = SoundsModule.mc.player.getMainHandItem();
        float threshold = this.weaponThreshold.getFloat() / 100.0f;
        boolean bl = sword = hand.is(ItemTags.SWORDS) && hand.isDamageableItem();
        if (!sword || sel != this.warnedSlot || hand.getItem() != this.warnedItem) {
            if (sword && SoundsModule.durability(hand) < threshold && (sel != this.warnedSlot || hand.getItem() != this.warnedItem)) {
                this.warnedSlot = sel;
                this.warnedItem = hand.getItem();
                this.warn(this.weaponLow, new String[]{"\u0422\u0432\u043e\u0439 \u043c\u0435\u0447\u2026 \u043d\u044f!", "\u0441\u043a\u043e\u0440\u043e \u0441\u043b\u043e\u043c\u0430\u0435\u0442\u0441\u044f!"});
            }
        } else if (SoundsModule.durability(hand) > threshold + 0.05f) {
            this.warnedSlot = -1;
            this.warnedItem = null;
        }
        EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        float armorThreshold = this.armorThresholdSetting.getFloat() / 100.0f;
        for (int i = 0; i < 4; ++i) {
            ItemStack piece = SoundsModule.mc.player.getItemBySlot(slots[i]);
            if (piece.isEmpty() || !piece.isDamageableItem() || piece.getItem() != this.armorWarnedItem[i]) {
                this.armorWarnedItem[i] = null;
            }
            if (piece.isEmpty() || !piece.isDamageableItem()) continue;
            float left = SoundsModule.durability(piece);
            if (left < armorThreshold && this.armorWarnedItem[i] == null) {
                this.armorWarnedItem[i] = piece.getItem();
                this.warn(this.armorLow, new String[]{"\u0422\u0432\u043e\u044f \u0431\u0440\u043e\u043d\u044f", "\u043d\u0430 \u0438\u0441\u0445\u043e\u0434\u0435!"});
                continue;
            }
            if (!(left > armorThreshold + 0.05f)) continue;
            this.armorWarnedItem[i] = null;
        }
    }

    private static float durability(ItemStack stack) {
        return (float)(stack.getMaxDamage() - stack.getDamageValue()) / (float)Math.max(1, stack.getMaxDamage());
    }

    private void warn(SoundSlot slot, String[] text) {
        if (slot.play(this::masterVolume) != null && ((Boolean)this.durabilityCaption.get()).booleanValue()) {
            this.caption = text;
            this.captionAt = System.currentTimeMillis();
        }
    }

    private void checkBreak() {
        if (SoundsModule.mc.player == null) {
            Arrays.fill(this.slotItems, null);
            return;
        }
        Inventory inv = SoundsModule.mc.player.getInventory();
        int size = Math.min(inv.getContainerSize(), this.slotItems.length);
        boolean broke = false;
        for (int i = 0; i < size; ++i) {
            int max;
            ItemStack stack = inv.getItem(i);
            Item prev = this.slotItems[i];
            if (prev != null && stack.isEmpty() && this.slotLeft[i] >= 0 && this.slotLeft[i] <= Math.max(3, (max = prev.getDefaultInstance().getMaxDamage()) / 50)) {
                broke = true;
            }
            this.slotItems[i] = stack.isEmpty() ? null : stack.getItem();
            this.slotLeft[i] = stack.isDamageableItem() ? stack.getMaxDamage() - stack.getDamageValue() : -1;
        }
        if (broke && !SoundsModule.mc.player.isDeadOrDying()) {
            this.itemBreak.play(this::masterVolume);
        }
    }

    private void checkDeath() {
        boolean dead;
        if (SoundsModule.mc.player == null) {
            this.deadPlayed = false;
            return;
        }
        boolean bl = dead = SoundsModule.mc.player.isDeadOrDying() || SoundsModule.mc.player.getHealth() <= 0.0f;
        if (dead && !this.deadPlayed) {
            this.deadPlayed = true;
            this.death.play(this::masterVolume);
        } else if (!dead && this.deadPlayed) {
            this.deadPlayed = false;
            this.respawn.play(this::masterVolume);
        }
    }

    private void checkHurt() {
        long now;
        if (SoundsModule.mc.player == null) {
            this.lastHurtTime = 0;
            return;
        }
        int ht = SoundsModule.mc.player.hurtTime;
        if (ht > this.lastHurtTime && ht > 0 && !SoundsModule.mc.player.isDeadOrDying() && SoundsModule.mc.player.getHealth() > 0.0f && (double)((now = System.currentTimeMillis()) - this.lastHurtSound) > (Double)this.hurtCooldown.get() * 1000.0) {
            this.lastHurtSound = now;
            this.hurt.play(this::masterVolume);
        }
        this.lastHurtTime = ht;
    }

    public void renderHud(GuiGraphics ctx) {
        long life;
        if (this.caption == null) {
            return;
        }
        long age = System.currentTimeMillis() - this.captionAt;
        if (age > (life = 5200L)) {
            this.caption = null;
            return;
        }
        float in = AnimationUtils.easeOutBack(Math.min(1.0f, (float)age / 450.0f));
        float out = Math.min(1.0f, (float)(life - age) / 500.0f);
        Theme t = ThemeManager.get();
        float size = 8.5f;
        float textW = 0.0f;
        for (String line : this.caption) {
            textW = Math.max(textW, Fonts.width(line, size, true));
        }
        float w = textW + 64.0f;
        float h = 44.0f;
        float x = ((float)ctx.guiWidth() - w) / 2.0f;
        float y = 14.0f - (1.0f - in) * 30.0f;
        float prev = Render2D.alpha;
        Render2D.alpha *= Math.min(1.0f, (float)age / 200.0f) * out;
        Render2D.glow(ctx, x, y, w, h, 12.0f, 10.0f, ColorUtil.withAlpha(t.accent2(), 0.45f));
        Render2D.roundRect(ctx, x, y, w, h, 12.0f, Paint.horizontal(x, x + w, ColorUtil.withAlpha(t.bg2(), 0.92f), ColorUtil.withAlpha(ColorUtil.lerp(t.bg2(), t.accent(), 0.35f), 0.92f)));
        Render2D.outline(ctx, x, y, w, h, 12.0f, 1.0f, Paint.horizontal(x, x + w, t.accent(), t.accent2()));
        ProfileStyle style = ProfileStyle.get();
        if (style != null) {
            style.drawAvatar(ctx, t, x + 7.0f, y + 6.0f, 32.0f);
        }
        float ty = y + (h - (float)this.caption.length * (size + 3.0f)) / 2.0f + 1.0f;
        for (int i = 0; i < this.caption.length; ++i) {
            int col = i == this.caption.length - 1 ? ColorUtil.lerp(t.accent2(), -1, 0.25f) : t.text();
            Fonts.draw(ctx, this.caption[i], x + 48.0f, ty + (float)i * (size + 3.0f), size, col, true);
        }
        Render2D.sparkle(ctx, x + w - 10.0f, y + 9.0f, 3.2f, (float)age * 0.2f, -1);
        Render2D.alpha = prev;
    }

    public void onTotemPop(Entity entity) {
        boolean self;
        if (SoundsModule.mc.player == null || entity == null) {
            return;
        }
        boolean bl = self = entity == SoundsModule.mc.player;
        if (self && !((Boolean)this.totemSelf.get()).booleanValue()) {
            return;
        }
        if (!self && entity.distanceToSqr((Entity)SoundsModule.mc.player) > 1024.0) {
            return;
        }
        this.totem.play(this::masterVolume);
    }

    public void onKill() {
        if (SoundsModule.mc.player != null && ((Boolean)this.killNoWeapon.enabled.get()).booleanValue()) {
            boolean weapon;
            ItemStack hand = SoundsModule.mc.player.getMainHandItem();
            boolean bl = weapon = hand.is(ItemTags.SWORDS) || hand.is(ItemTags.AXES) || hand.is(Items.TRIDENT) || hand.is(Items.MACE);
            if (!weapon && this.killNoWeapon.play(this::masterVolume) != null) {
                return;
            }
        }
        this.kill.play(this::masterVolume);
    }

    public void onTargetFound(LivingEntity target) {
        if (!(target instanceof Player) || target == SoundsModule.mc.player || target == this.lastFound) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastTargetSound < 5000L) {
            return;
        }
        this.lastFound = target;
        this.lastTargetSound = now;
        this.targetFound.play(this::masterVolume);
    }

    public void onFightStart() {
        long now = System.currentTimeMillis();
        if (now - this.lastFightSound < 10000L) {
            return;
        }
        this.lastFightSound = now;
        this.fight.play(this::masterVolume);
    }

    public void onMiss() {
        long now = System.currentTimeMillis();
        if (now - this.lastMissSound < 6000L || AstraSoundsRandom.next() * 100.0f >= this.missChance.getFloat()) {
            return;
        }
        this.lastMissSound = now;
        this.miss.play(this::masterVolume);
    }

    private void checkSoundboard() {
        boolean down;
        boolean bl = down = SoundsModule.mc.screen == null && this.boardKey.isBound() && InputConstants.isKeyDown((Window)mc.getWindow(), (int)this.boardKey.getKey());
        if (down && !this.boardWasDown) {
            this.board.play(this::masterVolume);
        }
        this.boardWasDown = down;
    }

    private void checkIdle() {
        boolean moved;
        if (SoundsModule.mc.player == null) {
            this.idleSince = System.currentTimeMillis();
            this.idlePlayed = false;
            return;
        }
        LocalPlayer p = SoundsModule.mc.player;
        boolean bl = moved = Math.abs(p.getX() - this.idleX) > 0.05 || Math.abs(p.getY() - this.idleY) > 0.05 || Math.abs(p.getZ() - this.idleZ) > 0.05 || Math.abs(p.getYRot() - this.idleYaw) > 0.5f || Math.abs(p.getXRot() - this.idlePitch) > 0.5f || SoundsModule.mc.screen != null || p.swinging;
        if (moved) {
            this.idleX = p.getX();
            this.idleY = p.getY();
            this.idleZ = p.getZ();
            this.idleYaw = p.getYRot();
            this.idlePitch = p.getXRot();
            this.idleSince = System.currentTimeMillis();
            this.idlePlayed = false;
            return;
        }
        if (!this.idlePlayed && (double)(System.currentTimeMillis() - this.idleSince) > (Double)this.idleSeconds.get() * 1000.0) {
            this.idlePlayed = true;
            this.idle.play(this::masterVolume);
        }
    }

    public void onZoom() {
        long now = System.currentTimeMillis();
        if (now - this.lastZoomSound < 1000L) {
            return;
        }
        this.lastZoomSound = now;
        this.zoom.play(this::masterVolume);
    }

    public void onModuleToggled(boolean on) {
        if (on) {
            this.toggleOn.play(this::masterVolume);
        } else {
            this.toggleOff.play(this::masterVolume);
        }
    }

    public void onMenuClose() {
        this.close.play(this::masterVolume);
    }

    public void onEditorOpen() {
        this.editor.play(this::masterVolume);
    }

    public void onMenuOpen() {
        this.menu.play(this::masterVolume);
    }

    public void onLeave() {
        this.joinDelay = -1;
        this.leaveDelay = 20;
    }

    public void onJoin() {
        this.joinDelay = 40;
    }

    @Override
    public void onTick() {
        SoundSlot.allowed = id -> (Boolean)this.adult.get() != false || !EXPLICIT.contains(id);
        AstraSounds.singleVoice = (Boolean)this.noOverlap.get();
        this.checkSoundboard();
        this.checkIdle();
        if (this.leaveDelay > 0 && --this.leaveDelay == 0) {
            this.leave.play(this::masterVolume);
            this.leaveDelay = -1;
        }
        this.checkBreak();
        this.checkWeapon();
        this.checkDeath();
        this.checkLowHealth();
        this.checkHurt();
        if (this.joinDelay > 0 && --this.joinDelay == 0 && SoundsModule.mc.player != null) {
            String played = this.join.play(this::masterVolume);
            if (((Boolean)this.joinCaption.get()).booleanValue()) {
                this.caption = SoundsModule.captionFor(played);
                this.captionAt = System.currentTimeMillis();
            }
            this.joinDelay = -1;
        }
    }

    private Section event(String group, SoundSlot slot, String description, Icon icon, Setting<?> ... extra) {
        slot.volume.displayAs("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c");
        slot.variant.displayAs(slot.variant.getName().startsWith("\u0412\u0430\u0440\u0438\u0430\u043d\u0442") ? "\u0412\u0430\u0440\u0438\u0430\u043d\u0442 \u0437\u0432\u0443\u043a\u0430" : "\u0417\u0432\u0443\u043a");
        slot.test.displayAs("\u041f\u0440\u043e\u0441\u043b\u0443\u0448\u0430\u0442\u044c");
        slot.test.label(() -> "\u25b6 \u041f\u0440\u043e\u0441\u043b\u0443\u0448\u0430\u0442\u044c");
        ArrayList list = new ArrayList(List.of(slot.volume, slot.variant));
        list.addAll(List.of(extra));
        list.add((ModeSetting)((Object)slot.test));
        return new Section(group, slot.enabled.getName(), description, icon, slot.enabled, false, list);
    }

    public List<Section> sections() {
        String main = "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0435";
        String fight = "\u0411\u043e\u0439";
        String life = "\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u0438 \u0441\u043c\u0435\u0440\u0442\u044c";
        String server = "\u0421\u0435\u0440\u0432\u0435\u0440 \u0438 \u043c\u0435\u043d\u044e";
        String items = "\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b";
        String other = "\u0420\u0430\u0437\u043d\u043e\u0435";
        this.comboEvery.displayAs("\u041a\u0430\u0436\u0434\u044b\u0435");
        this.missChance.displayAs("\u0428\u0430\u043d\u0441 \u0444\u0440\u0430\u0437\u044b");
        this.hurtCooldown.displayAs("\u041d\u0435 \u0447\u0430\u0449\u0435 \u0447\u0435\u043c \u0440\u0430\u0437 \u0432");
        this.totemSelf.displayAs("\u0418 \u043d\u0430 \u0441\u0432\u043e\u0439 \u0442\u043e\u0442\u0435\u043c");
        this.weaponThreshold.displayAs("\u041f\u043e\u0440\u043e\u0433 \u043f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u0438");
        this.armorThresholdSetting.displayAs("\u041f\u043e\u0440\u043e\u0433 \u043f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u0438");
        this.idleSeconds.displayAs("\u0410\u0424\u041a \u0447\u0435\u0440\u0435\u0437");
        this.boardKey.displayAs("\u041a\u043b\u0430\u0432\u0438\u0448\u0430");
        this.lowHpThreshold.displayAs("\u041f\u043e\u0440\u043e\u0433 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f");
        ArrayList<Section> list = new ArrayList<Section>();
        list.add(new Section(main, "\u0410\u043d\u0438\u043c\u0435-\u0437\u0432\u0443\u043a\u0438", "\u041e\u0431\u0449\u0430\u044f \u0433\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0438 \u043f\u043e\u0432\u0435\u0434\u0435\u043d\u0438\u0435 \u0444\u0440\u0430\u0437.", Icon.WAVE, null, true, List.of(this.master, this.adult, this.noOverlap)));
        list.add(new Section(main, "\u0417\u0432\u0443\u043a\u043e\u0432\u044b\u0435 \u043f\u0430\u043a\u0438", "\u0421\u0432\u043e\u0438 \u0437\u0432\u0443\u043a\u0438 \u043f\u043e\u0432\u0435\u0440\u0445 \u0432\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0445 \u0438\u043b\u0438 \u0432\u043c\u0435\u0441\u0442\u043e \u043d\u0438\u0445.", Icon.FOLDER, null, false, List.of(this.pack, this.packMode, this.openPacks, this.newPack, this.rescan, this.export)));
        list.add(this.event(fight, this.hit, "\u041e\u0431\u044b\u0447\u043d\u044b\u0439 \u0443\u0434\u0430\u0440 \u043f\u043e \u0446\u0435\u043b\u0438.", Icon.SWORD, new Setting[0]));
        list.add(this.event(fight, this.crit, "\u041a\u0440\u0438\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0443\u0434\u0430\u0440 \u0432 \u043f\u0440\u044b\u0436\u043a\u0435.", Icon.BOLT, new Setting[0]));
        list.add(this.event(fight, this.combo, "\u0421\u0435\u0440\u0438\u044f \u0443\u0434\u0430\u0440\u043e\u0432 \u0431\u0435\u0437 \u043f\u0440\u043e\u043f\u0443\u0441\u043a\u043e\u0432.", Icon.FLAME, this.comboEvery));
        list.add(this.event(fight, this.fight, "\u041f\u0435\u0440\u0432\u044b\u0439 \u0443\u0434\u0430\u0440 \u043f\u043e \u043d\u043e\u0432\u043e\u0439 \u0446\u0435\u043b\u0438.", Icon.TARGET, new Setting[0]));
        list.add(this.event(fight, this.targetFound, "Target HUD \u0437\u0430\u0445\u0432\u0430\u0442\u0438\u043b \u0438\u0433\u0440\u043e\u043a\u0430.", Icon.CROSSHAIR, new Setting[0]));
        list.add(this.event(fight, this.miss, "\u0423\u0434\u0430\u0440 \u043f\u043e \u0432\u043e\u0437\u0434\u0443\u0445\u0443.", Icon.MOUSE, this.missChance));
        list.add(this.event(fight, this.totem, "\u0423 \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430 \u0441\u0440\u0430\u0431\u043e\u0442\u0430\u043b \u0442\u043e\u0442\u0435\u043c.", Icon.CROWN, this.totemSelf));
        list.add(this.event(life, this.kill, "\u0426\u0435\u043b\u044c \u043f\u043e\u0432\u0435\u0440\u0436\u0435\u043d\u0430.", Icon.TROPHY, new Setting[0]));
        list.add(this.event(life, this.killNoWeapon, "\u041f\u043e\u0431\u0435\u0434\u0430 \u0433\u043e\u043b\u044b\u043c\u0438 \u0440\u0443\u043a\u0430\u043c\u0438.", Icon.HAND, new Setting[0]));
        list.add(this.event(life, this.hurt, "\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 \u0443\u0440\u043e\u043d.", Icon.HEART, this.hurtCooldown));
        list.add(this.event(life, this.lowHp, "\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u043e\u043f\u0443\u0441\u0442\u0438\u043b\u043e\u0441\u044c \u043d\u0438\u0436\u0435 \u043f\u043e\u0440\u043e\u0433\u0430.", Icon.HEART, this.lowHpThreshold));
        list.add(this.event(life, this.death, "\u0412\u0430\u0448\u0430 \u0441\u043c\u0435\u0440\u0442\u044c.", Icon.STAR, new Setting[0]));
        list.add(this.event(life, this.respawn, "\u0412\u043e\u0437\u0440\u043e\u0436\u0434\u0435\u043d\u0438\u0435 \u043f\u043e\u0441\u043b\u0435 \u0441\u043c\u0435\u0440\u0442\u0438.", Icon.RESET, new Setting[0]));
        list.add(this.event(server, this.join, "\u0417\u0430\u0445\u043e\u0434 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 \u0438\u043b\u0438 \u0432 \u043c\u0438\u0440.", Icon.HOME, this.joinCaption));
        list.add(this.event(server, this.leave, "\u0412\u044b\u0445\u043e\u0434 \u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430.", Icon.CLOSE, new Setting[0]));
        list.add(this.event(server, this.menu, "\u041e\u0442\u043a\u0440\u044b\u0442\u0438\u0435 \u043c\u0435\u043d\u044e Astra.", Icon.GRID, new Setting[0]));
        list.add(this.event(server, this.close, "\u0417\u0430\u043a\u0440\u044b\u0442\u0438\u0435 \u043c\u0435\u043d\u044e Astra.", Icon.CLOSE, new Setting[0]));
        list.add(this.event(server, this.editor, "\u041e\u0442\u043a\u0440\u044b\u0442\u0438\u0435 \u0440\u0435\u0434\u0430\u043a\u0442\u043e\u0440\u0430 HUD.", Icon.EDIT, new Setting[0]));
        list.add(this.event(server, this.toggleOn, "\u0424\u0443\u043d\u043a\u0446\u0438\u044f \u0432\u043a\u043b\u044e\u0447\u0435\u043d\u0430.", Icon.CHECK, new Setting[0]));
        list.add(this.event(server, this.toggleOff, "\u0424\u0443\u043d\u043a\u0446\u0438\u044f \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0430.", Icon.CLOSE, new Setting[0]));
        list.add(this.event(items, this.itemBreak, "\u041f\u0440\u0435\u0434\u043c\u0435\u0442 \u0441\u043b\u043e\u043c\u0430\u043b\u0441\u044f.", Icon.SHIELD, this.durabilityCaption));
        list.add(this.event(items, this.weaponLow, "\u041c\u0435\u0447 \u043f\u043e\u0447\u0442\u0438 \u0441\u043b\u043e\u043c\u0430\u043d.", Icon.SWORD, this.weaponThreshold));
        list.add(this.event(items, this.armorLow, "\u0411\u0440\u043e\u043d\u044f \u043f\u043e\u0447\u0442\u0438 \u0441\u043b\u043e\u043c\u0430\u043d\u0430.", Icon.SHIELD, this.armorThresholdSetting));
        list.add(this.event(other, this.zoom, "\u041f\u0440\u0438\u0431\u043b\u0438\u0436\u0435\u043d\u0438\u0435 \u043a\u0430\u043c\u0435\u0440\u044b.", Icon.ZOOM, new Setting[0]));
        list.add(this.event(other, this.idle, "\u0414\u043e\u043b\u0433\u043e \u0441\u0442\u043e\u0438\u0442\u0435 \u0431\u0435\u0437 \u0434\u0435\u043b\u0430.", Icon.CLOCK, this.idleSeconds));
        list.add(this.event(other, this.board, "\u0424\u0440\u0430\u0437\u0430 \u043f\u043e \u043a\u043b\u0430\u0432\u0438\u0448\u0435.", Icon.KEYBOARD, this.boardKey));
        Set used = Collections.newSetFromMap(new IdentityHashMap());
        for (Section sec : list) {
            used.addAll(sec.settings());
            if (sec.toggle() == null) continue;
            used.add(sec.toggle());
        }
        ArrayList rest = new ArrayList();
        for (Setting<?> st : this.getSettings()) {
            if (used.contains(st) || st.isEditorOnly()) continue;
            rest.add(st);
        }
        if (!rest.isEmpty()) {
            list.add(new Section(other, "\u0414\u0440\u0443\u0433\u043e\u0435", "\u041f\u0440\u043e\u0447\u0438\u0435 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u043e\u0437\u0432\u0443\u0447\u043a\u0438.", Icon.GEAR, null, false, rest));
        }
        return list;
    }

    private static final class AstraSoundsRandom {
        private AstraSoundsRandom() {
        }

        static float next() {
            return AstraSounds.random().nextFloat();
        }
    }

    public record Section(String group, String title, String description, Icon icon, BooleanSetting toggle, boolean moduleSwitch, List<Setting<?>> settings) {
    }
}

