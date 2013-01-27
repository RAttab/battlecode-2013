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
    static final double MACRO_RALLY_HQ = -200.0;
    static final double MACRO_CHARGE_GROUP_SIZE = 2.0;
    static final double MACRO_CHARGE_HQ = 400.0;
    static final double MACRO_CHARGE_ENEMIES = 5.0;
    static final double MACRO_CHARGE_MY_GROUP = 0.3;
    static final double MACRO_CHARGE_OTHER_GROUP = -1.0;
    static final double MACRO_CHARGE_MINES = -0.1;
    
}