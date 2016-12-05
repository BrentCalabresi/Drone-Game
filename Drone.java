package Project4;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Created by BAC on 11/23/2016.
 */

public class Drone {
    protected double vX,vY,velocity,lives,fuel,x,y,aX,aY,pitch,acceleration;
    protected Image drone;
    protected boolean thrusting;
    int height,width;

    public Drone(double vX, double vY, double velocity, double lives, double fuel, double x, double y, double aX, double ay, double acceleration, boolean thrusting, double pitch) {
        this.vX = vX;
        this.vY = vY;
        this.velocity = velocity;
        this.lives = lives;
        this.fuel = fuel;
        this.x = x;
        this.y = y;
        this.thrusting = thrusting;
        this.pitch = pitch;
        this.aX = aX;
        this.aY = ay;
        this.acceleration = acceleration;
    }

    @Deprecated
    public void render(Graphics2D g){

        try {
        drone = ImageIO.read(getClass().getResource("plane.png"));
    } catch (IOException e) {
        System.out.println("Error in loading picture");
    }
        g.drawImage(drone,(int)getX(),(int)getY(),null);
    }

    public void startOver(){
        this.setLives(this.getLives()-1);
        this.setX(5);//start from beginning
        this.setY(250);
        this.setPitch(0);
        this.setFuel(100);
    }

    public Image getDroneImage() {
        try {
            drone = ImageIO.read(getClass().getResource("plane_2.png"));
        } catch (IOException e) {
            System.out.println("Error in loading picture");
        }
        width = drone.getWidth(null);
        height = drone.getHeight(null);
        return drone;
    }

    public Rectangle boundingBox(){
        return new Rectangle((int)x, (int) y,width,height);
    }
    public double getaX() {
        return aX;
    }

    public void setaX(double aX) {
        this.aX = aX;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getvX() {
        return vX;
    }

    public void setvX(double vX) {
        this.vX = vX;
    }

    public double getvY() {
        return vY;
    }

    public void setvY(double vY) {
        this.vY = vY;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getLives() {
        return lives;
    }

    public void setLives(double lives) {
        this.lives = lives;
    }

    public double getFuel() {
        return fuel;
    }

    public void setFuel(double fuel) {
        this.fuel = fuel;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getaY() {
        return aY;
    }

    public void setaY(double aY) {
        this.aY = aY;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public void setDrone(Image drone) {
        this.drone = drone;
    }

    public boolean isThrusting() {
        return thrusting;
    }

    public void setThrusting(boolean thrusting) {
        this.thrusting = thrusting;
    }
}
