package lumien.custommainmenu.lib;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeVersion;

import cpw.mods.fml.common.Loader;

public class StringReplacer {

    static final String forgeVersion = ForgeVersion.getVersion();
    static final DateFormat dateFormat = DateFormat.getDateInstance(2, Locale.getDefault());
    static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    static Field mcpversionField;
    public static String mcpversion;
    public static final String[] dynamicPlaceholders = { "#date#", "#time#" };

    public static String replacePlaceholders(String source) {
        int tModCount = Loader.instance().getModList().size();
        int aModCount = Loader.instance().getActiveModList().size();
        return source.replace("#mcversion#", "1.7.10").replace("#fmlversion#", Loader.instance().getFMLVersionString())
                .replace("#mcpversion#", mcpversion).replace("#modsloaded#", Integer.toString(tModCount))
                .replace("#modsactive#", Integer.toString(aModCount)).replace("#forgeversion#", forgeVersion)
                .replace("#username#", Minecraft.getMinecraft().getSession().getUsername());
    }

    public static String replaceDynamicPlaceholders(String source) {
        Date currentDate = new Date();
        String time = timeFormat.format(currentDate.getTime());
        String date = dateFormat.format(currentDate);
        return source.replace("#date#", date).replace("#time#", time);
    }

    static {
        try {
            mcpversionField = Loader.class.getDeclaredField("mcpversion");
            mcpversionField.setAccessible(true);
            mcpversion = (String) mcpversionField.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
