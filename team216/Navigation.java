package team216;

import battlecode.common.*;

public class Navigation
{
    // Currently only used for mine logic. Could expand to a vector for more
    // long term info.
    static MapLocation prevLoc = null;

    double directions[] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
    double standStill = 0.0;

    boolean autoDefuse = true;
    boolean noDefuse = false;
    MapLocation defuseLoc = null;

    RobotType capture = null;
    double captureStrength = 0.0;

    double layMine = 0.0;

    RobotController rc;
    SenseCache sense;


    Navigation(RobotController rc, SenseCache sense)
    {
        this.rc = rc;
        this.sense = sense;
    }


    void defuse(MapLocation loc)
    {
        defuseLoc = loc;
    }

    void boost(double force)
    {
        // System.out.println("boost: force=" + force);
        standStill += force;
    }

    void boost(Direction dir, double force, boolean spread)
    {
        // System.out.println(
        //         "boost: dir=" + dir
        //         + ", force=" + force
        //         + ", spread=" + spread);

        if (dir == null) {
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

    Direction currentMoveDir()
    {
        double max = standStill;
        Direction dir = null;

        for (int i = 0; i < 8; ++i) {
            if (max >= directions[i]) continue;
            max = directions[i];
            dir = Utils.dirByOrd[i];
        }

        return dir;
    }

    boolean move() throws GameActionException
    {
        if (standStill == Double.POSITIVE_INFINITY)
            return false;

        double max = standStill;
        Direction dir = null;
        MapLocation myLoc = rc.getLocation();

        if (myLoc.isAdjacentTo(rc.senseHQLocation())) {
            MapLocation ugh = Utils.devsLoveTrollMaps(rc);
            if (ugh != null) {
                rc.defuseMine(ugh);
                return true;
            }
        }

        for (int i = 0; i < 8; ++i) {
            if (max >= directions[i]) continue;

            Direction dest = Utils.dirByOrd[i];
            if (!rc.canMove(dest)) continue;
            if ((noDefuse || !autoDefuse) && sense.nonAlliedMine(myLoc.add(dest)))
                continue;

            max = directions[i];
            dir = dest;
        }

        if (capture != null && max < captureStrength) {
            System.err.println("max=" + max + ", capStr=" + captureStrength);
            rc.captureEncampment(capture);
            return true;
        }

        if (!noDefuse && autoDefuse && defuseLoc == null && dir != null)
            defuse(myLoc.add(dir));

        if (!noDefuse && defuseLoc != null && sense.nonAlliedMine(defuseLoc)) {
            rc.defuseMine(defuseLoc);
            return true;
        }

        if (layMine > max && rc.senseMine(myLoc) == null) {
            rc.layMine();
            return true;
        }

        if (dir == null) return false;
        rc.move(dir);
        prevLoc = rc.getLocation();
        return true;
    }

    String debug_print() {
        String str = "stand=" + standStill;
        str += ", dir={ ";
        for (int i = 0; i < directions.length; ++i)
            str += directions[i] + ", ";
        str += "}";

        str += ", defuse=";
        if (noDefuse) str += "banned";
        else if (defuseLoc != null) str += defuseLoc;
        else if (autoDefuse) str += "auto";
        else str += "nil";

        return str;
    }

    void debug_dump() {
        System.out.println(debug_print());
    }
}