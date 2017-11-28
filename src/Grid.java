import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Time;
import java.util.*;

public class Grid {
    public static int SETTINGS_SIZE = 256;
    public static int SETTINGS_GRIDSIZE;
    public static Color SETTINGS_DEFAULTCOLOR = new Color( 240, 240, 240 );
    //public boolean SETTINGS_ROTATION = true;

    public boolean areaMode = false;
    public boolean rotMode = false;

    boolean leftClick = false;
    boolean rightClick = false;

    public JPanel panel;

    JButton[][] squares;
    Boolean[][] values;
    ArrayList<Piece> pieces = new ArrayList<>();

    static float rand = new Random().nextInt( 50 );






    void pr( Object o ) {
        System.out.println( o );
    }
    public static Color randomColor() {
        rand += 35;
        return Color.getHSBColor( (float) ( rand / 360 ), 0.5f, 0.9f );
    }


    Piece getPiece( int x, int y ) {
        for( Piece piece : pieces ) {
            for( Tile tile : piece.tiles )
                if( tile.x == x && tile.y == y ) return piece;
        }

        return null;
    }



    ArrayList<Piece> findSolution( Piece oldarea, ArrayList<Piece> oldpieces ) {
        return findSolution( oldarea, oldpieces, 0, oldpieces.size() );
    }



    @SuppressWarnings( "unchecked" )
    ArrayList<Piece> findSolution( Piece oldarea, ArrayList<Piece> oldpieces, int start, int pieceCount ) {
        if( start > oldarea.tiles.size() || start >= oldarea.tiles.size() ) {
            return new ArrayList<>();
        } else {
            Piece curPiece = oldpieces.get(0);
            Piece newPiece = new Piece();
            newPiece.col = curPiece.col;

            for( Tile tile : curPiece.tiles ) {
                Tile temp = new Tile( oldarea.get( start ).x + tile.x, oldarea.get( start ).y + tile.y );

                if( !oldarea.tiles.contains( temp ) )
                    return findSolution( oldarea, oldpieces, start + 1, pieceCount );

                newPiece.add( temp );
            }

            Piece area = oldarea.copy();
            ArrayList<Piece> pieces = (ArrayList<Piece>) oldpieces.clone();

            ArrayList<Piece> loseIt = findSolution( area, pieces, start + 1, pieceCount );
            area.tiles.removeAll( newPiece.tiles );
            pieces.remove(0);
            ArrayList<Piece> useIt = findSolution( area, pieces, 0, pieceCount );
            useIt.add( newPiece );

            if( loseIt.size() == pieceCount ) return loseIt;
            if( useIt.size() == pieceCount ) return useIt;

            return (useIt.size() > loseIt.size() ? useIt : loseIt);
        }
    }


    void rotTest() {
        Color color = pieces.get(0).col;

        Piece p = pieces.get(0).rotate();
        pieces.remove(0);

        clear();

        for( Tile tile : p.tiles )
            squares[ tile.x ][ tile.y ].setBackground( color );

        pieces.add(p);
    }

    void solve( Piece area ) {
        //If the topgrid has less pieces than the bottomgrid, then we already know solving is impossible
        int tileAmount = 0;
        for( Piece piece : pieces ) tileAmount += piece.tiles.size();
        if( tileAmount < area.tiles.size() ) return;



        ArrayList<Piece> piecesCopy = new ArrayList<>();
        for( Piece piece : pieces ) piecesCopy.add( piece );
        Piece areaCopy = area.copy();

        for( Piece piece : piecesCopy )
            piece.normalize();


        long start = System.nanoTime();
        System.out.println( start );
        ArrayList<Piece> solved = findSolution( areaCopy, piecesCopy );
        double end = (System.nanoTime() - start) / 1000000000d;
        System.out.println( end + " seconds");

        //If the solved piece group size is not the same size as the area, then solving is impossible
        tileAmount = 0;
        for( Piece piece : solved ) tileAmount += piece.tiles.size();
        if( tileAmount != area.tiles.size() ) return;

        clear();

        for( Piece piece : solved ) {
            for( Tile tile : piece.tiles )
                squares[ tile.x ][ tile.y ].setBackground( piece.col );
        }

        pieces = solved;
    }










