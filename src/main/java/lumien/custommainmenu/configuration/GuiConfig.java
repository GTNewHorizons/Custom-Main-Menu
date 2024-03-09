package lumien.custommainmenu.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.configuration.elements.Background;
import lumien.custommainmenu.configuration.elements.Button;
import lumien.custommainmenu.configuration.elements.Image;
import lumien.custommainmenu.configuration.elements.Panorama;
import lumien.custommainmenu.configuration.elements.Slideshow;
import lumien.custommainmenu.configuration.elements.SplashText;
import lumien.custommainmenu.configuration.elements.Text;
import lumien.custommainmenu.gui.GuiCustom;
import lumien.custommainmenu.lib.actions.ActionConnectToServer;
import lumien.custommainmenu.lib.actions.ActionLoadWorld;
import lumien.custommainmenu.lib.actions.ActionOpenFolder;
import lumien.custommainmenu.lib.actions.ActionOpenGUI;
import lumien.custommainmenu.lib.actions.ActionOpenLink;
import lumien.custommainmenu.lib.actions.ActionQuit;
import lumien.custommainmenu.lib.actions.ActionRefresh;
import lumien.custommainmenu.lib.actions.IAction;
import lumien.custommainmenu.lib.texts.IText;
import lumien.custommainmenu.lib.texts.TextResourceLocation;
import lumien.custommainmenu.lib.texts.TextString;
import lumien.custommainmenu.lib.texts.TextURL;
import lumien.custommainmenu.lib.textures.ITexture;
import lumien.custommainmenu.lib.textures.TextureResourceLocation;
import lumien.custommainmenu.lib.textures.TextureURL;

public class GuiConfig {

    public String name;
    public int guiScale;
    public ArrayList<Button> customButtons;
    public ArrayList<Text> customTexts;
    public ArrayList<Image> customImages;
    public HashMap<String, Alignment> alignments;
    public SplashText splashText;
    public Panorama panorama;
    public Background background;

    public void load(String name, JsonObject jsonObject) {
        if (name.endsWith("_small")) {
            this.guiScale = 1;
            name = name.replace("_small", "");
        } else if (name.endsWith("_normal")) {
            this.guiScale = 2;
            name = name.replace("_normal", "");
        } else if (name.endsWith("_large")) {
            this.guiScale = 3;
            name = name.replace("_large", "");
        } else if (name.endsWith("_auto")) {
            this.guiScale = 0;
            name = name.replace("_auto", "");
        } else {
            this.guiScale = -1;
        }
        this.name = name;
        this.loadAlignments(jsonObject);
        this.customTexts = new ArrayList<>();
        this.customImages = new ArrayList<>();
        this.customButtons = new ArrayList<>();
        this.splashText = null;
        this.panorama = null;
        this.background = null;
        this.loadButtons(jsonObject);
        this.loadTexts(jsonObject);
        this.loadImages(jsonObject);
        this.loadOthers(jsonObject);
    }

