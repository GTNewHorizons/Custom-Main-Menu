package lumien.custommainmenu.util.pngloader;

import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

public class PNGImage {

    int width;
    int height;
    int bitDepth;
    int colorType;
    int compression;
    int filter;
    int textureID;
    int interlace;
    Triple[] palette;

    public void setPalette(Triple[] palette) {
        this.palette = palette;
    }

    public void bindTexture() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureID);
    }

    public int getBitDepth() {
        return this.bitDepth;
    }

    public void setBitDepth(int bitDepth) {
        this.bitDepth = bitDepth;
    }

    public int getColorType() {
        return this.colorType;
    }

    public void setColorType(int colorType) {
        this.colorType = colorType;
    }

    public int getCompression() {
        return this.compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public int getFilter() {
        return this.filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    public int getTextureID() {
        return this.textureID;
    }

    public void setInterlace(int interlace) {
        this.interlace = interlace;
    }

    public Triple[] getPalette() {
        return this.palette;
    }
}