    void squarePressed( JButton button, boolean leftClick ) {
        int x = -1;
        int y = -1;

        for( int i = 0; i < squares.length; i++ ) {
            for( int j = 0; j < squares[i].length; j++ )
                if( button.equals( squares[i][j] ) ) { x = i; y = j; }
        }
        if( x == -1 || y == -1 ) { return; }





        if( leftClick ) { //Adding tiles and creating pieces
            //Ignore tiles already in a group
            for( Piece piece : pieces )
                if( piece.tiles.contains( new Tile( x, y ) ) ) return;

            if( values[x][y] ) return;
            values[x][y] = true;

            if( areaMode ) {
                if( pieces.size() == 0 )
                    pieces.add( new Piece() );

                Piece area = pieces.get( 0 );

                area.add( new Tile( x, y ) );
                pieces.set( 0, area );

                squares[x][y].setBackground( area.tiles.size() == 1 ? randomColor() : squares[ area.get( 0 ).x ][ area.get( 0 ).y ].getBackground() );
            } else {
                ArrayList<Piece> surr = new ArrayList<>();
                Tile cur = new Tile( -1, -1 );
                Piece temp;


                //Check surrounding squares for other pieces
                temp = getPiece( x + 1, y );
                if( temp != null ) {
                    if( !surr.contains( temp ) ) surr.add( temp );
                    cur = new Tile( x + 1, y );
                }
                temp = getPiece( x - 1, y );
                if( temp != null ) {
                    if( !surr.contains( temp ) ) surr.add( temp );
                    cur = new Tile( x - 1, y );
                }
                temp = getPiece( x, y + 1 );
                if( temp != null ) {
                    if( !surr.contains( temp ) ) surr.add( temp );
                    cur = new Tile( x, y + 1 );
                }
                temp = getPiece( x, y - 1 );
                if( temp != null ) {
                    if( !surr.contains( temp ) ) surr.add( temp );
                    cur = new Tile( x, y - 1 );
                }


                if( surr.size() == 0 ) { //If there are no surrounding pieces
                    Piece newgroup = new Piece();
                    newgroup.add( new Tile( x, y ) );
                    newgroup.col = randomColor();

                    pieces.add( newgroup );

                    squares[ x ][ y ].setBackground( newgroup.col );
                } else {

                    //Add the clicked-on tile to one of the surrounding pieces (which one doesnt matter)
                    for( Piece piece : pieces )
                        if( piece.tiles.contains( new Tile( cur.x, cur.y ) ) ) { piece.add( new Tile( x, y ) ); break; }


                    squares[x][y].setBackground( squares[ cur.x ][ cur.y ].getBackground() );


                    //If there are two or more surrounding pieces that are different
                    if( surr.size() > 1 ) {
                        Piece curgroup = getPiece( cur.x, cur.y );

                        for( Piece s : surr ) {
                            if( curgroup.equals( s ) ) continue; //prevent adding group onto itself

                            //
                            for( Piece piece : pieces )
                                if( piece.tiles.contains( new Tile( cur.x, cur.y ) ) ) { piece.addAll( s ); pieces.remove( s ); break; }

                            //Changing colors of all of the tiles in the new group to be the same
                            for( Tile tile : s.tiles )
                                squares[ tile.x ][ tile.y ].setBackground( squares[ cur.x ][ cur.y ].getBackground() );
                        }
                    }
                }
            }
        } else { //Deleting tiles and breaking up pieces
            if( !values[x][y] ) return;
            values[x][y] = false;
            squares[x][y].setBackground( SETTINGS_DEFAULTCOLOR );


            if( areaMode ) {
                pieces.get(0).remove( new Tile( x, y ) );
            } else {
                Piece curPiece = getPiece( x, y ).copy();
                curPiece.remove( new Tile( x, y ) );
                ArrayList<Piece> newPieces = new ArrayList<>();


                //Have to use an iterator here to avoid a ConcurrentModificationException
                Iterator<Piece> iter = pieces.iterator();
                while( iter.hasNext() ) {
                    Piece next = iter.next();

                    if( next.tiles.contains( new Tile( x, y ) ) ) {
                        iter.remove();
                        break;
                    }
                }

                for( Tile tile : curPiece.tiles ) {
                    ArrayList<Piece> surr = new ArrayList<>();

                    for( Piece piece : newPieces ) {
                        if( piece.tiles.contains( new Tile( tile.x + 1, tile.y ) ) ||
                                piece.tiles.contains( new Tile( tile.x - 1, tile.y ) ) ||
                                piece.tiles.contains( new Tile( tile.x, tile.y + 1 ) ) ||
                                piece.tiles.contains( new Tile( tile.x, tile.y - 1 ) ) )
                            if( !surr.contains( piece ) )
                                surr.add( piece );
                    }


                    if( surr.size() == 0 ) {
                        Piece newPiece = new Piece();
                        newPiece.add( tile );
                        newPieces.add( newPiece );
                    } else if( surr.size() == 1 ) { //might be able to get rid of this?
                        for( Piece piece : newPieces ) {
                            if( piece.equals( surr.get( 0 ) ) )
                                piece.add( tile );
                        }
                    } else {
                        newPieces.removeAll( surr );

                        Piece newPiece = new Piece();
                        newPiece.add( tile );
                        for( Piece piece : surr ) newPiece.addAll( piece );

                        newPieces.add( newPiece );
                    }
                }

                if( newPieces.size() > 0 ) {

                    //Update group colors
                    newPieces.get( 0 ).col = curPiece.col;
                    for( int i = 1; i < newPieces.size(); i++ ) newPieces.get( i ).col = randomColor();
                    for( Piece piece : newPieces ) {
                        for( Tile tile : piece.tiles )
                            squares[ tile.x ][ tile.y ].setBackground( piece.col );
                    }


                    pieces.addAll( newPieces );
                }
            }
        }

        Main.updateStatus();
    }





