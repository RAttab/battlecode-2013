package godotbot;

import battlecode.common.*;

/** Bot that just sits there waiting for godot. */
public class RobotPlayer
{

    public static void run(RobotController rc)
    {
        while (true) {
            System.out.println("[TRAIN] Round " + Clock.getRoundNum());
            rc.yield();
        }
    }

}
