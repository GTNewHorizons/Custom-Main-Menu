package lumien.custommainmenu.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;
import lumien.custommainmenu.gui.GuiCustomButton;
import lumien.custommainmenu.gui.GuiCustomWrappedButton;
import lumien.custommainmenu.gui.GuiFakeMain;

public class CMMEventHandler {

    public long displayMs = -1L;
    Field guiField;
    GuiCustom actualGui;

    public CMMEventHandler() {
        try {
            this.guiField = GuiScreenEvent.class.getDeclaredField("gui");
            this.guiField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void openGui(GuiOpenEvent event) {
        if (event.gui instanceof GuiMainMenu) {
            GuiCustom customMainMenu = CustomMainMenu.INSTANCE.config.getGUI("mainmenu");
            if (customMainMenu != null) {
                event.gui = customMainMenu;
            }
        } else if (event.gui instanceof GuiCustom) {
            GuiCustom custom = (GuiCustom) event.gui;
            GuiCustom target = CustomMainMenu.INSTANCE.config.getGUI(custom.guiConfig.name);
            if (target != custom) {
                event.gui = target;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void initGuiPostEarly(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiCustom) {
            GuiCustom custom = (GuiCustom) event.gui;
            if (custom.guiConfig.name.equals("mainmenu")) {
                event.buttonList = new ArrayList();
                this.actualGui = custom;
                try {
                    this.guiField.set(event, new GuiFakeMain());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void initGuiPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiFakeMain) {
            GuiFakeMain fake = (GuiFakeMain) event.gui;
            HashMap<Integer, GuiButton> removedButtons = new HashMap<Integer, GuiButton>();
            Iterator iterator = event.buttonList.iterator();
            while (iterator.hasNext()) {
                Object o = iterator.next();
                GuiButton b = (GuiButton) o;
                if (b instanceof GuiCustomButton) continue;
                iterator.remove();
                removedButtons.put(b.id, b);
                if (b.id == 666 && Loader.isModLoaded("OpenEye")) {
                    CustomMainMenu.INSTANCE.logger.log(
                            Level.DEBUG,
                            "Found OpenEye button, use a wrapped button to config this. (" + b.id + ")");
                    continue;
                }
                if (b.id == 404 && Loader.isModLoaded("VersionChecker")) {
                    CustomMainMenu.INSTANCE.logger.log(
                            Level.DEBUG,
                            "Found VersionChecker button, use a wrapped button to config this. (" + b.id + ")");
                    continue;
                }
                CustomMainMenu.INSTANCE.logger.log(
                        Level.DEBUG,
                        "Found unsupported button, use a wrapped button to config this. (" + b.id + ")");
            }
            for (GuiButton o : this.actualGui.getButtonList()) {
                if (!(o instanceof GuiCustomWrappedButton)) continue;
                GuiCustomWrappedButton b = (GuiCustomWrappedButton) o;
                CustomMainMenu.INSTANCE.logger.log(
                        Level.DEBUG,
                        "Initiating Wrapped Button " + b.wrappedButtonID
                                + " with "
                                + removedButtons.get(b.wrappedButtonID));
                b.init(removedButtons.get(b.wrappedButtonID));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (this.displayMs != -1L) {
            if (System.currentTimeMillis() - this.displayMs < 5000L) {
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(
                        "Error loading config file, see console for more information",
                        0,
                        80,
                        16711680);
            } else {
                this.displayMs = -1L;
            }
        }
    }
}
