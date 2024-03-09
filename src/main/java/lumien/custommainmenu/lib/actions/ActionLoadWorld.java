package lumien.custommainmenu.lib.actions;

import java.io.File;
import java.nio.file.Files;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.GuiOldSaveLoadConfirm;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.StartupQuery;
import lumien.custommainmenu.gui.GuiCustom;

public class ActionLoadWorld implements IAction {

    final String dirName;
    final String saveName;

    public ActionLoadWorld(String dirName, String saveName) {
        this.dirName = dirName;
        this.saveName = saveName;
    }

    @Override
    public void perform(Object source, GuiCustom menu) {
        NBTTagCompound leveldat;
        File dir = new File(FMLClientHandler.instance().getSavesDir(), this.dirName);
        try {
            leveldat = CompressedStreamTools.readCompressed(Files.newInputStream(new File(dir, "level.dat").toPath()));
        } catch (Exception e) {
            try {
                leveldat = CompressedStreamTools
                        .readCompressed(Files.newInputStream(new File(dir, "level.dat_old").toPath()));
            } catch (Exception e1) {
                FMLLog.warning(
                        "There appears to be a problem loading the save %s, both level files are unreadable.",
                        this.dirName);
                return;
            }
        }
        NBTTagCompound fmlData = leveldat.getCompoundTag("FML");
        if (fmlData.hasKey("ModItemData")) {
            FMLClientHandler.instance().showGuiScreen(new GuiOldSaveLoadConfirm(this.dirName, this.saveName, menu));
        } else {
            try {
                Minecraft.getMinecraft().launchIntegratedServer(this.dirName, this.saveName, null);
            } catch (StartupQuery.AbortedException e) {
                // empty catch block
            }
        }
    }
}
