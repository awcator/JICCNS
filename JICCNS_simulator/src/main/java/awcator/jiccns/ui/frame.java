package awcator.jiccns.ui;

import awcator.jiccns.cache_strats.*;
import awcator.jiccns.device_strats.asnnode;
import awcator.jiccns.device_strats.gpnode;
import awcator.jiccns.device_strats.jicnsDeviceImpl;
import awcator.jiccns.device_strats.tfidf_asnnode;
import awcator.jiccns.meta;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.*;

import static java.lang.Thread.sleep;

public class frame extends JFrame implements ActionListener {
    //Current higligheted Node by the user
    public static int NODE_POSITION = 0;
    public static JPanel centerPanel;
    public static RightPanel rightpanel;
    public static boolean QUICK_ANIMATIONS = false;
    public static String EXPERIMENT_NAME = "dummy";
    /**
     * metric variables
     */
    public static int total_network_time = 0;
    public static int total_network_usage = 0;
    public static int total_answer_first_time = 0;
    public static int total_asks = 0;
    public static int total_minimum_hopcount = 0;
    public static boolean REDUCE_GRAPHICS_MODE = false;
    //Nodes
    private static NodeUI[] nodes;
    private static jicnsDeviceImpl[] jicnsDevices;
    JButton reset; //reset Button
    JButton broadCastTo;//Dummy button for testing broadcasting
    JButton randomize_nodes; //node positons randomizer
    JButton writeMetrics;// A button to write metrics into database
    JButton exit;//A button to quit simulation
    JTextField searchNodes;// A simple textbox to search nodes in UI
    JTextArea sourceNodeTextArea, destNodeTextArea; //TextBox in southPanel to query info
    boolean checkForQuickAnswer = true;

    public frame(boolean QUICK_ANIMS, boolean NOGUI, String[] preload_tasks, String exp) {
        QUICK_ANIMATIONS = QUICK_ANIMS;
        REDUCE_GRAPHICS_MODE = NOGUI;
        EXPERIMENT_NAME = exp;
        System.out.println(EXPERIMENT_NAME);
        /**
         * Main Frame UI
         * set size of the window to parent size
         */
        setTitle(meta.JICNS_version);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());

        //load up buttons
        reset = new JButton("reset");
        writeMetrics = new JButton("Write Metrics into database");
        randomize_nodes = new JButton("randomize_nodes");
        sourceNodeTextArea = new JTextArea();
        sourceNodeTextArea.setText("Enter the Node Number (int) to query from");
        destNodeTextArea = new JTextArea();
        destNodeTextArea.setText("What do you query?");
        broadCastTo = new JButton("Query");
        exit = new JButton("Exit");

        reset.addActionListener(this);
        writeMetrics.addActionListener(this);
        randomize_nodes.addActionListener(this);
        broadCastTo.addActionListener(this);
        exit.addActionListener(this);

        //Borderlayout to place buttons
        BorderLayout bl = new BorderLayout();
        setLayout(bl);

        /**
         * South Panel : Control buttons
         * set size of 10% height and width as parent
         */
        JPanel southpanel = new JPanel();

        searchNodes = new JTextField("search Nodes");
        searchNodes.addActionListener(this);
        southpanel.setPreferredSize(new Dimension(getWidth(), (int) screenSize.getHeight() / 20));
        southpanel.add(writeMetrics);
        southpanel.add(reset);
        southpanel.add(randomize_nodes);
        southpanel.add(searchNodes);
        southpanel.add(sourceNodeTextArea);
        southpanel.add(destNodeTextArea);
        southpanel.add(broadCastTo);
        southpanel.add(exit);
        southpanel.setBackground(Color.darkGray);
        add(southpanel, BorderLayout.SOUTH);

        /**
         * SetUP CenterPanel: Nodes UI
         * Force the panel to free layout
         */
        centerPanel = new freePanel();
        centerPanel.setPreferredSize(new Dimension(getWidth() * 2, getHeight() * 2));
        centerPanel.setLayout(null);
        loadNodesUI(centerPanel, getWidth(), getHeight(), false);
        add(new JScrollPane(centerPanel), BorderLayout.CENTER);

        /**
         * SetUP Right Panel: Node Proeprteis
         *
         */
        rightpanel = new RightPanel();
        add(rightpanel, BorderLayout.EAST);

