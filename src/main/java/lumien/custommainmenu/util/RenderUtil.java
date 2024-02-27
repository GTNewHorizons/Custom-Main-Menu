package lumien.custommainmenu.util;

import org.lwjgl.opengl.GL11;

public class RenderUtil {

    public static void drawCompleteImage(int posX, int posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) posX, (float) posY, 0.0f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f((float) width, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f((float) width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void drawPartialImage(int posX, int posY, int imageX, int imageY, int width, int height,
            int imagePartWidth, int imagePartHeight) {
        double imageWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        double imageHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        double einsTeilerWidth = 1.0 / imageWidth;
        double uvWidth = einsTeilerWidth * (double) imagePartWidth;
        double uvX = einsTeilerWidth * (double) imageX;
        double einsTeilerHeight = 1.0 / imageHeight;
        double uvHeight = einsTeilerHeight * (double) imagePartHeight;
        double uvY = einsTeilerHeight * (double) imageY;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) posX, (float) posY, 0.0f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(uvX, uvY);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2d(uvX, uvY + uvHeight);
        GL11.glVertex3f(0.0f, (float) height, 0.0f);
        GL11.glTexCoord2d(uvX + uvWidth, uvY + uvHeight);
        GL11.glVertex3f((float) width, (float) height, 0.0f);
        GL11.glTexCoord2d(uvX + uvWidth, uvY);
        GL11.glVertex3f((float) width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }
}
