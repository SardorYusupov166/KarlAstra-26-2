/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.GuiMessage
 *  net.minecraft.client.GuiMessage$Line
 *  net.minecraft.client.gui.components.ChatComponent
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package astra.mixin;

import java.util.List;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ChatComponent.class})
public interface ChatHudAccessor {
    @Accessor(value="chatScrollbarPos")
    public int astra$scrolledLines();

    @Invoker(value="getLineHeight")
    public int astra$lineHeight();

    @Accessor(value="allMessages")
    public List<GuiMessage> astra$messages();

    @Accessor(value="trimmedMessages")
    public List<GuiMessage.Line> astra$visibleMessages();

    @Invoker(value="getWidth")
    public int astra$width();

    @Invoker(value="getScale")
    public double astra$chatScale();

    @Invoker(value="refreshTrimmedMessages")
    public void astra$refresh();
}

