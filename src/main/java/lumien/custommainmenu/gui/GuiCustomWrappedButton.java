package lumien.custommainmenu.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import lumien.custommainmenu.configuration.elements.Button;

public class GuiCustomWrappedButton extends GuiCustomButton {

    GuiButton wrappedButton;
    public final int wrappedButtonID;

    public GuiCustomWrappedButton(int buttonId, int wrappedButtonID, Button b) {
        super(buttonId, b);
        this.wrappedButtonID = wrappedButtonID;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.wrappedButton != null) {
            this.enabled = this.wrappedButton.visible && this.wrappedButton.enabled;
            this.visible = this.enabled;
        } else {
            this.enabled = false;
            this.visible = false;
        }
        super.drawButton(mc, mouseX, mouseY);
    }

    public void init(GuiButton wrappedButton) {
        this.wrappedButton = wrappedButton;
        if (wrappedButton == null) {
            this.enabled = false;
            this.visible = false;
        }
    }

    public GuiButton getWrappedButton() {
        return this.wrappedButton;
    }
}
