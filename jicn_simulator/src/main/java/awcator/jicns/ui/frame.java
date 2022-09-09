package awcator.jicns.ui;

import awcator.jicns.alg.SimpleNode;
import awcator.jicns.alg.jicnsNodeImpl;
import awcator.jicns.meta;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Random;

public class frame extends JFrame implements ActionListener {
    //Current higligheted Node by the user
    public static int NODE_POSITION = 0;
    public static JPanel centerPanel;
    public static RightPanel rightpanel;
    //Nodes
    private static JButton[] nodes;
    private static jicnsNodeImpl[] jicnsnodes;
    JButton reset; //reset Button
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

        reset.addActionListener(this);
        randomize_nodes.addActionListener(this);

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

    private static void loadNodesUI(JPanel centerpanel, int SCREEN_WIDTH, int SCREEN_HEIGHT, boolean reset_ui_positons) {
        if (reset_ui_positons) System.out.println("ReLoading Nodes UI");
        else System.out.println("Loading Nodes UI");

        JSONObject jsondata = new JSONObject(meta.blueprint_map);
        int node_count = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_count"));
        int node_UI_width = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_ui_width"));
        int node_UI_height = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_ui_height"));
        if (!reset_ui_positons) {
            nodes = new JButton[node_count];
            jicnsnodes = new jicnsNodeImpl[node_count];
        }
        dragListener mia = null;
        if (!reset_ui_positons) mia = new dragListener(centerpanel);
        popupMenu menu = new popupMenu();
        Random random = new Random();
        for (int i = 0; i < node_count; i++) {
            if (!reset_ui_positons) {
                String prefix = (String) jsondata.getJSONObject("nodes_blueprint").get("node_prefix");
                nodes[i] = new JButton(prefix + i);
                if (jsondata.getJSONObject(prefix + i).get("type").equals("TODO")) {
                    // TODO: 9/9/22
                } else {
                    int egressSize=jsondata.getJSONObject(prefix + i).getJSONObject("egress").length();
                    jicnsnodes[i] = new SimpleNode(i,egressSize);
                    System.out.println();
                }
            }
            nodes[i].setBounds(random.nextInt(SCREEN_WIDTH - node_UI_width - 100), random.nextInt(SCREEN_HEIGHT - node_UI_height - 100), node_UI_width, node_UI_height);
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
                    if (comp.getClass().equals(JButton.class)) {
                        JButton but = (JButton) comp;
                        if (comp.getName().toLowerCase().contains(x.toLowerCase()) || comp.toString().toLowerCase().contains(x.toLowerCase())) {
                            comp.setBackground(Color.RED);
                            Timer blinkTimer = new Timer(500, new ActionListener() {
                                private final int maxCount = 4;
                                private int count = 0;
                                private boolean on = false;

                                public void actionPerformed(ActionEvent e) {
                                    if (count >= maxCount) {
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
                // TODO: 9/9/22
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
            System.out.println("Pressed");
        }

        public void mouseDragged(MouseEvent me) {

            Point dragged = me.getLocationOnScreen();
            /**
             * int x = (int)(location.x + dragged.getX() - pressed.getX());
             * int y = (int)(location.y + dragged.getY() - pressed.getY());
             */
            me.getComponent().setLocation((int) dragged.getX(), (int) dragged.getY());
            mypanel.repaint();
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
            System.out.println("Presed " + node.getName() + " " + node.getText() + " ");
            // TODO: 9/9/22
        }
    }

    static class RightPanel extends JPanel {
        static JLabel title;
        static JButton apply;

        public RightPanel() {
            GridLayout layout = new GridLayout(3, 1);
            setLayout(layout);
            title = new JLabel();
            add(title);
            add(new JLabel("Asd"));
            add(new JButton("xcz"));
            // TODO: 9/9/22
        }

        public static void applayChanges() {
            title.setText(Integer.toString(NODE_POSITION));
            // TODO: 9/9/22
        }
    }

    class freePanel extends JPanel {
        public boolean showLines = true;

        public freePanel() {
            setBackground(Color.white);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (showLines) drawConnectors(g2);
        }

        private void drawConnectors(Graphics2D g2) {
            Rectangle r1, r2;
            double x1, y1, x2, y2;
            Component[] c = getComponents();
            for (int i = 0; i < c.length; i++) {
                r1 = c[i].getBounds();
                x1 = r1.getCenterX();
                y1 = r1.getCenterY();
                for (int j = 0; j < c.length; j++) {
                    if (j == i) continue;
                    r2 = c[j].getBounds();
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
    }
}
