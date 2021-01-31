package bot2;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Politician extends RobotPlayer {



    static boolean parentExists;
    static RobotInfo parentEnlightenmentCenter;

    static Direction moveDirection;

    public static void setup() throws GameActionException {
        System.out.println("New politician created at" + rc.getLocation().x + ", " + rc.getLocation().y);

        setParentEnlightenmentCenter();
        setInitialMoveDirection();
    }

    static public void runPolitician() throws GameActionException{
        giveSpeechIfProfitable(0.5);
        moveInDirectionSmart(moveDirection);
    }

    static void setParentEnlightenmentCenter(){
        RobotInfo[] adjacentRobotList = rc.senseNearbyRobots(2, rc.getTeam());
        parentExists = false;
        for (RobotInfo adjacentRobot : adjacentRobotList){
            if (adjacentRobot.type == RobotType.ENLIGHTENMENT_CENTER){
                parentExists = true;
                parentEnlightenmentCenter = adjacentRobot;
            }
        }
    }

    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    static void setInitialMoveDirection(){
        if (!parentExists) {
            moveDirection = randomDirection();
            return;
        }
        Direction directionOfParentEC = rc.getLocation().directionTo(parentEnlightenmentCenter.location);
        int oppositeDirectionNumber = (directionToNumberMap.get(directionOfParentEC) + 4) % 8;
        moveDirection = directions[oppositeDirectionNumber];
    }

    static void moveInDirectionNaive(Direction dir) throws GameActionException{
        if (rc.canMove(dir)) rc.move(dir);
    }


    static public double speechProfitability(){
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        int conviction = rc.getConviction();
        int robotCount = rc.detectNearbyRobots(actionRadius).length;
        int enemyCount = rc.senseNearbyRobots(actionRadius, enemy).length;

        if (conviction < 10 || enemyCount == 0) return 0.0;

        double convictionPerEnemy = (double) (conviction - 10) / (double) robotCount;
        double halfPoint = 15;

        return Math.atan(convictionPerEnemy/halfPoint)*(2/Math.PI);
    }

    static public void giveSpeechIfProfitable(double threshold) throws GameActionException {
        double profitability = speechProfitability();
        int actionRadius = rc.getType().actionRadiusSquared;
        if (profitability >= threshold && rc.canEmpower(actionRadius)){
            rc.empower(actionRadius);
        }
    }

}