package bobot;

public class Weights
{
    public static double DROPOFF = 0.5;

    public static double MICRO_COMBAT_STRIKE = 0.5;
    public static double MICRO_COMBAT_BREAK  = -1.0;
    // These 2 are probably useless since they won't interact with any other
    // values.
    public static double MICRO_COMBAT_OFFSET = 3.0;
    public static double MICRO_COMBAT_MUL    = 2.0;

    public static double MICRO_FL_FIRST_STRIKE  = 3.0;
    public static double MICRO_FL_SECOND_STRIKE = 2.0;
    public static double MICRO_FL_RETREAT       = 0.5;
    public static double MICRO_FL_CLOSE_IN      = 1.0;
    public static double MICRO_FL_ALLIES        = 1.0;

    public static double MINE_GTFO = 1.0;
}