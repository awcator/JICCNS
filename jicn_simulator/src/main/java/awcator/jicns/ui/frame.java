package awcator.jicns.ui;

import awcator.jicns.alg.SimpleNode;
import awcator.jicns.alg.jicnsNodeImpl;
import awcator.jicns.meta;
import org.json.JSONObject;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.*;

import static java.lang.Thread.sleep;

public class frame extends JFrame implements ActionListener {
    //Current higligheted Node by the user
    public static int NODE_POSITION = 0;
    public static JPanel centerPanel;
    public static RightPanel rightpanel;
    //Nodes
    private static NodeUI[] nodes;
    private static jicnsNodeImpl[] jicnsnodes;
    JButton reset; //reset Button
    JButton broadCastTo;//Dummy button for testing broadcasting
    JButton randomize_nodes; //node positons randomizer
    JTextField searchNodes;// A simple textbox to search nodes in UI

    public frame() {

        /**
         * Main Frame UI
         * set size of the window to parent size
         */
        setTitle(meta.JICNS_version);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());

        //load up buttons
        reset = new JButton("reset");
        randomize_nodes = new JButton("randomize_nodes");
        broadCastTo = new JButton("broadCastTo to Destination");

        reset.addActionListener(this);
        randomize_nodes.addActionListener(this);
        broadCastTo.addActionListener(this);

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
        southpanel.add(reset);
        southpanel.add(randomize_nodes);
        southpanel.add(searchNodes);
        southpanel.add(broadCastTo);
        southpanel.setBackground(Color.darkGray);
        add(southpanel, BorderLayout.SOUTH);