    void clear() {
        pieces.clear();
        for( int i = 0; i < values.length; i++ ) {
            for( int j = 0; j < values[i].length; j++ )
                values[i][j] = false;
        }

        for( JButton[] row : squares ) {
            for( JButton col : row )
                if( col != null ) col.setBackground( SETTINGS_DEFAULTCOLOR );
        }

        //Main.updateStatus();
    }

    public void makeGrid() {
        if( panel == null ) return;

        for( JButton[] row : squares ) {
            for( JButton col : row ) {
                if( col != null ) col.setVisible( false );
                col = null;
            }
        }
        panel.removeAll();

        squares = new JButton[ SETTINGS_GRIDSIZE ][ SETTINGS_GRIDSIZE ];
        values = new Boolean[ SETTINGS_GRIDSIZE ][ SETTINGS_GRIDSIZE ];
        clear();

        //create the grid of buttons
        for( int i = 0; i < SETTINGS_GRIDSIZE; i++ ) {
            for( int j = 0; j < SETTINGS_GRIDSIZE; j++ ) {
                JButton button = new JButton();
                button.setFocusPainted( false );
                button.setEnabled( true );
                button.setBackground( SETTINGS_DEFAULTCOLOR );
                button.setBorder( new LineBorder( new Color( 200, 200, 200, 100 ), 1 ) );
                button.addMouseListener( new MouseListener() {
                     public void mouseClicked(MouseEvent e) {}
                     public void mousePressed(MouseEvent e) {
                         squarePressed( button, SwingUtilities.isLeftMouseButton(e) );
                         if( SwingUtilities.isLeftMouseButton(e) ) leftClick = true;
                         if( SwingUtilities.isRightMouseButton(e) ) rightClick = true;
                     }
                     public void mouseReleased(MouseEvent e) {
                         leftClick = false;
                         rightClick = false;
                     }
                     public void mouseEntered(MouseEvent e) {
                         if( leftClick || rightClick )
                            squarePressed( button, leftClick );
                     }
                     public void mouseExited(MouseEvent e) {}
                 } );



                panel.add( button );
                squares[ i ][ j ] = button;
            }
        }

        panel.setLayout( new GridLayout( SETTINGS_GRIDSIZE, SETTINGS_GRIDSIZE ) );
        panel.revalidate();
    }
    public JPanel makePanel() {
        panel = new JPanel();
        panel.setPreferredSize( new Dimension( SETTINGS_SIZE, SETTINGS_SIZE ) );

        makeGrid();
        panel.validate();

        return panel;
    }

    public Grid() { this( false, 5 ); }
    public Grid( boolean mode ) { this( mode, 5 ); }
    public Grid( boolean mode, int gridSize ) {
        SETTINGS_GRIDSIZE = gridSize;

        squares = new JButton[ SETTINGS_GRIDSIZE ][ SETTINGS_GRIDSIZE ];
        values = new Boolean[ SETTINGS_GRIDSIZE ][ SETTINGS_GRIDSIZE ];

        this.areaMode = mode;

        clear();
    }
}
