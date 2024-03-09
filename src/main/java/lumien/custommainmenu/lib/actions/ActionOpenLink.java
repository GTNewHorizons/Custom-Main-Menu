package lumien.custommainmenu.lib.actions;

import net.minecraft.client.Minecraft;

import lumien.custommainmenu.gui.GuiCustom;
import lumien.custommainmenu.gui.GuiCustomConfirmOpenLink;

public class ActionOpenLink implements IAction {

    final String link;

    public ActionOpenLink(String link) {
        this.link = link;
    }

    @Override
    public void perform(Object source, GuiCustom menu) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiCustomConfirmOpenLink(menu, this.link, -1, false));
        menu.beingChecked = source;
    }

    public String getLink() {
        return this.link;
    }
}
