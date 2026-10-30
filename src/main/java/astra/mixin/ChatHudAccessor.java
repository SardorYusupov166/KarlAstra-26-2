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
    @Accessor(value="field_2066")
    public int astra$scrolledLines();

    @Invoker(value="method_44752")
    public int astra$lineHeight();

    @Accessor(value="field_2061")
    public List<GuiMessage> astra$messages();

    @Accessor(value="field_2064")
    public List<GuiMessage.Line> astra$visibleMessages();

    @Invoker(value="method_1811")
    public int astra$width();

    @Invoker(value="method_1814")
    public double astra$chatScale();

    @Invoker(value="method_44813")
    public void astra$refresh();
}

