package lumien.custommainmenu.lib.actions;

import cpw.mods.fml.common.event.FMLInterModComms;
import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;

/**
 * CMM action that sends an IMC runtime message to a target mod when performed. Config: {@code "action": {"type":
 * "sendIMC", "modid": "targetmodid", "message": "someKey"}}
 */
public class ActionSendIMC implements IAction {

    private final String targetModId;
    private final String message;

    public ActionSendIMC(String targetModId, String message) {
        this.targetModId = targetModId;
        this.message = message;
    }

    @Override
    public void perform(Object source, GuiCustom menu) {
        FMLInterModComms.sendRuntimeMessage(CustomMainMenu.INSTANCE, targetModId, message, "");
    }
}
