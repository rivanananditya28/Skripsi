/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

/**
 *
 * @author georg
 */
public class GeoInfo <Location, Direction, Speed, InfoTime> {
    private Location loc;
    private Direction dir;
    private Speed speed;
    private InfoTime time;    

    public GeoInfo() {
        this.loc = loc;
        this.dir = dir;
        this.speed = speed;
        this.time = time;
    }

    public GeoInfo(Location loc, Direction dir, Speed speed, InfoTime time) {
        this.loc = loc;
        this.dir = dir;
        this.speed = speed;
        this.time = time;
    }
    
    

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public Speed getSpeed() {
        return speed;
    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }

    public InfoTime getTime() {
        return time;
    }

    public void setTime(InfoTime time) {
        this.time = time;
    }
    
    public String toString(){
     return loc.toString() + ":" +dir.toString()+ ":" +speed.toString()+":"+time.toString();
    }
}
