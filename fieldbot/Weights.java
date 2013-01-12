package fieldbot;

import battlecode.common.*;

public class Weights
{
    public static final double ENEMY_HQ     = 20.0;
    public static final double ALLY_HQ      = -1.0;

    public static final double GL_ENEMY_SD  = 2.0;
    public static final double GL_ALLY_SD   = -0.01;

    public static final double LC_ENEMY_SD  = 1.0;
    public static final double LC_ALLY_SD   = 0.7;
    public static final double LC_MUL       = 100.0;

    public static final double NEUTRAL_MINE = -.3;
    public static final double ENEMY_MINE   = -.3;
}
