package movingcircle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

public class MovingCirclePanel extends JPanel implements KeyListener {

    private static final long hertz = 60;
    private static final long refreshRate = 1000 / hertz;
    private static final double deltaT = 1.0 / refreshRate;
    private static final double deltaT2 = deltaT * deltaT;
    private final int left = KeyEvent.VK_LEFT, up = KeyEvent.VK_UP, right = KeyEvent.VK_RIGHT, down = KeyEvent.VK_DOWN;
    private final double acceleration = hertz * 2 * deltaT;

    private final int ballSize = 20;
    private final Dimension dims;
    private final double friction = 1d - ((80.0 / 100.0) / hertz);
    private final double hardLimit = 5.0;
    private double maxSpeed = 100;
    private final HashMap<Integer, Boolean> pressed;
    private double velocityX = 0;
    private double velocityY = 0;
    private int x, y;
    long lastTime = System.currentTimeMillis();
    private int step = 1;
    private int currentBlobX, currentBlobY;
    private int points = 0;
    private int pointsBlob = 100;

    public MovingCirclePanel(Dimension iniDims) {
        super();
        this.pressed = new HashMap<Integer, Boolean>();
        this.pressed.put(left, false);
        this.pressed.put(up, false);
        this.pressed.put(right, false);
        this.pressed.put(down, false);
        this.dims = iniDims;
        x = (int) (dims.getWidth() / 2);
        y = (int) (dims.getHeight() / 2);
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                repaint();
            }
        }, 0L, refreshRate);
        currentBlobX = (int) (dims.getWidth() / 2);
        currentBlobY = (int) (dims.getHeight() / 2);

    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        registerKeyEvent(e, true);

    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        registerKeyEvent(e, false);

    }

    @Override
    public void keyTyped(KeyEvent e) {
        keyPressed(e);
        keyReleased(e);
    }

    private void hitBlob() {
        points += pointsBlob;
        currentBlobX = (int) (Math.random() * dims.getWidth() / 2);
        currentBlobY = (int) (Math.random() * dims.getHeight() / 2);
    }

    private void changeVelocityX(boolean swap) {
        if (Math.abs(velocityX) > maxSpeed) {
            return;
        }
        if (swap) {
            velocityX = (velocityX - acceleration);
        } else {
            velocityX = (velocityX + acceleration);
        }
    }

    private void changeVelocityY(boolean swap) {
        if (Math.abs(velocityY) > maxSpeed) {
            return;
        }

        if (swap) {
            velocityY = (velocityY - acceleration);
        } else {
            velocityY = (velocityY + acceleration);
        }

    }

    private void registerKeyEvent(KeyEvent e, Boolean b) {
        for (Map.Entry<Integer, Boolean> entry : pressed.entrySet()) {
            if (entry.getKey() == e.getKeyCode()) {
                pressed.put(entry.getKey(), b);
            }
        }

    }
    int blobSize = 20;
    private Color blobColor = Color.black;

    private boolean touchingBlob() {
        int tw = (int) (ballSize * 0.9);
        int th = (int) (ballSize);
        int rw = blobSize;
        int rh = blobSize;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = x;
        int ty = y;
        int rx = currentBlobX;
        int ry = currentBlobY;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
    }

    private int xPressed() {
        int toRet = 0;
        if (pressed.get(left)) {
            toRet++;
        }
        if (pressed.get(right)) {
            toRet++;
        }
        return toRet;
    }

    private int yPressed() {
        int toRet = 0;
        if (pressed.get(up)) {
            toRet++;
        }
        if (pressed.get(down)) {
            toRet++;
        }

        return toRet;
    }
    int oldX, oldY;
    boolean doCheck = false;

    @Override
    public synchronized void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        Graphics2D g2 = (Graphics2D) g;
        if (doCheck) {
            oldX = x;
            oldY = y;
            doCheck = false;
        } else {
            doCheck = true;
        }
        for (Map.Entry<Integer, Boolean> i : pressed.entrySet()) {
            if (!i.getValue()) {
                continue;
            }
            switch (i.getKey()) {
                case left:
                    changeVelocityX(true);
                    break;
                case up:
                    changeVelocityY(true);
                    break;
                case right:
                    changeVelocityX(false);
                    break;
                case down:
                    changeVelocityY(false);
                    break;
            }
        }

        // x
        int xPressed = xPressed();
        if (xPressed == 0 || xPressed == 2) {
            if (Math.abs(velocityX) > 0.0) {
                velocityX = velocityX * friction;
            }
            if (Math.abs(velocityX) <= hardLimit) {
                velocityX = 0;
            }
        }
        if (velocityX > 0) {
            if (x > getWidth()) {
                x = 0;
            }
        } else if (velocityX < 0) {
            if (x < 0) {
                x = getWidth() - 1;
            }
        }

        x = (int) (x + velocityX * deltaT);

        // y
        int yPressed = yPressed();
        if (yPressed == 0 || yPressed == 2) {
            if (Math.abs(velocityY) > 0.0) {
                velocityY = velocityY * friction;
            }
            if (Math.abs(velocityY) <= hardLimit) {
                velocityY = 0;
            }
        }
        if (velocityY > 0) {
            if (y > getHeight()) {
                y = 0;
            }
        } else if (velocityY < 0) {
            if (y < 0) {
                y = getHeight() - 1;
            }
        }

        y = (int) (y + velocityY * deltaT);
        // trail

        g2.setColor(Color.gray);
        g2.fillOval(oldX, oldY, ballSize, ballSize);

        g2.setColor(Color.black);
        g2.fillOval(x, y, ballSize, ballSize);

        g2.setColor(blobColor);
        g2.fillOval(currentBlobX, currentBlobY, blobSize, blobSize);
        if (touchingBlob()) {
            hitBlob();
        }
        g2.drawString("Points: " + points, 10, 10);

    }
}
