package team216;

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

    static final double MACRO_RALLY_GROUP_SIZE = 8.0;
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

    static final double LAY_MINE = 0.4;
    static final double NEARBY_MINES = 0.05;

    // Artillery
    // 1.5 the amount of energon a soldier has
    static final double ENEMY_HQ_TARGET_VALUE = 60;

    //capture stuff
    static final double ARTILLERY = 0.1;
    static final double MEDBAY = 0.9;
    static final double MIN_SHIELD_MAPSIZE = 150.00;

    static final double SHIELDS_IMPORTANCE = 10.00;
    static final double SUPPLIER_COEF = 0.6;
    public static final double SOLDIER_VAL = 1.05;
    public static final double MIL_CAMP_VAL = 2;
    public static final double MILITARY_DROP = 0.4;
    public static final double MIL_MAPSIZE = 0.01;
    public static final double STRAT_RATIO = 3.;
    public static final double DEF_RATIO = 5.;

    //HQ Weights
    // static final double 

}