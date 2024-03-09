package lumien.custommainmenu.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;

public class ConfigurationLoader {

    final Config config;

    public ConfigurationLoader(Config config) {
        this.config = config;
    }

    public void load() throws Exception {
        JsonReader reader;
        File[] jsonFiles;
        JsonObject jsonObject;
        File mainmenuConfig;
        String name;
        JsonElement jsonElement;
        GuiConfig guiConfig;
        JsonParser jsonParser = new JsonParser();
        File configFolder = new File(CustomMainMenu.INSTANCE.configFolder, "CustomMainMenu");
        if (!configFolder.exists()) {
            configFolder.mkdir();
        }
        if (!(mainmenuConfig = new File(configFolder, "mainmenu.json")).exists()) {
            InputStream input = null;
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mainmenuConfig);
                input = this.getClass().getResourceAsStream("/assets/custommainmenu/mainmenu_default.json");
                ByteStreams.copy(input, output);
            } catch (IOException e) {
                e.printStackTrace();
                IOUtils.closeQuietly(output);
                IOUtils.closeQuietly(input);
            }
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(input);
        }
        for (File guiFile : jsonFiles = configFolder.listFiles()) {
            if (!guiFile.getName().equals("mainmenu.json")) continue;
            guiConfig = new GuiConfig();
            name = guiFile.getName().replace(".json", "");
            reader = null;
            try {
                reader = new JsonReader(new FileReader(guiFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                jsonElement = jsonParser.parse(reader);
                jsonObject = jsonElement.getAsJsonObject();
                guiConfig.load(name, jsonObject);
            } catch (Exception e) {
                try {
                    reader.close();
                    throw e;
                } catch (IOException io) {
                    io.printStackTrace();
                }
                throw e;
            }
            try {
                reader.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
            this.config.addGui(guiConfig.name, new GuiCustom(guiConfig));
        }
        for (File guiFile : jsonFiles) {
            if (!guiFile.getName().equals("mainmenu.json") && guiFile.getName().endsWith(".json")) {
                guiConfig = new GuiConfig();
                name = guiFile.getName().replace(".json", "");
                reader = null;
                try {
                    reader = new JsonReader(new FileReader(guiFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    jsonElement = jsonParser.parse(reader);
                    jsonObject = jsonElement.getAsJsonObject();
                    guiConfig.load(name, jsonObject);
                } catch (Exception e) {
                    try {
                        reader.close();
                        throw e;
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    throw e;
                }
                try {
                    reader.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }
                this.config.addGui(guiConfig.name, new GuiCustom(guiConfig));
            }
        }
    }
}
