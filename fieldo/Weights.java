package fieldo;

import battlecode.common.*;

/** Every value in this class that is used for training should be a double and
    not depend on another weight. In otherwords, it should follow this pattern:

    double NAME_IN_CAPS = -0.0;

    where the 0s can be changed by an arbitrary number of decimals. Anything
    else won't be picked up by the parser and you'll have to go modify the
    regex. Trust me, you don't want that so just stick to the pattern and we'll
    all be happier.

 */
public class Weights
{
    public static final int SHORT_WINDOW    = 1;
    public static final int MEDIUM_WINDOW   = 2;
    public static final int LONG_WINDOW     = 3;

    public static final double MAPSIZE_S    = 50;
    public static final double MAPSIZE_M    = 120;
    public static final double MAPSIZE_L    = 9999;

    public static final double DROPOFF      = 0.5;

    public static final double BATTLE_MED   = 100;
    public static final double BATTLE_ARTIL = 120;
    public static final double BATTLE_SOLDI = 40;

    public static final double ENEMY_HQ     = 25.0;
    public static final double PANIC_HQ     = 100.0;
    public static final double ALLY_HQ      = -1.0;

    public static final double GL_ENEMY_SD  =  2.0;
    public static final double GL_ALLY_SD   = -0.01;

    public static final double LC_ENEMY_SD  =   1.0;
    public static final double LC_ALLY_SD   =   0.7;
    public static final double LC_MUL       = 100.0;

    public static final double EXPLORE_MINE = -0.3;
    public static final double BATTLE_MINE  = -0.5;
    public static final double CAPTURE      = 25.0;
    public static final double HEAL         = 10.0; // TODO: change heal & shield logic
    public static final double SHIELD       = 10.0; 

    public static final double MIN_CAPT_POW = 15.0;

    public static final double GROUP_ATTACK = 2.0;
    public static final double GROUP_UP     = 2.0;

    public static final double SOLDIER_VAL  = 1.05;
    public static final double MIL_CAMP_VAL = 2.5;
    public static final double MILITARY_DROP= 0.45;
    public static final double MIL_MAPSIZE  = 0.006;
    public static final double ARTILLERY    = 1;
    public static final double MEDBAY       = 1;

    public static final double LAY_MINE     = 0.002;
    public static final double NEARBY_MINE  = 0.0007;

    public static final double DEF_RATIO    = 4;
    public static final double STRAT_RATIO  = 3;

    public static final double ATTACK_CAMP  = 10.0;
    public static final double MAX_DPS      = 10.0;

    // public static final double SUPPLIER_SUM  = SUPPLIER;
    // public static final double GENERATOR_SUM = GENERATOR + SUPPLIER_SUM;


}
