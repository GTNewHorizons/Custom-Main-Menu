package lumien.custommainmenu.lib.actions;

import net.minecraftforge.common.MinecraftForge;

import lumien.custommainmenu.events.ActionIMCEvent;
import lumien.custommainmenu.gui.GuiCustom;

/**
 * CMM action that fires a {@link ActionIMCEvent} on the Forge event bus when performed. Config: {@code "action":
 * {"type": "sendIMC", "modid": "targetmodid", "message": "someKey"}}
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
        MinecraftForge.EVENT_BUS.post(new ActionIMCEvent(targetModId, message));
    }
}
