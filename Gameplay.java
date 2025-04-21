package BrickBreaker;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;
import javax.swing.JPanel;

public class Gameplay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score =0;
    private int rowbricks=5 ,colbricks=10;
    private int totalbricks = rowbricks*colbricks;
    private Timer time;
    private int delay = 5;
    private int player1= 310;
    private int ballposx = 120;
    private int ballposy = 350;
    private float ballxdir = -1;
    private float ballydir = -2;
    private float paddleSpeed = 1;
    private float initPaddleSpeed = 1;
    private float maxPaddleSpeed = 40;
    private float acceleration = 1f;
    private boolean isMovingRight = false;
    private boolean isMovingLeft = false;



    private mapbrick map;

    public Gameplay() {
        map = new mapbrick (rowbricks,colbricks);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        time = new Timer(delay, this);
        time.start();
    }

    public void paint(Graphics g) {
        //bg
        g.setColor(Color.black);
        g.fillRect(1,1, 692, 592);

        //draw
        map.draw((Graphics2D)g);

        //borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0,0 , 692, 3);
        g.fillRect(681,0,3, 592);

        //scores
        g.setColor(Color.white);
        g.setFont(new Font("Verdana", Font.BOLD, 25));
        g.drawString(""+score, 590, 30);
        //paddle
        g.setColor(Color.green);
        g.fillRect(player1, 550, 100,8);

        //ball
        g.setColor(Color.yellow);
        g.fillOval(ballposx,ballposy, 20, 20);

        if(totalbricks <=0) {
            play = false;
            ballxdir = 0;
            ballydir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("Verdana", Font.BOLD, 35));
            g.drawString("YOU WON!", 190, 300);

            g.setFont(new Font("Verdana", Font.BOLD, 30));
            g.drawString("Press ENTER to restart",230, 350);
        }

        if (ballposy > 570) {
            play = false;
            ballxdir = 0;
            ballydir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("Verdana", Font.BOLD, 35));
            g.drawString("GAME OVER!", 190, 300);

            g.setFont(new Font("Verdana", Font.BOLD, 30));
            g.drawString("Press ENTER to restart",230, 350);

        }
        g.dispose();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        time.start();
        if(play) {
            // Paddle movement
            if (isMovingRight) {
                moveright();
            }
            if (isMovingLeft) {
                moveleft();
            }
            //detecting interstion of 2 objs
//            if (new Rectangle(ballposx,ballposy, 20, 20).intersects(new Rectangle(player1, 550, 100, 8))) {
//                ballydir = -ballydir;
//            }

            // Constants
            final float MAX_BOUNCE_ANGLE = (float) Math.PI / 3; // Adjust as needed
            final float BALL_SPEED = 5.0f; // Adjust as needed

            if (new Rectangle(ballposx, ballposy, 20, 20).intersects(new Rectangle(player1, 550, 100, 8))) {
                // Simple bounding box intersection

                // Calculate the relative x position of the collision on the paddle
                float relativeIntersectX = (ballposx + 20 / 2.0f) - (player1 + 100 / 2.0f);
                float normalizedIntersectX = relativeIntersectX / (100 / 2.0f); // -1 to +1

                // Adjust the ball's angle based on where it hit the paddle
                float bounceAngle = normalizedIntersectX * MAX_BOUNCE_ANGLE;

                // Calculate the ball's new velocity components
                float newBallXSpeed = (float) (BALL_SPEED * Math.sin(bounceAngle));
                float newBallYSpeed = (float) (BALL_SPEED * -Math.cos(bounceAngle));

                // Set the ball's new velocity
                ballxdir = newBallXSpeed;
                ballydir = newBallYSpeed;

                // Normalize (optional, to keep speed constant)
                float speed = (float) Math.sqrt(ballxdir * ballxdir + ballydir * ballydir);
                ballxdir /= speed;
                ballydir /= speed;
                ballxdir *= BALL_SPEED;
                ballydir *= BALL_SPEED;
            }




            B: for (int i=0; i< map.map.length; i++) {
                for (int j=0; j<map.map[0].length; j++) {
                    if (map.map[i][j]>0) {
                        int brickx = j*map.brickwidth + 80;
                        int bricky = i* map.brickheight + 50;
                        int brickwidth = map.brickwidth;
                        int brickheight = map.brickheight;

                        Rectangle rect = new Rectangle(brickx, bricky, brickwidth, brickheight);
                        Rectangle ballrect = new Rectangle(ballposx, ballposy, 20, 20);
                        Rectangle brickrect = rect;

                        if(ballrect.intersects(brickrect)) {
                            map.setBrickval(0,i,j);
                            totalbricks--;
                            score += 10;

                            if(ballposx + 19 <= brickrect.x || ballposx + 1 >= brickrect.x + brickrect.width) {
                                ballxdir = - ballxdir;
                            } else {
                                ballydir = -ballydir;
                            }

                            break B;
                        }
                    }
                }
            }
            ballposx += ballxdir;
            ballposy += ballydir;
            if (ballposx < 0) {
                ballxdir = - ballxdir;
            }
            if (ballposy < 0) {
                ballydir = - ballydir;
            }
            if (ballposx > 670) {
                ballxdir = - ballxdir;
            }

        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

//    @Override
//    public void keyReleased(KeyEvent e) {}
@Override
public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
        isMovingRight = false;
        paddleSpeed = initPaddleSpeed; // Reset to initial speed
    }
    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
        isMovingLeft = false;
        paddleSpeed = initPaddleSpeed; // Reset to initial speed
    }
}


    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            isMovingRight = true;
            isMovingLeft = false;
            if(player1 >= 600) {
                player1 = 600;
            } else {
                moveright();
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            isMovingLeft = true;
            isMovingRight = false;
            if(player1 <10) {
                player1 = 10;
            } else {
                moveleft();
            }
        }

        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballposx = 120;
                ballposy = 350;
                ballxdir = 0;
                ballydir = 1;
                player1 = 310;
                score = 0;
                totalbricks = rowbricks*colbricks;
                map = new mapbrick(3,7);

                repaint();
            };

        }
    }

//    public void moveright() {
//        play = true;
//        player1+=20;
//    }
    public void moveright() {
        play = true;
        paddleSpeed = Math.min(maxPaddleSpeed, paddleSpeed + acceleration);
        player1 += paddleSpeed;
    }

//    public void moveleft() {
//        play = true;
//        player1 -=20;
//    }
    public void moveleft() {
        play = true;
        paddleSpeed = Math.min(maxPaddleSpeed, paddleSpeed + acceleration);
        player1 -= paddleSpeed;
    }


}