package bobot;

import battlecode.common.*;


public class Mines
{
    public static double getMineStr(
        RobotController rc, SenseCache sense)
    {
        // TODO :
        // if (Storage.nukePanic)
        //     return Double.NEGATIVE_INFINITY;

        MapLocation coord = rc.getLocation();

        if (rc.senseMine(coord) != null)
            return Double.NEGATIVE_INFINITY;

        double mineStr = sense.defensiveRelevance(coord) * Weights.LAY_MINE;

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
        double minesNearby = rc.senseMineLocations(coord, 63, rc.getTeam()).length;
        double minesNearbyFactor = Weights.NEARBY_MINES * (30-minesNearby);
        return mineStr + minesNearbyFactor;
    }
}