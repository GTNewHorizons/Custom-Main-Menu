package lumien.custommainmenu.gui;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.configuration.Alignment;
import lumien.custommainmenu.configuration.Config;
import lumien.custommainmenu.configuration.GuiConfig;
import lumien.custommainmenu.configuration.elements.Background;
import lumien.custommainmenu.configuration.elements.Button;
import lumien.custommainmenu.configuration.elements.Image;
import lumien.custommainmenu.configuration.elements.Panorama;
import lumien.custommainmenu.configuration.elements.SplashText;
import lumien.custommainmenu.configuration.elements.Text;
import lumien.custommainmenu.lib.actions.ActionOpenLink;
import lumien.custommainmenu.lib.textures.ITexture;
import lumien.custommainmenu.util.GlStateManager;
import lumien.custommainmenu.util.RenderUtil;

@SideOnly(value = Side.CLIENT)
public class GuiCustom extends GuiScreen implements GuiYesNoCallback {

    public static Config config;
    int buttonCounter;
    int panoramaTimer;
    ArrayList<GuiCustomLabel> textLabels;
    private ITexture[] titlePanoramaPaths;
    public Object beingChecked;
    public GuiConfig guiConfig;
    Random rand;
    protected DynamicTexture viewportTexture;
    protected ResourceLocation field_110351_G;
    protected String splashText;
    protected int field_92024_r;
    protected int field_92023_s;
    protected int field_92022_t;
    protected int field_92021_u;
    protected int field_92020_v;
    protected int field_92019_w;
    FontRenderer fontRenderer;
    private boolean loadedSplashText;