        /**
         * Back to Main frame container UI
         */
        setVisible(true);
        //SystemExit on frame close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            if (preload_tasks != null) {
                System.out.println("Got Preload tasks... Processing ");
                for (int i = 0; i < preload_tasks.length; i++) {
                    if (preload_tasks[i].equalsIgnoreCase("query")) {
                        System.out.println("**************************************************<TASK Node " + preload_tasks[i] + " querying for " + preload_tasks[i + 1] + ">***************************************");
                        destNodeTextArea.setText(preload_tasks[i + 2]);
                        sourceNodeTextArea.setText(preload_tasks[i + 1]);
                        startAnimation(true);
                        if (!QUICK_ANIMS) Thread.sleep(1000);
                        i = i + 2;
                        System.out.println("**************************************************</TASK>***************************************\n\n");
                    }
                    if (preload_tasks[i].equalsIgnoreCase("move")) {
                        System.out.println("**************************************************<TASK Node " + preload_tasks[i] + " querying for " + preload_tasks[i + 1] + ">***************************************");
                        nodes[Integer.parseInt(preload_tasks[i + 1])].setBounds(Integer.parseInt(preload_tasks[i + 2]), Integer.parseInt(preload_tasks[i + 3]), 100, 50);
                        if (!QUICK_ANIMS) Thread.sleep(1000);
                        i = i + 3;
                        System.out.println("**************************************************</TASK>***************************************\n\n");
                    }
                }
                System.out.println("All tasks completed ");
                /*metricsWriter mw = new metricsWriter();
                System.exit(0);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(preload_tasks.length);
        }
    }

    public static Color getRandomColor() {
        Random r = new Random();
        return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

    private static void updateGlobalRoutingTable() {
        System.out.println("Updating Routing Table...");
        System.out.println("Updated sucesffully");
    }

    private void loadNodesUI(JPanel centerpanel, int SCREEN_WIDTH, int SCREEN_HEIGHT, boolean reset_ui_positons) {
        if (reset_ui_positons) System.out.println("ReLoading Nodes UI");
        else System.out.println("Loading Nodes UI");

        JSONObject jsondata = new JSONObject(meta.blueprint_map);
        int node_count = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_count"));
        int node_UI_width = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_ui_width"));
        int node_UI_height = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_ui_height"));
        if (!reset_ui_positons) {
            nodes = new NodeUI[node_count];
            jicnsDevices = new jicnsDeviceImpl[node_count];
        }
        dragListener mia = null;
        if (!reset_ui_positons) mia = new dragListener(centerpanel);
        popupMenu menu = new popupMenu();
        HashSet<String> setOfDataInCacheAndMemory = new HashSet<>();
        Random random = new Random();
        for (int i = 0; i < node_count; i++) {
            String prefix = "";
            if (!reset_ui_positons) {
                prefix = (String) jsondata.getJSONObject("nodes_blueprint").get("node_prefix");
                System.out.println("----------------------------\nWorking on " + prefix + i);
                if (!jsondata.isNull(prefix + i) && jsondata.getJSONObject(prefix + i).get("device_type").equals("TODO")) {
                    // TODO: 9/9/22
                } else {
                    int egressSize = 0;
                    if (jsondata.isNull(prefix + i) || jsondata.getJSONObject(prefix + i).isNull("egress")) {
                        System.out.println("Found no egres etry for " + prefix + i);
                        egressSize = 0;
                    } else {
                        // TODO: 12/10/22 Egress size.length? is it working really?
                        egressSize = jsondata.getJSONObject(prefix + i).getJSONObject("egress").length();
                    }
                    String DEVICE_TYPE = jsondata.getJSONObject(prefix + i).get("device_type").toString();
                    String CACHE_TYPE = jsondata.getJSONObject(prefix + i).get("cache_type").toString();
                    jicnsCacheImpl cache_strtegy = null;
                    if (CACHE_TYPE.equalsIgnoreCase("Random_CRP")) {
                        cache_strtegy = new RandomCRP(i);
                    } else if (CACHE_TYPE.equalsIgnoreCase("fifoCRP")) {
                        cache_strtegy = new fifoCRP(i);
                    } else if (CACHE_TYPE.equalsIgnoreCase("tfidfCRP")) {
                        cache_strtegy = new tfidfCRP(i);
                    } else if (CACHE_TYPE.equalsIgnoreCase("tfidf_with_live_timeoutCRP")) {
                        cache_strtegy = new tfidf_with_live_timeoutCRP(i);
                    } else if (CACHE_TYPE.equalsIgnoreCase("Random_hop_aware_CRP")) {
                        cache_strtegy = new Random_hop_aware_CRP(i);
                    } else if (CACHE_TYPE.equalsIgnoreCase("noncacheable")) {
                        cache_strtegy = new noncacheable(i);
                    } else {
                        System.out.println("Did not understand CACHE_TYPE default to NoCahceType");
                        cache_strtegy = new noncacheable(i);
                    }

                    if (DEVICE_TYPE.equalsIgnoreCase("CONSUMER")) {
                        jicnsDevices[i] = new gpnode(i, egressSize, cache_strtegy);
                        jicnsDevices[i].setDeviceType("CONSUMER");
                    } else if (DEVICE_TYPE.equalsIgnoreCase("PRODUCER")) {
                        jicnsDevices[i] = new gpnode(i, egressSize, cache_strtegy);
                        jicnsDevices[i].setDeviceType("PRODUCER");
                    } else if (DEVICE_TYPE.equalsIgnoreCase("LOCAL_GATEWAY")) {
                        jicnsDevices[i] = new gpnode(i, egressSize, cache_strtegy);
                        jicnsDevices[i].setDeviceType("LOCAL_GATEWAY");
                    } else if (DEVICE_TYPE.equalsIgnoreCase("INTERNET_GATEWAY")) {
                        jicnsDevices[i] = new gpnode(i, egressSize, cache_strtegy);
                        jicnsDevices[i].setDeviceType("INTERNET_GATEWAY");
                    } else if (DEVICE_TYPE.equalsIgnoreCase("ROUTER")) {
                        jicnsDevices[i] = new gpnode(i, egressSize, cache_strtegy);
                        jicnsDevices[i].setDeviceType("ROUTER");
                    } else if (DEVICE_TYPE.equalsIgnoreCase("ASN")) {
                        jicnsDevices[i] = new asnnode(i, egressSize, cache_strtegy);
                        jicnsDevices[i].setDeviceType("ASN");
                    } else if (DEVICE_TYPE.equalsIgnoreCase("TF_IDF_ASN")) {
                        jicnsDevices[i] = new tfidf_asnnode(i, egressSize, cache_strtegy);
                        jicnsDevices[i].setDeviceType("ASN");
                        jicnsDevices[i].setCanonicalName("TF_IDF_ASN");
                    } else if (DEVICE_TYPE.equalsIgnoreCase("edge")) {
                        jicnsDevices[i] = new gpnode(i, egressSize, cache_strtegy);
                        jicnsDevices[i].setDeviceType("edge");
                    } else {
                        System.err.println("COuldnt not undertstand the device_type " + DEVICE_TYPE);
                        System.exit(0);
                    }
                    if (!jsondata.getJSONObject(prefix + i).isNull("description")) {
                        String device_descritpion = jsondata.getJSONObject(prefix + i).get("description").toString();
                        if (device_descritpion != null) {
                            jicnsDevices[i].setDeviceDescription(device_descritpion);
                        }
                    } else {
                        System.err.println("Clouldnt not load device description");
                        jicnsDevices[i].setDeviceDescription("Unknown ");
                    }
                    /**
                     * Egress Rules
                     */
                    if (jsondata.isNull(prefix + i) || jsondata.getJSONObject(prefix + i).isNull("egress")) {
                        // TODO: 9/10/22
                    } else {
                        int k = 0;
                        for (Iterator<String> it = jsondata.getJSONObject(prefix + i).getJSONObject("egress").keys(); it.hasNext(); ) {
                            String str = it.next();
                            int latency = Integer.parseInt(jsondata.getJSONObject(prefix + i).getJSONObject("egress").get(str).toString().replace("ms", ""));
                            jicnsDevices[i].EGRESS[k][0] = Integer.parseInt(str.replace(prefix, ""));
                            jicnsDevices[i].EGRESS[k][1] = latency;
                            k++;
                        }
                    }
                    /**
                     * node memory settings
                     */
                    if (!jsondata.isNull(prefix + i) && !jsondata.getJSONObject(prefix + i).isNull("max_cache_size")) {
                        System.out.println("AWCATOR " + prefix + i + "  " + Integer.parseInt((String) jsondata.getJSONObject(prefix + i).get("max_cache_size")));
                        jicnsDevices[i].getCacheStrategy().CACHE_MEMORY_SIZE = Integer.parseInt((String) jsondata.getJSONObject(prefix + i).get("max_cache_size"));

                        jicnsDevices[i].getCacheStrategy().allocateCacheMemorySize();
                    } else {
                        //allocate default size
                        jicnsDevices[i].getCacheStrategy().allocateCacheMemorySize();
                    }
                    if (!jsondata.isNull(prefix + i) && !jsondata.getJSONObject(prefix + i).isNull("max_payload_size")) {
                        jicnsDevices[i].getCacheStrategy().LOCAL_PAYLOAD_SIZE = Integer.parseInt((String) jsondata.getJSONObject(prefix + i).get("max_payload_size"));
                        jicnsDevices[i].getCacheStrategy().allocatePayloadMemorySize();
                    } else {
                        //allocate default size
                        jicnsDevices[i].getCacheStrategy().allocatePayloadMemorySize();
                    }
                    /**
                     * Load up node memory from blueprint if specified
                     */
                    if (!jsondata.isNull(prefix + i) && !jsondata.getJSONObject(prefix + i).isNull("payload")) {
                        for (Iterator<String> it = jsondata.getJSONObject(prefix + i).getJSONObject("payload").keys(); it.hasNext(); ) {
                            String str = it.next();
                            if (!jicnsDevices[i].getCacheStrategy().addToPayloadMemory(str, (String) jsondata.getJSONObject(prefix + i).getJSONObject("payload").get(str))) {
                                System.err.println("Failed to add into PayloadMemory");
                            } else {
                                System.out.println("Loaded Data into PayloadMemory");
                                setOfDataInCacheAndMemory.add(str);
                            }
                        }
                    }
                    /**
                     * Load up node cachememory from blueprint if specified
                     */
                    if (!jsondata.isNull(prefix + i) && !jsondata.getJSONObject(prefix + i).isNull("cached")) {
                        for (Iterator<String> it = jsondata.getJSONObject(prefix + i).getJSONObject("cached").keys(); it.hasNext(); ) {
                            String str = it.next();
                            System.out.println(str + " <-----This entry should be added to cache");
                            if (!jicnsDevices[i].getCacheStrategy().addToCacheMemory(str, (String) jsondata.getJSONObject(prefix + i).getJSONObject("cached").get(str), true)) {
                                System.err.println("Failed to add into cache ");
                            } else {
                                System.out.println("Loaded Cache into CacheMemory ");
                                //add the keys into the wordlist
                                setOfDataInCacheAndMemory.add(str);
                            }
                        }
                    }
                    nodes[i] = new NodeUI(prefix + i, jicnsDevices[i]);
                    try {
                        if (DEVICE_TYPE != null) {
                            if (!jsondata.getJSONObject(prefix + i).isNull("icon")) {
                                nodes[i].setOpaque(false);
                                nodes[i].setContentAreaFilled(false);
                                nodes[i].setBorderPainted(false);
                                nodes[i].setIcon(new ImageIcon(ImageIO.read(new File((String) jsondata.getJSONObject(prefix + i).get("icon")))));
                            } else {
                                System.err.println("No icons specified for " + prefix + i + " skipping");
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                        System.err.println("Error loading icon for " + prefix + i);
                    }
                }
            }
            if (reset_ui_positons == true)
                nodes[i].setBounds(random.nextInt(SCREEN_WIDTH - node_UI_width - 300), random.nextInt(SCREEN_HEIGHT - node_UI_height - 100), node_UI_width, node_UI_height);
            else {
                if (!jsondata.isNull(prefix + i) && !jsondata.getJSONObject(prefix + i).isNull("X") && !jsondata.getJSONObject(prefix + i).isNull("Y")) {
                    nodes[i].setBounds(Integer.parseInt((String) jsondata.getJSONObject(prefix + i).get("X")), Integer.parseInt((String) jsondata.getJSONObject(prefix + i).get("Y")), node_UI_width, node_UI_height);
                } else {
                    System.err.println(prefix + i + ": Did not found both coridnates for nodes. using random");
                    nodes[i].setBounds(random.nextInt(SCREEN_WIDTH - node_UI_width - 300), random.nextInt(SCREEN_HEIGHT - node_UI_height - 100), node_UI_width, node_UI_height);
                }
            }
            if (!reset_ui_positons) centerpanel.add(nodes[i]);
            if (!reset_ui_positons) {
                nodes[i].addMouseListener(mia);
                nodes[i].addMouseMotionListener(mia);
                nodes[i].setComponentPopupMenu(menu);
                nodes[i].setName(Integer.toString(i));
            }
        }
        System.out.println("Writing datasets at " + meta.getDataSetLocation());
        try {
            FileWriter filewriter = new FileWriter(meta.getDataSetLocation());
            for (String line : setOfDataInCacheAndMemory) {
                filewriter.write(line + System.lineSeparator());
            }
            filewriter.close();
        } catch (Exception e) {
            System.err.println("Failed to write DataSets");
            e.printStackTrace();
        }
        updateGlobalRoutingTable();

        System.out.println("\tDone");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            if (actionEvent.getSource() == searchNodes) {
                String x = searchNodes.getText();
                System.out.println("Searching " + x);
                Component[] component = centerPanel.getComponents();
                for (Component comp : component) {
                    if (comp.getClass().equals(NodeUI.class)) {
                        NodeUI but = (NodeUI) comp;
                        if (comp.getName().toLowerCase().contains(x.toLowerCase()) || comp.toString().toLowerCase().contains(x.toLowerCase())) {
                            comp.setBackground(Color.RED);
                            /**
                             * Timer search animation to blink buttons
                             */
                            Timer blinkTimer = new Timer(QUICK_ANIMATIONS ? 0 : 500, new ActionListener() {
                                private final int maxCount = 4;
                                private int count = 0;
                                private boolean on = false;

                                public void actionPerformed(ActionEvent e) {
                                    if (count >= maxCount) {
                                        /**
                                         * After animation completes stop timer and setback buttons default background
                                         */
                                        comp.setBackground(new JButton().getBackground());
                                        ((Timer) e.getSource()).stop();
                                    } else {
                                        comp.setBackground(on ? Color.YELLOW : Color.RED);
                                        on = !on;
                                        count++;
                                    }
                                }
                            });
                            blinkTimer.start();
                        }
                    }
                }
            }
            if (actionEvent.getSource() == reset) {
                System.out.println("Reset button clicked");
                // TODO: 9/9/22
            }
            if (actionEvent.getSource() == randomize_nodes) {
                System.out.println("Randomzing the nodes");
                loadNodesUI(centerPanel, getWidth(), getHeight(), true);
            }

            if (actionEvent.getSource() == broadCastTo) {
                //((freePanel) centerPanel).drawLineBetween(Color.RED, 3, 5);
                //((freePanel)centerPanel).showLines=false;
                //((freePanel)centerPanel).repaint();

                startAnimation(false);
            }
            if (actionEvent.getSource() == exit) {
                System.exit(0);
            }
            if (actionEvent.getSource() == writeMetrics) {
                metricsWriter mw = new metricsWriter();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAnimation(boolean disable_parllel) {
        Thread rlMF = new Thread(new Runnable() {
            double dataPointSpeed = QUICK_ANIMATIONS ? 1 : 40;

            public void drawMultiDataPaths(java.util.List<path> pathsToDsiplayAtAtime, Graphics2D g2) throws Exception {
                while (!pathsToDsiplayAtAtime.isEmpty()) {
                    //Incremnt all the nodes X axsis datapoint travel by 30 points and repaint the graphics after ploting
                    //and then remove the datapoint from storageMermory once it reaches destination
                    for (int i = 0; i < pathsToDsiplayAtAtime.size(); i++) {
                        path displayPath = pathsToDsiplayAtAtime.get(i);
                        double sourceX, sourceY, destinationX, destinationY;
                        if (displayPath.parent == null) {
                            //node has no parent then dont draw graphics
                            pathsToDsiplayAtAtime.remove(i);
                            continue;
                        }
                        Rectangle r1 = nodes[displayPath.parent.focusedNode].getBounds();
                        Rectangle r2 = nodes[displayPath.focusedNode].getBounds();
                        sourceX = r1.getCenterX();
                        sourceY = r1.getCenterY();
                        destinationX = r2.getCenterX();
                        destinationY = r2.getCenterY();
                        double distance = Math.sqrt(Math.pow(destinationX - sourceX, 2) + Math.pow(destinationY - sourceY, 2));
                        dataPointSpeed = distance / 30;
                        boolean moveInYdirection = false; //data point to move animation, if false datapoint to be moved in X diff
                        double diffX = destinationX - sourceX;
                        if (diffX == 0)
                            diffX = 1; //just to avoid slope tending to infity, we will avoid inifiy error by teling there is gap of 1 unit space between two points
                        double diffY = destinationY - sourceY;
                        if (diffY == 0) diffY = 1;
                        double slope = diffY / diffX;
                        moveInYdirection = Math.abs(diffY) > Math.abs(diffX);
                        //moveInYdirection = true;
                        try {
                            if (displayPath.currentDataPointX == -1 && displayPath.currentDataPointY == -1) { //default case . It symbolises data point is abouut to move from node to node
                                displayPath.currentDataPointX = sourceX; //so set datapoints animation location to source datapoint co-ordinates
                                displayPath.currentDataPointY = sourceY;
                            }
                            //for (int i = (int) minX; i <= MaxX; i = i + 30) {
                            g2.setColor(displayPath.pathColor);
                            if (moveInYdirection) {
                                // TODO: 11/5/22  Again this can be improved using single calcation without using many if loops.
                                //x=(y-y1)/m+x1
                                g2.fillOval((int) ((displayPath.currentDataPointY - sourceY) / slope + sourceX), (int) displayPath.currentDataPointY, 10, 10);
                                if (sourceY > destinationY) {
                                    displayPath.currentDataPointY -= dataPointSpeed;
                                    //dont recalc to speedUp animation
                                    // TODO: 11/5/22 ANIM SPEED: skip next line , no need to store y path in memoery so we can skipp calc
                                    displayPath.currentDataPointX = (int) ((displayPath.currentDataPointY - sourceY) / slope + sourceX);
                                    if (displayPath.currentDataPointY <= destinationY) {
                                        pathsToDsiplayAtAtime.remove(i);
                                        if (displayPath.forward)
                                            nodes[displayPath.focusedNode].jicnsNode.onIncomingReqData(displayPath.actual_query, nodes, displayPath);
                                        else
                                            nodes[displayPath.focusedNode].jicnsNode.onRespIncomingData(nodes, displayPath, displayPath.actual_query, displayPath.actual_query_answer);
                                    }
                                } else {
                                    displayPath.currentDataPointY += dataPointSpeed;
                                    displayPath.currentDataPointX = (int) ((displayPath.currentDataPointY - sourceY) / slope + sourceX);
                                    if (displayPath.currentDataPointY >= destinationY) {
                                        pathsToDsiplayAtAtime.remove(i);
                                        if (displayPath.forward)
                                            nodes[displayPath.focusedNode].jicnsNode.onIncomingReqData(displayPath.actual_query, nodes, displayPath);
                                        else
                                            nodes[displayPath.focusedNode].jicnsNode.onRespIncomingData(nodes, displayPath, displayPath.actual_query, displayPath.actual_query_answer);
                                    }
                                }
                            } else {
                                //y=m(x-x1)+y1
                                g2.fillOval((int) displayPath.currentDataPointX, (int) (slope * (displayPath.currentDataPointX - sourceX) + sourceY), 10, 10);
                                if (sourceX > destinationX) {
                                    displayPath.currentDataPointX -= dataPointSpeed;
                                    //dont recalc to speedUp animation
                                    // TODO: 11/5/22 ANIM SPEED: skip next line , no need to store y path in memoery so we can skipp calc
                                    displayPath.currentDataPointY = (int) (slope * (displayPath.currentDataPointX - sourceX) + sourceY);
                                    if (displayPath.currentDataPointX <= destinationX) {
                                        pathsToDsiplayAtAtime.remove(i);
                                        if (displayPath.forward)
                                            nodes[displayPath.focusedNode].jicnsNode.onIncomingReqData(displayPath.actual_query, nodes, displayPath);
                                        else
                                            nodes[displayPath.focusedNode].jicnsNode.onRespIncomingData(nodes, displayPath, displayPath.actual_query, displayPath.actual_query_answer);
                                    }
                                } else {
                                    displayPath.currentDataPointX += dataPointSpeed;
                                    displayPath.currentDataPointY = (int) (slope * (displayPath.currentDataPointX - sourceX) + sourceY);
                                    if (displayPath.currentDataPointX >= destinationX) {
                                        pathsToDsiplayAtAtime.remove(i);
                                        if (displayPath.forward)
                                            nodes[displayPath.focusedNode].jicnsNode.onIncomingReqData(displayPath.actual_query, nodes, displayPath);
                                        else
                                            nodes[displayPath.focusedNode].jicnsNode.onRespIncomingData(nodes, displayPath, displayPath.actual_query, displayPath.actual_query_answer);
                                    }
                                }
                            }
                            //}
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (!QUICK_ANIMATIONS) sleep(100);
                    //keep traces? remove repaint line
                    centerPanel.repaint();
                }
            }

            public void run() {
                try {
                    final String QUERY_FROM_USER = destNodeTextArea.getText();
                    Graphics2D g2 = (Graphics2D) centerPanel.getGraphics();
                    System.out.println("Broadcasting from node " + sourceNodeTextArea.getText());
                    int sourceNode = Integer.parseInt(sourceNodeTextArea.getText());
                    int endNode = 0;
                    path rootparent = new path("node" + sourceNode, 0, null, sourceNode, getRandomColor(), -1, null, true, QUERY_FROM_USER, null); //start from sourcenOde with inital timeout of 0ms
                    nodes[rootparent.focusedNode].jicnsNode.onBeginSession(QUERY_FROM_USER);
                    total_asks++;
                    checkForQuickAnswer = true;
                    class path_sorter implements Comparator<path> {
                        @Override
                        public int compare(path s1, path s2) {
                            if (s1.ms > s2.ms) return 1;
                            else if (s1.ms < s2.ms) return -1;
                            return 0;
                        }
                    }
                    boolean query_answered = false;
                    PriorityQueue<path> pq = new PriorityQueue<path>(new path_sorter());
                    path temppath = rootparent;
                    pq.add(temppath);
                    int previous_ms = temppath.ms;
                    boolean expectNewMs = false;
                    boolean check_previous_ms = true;
                    java.util.List<path> pathsToDsiplayAtAtime = new ArrayList<path>();
                    boolean drawAnime = false;
                    boolean parallelPaths = false;
                    boolean first_asn_handshake = false;
                    HashSet<Integer> force_these_nodes = null;
                    while (!pq.isEmpty()) {
                        //To see how fast the queues grows/ or the edges grows n(n-1) edges can be drawn using n vertex
                        //System.out.println("SiZE "+pq.size());

                        temppath = pq.poll();
                        /**
                         * ASN HANDSHAKE
                         */
                        if (first_asn_handshake == false && nodes[temppath.focusedNode].jicnsNode.getDeviceType().equalsIgnoreCase("ASN")) {
                            first_asn_handshake = true;
                            asnnode ASN_HANDSHAKE_INTITATOR = (asnnode) nodes[temppath.focusedNode].jicnsNode;
                            System.out.println("------------<ASN ROUTING TABLE>-------------");
                            force_these_nodes = ASN_HANDSHAKE_INTITATOR.getASN_short_distanceFinderInstance().getShortPath(ASN_HANDSHAKE_INTITATOR.getNodeID(), temppath.ms, QUERY_FROM_USER, -1, "", nodes).convertToset();
                            System.out.println("------------</ASN ROUTING TABLE>-------------");
                        }

                        //prints time digaram
                        System.out.print("\n" + temppath.ms + " " + temppath + "  " + temppath.actual_query + "  " + temppath.actual_query_answer);
                        int foucusedNode = temppath.focusedNode;

                        if (check_previous_ms) {
                            if (temppath.ms == previous_ms) {
                                //dontsleep
                                pathsToDsiplayAtAtime.add(temppath);
                                parallelPaths = true;
                            } else {
                                if (drawAnime && parallelPaths != false) {
                                    //System.out.println("parallel> " + pathsToDsiplayAtAtime);
                                    drawMultiDataPaths(pathsToDsiplayAtAtime, g2);
                                } else if (drawAnime) {
                                    //System.out.println("Single> " + pathsToDsiplayAtAtime);
                                    drawMultiDataPaths(pathsToDsiplayAtAtime, g2);
                                }
                                //Thread.sleep(0);
                                //((freePanel) centerPanel).repaint();
                                //System.out.println();
                                previous_ms = temppath.ms;
                                //Clear Parllel movemnts info array
                                // and add an path
                                //System.out.println(pathsToDsiplayAtAtime);
                                pathsToDsiplayAtAtime.clear();
                                pathsToDsiplayAtAtime.add(temppath);
                                drawAnime = true;
                                parallelPaths = false;
                            }
                        }
                        String LOOKUP_FOR_QUERY = null;
                        String temp_query_answer = null;
                        temp_query_answer = nodes[temppath.focusedNode].jicnsNode.getCacheStrategy().cacheLookUp(temppath.actual_query, false);
                        //System.out.println(temp_query_answer);
                        if (temp_query_answer == null) {
                            temp_query_answer = nodes[temppath.focusedNode].jicnsNode.getCacheStrategy().hddLookUp(temppath.actual_query, false);
                        }
                        LOOKUP_FOR_QUERY = temp_query_answer;
                        if ((temppath.destinationNode == -1 && (LOOKUP_FOR_QUERY != null)) || (foucusedNode == temppath.destinationNode)) {
                            nodes[temppath.focusedNode].jicnsNode.onRequestAnsweredByMe();
                            query_answered = true;
                            Timer blinkTimer = new Timer(QUICK_ANIMATIONS ? 0 : 500, new ActionListener() {
                                private final int maxCount = 4;
                                private int count = 0;
                                private boolean on = false;

                                public void actionPerformed(ActionEvent e) {
                                    if (count >= maxCount) {
                                        /**
                                         * After animation completes stop timer and setback buttons default background
                                         */
                                        nodes[foucusedNode].setBackground(new JButton().getBackground());
                                        ((Timer) e.getSource()).stop();
                                    } else {
                                        nodes[foucusedNode].setBackground(on ? Color.GREEN : Color.YELLOW);
                                        on = !on;
                                        count++;
                                    }
                                }
                            });
                            blinkTimer.start();

                            //System.out.println("Query Answered BY Node " + foucusedNode);
                            if (temppath.forward) {
                                path newpath = new path("node" + temppath.focusedNode, temppath.ms, null, temppath.focusedNode, getRandomColor(), sourceNode, temppath.pa, false, temppath.actual_query, LOOKUP_FOR_QUERY);
                                pq.add(newpath);
                                // to help timing diagram
                                System.out.print("[FEND]");
                                continue;
                            } else {
                                // to help timing diagram
                                System.out.print("[BEND]");
                                if (checkForQuickAnswer) {
                                    checkForQuickAnswer = false;
                                    total_answer_first_time += temppath.ms;
                                    total_minimum_hopcount += temppath.pa.split("-->").length - 1;
                                }
                                        /*
                                        if(temppath.parent==null){
                                            System.out.println("Self Answered");
                                        }
                                        else {
                                            System.out.println("Answered by other nodes ");
                                        }
                                        */
                                //Sucessfully answer packet reached to the source query packet
                                //nodes[temppath.focusedNode].jicnsNode.onRespIncomingData(temppath.actual_query+":"+temppath.actual_query_answer);
                                continue;
                            }
                        } else {
                            //This means node is about to send packets to other Nodes (Broadcast)? Should I forward it or should i send it back to requested guy?
                            if (temppath.forward) {
                                for (int i = 0; i < nodes.length; i++) {
                                    if (force_these_nodes != null) {
                                        if (force_these_nodes.contains(i)) {
                                            //cary on forwarding
                                        } else {
                                            //skip ith itteration
                                            continue;
                                        }
                                    }
                                    //broadcast into cycle?
                                    if (i != foucusedNode && nodes[foucusedNode].jicnsNode.isMyNeibhour(i) && nodes[foucusedNode].jicnsNode.shouldIPassThroughthisNode(i, null)) {
                                        if (nodes[foucusedNode].jicnsNode.allowCycles()) {
                                            nodes[temppath.focusedNode].jicnsNode.onReqOutGoingData(Integer.toString(i), temppath.pa);
                                            path newpath = new path(temppath.pa + "-->node" + i, temppath.ms + nodes[foucusedNode].jicnsNode.getMsToReachNode(i, nodes), temppath, i, getRandomColor(), temppath.parent == null ? -1 : temppath.parent.destinationNode, null, true, temppath.actual_query, null);
                                            pq.add(newpath);
                                            total_network_usage++;
                                        } else {
                                            //if (!temppath.pa.contains("node" + i)) {
                                            if (!Arrays.asList(temppath.pa.split("-->")).contains("node" + i)) {
                                                nodes[temppath.focusedNode].jicnsNode.onReqOutGoingData(Integer.toString(i), temppath.pa);
                                                path newpath = new path(temppath.pa + "-->node" + i, temppath.ms + nodes[foucusedNode].jicnsNode.getMsToReachNode(i, nodes), temppath, i, getRandomColor(), temppath.parent == null ? -1 : temppath.parent.destinationNode, null, true, temppath.actual_query, null);
                                                pq.add(newpath);
                                                total_network_usage++;
                                            }
                                        }
                                    }
                                }
                            } else {
                                //Reverse PATH Iteration
                                String PATH = temppath.backtrack;
                                String newPATHwithoutLastNode = PATH.substring(0, PATH.lastIndexOf("-->") == -1 ? 0 : PATH.lastIndexOf("-->"));
                                String last_node_onPATH = newPATHwithoutLastNode.substring(newPATHwithoutLastNode.lastIndexOf("-->") == -1 ? 0 : newPATHwithoutLastNode.lastIndexOf("-->") + 3);
                                int nodeIDfromNode = Integer.parseInt(last_node_onPATH.replace("node", ""));
                                nodes[temppath.focusedNode].jicnsNode.onRespOutGoingData();
                                path newpath = new path(temppath.pa + "-->node" + nodeIDfromNode, temppath.ms + nodes[nodeIDfromNode].jicnsNode.getMsToReachNode(foucusedNode, nodes), temppath, nodeIDfromNode, temppath.parent == null ? getRandomColor() : temppath.parent.pathColor, temppath.parent == null ? sourceNode : temppath.parent.destinationNode, newPATHwithoutLastNode, false, temppath.actual_query, temppath.actual_query_answer);
                                pq.add(newpath);
                                total_network_usage++;
                            }
                        }
                        if (check_previous_ms) {
                            previous_ms = temppath.ms;
                        }
                    }
                    if (!pathsToDsiplayAtAtime.isEmpty()) {
                        if (pathsToDsiplayAtAtime.size() >= 2) {
                            //System.out.println("parallel> " + pathsToDsiplayAtAtime);
                            drawMultiDataPaths(pathsToDsiplayAtAtime, g2);
                            pathsToDsiplayAtAtime.clear();
                        } else {
                            //System.out.println("Single> " + pathsToDsiplayAtAtime);
                            drawMultiDataPaths(pathsToDsiplayAtAtime, g2);
                            pathsToDsiplayAtAtime.clear();
                        }
                    } else {
                        //All data points are reached to the destination.
                    }
                    if (query_answered) {
                        System.out.println("---Done--Query Answered");
                        total_network_time += temppath.ms;
                    } else {
                        System.out.println("---Failed--- No node cant handle request");
                        total_network_time += temppath.ms;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Summary Metrics: " + total_network_time + "   " + total_network_usage + "    " + total_answer_first_time + "   " + total_asks + "  " + total_minimum_hopcount);
            }
        });
        rlMF.start();
        try {
            if (disable_parllel) {
                rlMF.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class dragListener extends MouseInputAdapter {
        Point location;
        JPanel mypanel;
        Point pressed;

        public dragListener(JPanel jPanel) {
            mypanel = jPanel;
        }

        public void mousePressed(MouseEvent me) {
            pressed = me.getLocationOnScreen();
            Window window = SwingUtilities.windowForComponent(me.getComponent());
            location = window.getLocation();
        }

        public void mouseDragged(MouseEvent me) {

            Point dragged = me.getLocationOnScreen();
            /**
             * int x = (int)(location.x + dragged.getX() - pressed.getX());
             * int y = (int)(location.y + dragged.getY() - pressed.getY());
             */
            me.getComponent().setLocation((int) dragged.getX(), (int) dragged.getY());
            mypanel.repaint();
            /**
             * // TODO: 9/10/22
             * More speedup animation can be done by animating only focused nodes arrows
             */
        }
    }

    static class popupMenu extends JPopupMenu implements ActionListener {
        public popupMenu() {
            JMenuItem nodeProperties = new JMenuItem("Show NodeProperties");
            add(nodeProperties);
            add(new JSeparator());
            add(nodeProperties);
            nodeProperties.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JPopupMenu jp = (JPopupMenu) ((JMenuItem) actionEvent.getSource()).getParent();
            JButton node = (JButton) jp.getInvoker();
            NODE_POSITION = Integer.parseInt(node.getName());
            RightPanel.applayChanges();
        }
    }

    static class RightPanel extends JPanel {
        static JTextArea title;
        static JTable table, table2;
        static DefaultTableModel payloadTableModel;
        static DefaultTableModel cacheTableModel;

        public RightPanel() {
            GridLayout layout = new GridLayout(2, 2);
            setLayout(layout);
            title = new JTextArea();
            title.setEditable(false);
            payloadTableModel = new DefaultTableModel();
            payloadTableModel.addColumn("#");
            payloadTableModel.addColumn("Key");
            payloadTableModel.addColumn("value");
            payloadTableModel.addRow(new String[]{"This is", "Payload", "contents"});

            cacheTableModel = new DefaultTableModel();
            cacheTableModel.addColumn("#");
            cacheTableModel.addColumn("Key");
            cacheTableModel.addColumn("value");
            cacheTableModel.addRow(new String[]{"This is", "Cached", "contents"});

            table = new JTable(payloadTableModel);
            table2 = new JTable(cacheTableModel);

            table.getTableHeader().setBackground(new JButton().getBackground());
            table2.getTableHeader().setBackground(new JButton().getBackground());

            JScrollPane scrollPane1 = new JScrollPane(table);
            JScrollPane scrollPane2 = new JScrollPane(table2);
            scrollPane1.setPreferredSize(new Dimension(150, 200));
            scrollPane2.setPreferredSize(new Dimension(150, 200));
            add(new JScrollPane(title));
            add(scrollPane2);
            add(new JButton("dsada"));
            add(scrollPane1);
        }

        public static void applayChanges() {
            title.setText("Node ID : " + NODE_POSITION);
            title.setText(title.getText() + "\nDeviceType: " + jicnsDevices[NODE_POSITION].getDeviceType() + "\nCannonName: " + jicnsDevices[NODE_POSITION].getCanonicalName());
            title.setText(title.getText() + "\nCacheType: " + jicnsDevices[NODE_POSITION].getCacheStrategy().getCacheType() + "\npostion: " + nodes[NODE_POSITION].getX() + "," + nodes[NODE_POSITION].getY() + " \n");
            payloadTableModel.setRowCount(0);

            String[][] x = jicnsDevices[NODE_POSITION].getCacheStrategy().getPayloadContents();
            if (x != null) {
                for (int i = 0; i < x.length; i++) {
                    payloadTableModel.addRow(new Object[]{i, x[i][0], x[i][1]});
                }
            }
            cacheTableModel.setRowCount(0);
            x = jicnsDevices[NODE_POSITION].getCacheStrategy().getCacheContents();
            if (x != null) {
                for (int i = 0; i < x.length; i++) {
                    cacheTableModel.addRow(new Object[]{i, x[i][0], x[i][1]});
                }
            }
            System.gc();
            x = null;
            System.gc();
        }
    }


    class freePanel extends JPanel {
        public boolean showLines = true;
        public Graphics2D mygraphics;

        public freePanel() {
            setBackground(Color.white);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //drawLineBetween(Color.RED,0,1);
            if (!REDUCE_GRAPHICS_MODE) drawConnectors(g2);
        }

        private void drawConnectors(Graphics2D g2) {
            Rectangle r1, r2;
            double x1, y1, x2, y2;
            //Component[] c = getComponents();
            NodeUI[] c = nodes;
            for (int i = 0; i < c.length; i++) {
                r1 = c[i].getBounds();
                x1 = r1.getCenterX();
                y1 = r1.getCenterY();
                g2.setColor(Color.BLACK);
                g2.drawString(c[i].jicnsNode.getDeviceDescription(), (int) r1.getMinX() - 15, (int) r1.getMaxY() + 15);
                for (int j = 0; j < c[i].jicnsNode.EGRESS.length; j++) {
                    r2 = c[c[i].jicnsNode.EGRESS[j][0]].getBounds();
                    x2 = r2.getCenterX();
                    y2 = r2.getCenterY();
                    g2.draw(new Line2D.Double(x1, y1, x2, y2));
                    drawArrow(g2, (int) x1, (int) y1, (int) (x2 + x1) / 2, (int) (y2 + y1) / 2, 20, 20);
                }
            }
        }

        private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2, int d, int h) {
            int dx = x2 - x1, dy = y2 - y1;
            double D = Math.sqrt(dx * dx + dy * dy);
            double xm = D - d, xn = xm, ym = h, yn = -h, x;
            double sin = dy / D, cos = dx / D;

            x = xm * cos - ym * sin + x1;
            ym = xm * sin + ym * cos + y1;
            xm = x;

            x = xn * cos - yn * sin + x1;
            yn = xn * sin + yn * cos + y1;
            xn = x;

            int[] xpoints = {x2, (int) xm, (int) xn};
            int[] ypoints = {y2, (int) ym, (int) yn};

            //g.drawLine(x1, y1, x2, y2);
            g.fillPolygon(xpoints, ypoints, 3);
        }

        public Graphics2D getFreePanelGraphis() {
            return mygraphics;
        }
    }

    class metricsWriter {
        Connection db_connection;
        String expirment_name = null;
        long expiremt_epoch_time = 0;

        public metricsWriter() {
            if (getExpirment_name() == null) {
                setExpirment_name(EXPERIMENT_NAME);
                expiremt_epoch_time = Instant.now().getEpochSecond();
            }
            loadDatabase();
            writeExpiremntEntry();
            System.out.println("Expirent ID " + getExpiremntID());
            writeNodeSummary();
        }

        public long getExpiremt_epoch_time() {
            return expiremt_epoch_time;
        }

        public void setExpiremt_epoch_time(long expiremt_epoch_time) {
            this.expiremt_epoch_time = expiremt_epoch_time;
        }

        public String getExpirment_name() {
            return expirment_name;
        }

        public void setExpirment_name(String expirment_name) {
            this.expirment_name = expirment_name;
        }

        /**
         * Load the database
         */
        private void loadDatabase() {
            try {
                final String USER_NAME = "root";
                final String USER_PASSWORD = "dev";
                Class.forName("org.mariadb.jdbc.Driver");// I run mariadb server so i use its. And make sure maridb  java lib is ints class path
                //check https://wiki.archlinux.org/title/JDBC_and_MySQL
                // https://aur.archlinux.org/packages/mariadb-jdbc for more info
                System.out.println("MYSQL Driver loaded");
                db_connection = DriverManager.getConnection("jdbc:mariadb://localhost/jiccns", USER_NAME, USER_PASSWORD);
                System.out.println("Database_Connected");
            } catch (Exception e) {
                System.err.println(meta.JICNS_version + " couldnot able to connect to the database ");
                e.printStackTrace();
            }
        }

        private boolean writeNodeSummary() {
            long node_epoch_time = Instant.now().getEpochSecond();
            try {
                //jicnsCacheImpl[] jicnsnodes; //this is the same as variable declared in statically and globally in superclass frame.java
                for (jicnsDeviceImpl tempnode : jicnsDevices) {
                    String query = "insert into nodesummary (" +
                            "nodename," +
                            "nodetype," +
                            "exp_id," +
                            "numberofrequestshandled," +
                            "numberofrequestsansweredBYME," +
                            "numberofrequestsforwarded," +
                            "numberofcachehits," +
                            "numberofcachemiss," +
                            "numberofcachelookups," +
                            "numberofhddhits," +
                            "numberofhddmiss," +
                            "numberofhddlookups," +
                            "cache_enq_count," +
                            "cache_dq_count," +
                            "req_answer_forwarded_count," +
                            "pc," +
                            "epochtime)" +

                            "value (" +
                            "\"" + "NODE" + tempnode.getNodeID() + "\"," +
                            "\"" + tempnode.getCanonicalName() + "\"," +
                            getExpiremntID() + "," +
                            tempnode.getNumberOfRequestsHandled() + "," +
                            tempnode.getNumberOfRequestsAnsweredBYME() + "," +
                            tempnode.getNumberOfRequestsForwarded() + "," +
                            tempnode.getCacheStrategy().getNumberOfCacheHits() + "," +
                            tempnode.getCacheStrategy().getNumberOfCacheMiss() + "," +
                            tempnode.getCacheStrategy().getNumberOfTimesCachelookups() + "," +
                            tempnode.getCacheStrategy().getNumberOfHDDHits() + "," +
                            tempnode.getCacheStrategy().getNumberOfHDDMiss() + "," +
                            tempnode.getCacheStrategy().getNumberOfTimesHDDlookups() + "," +
                            tempnode.getCacheStrategy().getNumberOfCacheEnque() + "," +
                            tempnode.getCacheStrategy().getNumberOfCacheDeque() + "," +
                            tempnode.getNumberOfRequestesAnswereForwardedCount() + "," +
                            tempnode.getCacheStrategy().CACHE_POWER_CONSUMPTION + tempnode.getNumberOfRequestsHandled() + "," +
                            node_epoch_time + ")";
                    Statement st = db_connection.createStatement();
                    System.out.println("Insert into expiremnt status: =" + st.executeQuery(query));
                    System.out.println(query);
                }
                String query = "insert into exp_summery  (total_network_time,total_network_usage,total_answer_first_time,total_asks,total_minimum_hopcount,epoch,exp_id) value (" +
                        total_network_time + "," +
                        total_network_usage + "," +
                        total_answer_first_time + "," +
                        total_asks + "," +
                        total_minimum_hopcount + "," +
                        node_epoch_time + "," +
                        getExpiremntID() + ")";
                Statement st = db_connection.createStatement();
                System.out.println("Insert into exp_summary: status: =" + st.executeQuery(query));
                System.out.println(query);
                return true;
            } catch (Exception e) {
                System.err.println("failed ot write node summary into db");
                e.printStackTrace();
            }
            return false;
        }

        private boolean writeExpiremntEntry() {
            int exp_id = getExpiremntID();
            try {
                String query = "insert into experiments (hashed,epochtime) values (\"" + getExpirment_name() + "\"," + getExpiremt_epoch_time() + ")";
                Statement st = db_connection.createStatement();
                System.out.println("Insert into expiremnt status: =" + st.executeQuery(query));
                return true;
            } catch (Exception e) {
                System.out.println("Failed to write expiremnt entry");
                e.printStackTrace();
            }
            return false;
        }

        private int getExpiremntID() {
            try {
                String query = "select id from experiments where hashed =\"" + getExpirment_name() + "\"";
                Statement st = db_connection.createStatement();
                ResultSet rs = st.executeQuery(query);
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (Exception e) {
                System.out.println("Failed to write expiremnt entry");
                e.printStackTrace();
            }
            return -1;
        }
    }
}

/**
 * // TODO: 10/21/22 Need imporve animation speed by skipping unwanteed node animations if it is out of screen
 * // Need to implement https://gist.github.com/cmcfarlen/7ca5cbb3f228c6996233
 * // Time Based Frame animation for good animation speed
 * // TODO: 11/26/22 Sessions based brodcasting to avoid rebrodcast from node if we know we alredy brodcasted from there before
 */