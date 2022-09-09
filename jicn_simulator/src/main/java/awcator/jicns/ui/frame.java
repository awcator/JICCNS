package awcator.jicns.ui;

import awcator.jicns.meta;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class frame extends JFrame implements ActionListener {
    JButton reset; //reset Button
    JButton randomize_nodes; //node positons randomizer

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
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        this.loadNodesUI(centerPanel, getWidth(), getHeight());
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
            // TODO: 9/9/22
        }
    }

    private static void loadNodesUI(JPanel centerpanel, int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        System.out.println("Loading Nodes UI");
        JSONObject jsondata = new JSONObject(meta.blueprint_map);
        int node_count = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_count"));
        int node_UI_width = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_count"));
        int node_UI_height = Integer.parseInt((String) jsondata.getJSONObject("nodes_blueprint").get("node_count"));
        nodes=new JButton[node_count];
        Random random = new Random();
        for (int i = 0; i < node_count; i++) {
            nodes[i] = new JButton(jsondata.getJSONObject("nodes_blueprint").get("node_prefix") + "" + i);
            nodes[i].setBounds(random.nextInt(SCREEN_WIDTH-100), random.nextInt(SCREEN_HEIGHT), 100, 50);
            centerpanel.add(nodes[i]);
        }
        System.out.println("\tDone");
    }
}
