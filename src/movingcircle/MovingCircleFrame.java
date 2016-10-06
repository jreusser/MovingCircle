/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movingcircle;

import java.awt.Dimension;
import java.awt.PopupMenu;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author jreusser
 */
class MovingCircleFrame extends JFrame {

    private MovingCirclePanel movingCirclePanel;

    public MovingCircleFrame() {
        super();
        Dimension dims = new Dimension(800, 700);
        setSize(dims);
        movingCirclePanel = new MovingCirclePanel(dims);
        getContentPane().add(movingCirclePanel);
        addKeyListener(movingCirclePanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(dims);
        pack();
        setVisible(true);
    }

}
