/*
 * Decompiled with CFR 0.152.
 */
package astra.decor;

public final class DecorLayer {
    public static final String MENU = "menu";
    public static final String SCREEN = "screen";
    public static final String DEPTH_BEHIND = "\u041f\u043e\u0434 \u043e\u043a\u043d\u043e\u043c";
    public static final String DEPTH_WINDOW = "\u0424\u043e\u043d \u043e\u043a\u043d\u0430";
    public static final String DEPTH_TOP = "\u041f\u043e\u0432\u0435\u0440\u0445 \u043c\u0435\u043d\u044e";
    public static final String DEPTH_UNDER_HUD = "\u041f\u043e\u0434 HUD";
    public static final String DEPTH_OVER_HUD = "\u041f\u043e\u0432\u0435\u0440\u0445 HUD";
    public static final String FIT_STRETCH = "\u0420\u0430\u0441\u0442\u044f\u043d\u0443\u0442\u044c";
    public static final String FIT_COVER = "\u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u044c (\u043e\u0431\u0440\u0435\u0437\u0430\u0442\u044c)";
    public static final String ANIM_NONE = "\u041d\u0435\u0442";
    public static final String ANIM_FLOAT = "\u041f\u0430\u0440\u0435\u043d\u0438\u0435";
    public static final String ANIM_PULSE = "\u041f\u0443\u043b\u044c\u0441";
    public static final String ANIM_SWAY = "\u041f\u043e\u043a\u0430\u0447\u0438\u0432\u0430\u043d\u0438\u0435";
    public static final String ANIM_SPIN = "\u0412\u0440\u0430\u0449\u0435\u043d\u0438\u0435";
    public static final String ANIM_BLINK = "\u041c\u0435\u0440\u0446\u0430\u043d\u0438\u0435";
    public static final String ANIM_NEON = "\u041d\u0435\u043e\u043d\u043e\u0432\u043e\u0435 \u0434\u044b\u0445\u0430\u043d\u0438\u0435";
    public static final String[] ANIMS = new String[]{"\u041d\u0435\u0442", "\u041f\u0430\u0440\u0435\u043d\u0438\u0435", "\u041f\u0443\u043b\u044c\u0441", "\u041f\u043e\u043a\u0430\u0447\u0438\u0432\u0430\u043d\u0438\u0435", "\u0412\u0440\u0430\u0449\u0435\u043d\u0438\u0435", "\u041c\u0435\u0440\u0446\u0430\u043d\u0438\u0435", "\u041d\u0435\u043e\u043d\u043e\u0432\u043e\u0435 \u0434\u044b\u0445\u0430\u043d\u0438\u0435"};
    public String image = "";
    public String target = "menu";
    public float x = 0.5f;
    public float y = 300.0f;
    public float w = 160.0f;
    public float h = 160.0f;
    public float rotation;
    public float opacity = 100.0f;
    public float radius;
    public String fit = "\u0420\u0430\u0441\u0442\u044f\u043d\u0443\u0442\u044c";
    public String depth = "\u041f\u043e\u0432\u0435\u0440\u0445 \u043c\u0435\u043d\u044e";
    public boolean glow;
    public float tint;
    public float fadeTop;
    public String animation = "\u041d\u0435\u0442";
    public float animSpeed = 100.0f;
    public boolean flipX;
    public boolean flipY;
    public boolean visible = true;
    public float phase = (float)(Math.random() * 10.0);

    public boolean isMenu() {
        return MENU.equals(this.target);
    }

    public DecorLayer copy() {
        DecorLayer c = new DecorLayer();
        c.image = this.image;
        c.target = this.target;
        c.x = this.x;
        c.y = this.y;
        c.w = this.w;
        c.h = this.h;
        c.rotation = this.rotation;
        c.opacity = this.opacity;
        c.radius = this.radius;
        c.fit = this.fit;
        c.depth = this.depth;
        c.glow = this.glow;
        c.tint = this.tint;
        c.fadeTop = this.fadeTop;
        c.animation = this.animation;
        c.animSpeed = this.animSpeed;
        c.flipX = this.flipX;
        c.flipY = this.flipY;
        c.visible = this.visible;
        c.phase = (float)(Math.random() * 10.0);
        return c;
    }
}

