package lumien.custommainmenu.util;

import java.util.ArrayList;

import lumien.custommainmenu.lib.StringReplacer;

public class LogicUtil {

    public static ArrayList<String> getTooltip(String tooltipString) {
        String[] split;
        ArrayList<String> tooltip = new ArrayList<>();
        for (String s : split = tooltipString.split("\n")) {
            tooltip.add(StringReplacer.replacePlaceholders(s));
        }
        return tooltip;
    }
}
