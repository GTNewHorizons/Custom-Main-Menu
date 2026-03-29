package lumien.custommainmenu.events;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * Fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS} when a CMM button with action type
 * {@code "sendIMC"} is clicked.
 */
public class ActionIMCEvent extends Event {

    public final String modId;
    public final String message;

    public ActionIMCEvent(String modId, String message) {
        this.modId = modId;
        this.message = message;
    }
}