    private void loadAlignments(JsonObject jsonObject) {
        this.alignments = new HashMap<>();
        this.alignments.put("bottom_left", new Alignment(0.0f, 1.0f));
        this.alignments.put("top_left", new Alignment(0.0f, 0.0f));
        this.alignments.put("top_right", new Alignment(1.0f, 0.0f));
        this.alignments.put("bottom_right", new Alignment(1.0f, 1.0f));
        this.alignments.put("center", new Alignment(0.5f, 0.5f));
        this.alignments.put("button", new Alignment(0.5f, 0.25f));
        this.alignments.put("top_center", new Alignment(0.5f, 0.0f));
        this.alignments.put("left_center", new Alignment(0.0f, 0.5f));
        this.alignments.put("bottom_center", new Alignment(0.5f, 1.0f));
        this.alignments.put("right_center", new Alignment(1.0f, 0.5f));
        if (jsonObject.has("alignments")) {
            JsonObject alignmentObject = (JsonObject) jsonObject.get("alignments");
            Set<Map.Entry<String, JsonElement>> buttons = alignmentObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : buttons) {
                String name = (String) entry.getKey();
                JsonObject object = (JsonObject) entry.getValue();
                Alignment a = new Alignment(
                        object.get("factorWidth").getAsFloat(),
                        object.get("factorHeight").getAsFloat());
                this.alignments.put(name, a);
            }
        }
    }

    private void loadOthers(JsonObject jsonObject) {
        JsonObject panoramaObject;
        JsonObject backgroundObject;
        JsonObject other = (JsonObject) jsonObject.get("other");
        JsonObject splashTextObject = (JsonObject) other.get("splash-text");
        if (splashTextObject != null) {
            this.splashText = splashTextObject.has("color") && splashTextObject.has("alignment")
                    ? new SplashText(
                            this,
                            splashTextObject.get("posX").getAsInt(),
                            splashTextObject.get("posY").getAsInt(),
                            splashTextObject.get("color").getAsInt(),
                            splashTextObject.get("alignment").getAsString())
                    : (splashTextObject.has("color")
                            ? new SplashText(
                                    this,
                                    splashTextObject.get("posX").getAsInt(),
                                    splashTextObject.get("posY").getAsInt(),
                                    splashTextObject.get("color").getAsInt(),
                                    "top_center")
                            : (splashTextObject.has("alignment")
                                    ? new SplashText(
                                            this,
                                            splashTextObject.get("posX").getAsInt(),
                                            splashTextObject.get("posY").getAsInt(),
                                            splashTextObject.get("alignment").getAsString())
                                    : new SplashText(
                                            this,
                                            splashTextObject.get("posX").getAsInt(),
                                            splashTextObject.get("posY").getAsInt(),
                                            "top_center")));
            if (splashTextObject.has("synced")) {
                this.splashText.synced = splashTextObject.get("synced").getAsBoolean();
            }
            if (splashTextObject.has("texts")) {
                this.splashText
                        .setSplashTexts(GuiConfig.getWantedText(this.getStringPlease(splashTextObject.get("texts"))));
            }
            if (splashTextObject.has("file")) {
                this.splashText
                        .setSplashTexts(new TextResourceLocation(this.getStringPlease(splashTextObject.get("file"))));
            }
        }
        if ((panoramaObject = (JsonObject) other.get("panorama")) != null) {
            this.panorama = new Panorama(
                    this,
                    this.getStringPlease(panoramaObject.get("images")),
                    panoramaObject.get("blur").getAsBoolean(),
                    panoramaObject.get("gradient").getAsBoolean());
            if (panoramaObject.has("animate")) {
                this.panorama.setAnimate(panoramaObject.get("animate").getAsBoolean());
            }
            if (panoramaObject.has("position")) {
                this.panorama.setPosition(panoramaObject.get("position").getAsInt());
            }
            if (panoramaObject.has("animationSpeed")) {
                this.panorama.setAnimationSpeed(panoramaObject.get("animationSpeed").getAsInt());
            }
            if (panoramaObject.has("synced")) {
                this.panorama.synced = panoramaObject.get("synced").getAsBoolean();
            }
        }
        if ((backgroundObject = (JsonObject) other.get("background")) != null) {
            this.background = new Background(
                    this,
                    GuiConfig.getWantedTexture(this.getStringPlease(backgroundObject.get("image"))));
            if (backgroundObject.has("mode")) {
                this.background.setMode(backgroundObject.get("mode").getAsString());
            }
            if (backgroundObject.has("slideshow")) {
                JsonObject slideShowObject = backgroundObject.get("slideshow").getAsJsonObject();
                this.background.ichBinEineSlideshow = true;
                if (slideShowObject.has("synced") && slideShowObject.get("synced").getAsBoolean()) {
                    GuiCustom mainMenu = CustomMainMenu.INSTANCE.config.getGUI("mainmenu");
                    this.background.slideShow = mainMenu.guiConfig.background.slideShow;
                } else {
                    JsonArray imageArray = slideShowObject.get("images").getAsJsonArray();
                    String[] images = new String[imageArray.size()];
                    for (int i = 0; i < images.length; ++i) {
                        images[i] = imageArray.get(i).getAsString();
                    }
                    Slideshow slideShow = new Slideshow(this, images);
                    if (slideShowObject.has("displayDuration")) {
                        slideShow.displayDuration = slideShowObject.get("displayDuration").getAsInt();
                    }
                    if (slideShowObject.has("fadeDuration")) {
                        slideShow.fadeDuration = slideShowObject.get("fadeDuration").getAsInt();
                    }
                    if (slideShowObject.has("shuffle") && slideShowObject.get("shuffle").getAsBoolean()) {
                        slideShow.shuffle();
                    }
                    this.background.slideShow = slideShow;
                }
            }
        }
    }

    private void loadImages(JsonObject jsonObject) {
        JsonObject textElements = jsonObject.get("images").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> images = textElements.entrySet();
        for (Map.Entry<String, JsonElement> entry : images) {
            String name = (String) entry.getKey();
            JsonElement element = (JsonElement) entry.getValue();
            this.customImages.add(this.getImage((JsonObject) element));
        }
    }

    private void loadTexts(JsonObject jsonObject) {
        JsonObject textElements = jsonObject.get("texts").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> texts = textElements.entrySet();
        for (Map.Entry<String, JsonElement> entry : texts) {
            String name = (String) entry.getKey();
            JsonElement element = (JsonElement) entry.getValue();
            this.customTexts.add(this.getText(name, (JsonObject) element));
        }
    }

    private void loadButtons(JsonObject jsonObject) {
        JsonObject buttonElements = jsonObject.get("buttons").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> buttons = buttonElements.entrySet();
        for (Map.Entry<String, JsonElement> entry : buttons) {
            String name = (String) entry.getKey();
            JsonObject object = (JsonObject) entry.getValue();
            Button b = this.getButton(object);
            b.name = (String) entry.getKey();
            this.customButtons.add(b);
        }
    }

    private Button getButton(JsonObject jsonObject) {
        Button b = new Button(
                this,
                GuiConfig.getWantedText(this.getStringPlease(jsonObject.get("text"))),
                jsonObject.get("posX").getAsInt(),
                jsonObject.get("posY").getAsInt(),
                jsonObject.get("width").getAsInt(),
                jsonObject.get("height").getAsInt());
        if (jsonObject.has("alignment")) {
            b.setStringAlignment(jsonObject.get("alignment").getAsString());
        }
        if (jsonObject.has("texture")) {
            b.setTexture(GuiConfig.getWantedTexture(this.getStringPlease(jsonObject.get("texture"))));
        }
        if (jsonObject.has("normalTextColor")) {
            b.normalTextColor = jsonObject.get("normalTextColor").getAsInt();
        }
        if (jsonObject.has("hoverTextColor")) {
            b.hoverTextColor = jsonObject.get("hoverTextColor").getAsInt();
        }
        if (jsonObject.has("shadow")) {
            b.shadow = jsonObject.get("shadow").getAsBoolean();
        }
        if (jsonObject.has("imageWidth")) {
            b.imageWidth = jsonObject.get("imageWidth").getAsInt();
        }
        if (jsonObject.has("imageHeight")) {
            b.imageHeight = jsonObject.get("imageHeight").getAsInt();
        }
        if (jsonObject.has("wrappedButton")) {
            b.setWrappedButton(jsonObject.get("wrappedButton").getAsInt());
        }
        if (jsonObject.has("action")) {
            JsonObject actionObject = (JsonObject) jsonObject.get("action");
            b.action = this.getWantedAction(actionObject);
        }
        if (jsonObject.has("tooltip")) {
            b.tooltip = GuiConfig.getWantedText(jsonObject.get("tooltip").getAsString());
        }
        if (jsonObject.has("hoverText")) {
            b.hoverText = GuiConfig.getWantedText(this.getStringPlease(jsonObject.get("hoverText")));
        }
        if (jsonObject.has("pressSound")) {
            b.pressSound = this.getStringPlease(jsonObject.get("pressSound"));
        }
        if (jsonObject.has("hoverSound")) {
            b.hoverSound = this.getStringPlease(jsonObject.get("hoverSound"));
        }
        return b;
    }

    private Image getImage(JsonObject jsonObject) {
        Image image = new Image(
                this,
                jsonObject.get("posX").getAsInt(),
                jsonObject.get("posY").getAsInt(),
                jsonObject.get("width").getAsInt(),
                jsonObject.get("height").getAsInt(),
                this.getAlignment("top_left"));
        if (jsonObject.has("alignment")) {
            image.alignment = this.getAlignment(jsonObject.get("alignment").getAsString());
        }
        if (jsonObject.has("hoverImage")) {
            image.hoverImage = GuiConfig.getWantedTexture(this.getStringPlease(jsonObject.get("hoverImage")));
        }
        if (jsonObject.has("image")) {
            image.image = GuiConfig.getWantedTexture(this.getStringPlease(jsonObject.get("image")));
        } else if (jsonObject.has("slideshow")) {
            JsonObject slideShowObject = jsonObject.get("slideshow").getAsJsonObject();
            image.ichBinEineSlideshow = true;
            JsonArray imageArray = slideShowObject.get("images").getAsJsonArray();
            String[] images = new String[imageArray.size()];
            for (int i = 0; i < images.length; ++i) {
                images[i] = imageArray.get(i).getAsString();
            }
            Slideshow slideShow = new Slideshow(this, images);
            if (slideShowObject.has("displayDuration")) {
                slideShow.displayDuration = slideShowObject.get("displayDuration").getAsInt();
            }
            if (slideShowObject.has("fadeDuration")) {
                slideShow.fadeDuration = slideShowObject.get("fadeDuration").getAsInt();
            }
            if (slideShowObject.has("shuffle") && slideShowObject.get("shuffle").getAsBoolean()) {
                slideShow.shuffle();
            }
            image.slideShow = slideShow;
        } else {
            throw new RuntimeException("Images either need an image or slideshow property");
        }
        return image;
    }

    private Text getText(String name, JsonObject jsonObject) {
        Text text = new Text(
                this,
                name,
                GuiConfig.getWantedText(this.getStringPlease(jsonObject.get("text"))),
                jsonObject.get("posX").getAsInt(),
                jsonObject.get("posY").getAsInt());
        if (jsonObject.has("alignment")) {
            text.setAlignment(jsonObject.get("alignment").getAsString());
        }
        if (jsonObject.has("color")) {
            text.setColor(jsonObject.get("color").getAsInt());
        }
        if (jsonObject.has("hoverColor")) {
            text.setHoverColor(jsonObject.get("hoverColor").getAsInt());
        }
        if (jsonObject.has("action")) {
            JsonObject actionObject = (JsonObject) jsonObject.get("action");
            text.action = this.getWantedAction(actionObject);
        }
        if (jsonObject.has("hoverText")) {
            text.hoverText = GuiConfig.getWantedText(this.getStringPlease(jsonObject.get("hoverText")));
        }
        if (jsonObject.has("fontSize")) {
            text.fontSize = jsonObject.get("fontSize").getAsFloat();
        }
        if (jsonObject.has("pressSound")) {
            text.pressSound = this.getStringPlease(jsonObject.get("pressSound"));
        }
        if (jsonObject.has("hoverSound")) {
            text.hoverSound = this.getStringPlease(jsonObject.get("hoverSound"));
        }
        return text;
    }

    private String getStringPlease(JsonElement jsonElement) {
        Random rng = new Random();
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        }
        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            return array.get(rng.nextInt(array.size())).getAsString();
        }
        CustomMainMenu.INSTANCE.logger.log(Level.ERROR, "Error getting random value out of " + jsonElement);
        return "ERROR";
    }

    public static ITexture getWantedTexture(String textureString) {
        if (textureString.startsWith("web:")) {
            String url = textureString.substring(4);
            return new TextureURL(url);
        }
        return new TextureResourceLocation(textureString);
    }

    private IAction getWantedAction(JsonObject actionObject) {
        String actionType = actionObject.get("type").getAsString();
        if (actionType.equals("openLink")) {
            return new ActionOpenLink(actionObject.get("link").getAsString());
        }
        if (actionType.equalsIgnoreCase("loadWorld")) {
            return new ActionLoadWorld(
                    actionObject.get("dirName").getAsString(),
                    actionObject.get("saveName").getAsString());
        }
        if (actionType.equalsIgnoreCase("connectToServer")) {
            return new ActionConnectToServer(actionObject.get("ip").getAsString());
        }
        if (actionType.equalsIgnoreCase("openGui")) {
            return new ActionOpenGUI(actionObject.get("gui").getAsString());
        }
        if (actionType.equalsIgnoreCase("quit")) {
            return new ActionQuit();
        }
        if (actionType.equalsIgnoreCase("refresh")) {
            return new ActionRefresh();
        }
        if (actionType.equalsIgnoreCase("openFolder")) {
            return new ActionOpenFolder(actionObject.get("folderName").getAsString());
        }
        return null;
    }

    public Alignment getAlignment(String name) {
        if (this.alignments.containsKey(name)) {
            return this.alignments.get(name);
        }
        return this.alignments.get("top_left");
    }

    public static IText getWantedText(String textString) {
        if (textString.startsWith("web:")) {
            String url = textString.substring(4);
            return new TextURL(url);
        }
        if (textString.startsWith("file:")) {
            String resource = textString.substring(5);
            return new TextResourceLocation(resource);
        }
        return new TextString(textString);
    }
}
