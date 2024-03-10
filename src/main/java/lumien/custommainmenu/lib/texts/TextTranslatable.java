package lumien.custommainmenu.lib.texts;

import net.minecraft.util.StatCollector;

public class TextTranslatable implements IText {

    private final String unlocalizedName;

    public TextTranslatable(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
    }

    @Override
    public String get() {
        return StatCollector.translateToLocal(unlocalizedName);
    }
}
