import awcator.jicns.meta;
import awcator.jicns.ui.frame;

public class Main {
    public static final String version = "JICNS_202209";

    public static void main(String[] args) {
        System.out.println("Loading " + version);
        meta.blueprint_map = meta.loadBluePrint();
        frame app = new frame();
    }
}
