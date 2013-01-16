package team216;

import battlecode.common.*;

/**
 *
 */
public class Utils
{

    public static final Direction dirByOrd[] = Direction.values();
    public static final int dirOrdMask = (8 - 1);

    public static int ceilDiv(int a, int b)
    {
        if (b == 0) return 0;
        return (a - 1) / b + 1;
    }
}