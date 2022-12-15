package awcator.jiccns.ui;

import awcator.jiccns.device_strats.jicnsDeviceImpl;

import javax.swing.*;

public class NodeUI extends JButton {
    public jicnsDeviceImpl jicnsNode;

    public NodeUI(String x, jicnsDeviceImpl jicnsNode) {
        this.jicnsNode = jicnsNode;
        setText(x);
    }
}