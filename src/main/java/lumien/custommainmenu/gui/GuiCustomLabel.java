package lumien.custommainmenu.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Strings;

import cpw.mods.fml.common.FMLCommonHandler;
import lumien.custommainmenu.configuration.elements.Text;
import lumien.custommainmenu.lib.StringReplacer;
import lumien.custommainmenu.lib.texts.TextString;
import lumien.custommainmenu.util.GlStateManager;

public class GuiCustomLabel extends Gui {

    final Text text;
    final int posX;
    final int posY;
    final FontRenderer fontRendererObj;
    final int width;
    final int height;
    final GuiCustom parent;
    boolean hovered;

    public GuiCustomLabel(GuiCustom customGUI, Text text, int posX, int posY) {
        this.text = text;
        this.posX = posX;
        this.posY = posY;
        this.parent = customGUI;
        this.fontRendererObj = Minecraft.getMinecraft().fontRenderer;
        this.hovered = false;
        this.width = this.fontRendererObj.getStringWidth(text.text.get());
        this.height = this.fontRendererObj.FONT_HEIGHT;
        if (text.name.equals("fml")) {
            StringBuilder string = new StringBuilder();
            List<String> brandings = FMLCommonHandler.instance().getBrandings(true);
            for (int i = 0; i < brandings.size(); ++i) {
                String brd = brandings.get(i);
                if (Strings.isNullOrEmpty(brd)) continue;
                string.append(brd).append(i < brandings.size() - 1 ? "\n" : "");
            }
            this.text.text = this.text.hoverText = new TextString(string.toString());
        }
    }

    public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
        if (this.text.fontSize != 1.0f) {
            GlStateManager.translate(this.posX, this.posY, 0.0f);
            GlStateManager.scale(this.text.fontSize, this.text.fontSize, 1.0f);
            GlStateManager.translate(-this.posX, -this.posY, 0.0f);
        }
        String toDraw = this.getDrawString();
        boolean newHovered = this.isMouseAboveLabel(mouseX, mouseY);
        if (newHovered && !this.hovered && this.text.hoverSound != null) {
            Minecraft.getMinecraft().getSoundHandler()
                    .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(this.text.hoverSound), 1.0f));
        }
        this.hovered = newHovered;
        if (toDraw.contains("\n")) {
            int modY = 0;
            for (String line : toDraw.split("\n")) {
                if (this.hovered) {
                    this.drawString(this.fontRendererObj, line, this.posX, this.posY + modY, this.text.hoverColor);
                } else {
                    this.drawString(this.fontRendererObj, line, this.posX, this.posY + modY, this.text.color);
                }
                modY += this.fontRendererObj.FONT_HEIGHT;
            }
        } else {
            if (this.hovered) {
                this.drawString(this.fontRendererObj, toDraw, this.posX, this.posY, this.text.hoverColor);
            } else {
                this.drawString(this.fontRendererObj, toDraw, this.posX, this.posY, this.text.color);
            }
        }
        if (this.text.fontSize != 1.0f) {
            GlStateManager.translate(this.posX, this.posY, 0.0f);
            GlStateManager.scale(1.0f / this.text.fontSize, 1.0f / this.text.fontSize, 1.0f);
            GlStateManager.translate(-this.posX, -this.posY, 0.0f);
        }
    }

    private String getDrawString() {
        String text = StringReplacer
                .replacePlaceholders(this.hovered ? this.text.hoverText.get() : this.text.text.get());

        for (String dynamicPlaceholder : StringReplacer.dynamicPlaceholders) {
            if (text.contains(dynamicPlaceholder)) {
                return StringReplacer.replaceDynamicPlaceholders(text);
            }
        }
        return text;
    }

    private boolean isMouseAboveLabel(int mouseX, int mouseY) {
        String stringText = this.text.text.get();
        if (stringText.contains("\n")) {
            String[] lines = stringText.split("\n");
            for (int i = 0; i < lines.length; ++i) {
                int width = this.fontRendererObj.getStringWidth(lines[i]);
                int height = this.fontRendererObj.FONT_HEIGHT;
                if (mouseX < this.posX || mouseY < this.posY + this.fontRendererObj.FONT_HEIGHT * i
                        || mouseX >= this.posX + width
                        || mouseY >= this.posY + this.fontRendererObj.FONT_HEIGHT * i + height)
                    continue;
                return true;
            }
            return false;
        }
        return mouseX >= this.posX && mouseY >= this.posY && mouseX < this.posX + width && mouseY < this.posY + height;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = this.isMouseAboveLabel(mouseX, mouseY);
        if (flag && this.text.action != null) {
            if (this.text.pressSound != null) {
                Minecraft.getMinecraft().getSoundHandler().playSound(
                        PositionedSoundRecord.func_147674_a(new ResourceLocation(this.text.pressSound), 1.0f));
            } else {
                Minecraft.getMinecraft().getSoundHandler()
                        .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0f));
            }
            this.text.action.perform(this.text, this.parent);
        }
    }

}
