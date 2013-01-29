package bobot;

import battlecode.common.*;

public class ByteCode
{
    static class Check
    {
        int round;
        int start;

        RobotController rc;

        Check(RobotController rc)
        {
            this.rc = rc;
            round = Clock.getRoundNum();
            start = Clock.getBytecodeNum();
        }

        void debug_check(String str)
        {
            if (Clock.getRoundNum() == round) return;

            int stop = Clock.getBytecodeNum();
            System.out.println("bytecode overflow(" + str + "): "
                    + "(" + start + ", " + round + ") -> "
                    + "(" + stop + ", " + Clock.getRoundNum() + ")");
            rc.breakpoint();
        }
    }

    static class Profiler
    {
        int start = 0;
        int round = 0;

        static final int OVERHEAD = 5;

        Profiler()
        {
            round = Clock.getRoundNum();
            start = Clock.getBytecodeNum();
        }

        void debug_dump(String str)
        {
            int stop = Clock.getBytecodeNum();
            int len;
            if (Clock.getRoundNum() > round)
                len = 10000 - start + stop;
            else len = stop - start;
            System.out.println("profiler(" + str + "): " + len + " / " + stop);
        }
    }
}