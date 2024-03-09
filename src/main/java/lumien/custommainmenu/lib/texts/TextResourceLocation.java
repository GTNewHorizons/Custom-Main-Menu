package lumien.custommainmenu.lib.texts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class TextResourceLocation implements IText {

    String string;
    ResourceLocation resourceLocation;

    public TextResourceLocation(String resourceString) {
        this.resourceLocation = new ResourceLocation(resourceString);
        this.string = "";
    }

    @Override
    public String get() {
        if (this.string == null) {
            return "";
        }
        if (this.string.isEmpty()) {
            IResource resource = null;
            try {
                resource = Minecraft.getMinecraft().getResourceManager().getResource(this.resourceLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (resource != null) {
                BufferedReader in = null;
                in = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String inputLine = null;
                try {
                    inputLine = in.readLine();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                do {
                    builder.append(inputLine);
                    try {
                        inputLine = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    if (inputLine == null) continue;
                    builder.append("\n");
                } while (inputLine != null);
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.string = builder.toString();
            } else {
                this.string = null;
                return "";
            }
        }
        return this.string;
    }
}
