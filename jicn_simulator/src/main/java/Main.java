import awcator.jicns.meta;
import awcator.jicns.ui.frame;

public class Main {
    public static final String version = "JICNCS_202209";
    /**
     * public static int[][] a = {{-1, 3, 2, -1, -1, -1}, //0
     *             {-1, -1, -1, -1, 2, -1}, //1
     *             {-1, -1, -1, -1, -1, 1}, //2
     *             {4, -1, 5, -1, -1, -1}, //3
     *             {1, -1, 1, -1, -1, 8},//4
     *             {-1, -1, -1, 1, -1, -1} //5
     *     };
     * A simple graph i used to test the environemt
    */
    public static int ms = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("Loading " + version);
        meta.blueprint_map = meta.loadBluePrint();
        frame app = new frame();
    }
}