        /**
         * SetUP CenterPanel: Nodes UI
         * Force the panel to free layout
         */
        centerPanel = new freePanel();
        centerPanel.setLayout(null);
        loadNodesUI(centerPanel, getWidth(), getHeight(), false);
        add(centerPanel, BorderLayout.CENTER);

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
    }

    public static Color getRandomColor() {
        Random r = new Random();
        return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
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
            jicnsnodes = new jicnsNodeImpl[node_count];
        }
        dragListener mia = null;
        if (!reset_ui_positons) mia = new dragListener(centerpanel);
        popupMenu menu = new popupMenu();
        Random random = new Random();
        for (int i = 0; i < node_count; i++) {
            if (!reset_ui_positons) {
                String prefix = (String) jsondata.getJSONObject("nodes_blueprint").get("node_prefix");
                if (!jsondata.isNull(prefix + i) && jsondata.getJSONObject(prefix + i).get("type").equals("TODO")) {
                    // TODO: 9/9/22
                } else {
                    int egressSize = 0;
                    if (jsondata.isNull(prefix + i) || jsondata.getJSONObject(prefix + i).isNull("egress")) {
                        System.out.println("Found no egres etry for " + prefix + i);
                        egressSize = 0;
                    } else {
                        egressSize = jsondata.getJSONObject(prefix + i).getJSONObject("egress").length();
                    }
                    jicnsnodes[i] = new SimpleNode(i, egressSize);
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
                            jicnsnodes[i].egress[k][0] = Integer.parseInt(str.replace(prefix, ""));
                            jicnsnodes[i].egress[k][1] = latency;
                            k++;
                        }
                    }
                    /**
                     * node memory settings
                     */
                    if (!jsondata.isNull(prefix + i) && !jsondata.getJSONObject(prefix + i).isNull("max_cache_size")) {
                        jicnsnodes[i].cacheMemorySize = Integer.parseInt((String) jsondata.getJSONObject(prefix + i).get("max_cache_size"));
                        jicnsnodes[i].allocateCacheMemorySize();
                    } else {
                        //allocate default size
                        jicnsnodes[i].allocateCacheMemorySize();
                    }
                    if (!jsondata.isNull(prefix + i) && !jsondata.getJSONObject(prefix + i).isNull("max_payload_size")) {
                        jicnsnodes[i].LocalPayloadSize = Integer.parseInt((String) jsondata.getJSONObject(prefix + i).get("max_payload_size"));
                        jicnsnodes[i].allocatePayloadMemorySize();
                    } else {
                        //allocate default size
                        jicnsnodes[i].allocatePayloadMemorySize();
                    }
                    /**
                     * Load up node memory from blueprint if specified
                     */
                    if (!jsondata.isNull(prefix + i) && !jsondata.getJSONObject(prefix + i).isNull("payload")) {
                        for (Iterator<String> it = jsondata.getJSONObject(prefix + i).getJSONObject("payload").keys(); it.hasNext(); ) {
                            String str = it.next();
                            jicnsnodes[i].addToPayloadMemory(str, (String) jsondata.getJSONObject(prefix + i).getJSONObject("payload").get(str));
                        }
                    }
                    /**
                     * Load up node cachememory from blueprint if specified
                     */
                    if (!jsondata.isNull(prefix + i) && !jsondata.getJSONObject(prefix + i).isNull("cached")) {
                        for (Iterator<String> it = jsondata.getJSONObject(prefix + i).getJSONObject("cached").keys(); it.hasNext(); ) {
                            String str = it.next();
                            System.out.println(str + " <-----This entry should be added to cache");
                            jicnsnodes[i].addToCacheMemory(str, (String) jsondata.getJSONObject(prefix + i).getJSONObject("cached").get(str));
                        }
                    }
                    nodes[i] = new NodeUI(prefix + i, jicnsnodes[i]);
                }
            }
            nodes[i].setBounds(random.nextInt(SCREEN_WIDTH - node_UI_width - 300), random.nextInt(SCREEN_HEIGHT - node_UI_height - 100), node_UI_width, node_UI_height);
            if (!reset_ui_positons) centerpanel.add(nodes[i]);
            if (!reset_ui_positons) {
                nodes[i].addMouseListener(mia);
                nodes[i].addMouseMotionListener(mia);
                nodes[i].setComponentPopupMenu(menu);
                nodes[i].setName(Integer.toString(i));
            }
        }
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
                            Timer blinkTimer = new Timer(500, new ActionListener() {
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

                Thread rlMF = new Thread(new Runnable() {
                    double dataPointSpeed = 40;

                    public void drawMultiDataPaths(java.util.List<path> pathsToDsiplayAtAtime, Graphics2D g2) throws Exception {
                        while (!pathsToDsiplayAtAtime.isEmpty()) {
                            //Incremnt all the nodes X axsis datapoint travel by 30 points and repaint the graphics after ploting
                            //and then remove the datapoint from storageMermory once it reaches destination
                            for (int i = 0; i < pathsToDsiplayAtAtime.size(); i++) {
                                path displayPath = pathsToDsiplayAtAtime.get(i);
                                double sourceX, sourceY, destinationX, destinationY;

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
                                if (diffY == 0)
                                    diffY = 1;
                                double slope = diffY / diffX;
                                if (Math.abs(diffY) > Math.abs(diffX)) {
                                    moveInYdirection = true;
                                } else {
                                    moveInYdirection = false;
                                }
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
                                                nodes[displayPath.focusedNode].jicnsNode.onIncomingReqData();
                                            }
                                        } else {
                                            displayPath.currentDataPointY += dataPointSpeed;
                                            displayPath.currentDataPointX = (int) ((displayPath.currentDataPointY - sourceY) / slope + sourceX);
                                            if (displayPath.currentDataPointY >= destinationY) {
                                                pathsToDsiplayAtAtime.remove(i);
                                                nodes[displayPath.focusedNode].jicnsNode.onIncomingReqData();
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
                                                nodes[displayPath.focusedNode].jicnsNode.onIncomingReqData();
                                            }
                                        } else {
                                            displayPath.currentDataPointX += dataPointSpeed;
                                            displayPath.currentDataPointY = (int) (slope * (displayPath.currentDataPointX - sourceX) + sourceY);
                                            if (displayPath.currentDataPointX >= destinationX) {
                                                pathsToDsiplayAtAtime.remove(i);
                                                nodes[displayPath.focusedNode].jicnsNode.onIncomingReqData();
                                            }
                                        }
                                    }
                                    //}
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            sleep(100);
                            //keep traces? remove repaint line
                            ((freePanel) centerPanel).repaint();
                        }
                    }

                    public void run() {
                        try {
                            Graphics2D g2 = (Graphics2D) centerPanel.getGraphics();
                            System.out.println("Broadcasting from node 3 to 5");
                            path rootparent = new path("node3", 0, null, 3, getRandomColor()); //start from node4 with inital timeout of 4ms
                            class path_sorter implements Comparator<path> {
                                @Override
                                public int compare(path s1, path s2) {
                                    if (s1.ms > s2.ms) return 1;
                                    else if (s1.ms < s2.ms) return -1;
                                    return 0;
                                }
                            }
                            PriorityQueue<path> pq = new PriorityQueue<path>(new path_sorter());
                            path temppath = rootparent;
                            pq.add(temppath);
                            int previous_ms = temppath.ms;
                            boolean expectNewMs = false;
                            boolean check_previous_ms = true;
                            java.util.List<path> pathsToDsiplayAtAtime = new ArrayList<path>();
                            boolean drawAnime = false;
                            boolean parallelPaths = false;
                            while (!pq.isEmpty()) {
                                //To see how fast the queues grows/ or the edges grows n(n-1) edges can be drawn using n vertex
                                //System.out.println("SiZE "+pq.size());
                                temppath = pq.poll();
                                //prints time digaram
                                System.out.print("\n" + temppath.ms + " " + temppath);
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
                                if (foucusedNode == 5) {
                                    System.out.print("[END]");
                                    continue;
                                } else {
                                    for (int i = 0; i < nodes.length; i++) {
                                        if (i != foucusedNode && nodes[foucusedNode].jicnsNode.isMyNeibhour(i)) {
                                            if (nodes[foucusedNode].jicnsNode.allowCycles()) {
                                                path newpath = new path(temppath.pa + "-->node" + i, temppath.ms + nodes[foucusedNode].jicnsNode.getMsToReachNode(i), temppath, i, getRandomColor());
                                                pq.add(newpath);
                                            } else {
                                                if (!temppath.pa.contains("node" + i)) {
                                                    path newpath = new path(temppath.pa + "-->node" + i, temppath.ms + nodes[foucusedNode].jicnsNode.getMsToReachNode(i), temppath, i, getRandomColor());
                                                    pq.add(newpath);
                                                }
                                            }
                                        }
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                rlMF.start();
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
        static JLabel title;
        static JButton apply;
        static JTable table, table2;
        static DefaultTableModel payloadTableModel;
        static DefaultTableModel cacheTableModel;

        public RightPanel() {
            GridLayout layout = new GridLayout(2, 2);
            setLayout(layout);
            title = new JLabel();
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
            add(title);
            add(scrollPane2);
            add(new JButton("dsada"));
            add(scrollPane1);
        }

        public static void applayChanges() {
            title.setText(Integer.toString(NODE_POSITION));
            payloadTableModel.setRowCount(0);

            String[][] x = jicnsnodes[NODE_POSITION].getPayloadContents();
            if (x != null) {
                for (int i = 0; i < x.length; i++) {
                    payloadTableModel.addRow(new Object[]{i, x[i][0], x[i][1]});
                }
            }
            cacheTableModel.setRowCount(0);
            x = jicnsnodes[NODE_POSITION].getCacheContents();
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

    static class path {
        public String pa;
        public path parent;
        public int ms;
        public int focusedNode = 0;

        /**
         * currentDataPointX=-1 symbolizes data point (in Animation) is not set or it is about to travel from parent towards focusNode
         * currentDataPointX=80 symbolizes data point currently at Xaxsis 80 and it is moving towards focusNode from parent Node
         * <p>
         * Similarly for Yaxsis
         */
        double currentDataPointX = -1;
        double currentDataPointY = -1;
        Color pathColor;

        public path(String p, int mss, path par, int fc, Color c) {
            pa = p;
            ms = mss;
            parent = par;
            focusedNode = fc;
            pathColor = c;
        }

        @Override
        public String toString() {
            return pa;
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
            if (showLines) drawConnectors(g2);
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
                for (int j = 0; j < c[i].jicnsNode.egress.length; j++) {
                    r2 = c[c[i].jicnsNode.egress[j][0]].getBounds();
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

    class NodeUI extends JButton {
        jicnsNodeImpl jicnsNode;

        public NodeUI(String x, jicnsNodeImpl jicnsNode) {
            this.jicnsNode = jicnsNode;
            setText(x);
        }
    }
}

/**
 * // TODO: 10/21/22 Need imporve animation speed by skipping unwanteed node animations if it is out of screen
 * // Need to implement https://gist.github.com/cmcfarlen/7ca5cbb3f228c6996233
 * // Time Based Frame animation for good animation speed
 */