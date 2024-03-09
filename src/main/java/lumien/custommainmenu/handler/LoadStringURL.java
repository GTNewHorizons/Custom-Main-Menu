package lumien.custommainmenu.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lumien.custommainmenu.lib.texts.TextURL;

public class LoadStringURL extends Thread {

    final TextURL text;

    public LoadStringURL(TextURL text) {
        this.text = text;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        String newInput;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.text.getURL().openStream()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        String inputLine = null;
        do {
            if (inputLine != null) {
                builder.append(inputLine);
            }
            newInput = null;
            try {
                newInput = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inputLine == null) continue;
            builder.append("\n");
        } while ((inputLine = newInput) != null);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String e = this.text.string;
        synchronized (e) {
            this.text.string = builder.toString();
        }
    }
}
