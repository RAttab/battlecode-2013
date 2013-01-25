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
    SenseCache sense;


    Navigation(RobotController rc, SenseCache sense)
    {
        this.rc = rc;
        this.sense = sense;
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

    void boost(Direction dir, double force, boolean spread)
    {
        if (dir == Direction.OMNI) {
            standStill += force;
            return;
        }

        int ord = dir.ordinal();
        directions[ord] += force;

        if (spread) {
            force *= Weights.DROPOFF;
            directions[((ord - 1) & 7)] += force;
            directions[((ord + 1) & 7)] += force;
        }

        // force *= Weights.DROPOFF;
        // directions[((ord-2) & 7)] += force;
        // directions[((ord+2) & 7)] += force;
    }

    void block(Direction dir)
    {
        directions[dir.ordinal()] = Double.NEGATIVE_INFINITY;
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

            Direction dest = Utils.dirByOrd[i];
            if (!rc.canMove(dest)) continue;
            if (!autoDefuse && sense.nonAlliedMine(myLoc.add(dest))) continue;

            max = directions[i];
            dir = dest;
        }

        if (autoDefuse) {
            defuseLoc = rc.getLocation().add(dir);
            defuse = Double.POSITIVE_INFINITY;
        }

        if (defuseLoc != null && defuse > max && sense.nonAlliedMine(defuseLoc)) {
            rc.defuseMine(defuseLoc);
            return true;
        }

        if (dir != null) rc.move(dir);
        return true;
    }

    String debug_print() {
        String str = "{ ";
        for (int i = 0; i < directions.length; ++i)
            str += directions[i] + ", ";
        str += "} stand=" + standStill;
        return str;
    }

    void debug_dump() {
        System.out.println(debug_print());
    }
}