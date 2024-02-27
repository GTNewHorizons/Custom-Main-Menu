package lumien.custommainmenu.util;

import org.lwjgl.opengl.GL11;

public class RenderUtil {

    public static void drawCompleteImage(int posX, int posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) posX, (float) posY, (float) 0.0f);
        GL11.glBegin((int) GL11.GL_QUADS);
        GL11.glTexCoord2f((float) 0.0f, (float) 0.0f);
        GL11.glVertex3f((float) 0.0f, (float) 0.0f, (float) 0.0f);
        GL11.glTexCoord2f((float) 0.0f, (float) 1.0f);
        GL11.glVertex3f((float) 0.0f, (float) height, (float) 0.0f);
        GL11.glTexCoord2f((float) 1.0f, (float) 1.0f);
        GL11.glVertex3f((float) width, (float) height, (float) 0.0f);
        GL11.glTexCoord2f((float) 1.0f, (float) 0.0f);
        GL11.glVertex3f((float) width, (float) 0.0f, (float) 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void drawPartialImage(int posX, int posY, int imageX, int imageY, int width, int height,
            int imagePartWidth, int imagePartHeight) {
        double imageWidth = GL11
                .glGetTexLevelParameteri((int) GL11.GL_TEXTURE_2D, (int) 0, (int) GL11.GL_TEXTURE_WIDTH);
        double imageHeight = GL11
                .glGetTexLevelParameteri((int) GL11.GL_TEXTURE_2D, (int) 0, (int) GL11.GL_TEXTURE_HEIGHT);
        double einsTeilerWidth = 1.0 / imageWidth;
        double uvWidth = einsTeilerWidth * (double) imagePartWidth;
        double uvX = einsTeilerWidth * (double) imageX;
        double einsTeilerHeight = 1.0 / imageHeight;
        double uvHeight = einsTeilerHeight * (double) imagePartHeight;
        double uvY = einsTeilerHeight * (double) imageY;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) posX, (float) posY, (float) 0.0f);
        GL11.glBegin((int) GL11.GL_QUADS);
        GL11.glTexCoord2d((double) uvX, (double) uvY);
        GL11.glVertex3f((float) 0.0f, (float) 0.0f, (float) 0.0f);
        GL11.glTexCoord2d((double) uvX, (double) (uvY + uvHeight));
        GL11.glVertex3f((float) 0.0f, (float) height, (float) 0.0f);
        GL11.glTexCoord2d((double) (uvX + uvWidth), (double) (uvY + uvHeight));
        GL11.glVertex3f((float) width, (float) height, (float) 0.0f);
        GL11.glTexCoord2d((double) (uvX + uvWidth), (double) uvY);
        GL11.glVertex3f((float) width, (float) 0.0f, (float) 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }
}
