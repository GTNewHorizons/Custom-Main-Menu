package lumien.custommainmenu;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lumien.custommainmenu.configuration.Config;
import lumien.custommainmenu.configuration.ConfigurationLoader;
import lumien.custommainmenu.handler.CMMEventHandler;

@Mod(modid = CustomMainMenu.MOD_ID, name = CustomMainMenu.MOD_NAME, version = Tags.VERSION)
public class CustomMainMenu {

    public static final String MOD_ID = "custommainmenu";
    public static final String MOD_NAME = "Custom Main Menu";

    @Mod.Instance(value = CustomMainMenu.MOD_ID)
    public static CustomMainMenu INSTANCE;

    public static CMMEventHandler EVENT_HANDLER;
    private ConfigurationLoader configLoader;
    public Config config;
    public Logger logger;
    public File configFolder;
    static ResourceLocation transparentTexture;

    public static void bindTransparent() {
        Minecraft.getMinecraft().renderEngine.bindTexture(transparentTexture);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.configFolder = event.getModConfigurationDirectory();
        this.config = new Config();
        transparentTexture = new ResourceLocation("custommainmenu:textures/gui/transparent.png");
        EVENT_HANDLER = new CMMEventHandler();
        MinecraftForge.EVENT_BUS.register(EVENT_HANDLER);
        FMLCommonHandler.instance().bus().register(EVENT_HANDLER);
        this.logger = event.getModLog();
        this.configLoader = new ConfigurationLoader(this.config);
        try {
            this.configLoader.load();
        } catch (Exception e) {
            this.logger.log(Level.ERROR, "Error while loading config file. Will have to crash here :(.");
            throw new RuntimeException(e);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("curseProjectName", "226406-custom-main-menu");
        compound.setString("curseFilenameParser", "CustomMainMenu-MC1.8-[].jar");
        compound.setString("modDisplayName", MOD_NAME);
        FMLInterModComms.sendRuntimeMessage(MOD_ID, "VersionChecker", "addCurseCheck", compound);
    }

    public void reload() {
        Config backup = this.config;
        this.config = new Config();
        this.configLoader = new ConfigurationLoader(this.config);
        try {
            this.configLoader.load();
            CustomMainMenu.EVENT_HANDLER.displayMs = -1L;
        } catch (Exception e) {
            e.printStackTrace();
            CustomMainMenu.EVENT_HANDLER.displayMs = System.currentTimeMillis();
            this.logger.log(Level.ERROR, "Error while loading new config file, trying to keep the old one loaded.");
            this.config = backup;
        }
    }
}
