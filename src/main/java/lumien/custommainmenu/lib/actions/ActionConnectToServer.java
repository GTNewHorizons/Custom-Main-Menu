package lumien.custommainmenu.lib.actions;

import net.minecraft.client.multiplayer.ServerData;

import cpw.mods.fml.client.FMLClientHandler;
import lumien.custommainmenu.gui.GuiCustom;

public class ActionConnectToServer implements IAction {

    final String ip;
    String serverName;

    public ActionConnectToServer(String ip) {
        this.ip = ip;
    }

    @Override
    public void perform(Object source, GuiCustom menu) {
        ServerData serverData = new ServerData(null, this.ip);
        FMLClientHandler.instance().setupServerList();
        FMLClientHandler.instance().connectToServer(menu, serverData);
    }
}
