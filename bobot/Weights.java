package bobot;

public class Weights
{
    static final double DROPOFF = 0.5;

    static final double MICRO_COMBAT_STRIKE = 0.5;
    static final double MICRO_COMBAT_BREAK  = -1.0;
    // These 2 are probably useless since they won't interact with any other
    // values.
    static final double MICRO_COMBAT_OFFSET = 3.0;
    static final double MICRO_COMBAT_MUL    = 2.0;

    static final double MICRO_FL_FIRST_STRIKE  = 3.0;
    static final double MICRO_FL_SECOND_STRIKE = 2.0;
    static final double MICRO_FL_CHARGE_THRESH = 2.0;
    static final double MICRO_FL_CHARGE_BASES  = 10.0;
    static final double MICRO_FL_RETREAT       = 0.5;
    static final double MICRO_FL_CLOSE_IN      = 1.0;
    static final double MICRO_FL_ALLIES        = 0.5;
    static final double MICRO_FL_MINES         = -2.0;
    static final double MICRO_FL_ENEMY_HQ      = 2.0;

    static final double MINE_GTFO = 3.0;

    static final double MACRO_RALLY_GROUP_SIZE = 10.0;
    static final double MACRO_RALLY_POINT = 3.0;
    static final double MACRO_RALLY_MY_GROUP = 1.0;
    static final double MACRO_RALLY_OTHER_GROUP = -1.0;
    static final double MACRO_RALLY_HQ = -20.0;
    static final double MACRO_CHARGE_GROUP_SIZE = 2.0;
    static final double MACRO_CHARGE_HQ = 40.0;
    static final double MACRO_CHARGE_ENEMIES = 5.0;
    static final double MACRO_CHARGE_MY_GROUP = 0.5;
    static final double MACRO_CHARGE_OTHER_GROUP = -0.3;
    static final double MACRO_CHARGE_MINES = -0.3;

    static final double MACRO_GET_CAMPS = 25;

    static final double LAY_MINE = 0.5;
    static final double NEARBY_MINES = 0.05;


    //capture stuff
    static final double ARTILLERY = 0.5;
    static final double MEDBAY = 0.5;
    public static final double SOLDIER_VAL = 1.05;
    public static final double MIL_CAMP_VAL = 1.5;
    public static final double MILITARY_DROP = 0.3;
    public static final double MIL_MAPSIZE = 0.01;
    public static final double STRAT_RATIO = 0.8;
    public static final double DEF_RATIO = 1-STRAT_RATIO;

}