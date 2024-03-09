package lumien.custommainmenu.configuration.elements;

import lumien.custommainmenu.configuration.GuiConfig;

public abstract class Element {

    final GuiConfig parent;

    public Element(GuiConfig parent) {
        this.parent = parent;
    }
}
