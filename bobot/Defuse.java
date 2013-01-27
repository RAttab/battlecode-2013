package bobot;

import battlecode.common.*;

public class Defuse
{
    RobotController rc;
    Navigation nav;
    SenseCache sense;

    Defuse(RobotController rc, Navigation nav, SenseCache sense)
    {
        this.rc = rc;
        this.nav = nav;
        this.sense = sense;
    }

    /** Check if we're on a mine and GTFO if we are. */
    void onMine() throws GameActionException
    {
        if (!sense.nonAlliedMine(rc.getLocation()) || rc.getShields() > 0)
            return;

        // Uh Oh. Standing on a mine! GTFO!
        nav.noDefuse = true; // I feel safer having this flag in nav.
        nav.boost(Double.NEGATIVE_INFINITY);
        if (Navigation.prevLoc == null) return;

        // Go back the way you came if possible.
        Direction backoff =
            rc.getLocation().directionTo(Navigation.prevLoc);
        nav.boost(backoff, Weights.MINE_GTFO, true);
    }


    /** Note that while we may be in micro mode, we aren't in contact with the
        enemy.
     */
    boolean micro() throws GameActionException
    {
        if (nav.noDefuse) return false;
        nav.autoDefuse = false;

        Direction dir = nav.currentMoveDir();
        if (dir == null) return false;

        if (rc.hasUpgrade(Upgrade.DEFUSION))
            return microWithDefusion(dir);
        return micro(dir);
    }

    private boolean micro(Direction dir)
        throws GameActionException
    {
        Team me = rc.getTeam();
        MapLocation myLoc = rc.getLocation();
        MapLocation inFront = myLoc.add(dir); // yeah, yeah. the name sucks.

        if (!sense.nonAlliedMine(inFront)) return false;

        // Try to form a pattern where every other robot is mining.
        // Allows us to make forward progress without dying horribly.
        MapLocation left = myLoc.add(dir.rotateLeft().rotateLeft());
        if (sense.robotBusy(left, me)) return false;

        nav.defuse(inFront);
        return true;
    }

    private boolean microWithDefusion(Direction dir)
        throws GameActionException
    {
        Team me = rc.getTeam();

        MapLocation myLoc = rc.getLocation();
        MapLocation behind = myLoc.add(dir.opposite());
        MapLocation inFront = myLoc.add(dir);
        MapLocation twoAway = inFront.add(dir);
        MapLocation threeAway = twoAway.add(dir);

        // If you're in front of the mine field, wait for the telekinesis
        // support to defuse the mine.
        if (sense.nonAlliedMine(inFront)) {
            if (sense.robotInfo(behind, me) != null) return false;

            // We have no telekinesis support so revert to default behaviour.
            return micro(dir);
        }

        RobotInfo ally = sense.robotInfo(inFront, me);

        // This allows us to defuse a mine that our ally just stepped on while
        // still allowing him to retreat.
        if (ally == null) ally = sense.robotInfo(twoAway, me);

        // No mines and no-one to help out...
        if (ally == null) return false;


        // If our ally is mining then our heuristic says he's mining twoAway.
        if (sense.busy(ally)) {
            // This prevents the introduction of a bad defusion cycle where your
            // ally finishes to defuse before you, moves on, notices that nobody
            // is backing him up and starts defusing again.
            return false;
        }

        // Our ally is waiting for us to defuse twoAway.
        if (sense.nonAlliedMine(twoAway)) {
            nav.defuse(twoAway);
            return true;
        }

        return false;
    }


    boolean macro() throws GameActionException
    {
        if (nav.noDefuse) return false;
        nav.autoDefuse = true;

        Direction dir = nav.currentMoveDir();
        if (dir == null) return false;

        // Just rely on autoDefuse.
        if (!rc.hasUpgrade(Upgrade.DEFUSION)) return false;

        return macroWithDefusion(dir);
    }

    private boolean macroWithDefusion(Direction dir)
        throws GameActionException
    {
        Team me = rc.getTeam();

        MapLocation myLoc = rc.getLocation();

        MapLocation behind = myLoc.add(dir.opposite());
        MapLocation twoBehind = behind.add(dir.opposite());

        MapLocation inFront = myLoc.add(dir);
        MapLocation twoAway = inFront.add(dir);
        MapLocation threeAway = twoAway.add(dir);

        // Rely on auto-defuse.
        if (sense.nonAlliedMine(inFront)) {
            RobotInfo ally = sense.robotInfo(behind, me);

            // Our ally might be lagging behind a bit.
            if (ally == null) ally = sense.robotInfo(twoBehind, me);

            // No allies around so rely on auto-defuse
            if (ally == null) return false;

            // If there's an ally behind us then the heuristic says he's
            // defusing the mine in front of us.
            if (sense.busy(ally) && sense.nonAlliedMine(twoAway)) {
                nav.defuse(twoAway);
                return true;
            }

            return false;
        }

        RobotInfo ally = sense.robotInfo(inFront, me);

        // This allows us to defuse a mine that our ally just stepped on while
        // still allowing him to retreat.
        if (ally == null) ally = sense.robotInfo(twoAway, me);

        // No mines and no-one to help out...
        if (ally == null) return false;

        if (sense.nonAlliedMine(twoAway) && !sense.busy(ally)) {
            nav.defuse(twoAway);
            return true;
        }

        if (sense.nonAlliedMine(threeAway)) {
            nav.defuse(threeAway);
            return true;
        }

        return false;
    }

}