    public GuiCustom(GuiConfig guiConfig) {
        this.guiConfig = guiConfig;
        this.rand = new Random();
        this.loadedSplashText = false;
    }

    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        if (p_73869_2_ == 1) {
            this.mc.displayGuiScreen(null);
        }
    }

    public List<GuiButton> getButtonList() {
        return this.buttonList;
    }

    private void loadSplashTexts() {
        if (this.guiConfig.splashText != null) {
            String texts = this.guiConfig.splashText.texts.get();
            String[] seperateLines = texts.split("\n");
            this.splashText = seperateLines[this.rand.nextInt(seperateLines.length)];
        }
    }

    public void initGui() {
        if (!this.loadedSplashText && this.guiConfig.splashText != null) {
            if (this.guiConfig.splashText.synced) {
                this.splashText = CustomMainMenu.INSTANCE.config.getGUI("mainmenu").splashText;
            } else {
                this.loadSplashTexts();
            }
            this.loadedSplashText = true;
        }
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
        this.textLabels = new ArrayList();
        this.buttonCounter = 0;
        this.viewportTexture = new DynamicTexture(256, 256);
        this.field_110351_G = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(2) + 1 == 11 && calendar.get(5) == 9) {
            this.splashText = "Happy birthday, ez!";
        } else if (calendar.get(2) + 1 == 6 && calendar.get(5) == 1) {
            this.splashText = "Happy birthday, Notch!";
        } else if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
            this.splashText = "Merry X-mas!";
        } else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
            this.splashText = "Happy new year!";
        } else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }
        int idCounter = 6000;
        for (Button b : this.guiConfig.customButtons) {
            if (b.wrappedButtonID != -1) {
                this.buttonList
                        .add(this.alignButton(b, new GuiCustomWrappedButton(b.wrappedButtonID, b.wrappedButtonID, b)));
                continue;
            }
            this.buttonList.add(this.alignButton(b, new GuiCustomButton(idCounter, b)));
            ++idCounter;
        }
        for (Text t : this.guiConfig.customTexts) {
            this.textLabels
                    .add(new GuiCustomLabel(this, t, this.modX(t.posX, t.alignment), this.modY(t.posY, t.alignment)));
        }
    }

    private GuiCustomButton alignButton(Button configButton, GuiCustomButton guiButton) {
        if (configButton != null) {
            guiButton.xPosition = this.modX(configButton.posX, configButton.alignment);
            guiButton.yPosition = this.modY(configButton.posY, configButton.alignment);
        }
        return guiButton;
    }

    public void confirmClicked(boolean result, int id) {
        if (result) {
            String link = null;
            if (this.beingChecked instanceof Button) {
                Button button = (Button) this.beingChecked;
                if (button.action instanceof ActionOpenLink) {
                    link = ((ActionOpenLink) button.action).getLink();
                }
            } else if (this.beingChecked instanceof Text) {
                Text text = (Text) this.beingChecked;
                if (text.action instanceof ActionOpenLink) {
                    link = ((ActionOpenLink) text.action).getLink();
                }
            }
            if (link != null) {
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI(link));
                } catch (Throwable throwable) {
                    CustomMainMenu.INSTANCE.logger.error("Couldn't open link", throwable);
                }
            }
        }
        this.mc.displayGuiScreen(this);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            for (GuiCustomLabel label : this.textLabels) {
                label.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int w;
        GlStateManager.disableBlend();
        Panorama panorama = this.guiConfig.panorama;
        Tessellator worldrenderer = Tessellator.instance;
        if (panorama != null) {
            this.titlePanoramaPaths = panorama.locations;
            GlStateManager.disableAlpha();
            this.renderSkybox(mouseX, mouseY, partialTicks);
            GlStateManager.enableAlpha();
            if (panorama.gradient) {
                this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
                this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
            }
        } else {
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor3f(0.0f, 0.0f, 0.0f);
            GL11.glVertex3f(0.0f, 0.0f, 0.0f);
            GL11.glColor3f(0.0f, 0.0f, 0.0f);
            GL11.glVertex3f(0.0f, (float) this.height, 0.0f);
            GL11.glColor3f(0.0f, 0.0f, 0.0f);
            GL11.glVertex3f((float) this.width, (float) this.height, 0.0f);
            GL11.glColor3f(0.0f, 0.0f, 0.0f);
            GL11.glVertex3f((float) this.width, 0.0f, 0.0f);
            GL11.glEnd();
        }
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        Background background = this.guiConfig.background;
        if (background != null) {
            if (!background.ichBinEineSlideshow) {
                GlStateManager.enableBlend();
                background.image.bind();
                this.drawBackground(background.mode);
                GlStateManager.disableBlend();
            } else {
                background.slideShow.getCurrentResource1().bind();
                this.drawBackground(background.mode);
                if (background.slideShow.fading()) {
                    GlStateManager.enableBlend();
                    background.slideShow.getCurrentResource2().bind();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, background.slideShow.getAlphaFade(partialTicks));
                    this.drawBackground(background.mode);
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        for (Image i : this.guiConfig.customImages) {
            int posX = this.modX(i.posX, i.alignment);
            int posY = this.modY(i.posY, i.alignment);
            if (i.ichBinEineSlideshow) {
                i.slideShow.getCurrentResource1().bind();
                RenderUtil.drawCompleteImage(posX, posY, i.width, i.height);
                if (!i.slideShow.fading()) continue;
                GlStateManager.enableBlend();
                i.slideShow.getCurrentResource2().bind();
                GlStateManager.color(1.0f, 1.0f, 1.0f, i.slideShow.getAlphaFade(partialTicks));
                RenderUtil.drawCompleteImage(posX, posY, i.width, i.height);
                GlStateManager.disableBlend();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                continue;
            }
            if (i.hoverImage != null && mouseX >= posX
                    && mouseX <= posX + i.width
                    && mouseY >= posY
                    && mouseY <= posY + i.height) {
                i.hoverImage.bind();
            } else {
                i.image.bind();
            }
            RenderUtil.drawCompleteImage(posX, posY, i.width, i.height);
        }
        GlStateManager.disableBlend();
        worldrenderer.setColorOpaque_I(-1);
        SplashText splashText = this.guiConfig.splashText;
        if (splashText != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(
                    this.modX(splashText.posX, splashText.alignment),
                    this.modY(splashText.posY, splashText.alignment),
                    0.0f);
            GlStateManager.rotate(-20.0f, 0.0f, 0.0f, 1.0f);
            float f1 = 1.8f - MathHelper.abs(
                    MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L) / 1000.0f * (float) Math.PI * 2.0f)
                            * 0.1f);
            f1 = f1 * 100.0f / (float) (this.fontRenderer.getStringWidth(this.splashText) + 32);
            GlStateManager.scale(f1, f1, f1);
            this.drawCenteredString(this.fontRenderer, this.splashText, 0, -8, splashText.color);
            GlStateManager.popMatrix();
        }
        for (GuiCustomLabel label : this.textLabels) {
            label.drawLabel(this.mc, mouseX, mouseY);
        }
        for (w = 0; w < this.buttonList.size(); ++w) {
            GlStateManager.resetColor();
            ((GuiButton) this.buttonList.get(w)).drawButton(this.mc, mouseX, mouseY);
        }
        for (Object o : this.buttonList) {
            if (!(o instanceof GuiCustomButton)) continue;
            ((GuiCustomButton) o).drawTooltip(this.mc, mouseX, mouseY);
        }
        for (w = 0; w < this.labelList.size(); ++w) {
            ((GuiLabel) this.labelList.get(w)).func_146159_a(this.mc, mouseX, mouseY);
        }
    }

    private void drawBackground(Background.MODE mode) {
        int imageWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int imageHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        int drawWidth = 0;
        int drawHeight = 0;
        float factorWidth = (float) this.width / (float) imageWidth;
        float factorHeight = (float) this.height / (float) imageHeight;
        switch (mode) {
            case FILL: {
                if (factorWidth > factorHeight) {
                    drawWidth = (int) ((float) imageWidth * factorWidth);
                    drawHeight = (int) ((float) imageHeight * factorWidth);
                } else {
                    drawWidth = (int) ((float) imageWidth * factorHeight);
                    drawHeight = (int) ((float) imageHeight * factorHeight);
                }
                RenderUtil.drawPartialImage(0, 0, 0, 0, drawWidth, drawHeight, imageWidth, imageHeight);
                break;
            }
            case STRETCH: {
                RenderUtil.drawCompleteImage(0, 0, this.width, this.height);
                break;
            }
            case CENTER: {
                RenderUtil.drawCompleteImage(
                        (int) ((float) this.width / 2.0f - (float) imageWidth / 2.0f),
                        (int) ((float) this.height / 2.0f - (float) imageHeight / 2.0f),
                        imageWidth,
                        imageHeight);
                break;
            }
            case TILE: {
                int countX = (int) Math.ceil((float) this.width / (float) imageWidth);
                int countY = (int) Math.ceil((float) this.height / (float) imageHeight);
                for (int cX = 0; cX < countX; ++cX) {
                    for (int cY = 0; cY < countY; ++cY) {
                        RenderUtil.drawCompleteImage(cX * imageWidth, cY * imageHeight, imageWidth, imageHeight);
                    }
                }
                break;
            }
        }
    }

    protected void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_) {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        Tessellator worldrenderer = Tessellator.instance;
        worldrenderer.startDrawingQuads();
        float f1 = this.width > this.height ? 120.0f / (float) this.width : 120.0f / (float) this.height;
        float f2 = (float) this.height * f1 / 256.0f;
        float f3 = (float) this.width * f1 / 256.0f;
        worldrenderer.setColorRGBA_F(1.0f, 1.0f, 1.0f, 1.0f);
        int k = this.width;
        int l = this.height;
        worldrenderer.addVertexWithUV(0.0, l, this.zLevel, 0.5f - f2, 0.5f + f3);
        worldrenderer.addVertexWithUV(k, l, this.zLevel, 0.5f - f2, 0.5f - f3);
        worldrenderer.addVertexWithUV(k, 0.0, this.zLevel, 0.5f + f2, 0.5f - f3);
        worldrenderer.addVertexWithUV(0.0, 0.0, this.zLevel, 0.5f + f2, 0.5f + f3);
        worldrenderer.draw();
    }

    private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_) {
        Tessellator worldrenderer = Tessellator.instance;
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0f, 1.0f, 0.05f, 10.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int b0 = 8;
        for (int k = 0; k < b0 * b0; ++k) {
            GlStateManager.pushMatrix();
            float f1 = ((float) (k % b0) / (float) b0 - 0.5f) / 64.0f;
            float f2 = ((float) (k / b0) / (float) b0 - 0.5f) / 64.0f;
            float f3 = 0.0f;
            GlStateManager.translate(f1, f2, f3);
            float animationCounter = p_73970_3_;
            if (!this.guiConfig.panorama.animate) {
                animationCounter = 0.0f;
            }
            GlStateManager.rotate(
                    MathHelper.sin(((float) this.panoramaTimer + animationCounter) / 400.0f) * 25.0f + 20.0f,
                    1.0f,
                    0.0f,
                    0.0f);
            GlStateManager.rotate(-((float) this.panoramaTimer + animationCounter) * 0.1f, 0.0f, 1.0f, 0.0f);
            for (int l = 0; l < 6; ++l) {
                GlStateManager.pushMatrix();
                if (l == 1) {
                    GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
                }
                if (l == 2) {
                    GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                }
                if (l == 3) {
                    GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
                }
                if (l == 4) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                }
                if (l == 5) {
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
                }
                this.titlePanoramaPaths[l].bind();
                worldrenderer.startDrawingQuads();
                worldrenderer.setColorRGBA_I(16777215, 255 / (k + 1));
                float f4 = 0.0f;
                worldrenderer.addVertexWithUV(-1.0, -1.0, 1.0, 0.0f + f4, 0.0f + f4);
                worldrenderer.addVertexWithUV(1.0, -1.0, 1.0, 1.0f - f4, 0.0f + f4);
                worldrenderer.addVertexWithUV(1.0, 1.0, 1.0, 1.0f - f4, 1.0f - f4);
                worldrenderer.addVertexWithUV(-1.0, 1.0, 1.0, 0.0f + f4, 1.0f - f4);
                worldrenderer.draw();
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }
        worldrenderer.setTranslation(0.0, 0.0, 0.0);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    public void updateScreen() {
        for (Image i : this.guiConfig.customImages) {
            if (!i.ichBinEineSlideshow) continue;
            i.slideShow.update();
        }
        if (this.guiConfig.panorama != null) {
            if (this.guiConfig.panorama.synced) {
                GuiCustom mainMenu = CustomMainMenu.INSTANCE.config.getGUI("mainmenu");
                this.panoramaTimer = mainMenu.panoramaTimer;
                this.panoramaTimer = mainMenu.guiConfig.panorama.animate
                        ? (this.panoramaTimer += mainMenu.guiConfig.panorama.animationSpeed)
                        : mainMenu.guiConfig.panorama.position;
                mainMenu.panoramaTimer = this.panoramaTimer;
            } else {
                this.panoramaTimer = this.guiConfig.panorama.animate
                        ? (this.panoramaTimer += this.guiConfig.panorama.animationSpeed)
                        : this.guiConfig.panorama.position;
            }
        }
        if (this.guiConfig.background != null && this.guiConfig.background.ichBinEineSlideshow) {
            this.guiConfig.background.slideShow.update();
        }
        if (Keyboard.isKeyDown(29) && Keyboard.isKeyDown(19)) {
            CustomMainMenu.INSTANCE.reload();
            if (Keyboard.isKeyDown(42)) {
                this.mc.refreshResources();
            }
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    private void rotateAndBlurSkybox(float p_73968_1_) {
        this.mc.getTextureManager().bindTexture(this.field_110351_G);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator worldrenderer = Tessellator.instance;
        worldrenderer.startDrawingQuads();
        GlStateManager.disableAlpha();
        int b0 = 3;
        if (this.guiConfig.panorama.blur) {
            for (int i = 0; i < b0; ++i) {
                worldrenderer.setColorRGBA_F(1.0f, 1.0f, 1.0f, 1.0f / (float) (i + 1));
                int j = this.width;
                int k = this.height;
                float f1 = (float) (i - b0 / 2) / 256.0f;
                worldrenderer.addVertexWithUV(j, k, this.zLevel, 0.0f + f1, 1.0);
                worldrenderer.addVertexWithUV(j, 0.0, this.zLevel, 1.0f + f1, 1.0);
                worldrenderer.addVertexWithUV(0.0, 0.0, this.zLevel, 1.0f + f1, 0.0);
                worldrenderer.addVertexWithUV(0.0, k, this.zLevel, 0.0f + f1, 0.0);
            }
        }
        worldrenderer.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private int modX(int posX, Alignment alignment) {
        return (int) ((float) posX + (float) this.width * alignment.factorX);
    }

    private int modY(int posY, Alignment alignment) {
        return (int) ((float) posY + (float) this.height * alignment.factorY);
    }

    protected void actionPerformed(GuiButton button) {
        if (button instanceof GuiCustomWrappedButton && this.guiConfig.name.equals("mainmenu")) {
            GuiCustomWrappedButton wrapped = (GuiCustomWrappedButton) button;
            if (wrapped.wrappedButton != null) {
                GuiScreenEvent.ActionPerformedEvent.Pre event = new GuiScreenEvent.ActionPerformedEvent.Pre(
                        new GuiFakeMain(),
                        wrapped.wrappedButton,
                        new ArrayList());
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    return;
                }
                event.button.func_146113_a(this.mc.getSoundHandler());
                if (this.equals(this.mc.currentScreen)) {
                    MinecraftForge.EVENT_BUS.post(
                            new GuiScreenEvent.ActionPerformedEvent.Post(
                                    new GuiFakeMain(),
                                    wrapped.wrappedButton,
                                    new ArrayList()));
                }
            }
        } else if (button.id >= 6000 && button instanceof GuiCustomButton) {
            GuiCustomButton custom = (GuiCustomButton) button;
            if (custom.b.action != null) {
                custom.b.action.perform(custom.b, this);
            }
        }
    }
}
