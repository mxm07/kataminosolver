import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;


public class Main {
    static JLabel status;
    static Grid topGrid;
    static Grid bottomGrid;

    JPanel topGridPanel;
    JPanel bottomGridPanel;
    
    
    public static void updateStatus() {
        int topnum = 0;
        int bottomnum = 0;


        if( topGrid.pieces != null ) for( Piece p : topGrid.pieces ) { topnum += p.tiles.size(); }
        if( bottomGrid.pieces != null ) for( Piece p : bottomGrid.pieces ) { bottomnum += p.tiles.size(); }

        status.setText( "Top: " + topnum + " tiles      Bottom: " + bottomnum + " tiles      Pieces: " + topGrid.pieces.size() );
    }
    public static void main( String[] args ) {
        topGrid = new Grid();
        JPanel topGridPanel = topGrid.makePanel();
        bottomGrid = new Grid( true );
        JPanel bottomGridPanel = bottomGrid.makePanel();



        JFrame frame = new JFrame( "KATAMINO SOLVER" );
        frame.setVisible( true );
        frame.setSize( 800, Grid.SETTINGS_SIZE*2 + 90 );
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setResizable( false );
        frame.setLayout( new FlowLayout( FlowLayout.LEADING, 30, 30 ) );




        JPanel mainPanel = new JPanel();
        mainPanel.setAlignmentY( Component.TOP_ALIGNMENT );
        mainPanel.setLayout( new FlowLayout( FlowLayout.CENTER, 0, 10 ) );
        mainPanel.setPreferredSize( new Dimension( Grid.SETTINGS_SIZE, Grid.SETTINGS_SIZE*2 + 90 ) );

        mainPanel.add( topGridPanel );
        mainPanel.add( bottomGridPanel );


        status = new JLabel("Top: 0 tiles      Bottom: 0 tiles      Pieces: 0");
        status.setAlignmentX( Component.CENTER_ALIGNMENT );
        mainPanel.add( status );




        /*/////////////////////////////////
        /////////   Menu Bar   //////////
        /////////////////////////////////
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic( KeyEvent.VK_F );
        menuBar.add( fileMenu );

        JMenuItem solveItem = new JMenuItem( "Solve" );
        solveItem.setMnemonic( KeyEvent.VK_S );
        solveItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( bottomGrid.groups.size() != 1 ) return;
                topGrid.solve( bottomGrid.groups.get(0) );
            }
        } );
        fileMenu.add( solveItem );


        fileMenu.add( new JSeparator() );



        JMenuItem clearItem = new JMenuItem( "Clear" );
        clearItem.setMnemonic( KeyEvent.VK_C );
        clearItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                topGrid.clear();
                bottomGrid.clear();
            }
        } );
        fileMenu.add( clearItem );



        JMenuItem gridSizeItem = new JMenuItem( "Grid Size" );
        gridSizeItem.setMnemonic( KeyEvent.VK_G );
        gridSizeItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JDialog dialog = new JDialog( new JFrame(), "Grid Size" );
                dialog.setVisible( true );
                dialog.setSize( 200, 100 );
                dialog.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
                dialog.setResizable( false );

                JSlider slider = new JSlider( JSlider.HORIZONTAL, 6, 12, 8 );
                slider.setMajorTickSpacing( 3 );
                slider.setMinorTickSpacing( 1 );
                slider.setValue( Grid.SETTINGS_GRIDSIZE );
                slider.setPaintTicks( true );
                slider.setPaintLabels( true );
                slider.setSnapToTicks( true );
                dialog.add( slider, BorderLayout.CENTER );

                dialog.addWindowListener( new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        if( Grid.SETTINGS_GRIDSIZE != slider.getValue() ) {
                            frame.dispose();
                            Grid.SETTINGS_GRIDSIZE = slider.getValue();
                            main( new String[ 0 ] );
                        }
                    }
                });


                JPanel padleft = new JPanel();
                padleft.setSize( new Dimension( 30, 30 ) );
                dialog.add( padleft, BorderLayout.WEST );

                JPanel padright = new JPanel();
                padright.setSize( new Dimension( 30, 30 ) );
                dialog.add( padright, BorderLayout.EAST );
            }
        } );
        fileMenu.add( gridSizeItem );
        ////////////////////////////////
        ///////  Menu Bar End  /////////
        ////////////////////////////////


        frame.setJMenuBar( menuBar );*/
        frame.add( mainPanel );





        JPanel sidePanel = new JPanel();
        sidePanel.setAlignmentY( Component.TOP_ALIGNMENT );
        sidePanel.setLayout( new FlowLayout( FlowLayout.CENTER, 20, 20 ) );
        sidePanel.setPreferredSize( new Dimension( Grid.SETTINGS_SIZE / 2 + 100, Grid.SETTINGS_SIZE * 2 ) );

        frame.add( sidePanel );

        /*JLabel titleLabel1 = new JLabel( "KATAMINO SOLVER" );
        titleLabel1.setFont( new Font( "Sans-serif", Font.BOLD, 20 ) );
        sidePanel.add( titleLabel1 );*/

        JLabel gridSizeLabel = new JLabel( "Grid Size" );
        sidePanel.add( gridSizeLabel );

        JSlider gridSizeSlider = new JSlider( JSlider.HORIZONTAL, 5, 20, Grid.SETTINGS_GRIDSIZE );
        gridSizeSlider.setPreferredSize( new Dimension( 100, 50 ) );
        gridSizeSlider.setMajorTickSpacing( 3 );
        gridSizeSlider.setMinorTickSpacing( 1 );
        gridSizeSlider.setPaintTicks( true );
        gridSizeSlider.setPaintLabels( true );
        gridSizeSlider.setSnapToTicks( true );
        gridSizeSlider.addChangeListener( new ChangeListener() {
                                                public void stateChanged( ChangeEvent event ) {
                                                    Grid.SETTINGS_GRIDSIZE = gridSizeSlider.getValue();
                                                    topGrid.makeGrid();
                                                    bottomGrid.makeGrid();
                                                } } );
        sidePanel.add( gridSizeSlider );



        /*JCheckBox rotateCheckBox = new JCheckBox( "Allow rotation?" );
        rotateCheckBox.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    topGrid.rotMode = rotateCheckBox.isSelected();
                                                    System.out.println( topGrid.rotMode );
                                                }
                                          } );
        sidePanel.add( rotateCheckBox );*/




        JButton clearButton = new JButton( "   Clear   " );
        clearButton.addActionListener( new ActionListener() {
                                            public void actionPerformed( ActionEvent e ) {
                                                topGrid.clear();
                                                bottomGrid.clear();
                                            }
                                        } );
        sidePanel.add( clearButton );




        JButton solveButton = new JButton( "   Solve   " );
        solveButton.addActionListener( new ActionListener() {
                                           public void actionPerformed( ActionEvent e ) {
                                               if( bottomGrid.pieces.size() != 1 ) return;
                                               topGrid.solve( bottomGrid.pieces.get( 0 ) );
                                           }
                                       } );
        sidePanel.add( solveButton );


        JButton rotateTestButton = new JButton( "   Rotate   " );
        rotateTestButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                topGrid.rotTest();
            }
        } );
        sidePanel.add( rotateTestButton );

        frame.pack();
    }
}
