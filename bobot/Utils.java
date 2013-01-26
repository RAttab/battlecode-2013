package bobot;

import battlecode.common.*;

public class Utils
{

    public static final Direction dirByOrd[] = Direction.values();

    public static int ceilDiv(int a, int b)
    {
        if (b == 0) return 1;
        return (a - 1) / b + 1;
    }

}
