package bobot;

import battlecode.common.*;


public class Soldier
{

    public static void run(RobotController rc)
        throws GameActionException
    {
        SenseCache sense = new SenseCache(rc);

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }

            ByteCode.Check bcCheck = new ByteCode.Check(rc);
            Navigation nav = new Navigation(rc, sense);
            Defuse defuse = new Defuse(rc, nav, sense);
            sense.reset();

            // System.out.println(
            //         "startLoc=" + rc.getLocation()
            //         + ", prevLoc=" + Navigation.prevLoc);

            defuse.onMine();

            if (SoldierMicro.isMicro(sense))
                new SoldierMicro(rc, nav, sense, defuse).fight();
            else
                new SoldierMacro(rc, nav, sense, defuse).formup();

            rc.setIndicatorString(0, nav.debug_print());
            boolean hasMoved = nav.move();
            if (!hasMoved) Hat.wearHat(rc);

            bcCheck.debug_check("Soldier.end");
            rc.yield();
        }
    }

    public static double getMineStr(RobotController rc)
    {

        if (Storage.nukePanic)
            return Double.NEGATIVE_INFINITY;

        if (rc.senseMine(rc.getLocation()) != null)
            return Double.NEGATIVE_INFINITY;

        double mineStr = defense * Weights.LAY_MINE;
        if (rc.hasUpgrade(Upgrade.PICKAXE)) {
            int orthogonalMines = 0;
            if (rc.senseMine(coord.add(Direction.NORTH)) != null)
                orthogonalMines++;
            if (rc.senseMine(coord.add(Direction.SOUTH)) != null)
                orthogonalMines++;
            if (rc.senseMine(coord.add(Direction.EAST)) != null)
                orthogonalMines++;
            if (rc.senseMine(coord.add(Direction.WEST)) != null)
                orthogonalMines++;
            mineStr *= 5-orthogonalMines;
        }
        // TODO : make areas with encampments more enticing
        // if (rc.senseEncampmentSquare(coord)){
        //     mineStr += Weights.LAY_MINE;
        // }
        double minesNearbyFactor = Weights.NEARBY_MINE * ((LC_RADIUS/2)-(minesNearby));
        return mineStr + minesNearbyFactor;
    }

}
