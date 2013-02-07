package iter1;

import battlecode.common.*;

/** Every value in this class that is used for training should be a double and
    not depend on another weight. In otherwords, it should follow this pattern:

    double NAME_IN_CAPS = -0.000000;

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
    public static final double MAPSIZE_S = 30.000000;
    public static final double MAPSIZE_M = 10.000000;
    public static final double MAPSIZE_L = 1489.029760;

    public static final double DROPOFF = 0.369713;

    public static final double BATTLE_MED = 100.000000;
    public static final double BATTLE_ARTIL = 0.000419;
    public static final double BATTLE_SOLDI = 2.345305;

    public static final double ENEMY_HQ = 20.000000;
    public static final double PANIC_HQ = 100.000000;
    public static final double ALLY_HQ = -1.265160;

    public static final double GL_ENEMY_SD = 2.813491;
    public static final double GL_ALLY_SD = -0.009734;

    public static final double LC_ENEMY_SD = 0.228894;
    public static final double LC_ALLY_SD = 16.000000;
    public static final double LC_MUL = 10.000000;

    public static final double EXPLORE_MINE = -0.300000;
    public static final double BATTLE_MINE = 0.590963;
    public static final double CAPTURE = 20.000000;
    public static final double HEAL = 25.425396;
    public static final double SHIELD = 1.641862;

    public static final double MIN_CAPT_POW = 10.000000;

    public static final double GROUP_ATTACK = 2.000000;
    public static final double GROUP_UP = 2.007751;

    public static final double STRAT_CAMP = -0.911102;
    public static final double DEF_CAMP     = 1 - STRAT_CAMP;

    public static final double MILITARY = 0.629210;
    public static final double STRUCTURAL   = 1 - MILITARY;

    public static final double MEDBAY = 10.000000;
    public static final double SHIELDS = 0.100000;
    public static final double ARTILLERY = 15.622950;

    public static final double MIN_POWER = 0.700000;
    public static final double MAX_POWER = 10.204806;
    public static final double OURBASE_MULT = 0.893249;
    public static final double NEUTBASE_MULT = 2.000000;
    public static final double MIN_ROUND = 20.000000;

    public static final double MEDBAY_SUM    = MEDBAY;
    public static final double SHIELDS_SUM   = SHIELDS   + MEDBAY_SUM;
    public static final double ARTILLERY_SUM = ARTILLERY + SHIELDS_SUM;

    public static final double LAY_MINE = 0.001333;
    public static final double NEARBY_MINE = 120.000000;

    public static final double DEF_RATIO = 61.340250;
    public static final double STRAT_RATIO = 0.100000;

    // public static final double SUPPLIER_SUM  = SUPPLIER;
    // public static final double GENERATOR_SUM = GENERATOR + SUPPLIER_SUM;


}