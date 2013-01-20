package team216;

import battlecode.common.*;

public class Storage {

    // Constants

    public static Team MY_TEAM;
    public static Team ENEMY_TEAM;
    public static MapLocation MY_HQ;
    public static MapLocation ENEMY_HQ;
    public static MapLocation CENTER; //center between HQs, not true center
    public static double DISTANCE_BETWEEN_HQ;
    public static double SLOPE;
    public static int MAP_HEIGHT;
    public static int MAP_WIDTH;
    public static int MAP_SIZE;
    public static RobotInfo MY_INFO;
    public static Robot ME;
    public static double EST_RUSH_TIME;

    public static void calculateValues(RobotController rc) {
        try {
            MY_TEAM = rc.getTeam();
            ENEMY_TEAM = MY_TEAM.opponent();
            MY_HQ = rc.senseHQLocation();
            ENEMY_HQ = rc.senseEnemyHQLocation();
            ME = rc.getRobot();
            MY_INFO = rc.senseRobotInfo(ME);
            MAP_HEIGHT = rc.getMapHeight();
            MAP_WIDTH = rc.getMapWidth();
            MAP_SIZE = MAP_HEIGHT * MAP_WIDTH;
        }
        catch(Exception e) { e.printStackTrace(); }
    }

    // Variables

    public static double getRushTime(RobotController rc) {
        //calculate estimated turns for rush
        if (EST_RUSH_TIME == 0.0) {
            double x_dif = MY_HQ.x - ENEMY_HQ.x;
            double y_dif = MY_HQ.y - ENEMY_HQ.y;
            double x;
            double y;
            double offset;
            double time = 0.0;
            String s = "";
            //rc.setIndicatorString(0, "c=" + CENTER + ", m=" + MY_HQ + ", e=" + ENEMY_HQ + ", xdif=" + x_dif + ", ydif" + y_dif + ", slope=" + SLOPE);
            for (int i=0; i<20; i++) {
                offset = 6 * Math.random() - 3;
                x = Math.random() * x_dif;
                y = SLOPE * x + ENEMY_HQ.y;
                s += " (" + (int)x + ", " + (int)y + ")";
                if (Team.NEUTRAL.equals(rc.senseMine(new MapLocation((int)x, (int)y)))){
                    time += 12;
                    s += "!";
                }
            }
            rc.setIndicatorString(2, s);
            time *= (distanceBetweenHQs()/20.0);
            EST_RUSH_TIME = time;
            return time;
        } else
            return EST_RUSH_TIME;
    }

    public static double distanceBetweenHQs() {
        return DISTANCE_BETWEEN_HQ == 0.0 ? Utils.distTwoPoints(Storage.MY_HQ, Storage.ENEMY_HQ) : DISTANCE_BETWEEN_HQ;
    }

    public static double slopeBetweenHQs() {
        return SLOPE == 0.0 ? (double)(MY_HQ.y - ENEMY_HQ.y) / (MY_HQ.x - ENEMY_HQ.x) : SLOPE;
    }

    public static MapLocation centerBetweenHQs() {
        return CENTER == null ? new MapLocation((MY_HQ.x + ENEMY_HQ.x)/2,(MY_HQ.y + ENEMY_HQ.y)/2) : CENTER;
    }


    // Combat methods
    // getEnemyCenter
    // getFriendlyCenter

}
