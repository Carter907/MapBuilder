package org.example.items;

public class Wall {

    public enum WallStyle {

        HORIZONTAL,
        VERTICAL;
    }

    private int length;
    private WallStyle style;
    private int x;
    private int y;

    public Wall(int x, int y) {
        this.x = x;
        this.y = y;
        this.style = null;
        this.length = 1;
    }

    public Wall(int x, int y, WallStyle style ) {
        this.x = x;
        this.y = y;
        this.length = 1;
        this.style = style;

    }

    public Wall(int x, int y, int length, WallStyle style ) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.style = style;

    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setStyle(WallStyle style) {
        this.style = style;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getLength() {
        return length;
    }

    public WallStyle getStyle() {
        return style;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString () {
        return String.format("wall[x=%d][y=%d][length=%d][style=%s]", x, y, length, style.toString().toLowerCase());
    }
}
