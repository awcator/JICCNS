import awcator.jiccns.meta;
import awcator.jiccns.ui.frame;
import org.apache.commons.cli.*;

public class Main {
    public static final String version = "JICNCS_202209";
    /**
     * public static int[][] a = {{-1, 3, 2, -1, -1, -1}, //0
     * {-1, -1, -1, -1, 2, -1}, //1
     * {-1, -1, -1, -1, -1, 1}, //2
     * {4, -1, 5, -1, -1, -1}, //3
     * {1, -1, 1, -1, -1, 8},//4
     * {-1, -1, -1, 1, -1, -1} //5
     * };
     * A simple graph i used to test the environemt
     */
    public static int ms = 0;

    public static void main(String[] args) throws Exception {
        boolean QUICK_ANIMATION = false;
        boolean reduce_graphics_mode = false;
        boolean support_cli = true;
        String EXP = "DUMMY";
        String[] tasks = null;
        if (support_cli) {
            Options options = new Options();
            Option input = new Option("i", "input", true, "input file path");
            input.setRequired(true);
            options.addOption(input);

            Option output = new Option("o", "output", true, "Where to write metrics? default: Mariadb");
            output.setRequired(false);
            options.addOption(output);

            Option reduce_graphics_option = new Option("rg", "reduce_graphics_mode", false, "Should graphics/arraws be displayed? default: false");
            reduce_graphics_option.setRequired(false);
            options.addOption(reduce_graphics_option);

            Option exp = new Option("exp", "experiment_name", true, "Name of the experiment");
            exp.setRequired(true);
            options.addOption(exp);

            Option quickAnimation = new Option("qa", "quick-animations", false, "Should Animations to be rendered fast? default: false");
            reduce_graphics_option.setRequired(false);
            options.addOption(quickAnimation);

            Option trail = Option.builder("t")
                    .hasArgs()
                    .longOpt("trail")
                    .valueSeparator('=')
                    .desc("MUST BE USED WITH NOGUI option. Automated experiment trail you wanted to run. eg. --trail node5:king,node7:black")
                    .build();

            options.addOption(trail);
            CommandLineParser parser = new DefaultParser();
            HelpFormatter formatter = new HelpFormatter();
            CommandLine cmd = null;//not a good practice, it serves it purpose
            try {
                cmd = parser.parse(options, args);
                if (cmd.hasOption(quickAnimation)) {
                    QUICK_ANIMATION = true;
                }
                if (cmd.hasOption(reduce_graphics_option)) {
                    reduce_graphics_mode = true;
                }
                if (cmd.hasOption(trail)) {
                    tasks = cmd.getOptionValues("trail");
                }
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                formatter.printHelp("jiccns.sh ", options);
                System.exit(1);
            }
            String inputFilePath = cmd.getOptionValue("input");
            meta.filePath = inputFilePath;
            String outputFilePath = cmd.getOptionValue("output");
            EXP = cmd.getOptionValue("experiment_name");
        }
        System.out.println("Loading " + version);
        meta.blueprint_map = meta.loadBluePrint();
        frame app = new frame(QUICK_ANIMATION, reduce_graphics_mode, tasks, EXP);
    }
}
/**
 * // TODO: 12/10/22
 * * FIFO CRP node is missing
 * <p>
 * // TODO: 11/5/22  Features
 * Save Architecture and reload option ---PRIORITY LOWEST
 * LRU NODE  PRIORITY Medium
 * Harddisk cache read write speed -MEDIUM
 * MaxEgressAwareness Node PRIORITY LOW
 * LinkedList cache/queue cache/hashmap cache PRIORITY LOW
 * FX GUI for graphs generate PRIORITY LOW
 * ML based apprch Node PRIORITY HIGH
 * Summary generator on throughput/hitsration and etc PRIORITY LOW
 * Compartor with other type of nodes PRIORITY LOW
 * <p>
 * __________________________________________________________________________
 * <p>
 * // TODO: 11/5/22  KNOWN BUGS TO BE FIX
 * A     B
 * \   /
 * c    Implimentation BUG: if data sent by A and B reaches at C at same time what should be done? curreny two broadcast msgs from c to node D (Inefficent)
 * |
 * D
 * <p>
 * Notedown average time requred to process the query and grph it
 * Total network packets moved
 * <p>
 * // TODO: 11/6/22 Algorithm validation
 * run 2 times with unkown data query
 * query from all node with random query
 */

