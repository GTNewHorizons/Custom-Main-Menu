package lumien.custommainmenu.util.apngloader;

import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

public class APNGImage {

    int width;
    int height;
    int bitDepth;
    int colorType;
    int compression;
    int filter;
    int textureID;
    int interlace;
    Triple[] palette;
    Frame[] frames;
    int numberOfFrames;
    int numberOfLoops;

    public void draw(int x, int y, int width, int height) {
        Frame currentFrame = this.frames[(int) (System.currentTimeMillis() / 100L % (long) this.frames.length)];
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentFrame.textureID);
    }

    public void setPalette(Triple[] palette) {
        this.palette = palette;
    }

    public void bindTexture() {
        GL11.glBindTexture(
                3553,
                this.frames[(int) (System.currentTimeMillis() / 100L % (long) this.frames.length)].textureID);
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

    public void setNumberOfFrames(int numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
        this.frames = new Frame[numberOfFrames];
    }

    public void setNumberOfLoops(int numberOfLoops) {
        this.numberOfLoops = numberOfLoops;
    }
}
