package team216;

import battlecode.common.*;

public class Weights
{
    public static final double DROPOFF      = 0.4;

    public static final double BATTLE_MED   = 100;
    public static final double BATTLE_ARTIL = 120;
    public static final double BATTLE_SOLDI = 40;

    public static final double ENEMY_HQ     = 20.0;
    public static final double ALLY_HQ      = -1.0;

    public static final double GL_ENEMY_SD  =  2.0;
    public static final double GL_ALLY_SD   = -0.01;

    public static final double LC_ENEMY_SD  =   1.0;
    public static final double LC_ALLY_SD   =   0.7;
    public static final double LC_MUL       = 100.0;

    public static final double EXPLORE_MINE = -0.3;
    public static final double BATTLE_MINE  = -0.5;
    public static final double CAPTURE      =  1.0;
    public static final double HEAL         = 10.0;
    public static final double SHIELD       = 10.0;

    public static final double MIN_CAPT_POW = 15.0;

    public static final double GROUP_ATTACK = 2.0;
    public static final double GROUP_UP     = 2.0;

    public static final double PATH         = 0.7;
    public static final double TO_HQ        = 1 - PATH;

    public static final double MILITARY     = 0.8;
    public static final double STRUCTURAL   = 1 - MILITARY;

    public static final double MEDBAY       = 0.1;
    public static final double SHIELDS      = 0.1;
    public static final double ARTILLERY    = 0.8;

    public static final double MIN_POWER    = 16;
    public static final double MAX_POWER    = 60;
    public static final double OURBASE_MULT = 10;
    public static final double NEUTBASE_MULT= 2;
    public static final double MIN_ROUND    = 20;

    public static final double MEDBAY_SUM    = MEDBAY;
    public static final double SHIELDS_SUM   = SHIELDS   + MEDBAY_SUM;
    public static final double ARTILLERY_SUM = ARTILLERY + SHIELDS_SUM;

    // public static final double SUPPLIER_SUM  = SUPPLIER;
    // public static final double GENERATOR_SUM = GENERATOR + SUPPLIER_SUM;


}
