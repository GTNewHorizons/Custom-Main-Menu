package lumien.custommainmenu.lib.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiSnooper;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.client.gui.stream.GuiStreamOptions;
import net.minecraft.client.gui.stream.GuiStreamUnavailable;
import net.minecraft.client.stream.IStream;

import cpw.mods.fml.client.GuiModList;
import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;

public class ActionOpenGUI implements IAction {

    final String guiName;

    public ActionOpenGUI(String guiName) {
        this.guiName = guiName;
    }

    @Override
    public void perform(Object source, GuiCustom menu) {
        GuiScreen gui = null;
        if (this.guiName.startsWith("custom.")) {
            String customName = this.guiName.substring(7);
            gui = CustomMainMenu.INSTANCE.config.getGUI(customName);
        } else if (this.guiName.equalsIgnoreCase("mods")) {
            gui = new GuiModList(menu);
        } else if (this.guiName.equalsIgnoreCase("singleplayer")) {
            gui = new GuiSelectWorld(menu);
        } else if (this.guiName.equalsIgnoreCase("singleplayer.createworld")) {
            gui = new GuiCreateWorld(menu);
        } else if (this.guiName.equalsIgnoreCase("multiplayer")) {
            gui = new GuiMultiplayer(menu);
        } else if (this.guiName.equalsIgnoreCase("options")) {
            gui = new GuiOptions(menu, menu.mc.gameSettings);
        } else if (this.guiName.equalsIgnoreCase("languages")) {
            gui = new GuiLanguage(menu, menu.mc.gameSettings, menu.mc.getLanguageManager());
        } else if (this.guiName.equalsIgnoreCase("options.ressourcepacks")) {
            gui = new GuiScreenResourcePacks(menu);
        } else if (this.guiName.equalsIgnoreCase("options.snooper")) {
            gui = new GuiSnooper(menu, menu.mc.gameSettings);
        } else if (this.guiName.equalsIgnoreCase("options.sounds")) {
            gui = new GuiScreenOptionsSounds(menu, menu.mc.gameSettings);
        } else if (this.guiName.equalsIgnoreCase("options.broadcast")) {
            menu.mc.gameSettings.saveOptions();
            IStream istream = menu.mc.func_152346_Z();
            if (istream.func_152936_l() && istream.func_152928_D()) {
                gui = new GuiStreamOptions(menu, menu.mc.gameSettings);
            } else {
                GuiStreamUnavailable.func_152321_a(menu);
            }
        } else if (this.guiName.equalsIgnoreCase("options.video")) {
            gui = new GuiVideoSettings(menu, menu.mc.gameSettings);
        } else if (this.guiName.equalsIgnoreCase("options.controls")) {
            gui = new GuiControls(menu, menu.mc.gameSettings);
        } else if (this.guiName.equalsIgnoreCase("options.multiplayer")) {
            gui = new ScreenChatOptions(menu, menu.mc.gameSettings);
        } else if (this.guiName.equalsIgnoreCase("mainmenu")) {
            gui = new GuiMainMenu();
        }
        if (gui != null) {
            Minecraft.getMinecraft().displayGuiScreen(gui);
        }
    }
}
