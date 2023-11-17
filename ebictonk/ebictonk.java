import dev.robocode.tankroyale.botapi.*;
import dev.robocode.tankroyale.botapi.events.*;

public class ebictonk extends Bot{
    int tlock = 0;
    double rota = 0;
    int spiin = 5;
    int lastHitWall = -10;
    public static void main(String[] args){
        new ebictonk().start();
    }

    ebictonk(){
        super(BotInfo.fromFile("ebictonk.json"));
    }

    @Override
    public void run(){
        //System.out.println("test");
        //rescan();
        tlock = 0;
        rota = 0;
        lastHitWall = -30;
        //System.out.println("restart" + tlock);
        setAdjustGunForBodyTurn(true);
        setAdjustRadarForBodyTurn(true);
        setAdjustRadarForGunTurn(true);
        setBodyColor(Color.PURPLE);
        setScanColor(Color.RED);
        setBulletColor(Color.PURPLE);
        while(isRunning()){
            turnRadarRight(20);
        }
    }

    public double[] prediaim(ScannedBotEvent e){
        double timeDif = 40;
        double deltaX = 0;
        double deltaY = 0;
        for(int x = 0; x < 40 && Math.abs(timeDif) > 1; x++){
            deltaX = Math.cos(Math.toRadians(e.getDirection())) * (e.getSpeed()*x);
            deltaY = Math.sin(Math.toRadians(e.getDirection())) * (e.getSpeed()*x);
            double opp = (e.getY() + deltaY) - getY();
            double ajd = (e.getX() + deltaX) - getX();
            double angR = Math.atan(opp/ajd);
            double hyp = Math.abs(opp / Math.sin(angR));
            timeDif = x - (hyp / 17);
            //System.out.println(x + " " + Math.round(timeDif) + " " + Math.round(deltaX) + " " + Math.round(deltaY) + " " + Math.round(hyp));
        }
        if(timeDif < 5){
            double result[] = {deltaX,deltaY};
            return result;
        }
        else{
            double result[] = {0,0};
            return result;
        }
    }

    public void circle(ScannedBotEvent e){
        if(e.getScannedBotId() == tlock){
            double dX2 = Math.cos(rota) * 300;
            double dY2 = Math.sin(rota) * 300;
            setTurnLeft(calcBearing(directionTo(e.getX() + dX2,e.getY() + dY2)));
            if(getTurnNumber() - lastHitWall > 20){
                setForward(100);
            }
            if(distanceTo(e.getX() + dX2,e.getY() + dY2) < 50){
                rota += Math.toRadians(spiin);
            }
            if(rota >= Math.toRadians(360)){
                //rota = Math.toRadians(0);
            }
            //System.out.println(Math.toDegrees(rota)+" " + tlock + " " + dX2 + " " + dY2);
        }
        else{
            rota = Math.atan((e.getY()-getY())/(e.getX()-getX()));
            if(e.getX() - getX() > 0){
                rota = rota + Math.toRadians(180);
                System.out.println("test");
            }
            tlock = e.getScannedBotId();
        }
    }

    public int wallDanger(){
        double edge = getArenaWidth();
        double top = getArenaHeight();
        
        if(getX() > edge - 150){
            return 1;
        }
        else if(getX() < 150){
            return 3;
        }
        else if(getY() > top - 150){
            return 4;
        }
        else if(getY() < 150){
            return 2;
        }
        else{
            return 0;
        }
    }

    public void awayFromWall(int side){
        double deltaaaah = 0;
        if(side == 1){
            deltaaaah = calcBearing(0);
        }
        else if(side == 2){
            deltaaaah = calcBearing(270);
        }
        else if(side == 3){
            deltaaaah = calcBearing(180);
        }
        else if(side == 4){
            deltaaaah = calcBearing(90);
        }

        if(deltaaaah > 0){
            setTurnLeft(-90);
        }
        else{
            setTurnLeft(90);
        }
        setForward(100);
    }

    public void onScannedBot(ScannedBotEvent e){
        double radber = calcRadarBearing(directionTo(e.getX(),e.getY()));
        //double deltaX = Math.cos(Math.toRadians(e.getDirection())) * (e.getSpeed()*prediRange);
        //double deltaY = Math.sin(Math.toRadians(e.getDirection())) * (e.getSpeed()*prediRange);
        double aim[] = prediaim(e);
        double deltaX = aim[0];
        double deltaY = aim[1];
        double gunber = calcGunBearing(directionTo(e.getX() + deltaX,e.getY() + deltaY));
        //System.out.println(deltaX + deltay + gunber);
        
        setTurnRadarLeft(radber);
        setTurnGunLeft(gunber);
        int walldetec = wallDanger();
        if(walldetec == 0){
        circle(e);
        }
        else{
            awayFromWall(walldetec);
        }
        //if(distanceTo(e.getX(),e.getY()) > 300){
            //setForward(distanceTo(e.getX() + deltaX,e.getY() + deltaY));
        //}
        //else{
            //setBack(distanceTo(e.getX() + deltaX,e.getY() + deltaY));
        //}
        //setTurnLeft(calcBearing(directionTo(e.getX() + deltaX,e.getY() + deltaY)));
        //System.out.println(deltaX + " " + deltaY + " " + e.getDirection() + " " + e.getSpeed());

        if (gunber < 3){
            fire(1);
        }


    }

}
