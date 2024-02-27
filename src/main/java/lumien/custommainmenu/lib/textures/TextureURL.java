package lumien.custommainmenu.lib.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.TextureUtil;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.handler.LoadTextureURL;
import lumien.custommainmenu.util.GlStateManager;

public class TextureURL implements ITexture {

    URL url;
    int textureID = -1;
    private BufferedImage bi;

    public TextureURL(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            CustomMainMenu.INSTANCE.logger.log(Level.ERROR, "Invalid URL: " + url);
            e.printStackTrace();
        }
        new LoadTextureURL(this).start();
    }

    public void load() {
        InputStream inputStream = null;
        try {
            inputStream = this.url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage bi = null;
        if (inputStream != null) {
            try {
                bi = TextureURL.readBufferedImage(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            IOUtils.closeQuietly(inputStream);
        }
        if (bi != null) {
            this.textureID = TextureUtil.uploadTextureImageAllocate(GL11.glGenTextures(), bi, false, false);
        }
    }

    @Override
    public void bind() {
        if (this.textureID != -1) {
            GlStateManager.bindTexture(this.textureID);
        } else {
            if (this.bi != null) {
                this.setTextureID(TextureUtil.uploadTextureImageAllocate(GL11.glGenTextures(), this.bi, false, false));
                this.bind();
                return;
            }
            CustomMainMenu.bindTransparent();
        }
    }

    public void finishLoading(BufferedImage bi) {
        this.bi = bi;
    }

    public URL getURL() {
        return this.url;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException {
        BufferedImage bufferedimage;
        try {
            bufferedimage = ImageIO.read(imageStream);
        } finally {
            IOUtils.closeQuietly(imageStream);
        }
        return bufferedimage;
    }
}
