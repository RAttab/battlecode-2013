package bobot;

import battlecode.common.*;

public class Navigation
{
    double directions[] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
    double standStill = 0.0;
    boolean canDefuse = true;

    RobotController rc;


    Navigation(RobotController rc)
    {
        this.rc = rc;
    }

    void boost(Direction dir, double force)
    {
        if (dir == Direction.OMNI) {
            standStill += force;
            return;
        }

        int ord = dir.ordinal();
        directions[ord] += force;

        force *= Weights.DROPOFF;
        directions[((ord - 1) & 7)] += force;
        directions[((ord + 1) & 7)] += force;

        // force *= Weights.DROPOFF;
        // directions[((ord-2) & 7)] += force;
        // directions[((ord+2) & 7)] += force;
    }

    void block(Direction dir)
    {
        directions[dir.ordinal()] = Double.NEGATIVE_INFINITY;
    }

    bool move()
    {
        if (standStill == Double.POSITIVE_INFINITY)
            return false;

        double max = standStill;
        Direction dir = null;

        for (int i = 0; i < 8; ++i) {
            if (max > directions[i]) continue;
            if (!rc.canMove(Utils.dirByOrd[i])) continue;

            max = directions[i];
            dir = Utils.dirByOrd[i];
        }

        if (dir == null) return false;

        if (canDefuse) {
            MapLocation pos = rc.getLocation().add(dir);
            Team team = rc.senseMine(pos);

            if (team == Team.NEUTRAL || team == rc.getTeam()) {
                rc.defuseMine(pos);
                return false;
            }
        }

        rc.move(dir);
        return true;
    }
}