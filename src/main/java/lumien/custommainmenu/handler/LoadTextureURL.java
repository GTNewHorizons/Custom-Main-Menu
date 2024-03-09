package lumien.custommainmenu.handler;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import lumien.custommainmenu.lib.textures.TextureURL;

public class LoadTextureURL extends Thread {

    final TextureURL texture;

    public LoadTextureURL(TextureURL texture) {
        this.texture = texture;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(this.texture.getURL());
        } catch (IOException e) {
            // empty catch block
        }
        if (bi != null) {
            this.texture.finishLoading(bi);
        }
    }
}
