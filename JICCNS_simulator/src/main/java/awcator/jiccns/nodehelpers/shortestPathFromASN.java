package awcator.jiccns.nodehelpers;

import java.util.HashSet;

public class shortestPathFromASN {
    int MS = Integer.MAX_VALUE;
    String Path = "";

    public shortestPathFromASN(String x, int y) {
        MS = y;
        Path = x;
    }

    @Override
    public String toString() {
        return Path;
    }

    public HashSet<Integer> convertToset() {
        HashSet<Integer> myset = new HashSet<>();
        for (String x : Path.split("-->")) {
            if (x.trim().length() >= 1)
                myset.add(Integer.parseInt(x.trim()));
        }
        return myset;
    }
}
