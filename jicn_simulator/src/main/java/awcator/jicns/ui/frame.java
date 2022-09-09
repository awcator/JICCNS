package awcator.jicns.ui;

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
    JButton reset; //reset Button
    JButton randomize_nodes; //node positons randomizer
    public static JPanel centerPanel;
    //Nodes
    private static JButton nodes[];

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
        southpanel.setSize(getWidth(), (int) screenSize.getHeight() / 20);
        southpanel.add(reset);
        southpanel.add(randomize_nodes);
        southpanel.setBackground(Color.darkGray);
        add(southpanel, bl.SOUTH);

        /**
         * SetUP CenterPanel: Nodes UI
         * Force the panel to free layout
         */
        JPanel centerPanel = new freePanel();
        centerPanel.setLayout(null);
        this.loadNodesUI(centerPanel, getWidth(), getHeight(), false);
        add(centerPanel, bl.CENTER);

        /**
         * Back to Main frame container UI
         */
        setVisible(true);
        //SystemExit on frame close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == reset) {
            System.out.println("Reset button clicked");
            // TODO: 9/9/22
        }
        if (actionEvent.getSource() == randomize_nodes) {
            System.out.println("Randomzing the nodes");
            loadNodesUI(centerPanel, getWidth(), getHeight(), true);
            // TODO: 9/9/22
        }
    }

    private static void loadNodesUI(JPanel centerpanel, int SCREEN_WIDTH, int SCREEN_HEIGHT, boolean reset_ui_positons) {
        if (reset_ui_positons) System.out.println("ReLoading Nodes UI");
        else System.out.println("Loading Nodes UI");

        JSONObject jsondata = new JSONObject(meta.blueprint_map);
        int node_count = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_count"));
        int node_UI_width = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_ui_width"));
        int node_UI_height = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_ui_height"));
        if (!reset_ui_positons) nodes = new JButton[node_count];
        dragListener mia = null;
        if (!reset_ui_positons) mia = new dragListener(centerpanel);

        Random random = new Random();
        for (int i = 0; i < node_count; i++) {
            if (!reset_ui_positons)
                nodes[i] = new JButton(jsondata.getJSONObject("nodes_blueprint").get("node_prefix") + "" + i);
            nodes[i].setBounds(random.nextInt(SCREEN_WIDTH - node_UI_width - 100), random.nextInt(SCREEN_HEIGHT - node_UI_height - 100), node_UI_width, node_UI_height);
            if (!reset_ui_positons) centerpanel.add(nodes[i]);
            if (!reset_ui_positons) {
                nodes[i].addMouseListener(mia);
                nodes[i].addMouseMotionListener(mia);
            }
        }
        System.out.println("\tDone");
    }

    class freePanel extends JPanel {
        public boolean showLines = true;

        public freePanel() {
            setBackground(Color.white);

        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            if (showLines)
                drawConnectors(g2);
        }

        private void drawConnectors(Graphics2D g2) {
            Rectangle r1, r2;
            double x1, y1, x2, y2;
            Component[] c = getComponents();
            for (int i = 0; i < c.length; i++) {
                r1 = c[i].getBounds();
                x1 = r1.getCenterX();
                y1 = r1.getCenterY();
                for (int j = i + 1; j < c.length; j++) {
                    r2 = c[j].getBounds();
                    x2 = r2.getCenterX();
                    y2 = r2.getCenterY();
                    g2.draw(new Line2D.Double(x1, y1, x2, y2));
                }
            }
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
            System.out.println("Dragiingin");
            Point dragged = me.getLocationOnScreen();
            /**
             * int x = (int)(location.x + dragged.getX() - pressed.getX());
             * int y = (int)(location.y + dragged.getY() - pressed.getY());
             */
            me.getComponent().setLocation((int) dragged.getX(), (int) dragged.getY());
            mypanel.repaint();
        }
    }
}
