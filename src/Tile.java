public class Tile {
    int x = -1;
    int y = -1;

    public Tile( int x, int y ) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals( Object o ) {
        return o instanceof Tile && this.x == ( (Tile) o ).x && this.y == ( (Tile) o ).y;
    }


    @Override
    public int hashCode() {
        return (int) ((x*y + 30 + x - y)/5);
    }
}
