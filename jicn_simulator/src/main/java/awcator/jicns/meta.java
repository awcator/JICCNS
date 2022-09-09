package awcator.jicns;

public final class meta {
    public static final String JICNS_version = "JICNS_202209";
    public static String blueprint_map = "{}";

    public static String loadBluePrint() {
        return "{\"nodes_blueprint\":{\"node_count\":\"6\",\"node_prefix\":\"node\",\"node_ui_width\":\"100\",\"node_ui_height\":\"50\"},\"frame_blueprint\":{\"width\":\"1000\",\"height\":\"2000\"},\"node0\":{\"type\":\"simplenode\",\"egress\":{\"node1\":\"5ms\",\"node2\":\"2ms\"}},\"node1\":{\"type\":\"simplenode\",\"egress\":{\"node4\":\"2ms\"}},\"node4\":{\"type\":\"simplenode\",\"egress\":{\"node5\":\"8ms\",\"node2\":\"1ms\"}},\"node5\":{\"type\":\"simplenode\",\"egress\":{\"node3\":\"1ms\"}},\"node3\":{\"type\":\"simplenode\",\"egress\":{\"node3\":\"5ms\",\"node0\":\"4ms\"}},\"node2\":{\"type\":\"simplenode\",\"egress\":{\"node5\":\"1ms\"}}}\n";
    }
}
