package lumien.custommainmenu.configuration.elements;

import lumien.custommainmenu.configuration.Alignment;
import lumien.custommainmenu.configuration.GuiConfig;
import lumien.custommainmenu.lib.textures.ITexture;

public class Image extends Element {

    public final int posX;
    public final int posY;
    public final int width;
    public final int height;
    public ITexture image;
    public ITexture hoverImage;
    public Alignment alignment;
    public boolean ichBinEineSlideshow;
    public Slideshow slideShow;

    public Image(GuiConfig parent, int posX, int posY, int width, int height, Alignment alignment) {
        super(parent);
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.alignment = alignment;
    }
}
