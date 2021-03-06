package team216;

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

            // rc.setIndicatorString(0, nav.debug_print());

            boolean hasMoved = nav.move();
            if (!hasMoved) Hat.wearHat(rc);
            Communication.spam();

            bcCheck.debug_check("Soldier.end");

            rc.yield();
        }
    }

}