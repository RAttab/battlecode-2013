package %(team)s;

import battlecode.common.*;

/** Every value in this class that is used for training should be a double and
    not depend on another weight. In otherwords, it should follow this pattern:

    double NAME_IN_CAPS = %(NAME_IN_CAPS)f;

    where the 0s can be changed by an arbitrary number of decimals. Anything
    else won't be picked up by the parser and you'll have to go modify the
    regex. Trust me, you don't want that so just stick to the pattern and we'll
    all be happier.

 */
public class Weights
{
    public static final int SHORT_WINDOW = %(SHORT_WINDOW)i;
    public static final int MEDIUM_WINDOW = %(MEDIUM_WINDOW)i;
    public static final int LONG_WINDOW = %(LONG_WINDOW)i;
    public static final double MAPSIZE_S = %(MAPSIZE_S)f;
    public static final double MAPSIZE_M = %(MAPSIZE_M)f;
    public static final double MAPSIZE_L = %(MAPSIZE_L)f;

    public static final double DROPOFF = %(DROPOFF)f;

    public static final double BATTLE_MED = %(BATTLE_MED)f;
    public static final double BATTLE_ARTIL = %(BATTLE_ARTIL)f;
    public static final double BATTLE_SOLDI = %(BATTLE_SOLDI)f;

    public static final double ENEMY_HQ = %(ENEMY_HQ)f;
    public static final double ALLY_HQ = %(ALLY_HQ)f;

    public static final double GL_ENEMY_SD = %(GL_ENEMY_SD)f;
    public static final double GL_ALLY_SD = %(GL_ALLY_SD)f;

    public static final double LC_ENEMY_SD = %(LC_ENEMY_SD)f;
    public static final double LC_ALLY_SD = %(LC_ALLY_SD)f;
    public static final double LC_MUL = %(LC_MUL)f;

    public static final double EXPLORE_MINE = %(EXPLORE_MINE)f;
    public static final double BATTLE_MINE = %(BATTLE_MINE)f;
    public static final double CAPTURE = %(CAPTURE)f;
    public static final double HEAL = %(HEAL)f;
    public static final double SHIELD = %(SHIELD)f;

    public static final double MIN_CAPT_POW = %(MIN_CAPT_POW)f;

    public static final double GROUP_ATTACK = %(GROUP_ATTACK)f;
    public static final double GROUP_UP = %(GROUP_UP)f;

    public static final double STRAT_CAMP = %(STRAT_CAMP)f;
    public static final double DEF_CAMP     = 1 - STRAT_CAMP;

    public static final double MILITARY = %(MILITARY)f;
    public static final double STRUCTURAL   = 1 - MILITARY;

    public static final double MEDBAY = %(MEDBAY)f;
    public static final double SHIELDS = %(SHIELDS)f;
    public static final double ARTILLERY = %(ARTILLERY)f;

    public static final double MIN_POWER = %(MIN_POWER)f;
    public static final double MAX_POWER = %(MAX_POWER)f;
    public static final double OURBASE_MULT = %(OURBASE_MULT)f;
    public static final double NEUTBASE_MULT = %(NEUTBASE_MULT)f;
    public static final double MIN_ROUND = %(MIN_ROUND)f;

    public static final double MEDBAY_SUM    = MEDBAY;
    public static final double SHIELDS_SUM   = SHIELDS   + MEDBAY_SUM;
    public static final double ARTILLERY_SUM = ARTILLERY + SHIELDS_SUM;

    public static final double LAY_MINE = %(LAY_MINE)f;
    public static final double NEARBY_MINE = %(NEARBY_MINE)f;

    public static final double DEF_RATIO = %(DEF_RATIO)f;
    public static final double STRAT_RATIO = %(STRAT_RATIO)f;

    // public static final double SUPPLIER_SUM  = SUPPLIER;
    // public static final double GENERATOR_SUM = GENERATOR + SUPPLIER_SUM;


}
