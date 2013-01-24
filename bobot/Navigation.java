package bobot;

import battlecode.common.*;

public class Navigation
{
    double directions[] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
    double standStill = 0.0;

    double defuse = 0.0;
    MapLocation defuseLoc;

    boolean autoDefuse = true;

    RobotController rc;


    Navigation(RobotController rc)
    {
        this.rc = rc;
    }


    void boostDefuse(MapLocation loc, double force)
    {
        if (force <= defuse) return;

        defuse = force;
        defuseLoc = loc;
    }

    void resetDefuse()
    {
        defuse = 0.0;
        defuseLoc = null;
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

    private boolean hasMine(MapLocation loc)
        throws GameActionException
    {
        Team mine = rc.senseMine(defuseLoc);
        return mine == Team.NEUTRAL || mine == rc.getTeam().opponent();
    }

    boolean move() throws GameActionException
    {
        if (standStill == Double.POSITIVE_INFINITY)
            return false;

        double max = standStill;
        Direction dir = null;
        MapLocation myLoc = rc.getLocation();

        for (int i = 0; i < 8; ++i) {
            if (max > directions[i]) continue;
            if (!rc.canMove(Utils.dirByOrd[i])) continue;
            if (!autoDefuse && hasMine(myLoc.add(dir))) continue;

            max = directions[i];
            dir = Utils.dirByOrd[i];
        }

        if (autoDefuse) {
            defuseLoc = rc.getLocation().add(dir);
            defuse = Double.POSITIVE_INFINITY;
        }

        if (defuseLoc != null && defuse > max && hasMine(defuseLoc)) {
            rc.defuseMine(defuseLoc);
            return true;
        }

        rc.move(dir);
        return true;
    }
}