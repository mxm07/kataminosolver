import java.awt.*;
import java.util.ArrayList;


public class Piece {
    ArrayList<Tile> tiles = new ArrayList<>();
    ArrayList<Piece> rotatedPieces = new ArrayList<>();

    public Color col = new Color( 0, 0, 0 );

    public Piece() { this( new ArrayList<>() ); }
    public Piece( ArrayList<Tile> tiles ) {
        this.tiles = tiles;
    }

    public Tile get( int index ) { return tiles.get( index ); }
    public void remove( int index ) { tiles.remove( index ); }
    public void remove( Object o ) { tiles.remove(o); }
    public void removeAll( ArrayList<Object> o ) {
        for( Object obj : o ) tiles.remove( o );
    }
    public void add( Tile tile ) { tiles.add( tile ); }
    public void addAll( Piece g ) { tiles.addAll( g.tiles ); }
    public void addAll( int index, Piece g ) {
        ArrayList<Tile> n = new ArrayList<>();

        for( int i = 0; i < index; i++ ) n.add( tiles.get(i) );
        for( Tile tile : g.tiles ) n.add( tile );
        for( int i = index; i < tiles.size(); i++ ) n.add( tiles.get(i) );

        tiles = n;
    }
    public Piece copy() {
        Piece copy = new Piece();
        for( Tile tile : tiles ) copy.add( tile );
        copy.col = col;

        return copy;
    }





    @Override
    public String toString() {
        String out = "";
        for( Tile tile : tiles ) {
            out += "[" + tile.x + ", " + tile.y + "] ";
        }
        return out;
    }
    @Override
    public boolean equals( Object o ) {
        if( !(o instanceof Piece ) ) return false;

        for( int i = 0; i < ((Piece) o).tiles.size(); i++ ) {
            if( !((Piece) o).tiles.get(i).equals(tiles.get(i)) ) return false;
        }

        return true;
    }
    @Override
    public int hashCode() {
        int x = 0;
        int y = 0;

        for( Tile tile : tiles ) {
            x += tile.x; x *= tile.x;
            y += tile.y; y *= tile.y;
        }

        return (int) ((x*y + 50 + x - y)/15);
    }

    public Tile getBL() {
        int left = 9999;
        int top = -9999;

        for( Tile tile : tiles )
            if( tile.x < left || tile.y > top ) { left = tile.x; top = tile.y; }

        return new Tile( left, top );
    }
    public void normalize() {
        Tile bl = getBL();
        for( int i = 0; i < tiles.size(); i++ )
            tiles.set( i, new Tile( tiles.get(i).x - bl.x, tiles.get(i).y - bl.y ) );
    }

    public Piece rotate() {
        Piece rotated = new Piece();
        Piece old = this.copy(); old.normalize();
        Tile bl = getBL();

        for( Tile tile : old.tiles )
            rotated.add( new Tile( 2-tile.y, 2+tile.x ) );

        rotated.col = this.col;
        return rotated;
    }

    public void buildRotated() {
        Piece p = this;
        for( int i = 0; i < 3; i++ ) {
            p = p.rotate();
            p.normalize();
            rotatedPieces.add( p );
        }
    }
}
