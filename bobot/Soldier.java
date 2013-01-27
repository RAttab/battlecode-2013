package bobot;

import battlecode.common.*;


public class Soldier
{
    static boolean isHatless = true;

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

            // Time to fight!
            if (SoldierMicro.isMicro(sense))
                new SoldierMicro(rc, nav, sense, defuse).fight();

            // It's like herding cats...
            else {
                MapLocation loc = rc.senseEnemyHQLocation();
                Direction dir = rc.getLocation().directionTo(loc);
                nav.boost(dir, 1.0, true);

                defuse.macro();
            }

            rc.setIndicatorString(0, nav.debug_print());
            boolean hasMoved = nav.move();
            if (!hasMoved) Hat.wearHat(rc);

            bcCheck.debug_check("Soldier.end");
            rc.yield();
        }
    }

}
