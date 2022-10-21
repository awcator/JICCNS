package awcator.jicns;

import java.nio.file.Files;
import java.nio.file.Path;

public final class meta {
    public static final String JICNS_version = "JICNS_202209";
    public static String blueprint_map = "{}";

    public static String loadBluePrint()throws Exception {
        return loadBluePrintFromFile("/home/Awcator/work/JICNS/simulation_blueprint.json2");
        //return "{\"nodes_blueprint\":{\"node_count\":\"7\",\"node_prefix\":\"node\",\"node_ui_width\":\"100\",\"node_ui_height\":\"50\"},\"frame_blueprint\":{\"width\":\"1000\",\"height\":\"2000\"},\"node0\":{\"max_cache_size\":\"5\",\"max_payload_size\":\"10\",\"payload\":{\"temprature_at_bangalore\":\"25c\",\"pm\":\"modi\",\"cm\":\"bommai\",\"avenger\":\"hulk\"},\"cached\":{\"fastestcar\":\"koinseg agera\"},\"type\":\"simplenode\",\"egress\":{\"node1\":\"5ms\",\"node2\":\"2ms\"}},\"node1\":{\"max_cache_size\":\"4\",\"max_payload_size\":\"10\",\"type\":\"simplenode\",\"egress\":{\"node4\":\"2ms\"},\"payload\":{\"pm\":\"modi\",\"cm\":\"bommai\",\"avenger\":\"hulk\"},\"cached\":{\"temprature_at_bangalore\":\"10c\"}},\"node4\":{\"max_cache_size\":\"3\",\"max_payload_size\":\"6\",\"payload\":{\"temprature_at_chennai\":\"35c\",\"supercell_game\":\"clash_of_clans\"},\"cached\":{\"richest_man\":\"elon musk\"},\"type\":\"simplenode\",\"egress\":{\"node5\":\"8ms\",\"node2\":\"1ms\"}},\"node5\":{\"max_cache_size\":\"10\",\"max_payload_size\":\"100\",\"payload\":{\"error_code\":\"404\",\"health_of_node5\":\"85/100\"},\"type\":\"simplenode\",\"egress\":{\"node3\":\"1ms\"}},\"node3\":{\"type\":\"simplenode\",\"egress\":{\"node3\":\"5ms\",\"node0\":\"4ms\"}},\"node2\":{\"cached\":{\"health_of_node5\":\"85/100\"},\"type\":\"simplenode\",\"egress\":{\"node5\":\"1ms\"}}}";
    }

    public static String loadBluePrintFromFile(String path)throws Exception{
        System.out.println("Loading Blueprint from "+path);
        return Files.readString(Path.of(path));
    }
}
