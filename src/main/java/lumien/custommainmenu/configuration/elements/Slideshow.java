package lumien.custommainmenu.configuration.elements;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lumien.custommainmenu.configuration.GuiConfig;
import lumien.custommainmenu.lib.textures.ITexture;

public class Slideshow extends Element {

    public ITexture[] ressources;
    public int displayDuration;
    private int counter;
    public int fadeDuration;
    private boolean fading = false;

    public Slideshow(GuiConfig parent, String[] images) {
        super(parent);
        this.ressources = new ITexture[images.length];
        this.displayDuration = 60;
        this.counter = 0;
        this.fadeDuration = 20;
        for (int i = 0; i < images.length; ++i) {
            this.ressources[i] = GuiConfig.getWantedTexture(images[i]);
        }
    }

    public void shuffle() {
        List<ITexture> list = Arrays.asList(this.ressources);
        Collections.shuffle(list);
        this.ressources = (ITexture[]) list.toArray();
    }

    public void update() {
        ++this.counter;
        this.fading = this.counter % (this.displayDuration + this.fadeDuration) > this.displayDuration;
    }

    public boolean fading() {
        return this.fading;
    }

    public float getAlphaFade(float partial) {
        float counterProgress = ((float) this.counter + partial) % (float) (this.displayDuration + this.fadeDuration)
                - (float) this.displayDuration;
        float durationTeiler = 1.0f / (float) this.fadeDuration;
        return durationTeiler * counterProgress;
    }

    public ITexture getCurrentResource1() {
        int index = this.counter / (this.displayDuration + this.fadeDuration) % this.ressources.length;
        return this.ressources[index];
    }

    public ITexture getCurrentResource2() {
        if (this.fading) {
            int index = (this.counter + this.fadeDuration) / (this.displayDuration + this.fadeDuration)
                    % this.ressources.length;
            return this.ressources[index];
        }
        return null;
    }
}
