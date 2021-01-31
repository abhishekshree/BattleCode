package bot2;

import battlecode.common.*;

import java.util.HashMap;
import java.util.Map;

import static battlecode.common.Direction.*;

public class Slanderer extends RobotPlayer {

    public static int sourceID; // ID of the source Enlightenment Center
    public static int roundCreated;    // round in which it is built
    public static int currentRound;
    public static Direction directionToEC;  // shortest approximate direction of the EC from the current location of slanderer


    public static int dangerDetectedInitial = 0;   // to be set to 1 if slanderer detects any enemy around it while encircling the EC and then it is supposed to move opposite to it till it reaches a safe place
    public static Team enemy;
    public static Direction moveInDir;    // direction in which the slanderer is supposed to move at the current time
    public static MapLocation sourceLoc;    // maplocation of the source EC
    public static int initialMovementDone = 0;   // to check whether the most initial movement of the slanderer has been completed or not
    public static int firstMovementDone = 0;       // checks whether it has moved Eastwards by 9 sq.units just after creation or not
    public static int secondMovementDone = 0;     // similar to above variable
    public static int dangerDetectedAfterwards = 0; // danger detected after 50 rounds
    public static Direction firstMovementDir, secondMovementDir;

    public static int enemy_loc_x = 0;  //x coordinates of enemy detected
    public static int enemy_loc_y = 0;   // y coordinate of enemy detected
    public static int flag_x;    // average of x coordinates of all the enemies detected
    public static int flag_y;   // avg of y coordinates of all the enemies detected
    public static int count_enemy = 0; // count of enemy within the sensor radius of slanderer
    public static int avgDistSquared;    // distance squared of slanderer from the avg location of enemies
    public static Direction directionOfEnemy;    // direction of avg map location from current location of slanderer
    public static int flag_dir;     // direction part of the flag to be raised after sensing an enemy
    public static int flagValue;

    public static Team homeTeam;
    public static int directionDecided;    // determines whether a direction can be decided to move in, according to the flag received
    public static int flag_fellow;    // flag received by team member
    public static int distPartOfFlag;     // part of flag_fellow signifying the distance
    public static int directionPartOfFlag;    // part of flag_fellow signifying the direction
    public static Direction passiveSuggestedDirection;    // direction suggested according to the fellow_flag
    public static int gotFirstFlag = 0;

    public static void setup() throws GameActionException  // called only once i.e. when the slanderer is first created to setup constants
    {
        sourceLoc = rc.getLocation().add(WEST);
        enemy = rc.getTeam().opponent();
        homeTeam = rc.getTeam();
        sourceID = rc.senseRobotAtLocation(rc.getLocation().add(WEST)).ID;
        roundCreated = rc.getRoundNum();
        firstMovementDir = rc.getLocation().directionTo(sourceLoc).opposite();
        secondMovementDir = firstMovementDir.rotateLeft().rotateLeft();


    }


    public static void dangerDetectedAndMove() throws GameActionException {

        if (rc.getRoundNum() - roundCreated < 50)
            dangerDetectedInitial = 1;
        else
            dangerDetectedAfterwards = 1;
        // if danger is detected within the sensor radius, move in a direction opposite to the enemy direction
        // Enemy location in increasing order
        RobotPlayer.moveInDirectionSmart(rc.getLocation().directionTo(rc.senseNearbyRobots(rc.getLocation(),rc.getType().sensorRadiusSquared, enemy)[0].location).opposite());

    }

    public static void encircleEnlightenmentCenter() throws GameActionException {

        if (dangerDetectedInitial == 1)    // if danger is detected before first 50 rounds, no need to encircle the EC, first priority is to run
            return;



        // following are two if blocks to encircle the source EC for first 50 rounds
        if (initialMovementDone == 0) {
            if (rc.getLocation().distanceSquaredTo(sourceLoc) == 9)
                firstMovementDone = 1;
            if (firstMovementDone == 0)
                moveInDirectionSmart(firstMovementDir);
            if (rc.getLocation().distanceSquaredTo(sourceLoc) == 18)
                secondMovementDone = 1;
            if (firstMovementDone == 1 && secondMovementDone == 0)
                moveInDirectionSmart(secondMovementDir);
            if (secondMovementDone == 1 && firstMovementDone == 1)
                initialMovementDone = 1;


        }

        if (initialMovementDone == 1) {
            directionToEC = rc.getLocation().directionTo(sourceLoc);

            if((rc.getLocation().distanceSquaredTo(sourceLoc) >=18 && rc.getLocation().distanceSquaredTo(sourceLoc) <= 20) || moveInDir == directionToEC.opposite())
                moveInDir = directionToEC;


            RobotPlayer.moveInDirectionSmart(moveInDir);

        }

    }


    public static void moveToSafeZone() throws GameActionException {               //function to govern the behaviour of slanderer for rounds between 50 to 300

        if(dangerDetectedInitial==1 || dangerDetectedAfterwards==1)
            return;
        moveInDir = rc.getLocation().directionTo(sourceLoc).opposite().rotateRight();
        RobotPlayer.moveInDirectionSmart(moveInDir);

    }


    public static void runSlanderer() throws GameActionException {


        // If an enemy is detected within the sensor radius then set both the dangerDetectedVariables to 1, move in the opposite direction
//            and set an appropriate flag for the team
        if (rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, enemy).length != 0) {
            dangerDetectedAndMove();

        }

        else {
            dangerDetectedAfterwards = 0;
            dangerDetectedInitial = 0;
        }

        if(rc.getRoundNum() - roundCreated <= 50)
            encircleEnlightenmentCenter();

        if (rc.getRoundNum() - roundCreated > 50 && rc.getRoundNum() - roundCreated < 300 && dangerDetectedInitial==0 && dangerDetectedAfterwards==0)
            moveToSafeZone();




//        if (rc.getRoundNum() - roundCreated >= 50 && dangerDetectedInitial == 0 && dangerDetectedAfterwards == 0 && rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, homeTeam).length!=0)
//            getTeamFlag();



    }


}