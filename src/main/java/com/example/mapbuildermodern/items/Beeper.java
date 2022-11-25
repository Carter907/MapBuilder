package com.example.mapbuildermodern.items;

public class Beeper {
    private int num;
    private int x;
    private int y;

    public Beeper(int x, int y, int num) {
        this.x = x;
        this.y = y;
        this.num = num;
    }
    public Beeper(int x, int y) {
        this.x = x;
        this.y = y;
        this.num = 0;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getNum() {
        return num;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("beeper[x=%d][y=%d][num=%d]",x,y,num);
    }


}
