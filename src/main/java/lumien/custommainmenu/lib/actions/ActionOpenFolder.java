package lumien.custommainmenu.lib.actions;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;

import lumien.custommainmenu.gui.GuiCustom;

public class ActionOpenFolder implements IAction {

    final String folderName;

    public ActionOpenFolder(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public void perform(Object source, GuiCustom parent) {
        File toOpen = new File(Minecraft.getMinecraft().mcDataDir, this.folderName);
        boolean isInMinecraftFolder = false;
        try {
            File parentFile = toOpen.getCanonicalFile();
            while ((parentFile = parentFile.getParentFile()) != null) {
                if (!parentFile.getCanonicalPath().equals(Minecraft.getMinecraft().mcDataDir.getCanonicalPath()))
                    continue;
                isInMinecraftFolder = true;
            }
            if (isInMinecraftFolder && toOpen.isDirectory() && Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(toOpen);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // empty catch block
        }
    }
}
