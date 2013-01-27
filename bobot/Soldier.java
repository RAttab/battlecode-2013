package bobot;

import battlecode.common.*;


public class Soldier
{

    public static void run(RobotController rc)
        throws GameActionException
    {
        // Never leave home without it!
        rc.wearHat();

        SenseCache sense = new SenseCache(rc);

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }

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
                defuse.macro();
            }


            rc.setIndicatorString(0, nav.debug_print());
            nav.move();
            rc.yield();
        }
    }

}
