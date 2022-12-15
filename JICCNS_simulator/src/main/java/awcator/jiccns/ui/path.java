package awcator.jiccns.ui;

import java.awt.*;

public class path {
    public String pa;
    public path parent;
    public int ms;
    public int focusedNode = 0;
    public int destinationNode = -1;
    /**
     * currentDataPointX=-1 symbolizes data point (in Animation) is not set or it is about to travel from parent towards focusNode
     * currentDataPointX=80 symbolizes data point currently at Xaxsis 80 and it is moving towards focusNode from parent Node
     * <p>
     * Similarly for Yaxsis
     */
    double currentDataPointX = -1;
    double currentDataPointY = -1;
    Color pathColor;
    boolean forward = true;
    String backtrack = "";
    String actual_query = "";
    String actual_query_answer = "";

    public path(String p, int mss, path par, int fc, Color c, int des, String tracePath, boolean isForward, String q, String qa) {
        pa = p;
        ms = mss;
        parent = par;
        focusedNode = fc;
        pathColor = c;
        destinationNode = des;
        backtrack = tracePath;
        forward = isForward;
        actual_query = q;
        actual_query_answer = qa;
    }

    @Override
    public String toString() {
        return pa;
    }
}