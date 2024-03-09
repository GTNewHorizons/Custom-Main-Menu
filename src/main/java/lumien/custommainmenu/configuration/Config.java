package lumien.custommainmenu.configuration;

import java.util.HashMap;

import lumien.custommainmenu.gui.GuiCustom;

public class Config {

    final HashMap<String, GuiEntry> guis = new HashMap<>();

    public void addGui(String name, GuiCustom gc) {
        int scale;
        GuiEntry entry = this.guis.computeIfAbsent(name, k -> new GuiEntry());
        if ((scale = gc.guiConfig.guiScale) == -1) {
            entry.standard = gc;
        } else if (scale == 0) {
            entry.auto = gc;
        } else if (scale == 1) {
            entry.small = gc;
        } else if (scale == 2) {
            entry.normal = gc;
        } else if (scale == 3) {
            entry.large = gc;
        }
    }

    public GuiCustom getGUI(String name) {
        return this.guis.get(name).getCurrentGUI();
    }
}
