package bobot;

import battlecode.common.*;


public class Mines
{
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