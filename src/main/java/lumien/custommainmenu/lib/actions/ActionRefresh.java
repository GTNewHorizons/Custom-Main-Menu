package lumien.custommainmenu.lib.actions;

import net.minecraft.client.gui.GuiMainMenu;

import org.lwjgl.input.Keyboard;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;

public class ActionRefresh implements IAction {

    @Override
    public void perform(Object source, GuiCustom menu) {
        CustomMainMenu.INSTANCE.reload();
        if (Keyboard.isKeyDown(42)) {
            menu.mc.refreshResources();
        }
        menu.mc.displayGuiScreen(new GuiMainMenu());
    }
}
