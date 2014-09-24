/*
 * Tam_Ryan, May_2013, Final Project, ICS3U, Lim_Mister
 */
package tam_ryan_final_project;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Ryan
 */
public class gamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

    //images-images used
    Image startScreen;
    Image backgroundImage;
    Image gameover;
    Image instructionsScreen;
    Image levelComplete;
    Image levelFailed;
    Image tankBase;
    Image enemyTankBase;
    Image blackX;
    Image redX;
    Image paused;
    Image loosingScreen;
    Image highScoreScreen;
    Image bulletBillA;
    Image brick;
    BufferedImage bullet;
    URL imgURL;
    MediaTracker mediaTracker = new MediaTracker(this);
    //window set up-size of window
    int width;
    int height;
    int boundary = 100;
    //
    //pregame-start menu
    boolean gameStarted = false;
    boolean cheatON = false;//if space was pressed when you pressed play it will jump to 15
    boolean instructions = false;//dispace instructions page
    //
    //game set up-setting up game
    int level = 1;//level
    int lives = 3;//lives
    double tankSpeed = 2;//my speed
    double enemySpeed = 2;//enemy speed
    //
    //level set up-parameters for each level
    int enemyNumb = 0;//number of enemies each match
    int enemyDeathsToTurnOnAI = 0;//number of enemy deaths untill all tanks turn ai
    //
    //game
    int enemyDeaths = 0;//number dead
    //
    //movement
    int tankX = 200;//my X
    int tankY = 448;//my Y
    boolean moveRight = false;//move right
    boolean moveLeft = false;
    boolean moveUp = false;
    boolean moveDown = false;
    boolean hitRight = false;//hit something on the right
    boolean hitLeft = false;
    boolean hitUp = false;
    boolean hitDown = false;
    //
    //shooting
    int mouseX;
    int mouseY;
    int bulletsFired = 0;//how many you fired
    double bulletSpeed = 5;//speed of bullet
    long lastFiredTime;//time last fired
    int bulletMag = 6;//how many shoot you have
    long recoverTime = 600000000;//recover time of bullets
    long triggerSpeed = 300000000;
    boolean fire = false;//fire
    boolean spawned = false;// to prevent fireing twice with on click
    //
    //pause
    boolean pause = false;
    boolean ppressed = false;//to prvent double pressed
    //
    //Collision Detection
    int tankSize = 75;//size of tank
    int tankBulletMin = 34;//distance between bullet and tank that are considered collision
    int tankBulletClear = 36;//distance between them that the bullet has left the tank
    int bulletBulletMin = 10;//distance btween bullet and bullet that is considered them colliding
    int tankStopMin = 200;//distacne between 2 tanks that one should yield
    int tankStopClear = 300;//distance between 2 tanks that they are far enought to both move
    int tankTankMin = 85;//distance between two tanks that they should revers dirrection
    int tankTankClear = 90;//distance bwetween two tanks that they are far enough appart to be filped again
    //
    //enemies & bullets
    int trackDistance = 300;//distance less than this the enemy should follow my tank in a circle
    int unTrackWidth = 350;//distance greter than this the enemy should follow me
    ArrayList<bullet> activeBullets = new ArrayList();//active bullets array list
    ArrayList<enemyTank> activeTanks = new ArrayList();//active enemy array list
    ArrayList<brick>  activeBricks = new ArrayList(); 
    Iterator bulletitr;
    Iterator tankitr;
    Iterator bulletremover;
    Iterator tankremover;
    Iterator brickitr;
    Iterator brickremover;
    //
    //god mode
    int godModeCounter = 0;
    double godModeBulletSpeed = 5;//bullet speed in god mode
    boolean godMODE = false;
    boolean godMODEDispalyed = false;
    //
    //post game
    boolean levelFail = false;
    boolean levelWon = false;
    boolean gameOver = false;
    boolean statsPage = false;
    //
    //highscores
    int y;//distnae to start preting the high score
    int fontSizeA = 100;//font size
    int decryptionKey = 2;//decryption key for high score
    String slevel;//level in string so it can be displayed
    String slives;//lives in string form
    String name = null;//name of the person entering high score
    String highScoreLevel;//that level
    boolean vaildName = false;//if the name is valid
    boolean displayHighScore = false;
    boolean highscoreUnavalible = false;
    highScores[] scores = new highScores[5];
    //
    //other
    char keyPressed;
    char keyReleased;
    String logger;
    boolean printLine = false;//turns the game debug print line on or off

    //reset the field
    public void resetField() {
        //reset variables
        enemyDeaths = 0;
        enemyDeathsToTurnOnAI = 0;
        bulletsFired = 0;

        //if you won move to the next level
        if (levelWon == true) {
            levelWon = false;
            level++;
        }

        //convert level and lives to a string so they can be displayed
        slevel = Integer.toString(level);
        slives = "Lives:" + Integer.toString(lives);

        //remove all the tanks
        tankremover = activeTanks.iterator();

        while (tankremover.hasNext()) {
            enemyTank theTank = (enemyTank) tankremover.next();

            theTank.dead = true;
            try {
                tankremover.remove();
            } catch (ConcurrentModificationException e) {
                theTank = null;
            }
        }

        //remove all the bullets
        bulletremover = activeBullets.iterator();

        while (bulletremover.hasNext()) {
            bullet theBullet = (bullet) bulletremover.next();

            theBullet.hitBullet = true;
            try {
                bulletremover.remove();
            } catch (ConcurrentModificationException e) {
                theBullet = null;
            }
        }
        
        //wall removers
        brickremover = activeBricks.iterator();
        
        while (brickremover.hasNext()){
            brick theBrick = (brick) brickremover.next();
                try {
                brickremover.remove();
            } catch (ConcurrentModificationException e) {
                theBrick = null;
            }
        }

        //spawn opponents based on level
        switch (level) {
            case 1:
                tankX = 450;//my coridinates
                tankY = 450;
                activeTanks.add(new enemyTank(1150, 450, 1, false));//first enemy (coridinates, its number, weather it is smart)
                activeBricks.add(new brick(400,500));
                enemyNumb = 1;//number of ennemies
                break;
            case 2:
                tankX = 450;
                tankY = 450;
                activeTanks.add(new enemyTank(1150, 275, 1, false));
                activeTanks.add(new enemyTank(1150, 625, 2, false));
                enemyNumb = 2;
                break;
            case 3:
                tankX = 800;
                tankY = 450;
                activeTanks.add(new enemyTank(450, 625, 1, false));
                activeTanks.add(new enemyTank(1150, 275, 2, false));
                enemyNumb = 2;
                break;
            case 4:
                tankX = 450;
                tankY = 450;
                activeTanks.add(new enemyTank(1150, 450, 1, true));
                enemyNumb = 1;
                break;
            case 5:
                tankX = 1150;
                tankY = 625;
                activeTanks.add(new enemyTank(450, 275, 1, false));
                activeTanks.add(new enemyTank(450, 625, 2, false));
                activeTanks.add(new enemyTank(800, 450, 3, false));
                activeTanks.add(new enemyTank(1150, 275, 4, false));
                enemyNumb = 4;
                break;
            case 6:
                tankX = 450;
                tankY = 450;
                activeTanks.add(new enemyTank(1150, 275, 1, true));
                activeTanks.add(new enemyTank(1150, 625, 2, true));
                enemyNumb = 2;
                break;
            case 7:
                tankX = 800;
                tankY = 450;
                activeTanks.add(new enemyTank(450, 275, 1, true));
                activeTanks.add(new enemyTank(450, 625, 2, false));
                activeTanks.add(new enemyTank(1150, 625, 3, true));
                activeTanks.add(new enemyTank(1150, 275, 4, false));
                enemyNumb = 4;
                break;
            case 8:
                tankX = 800;
                tankY = 450;
                activeTanks.add(new enemyTank(450, 275, 1, false));
                activeTanks.add(new enemyTank(450, 625, 2, false));
                activeTanks.add(new enemyTank(1150, 625, 3, false));
                activeTanks.add(new enemyTank(1150, 275, 4, false));
                activeTanks.add(new enemyTank(800, 725, 5, false));
                activeTanks.add(new enemyTank(800, 175, 6, false));
                enemyNumb = 6;
                enemyDeathsToTurnOnAI = 4;//after 4 have been killed all tanks become AI
                break;
            case 9:
                tankX = 800;
                tankY = 725;
                activeTanks.add(new enemyTank(200, 625, 1, false));
                activeTanks.add(new enemyTank(450, 175, 2, true));
                activeTanks.add(new enemyTank(1150, 175, 3, true));
                activeTanks.add(new enemyTank(1400, 625, 4, false));
                enemyNumb = 4;
                break;
            case 10:
                tankX = 450;
                tankY = 450;
                activeTanks.add(new enemyTank(1400, 175, 1, false));
                activeTanks.add(new enemyTank(1400, 275, 2, false));
                activeTanks.add(new enemyTank(1400, 450, 3, false));
                activeTanks.add(new enemyTank(1400, 625, 4, false));
                activeTanks.add(new enemyTank(1400, 725, 5, false));
                enemyDeathsToTurnOnAI = 3;
                enemyNumb = 5;
                break;
            case 11:
                tankX = 800;
                tankY = 450;
                activeTanks.add(new enemyTank(200, 175, 1, false));
                activeTanks.add(new enemyTank(200, 725, 2, false));
                activeTanks.add(new enemyTank(450, 450, 3, false));
                activeTanks.add(new enemyTank(1150, 450, 4, false));
                activeTanks.add(new enemyTank(1400, 725, 5, false));
                activeTanks.add(new enemyTank(1400, 125, 6, false));
                enemyDeathsToTurnOnAI = 4;
                enemyNumb = 6;
                break;
            case 12:
                tankX = 800;
                tankY = 450;
                activeTanks.add(new enemyTank(200, 450, 1, true));
                activeTanks.add(new enemyTank(800, 175, 2, true));
                activeTanks.add(new enemyTank(1400, 450, 3, true));
                activeTanks.add(new enemyTank(800, 725, 4, true));
                enemyNumb = 4;
                break;
            case 13:
                tankX = 800;
                tankY = 450;
                activeTanks.add(new enemyTank(200, 275, 1, false));
                activeTanks.add(new enemyTank(200, 625, 2, false));
                activeTanks.add(new enemyTank(450, 450, 3, true));
                activeTanks.add(new enemyTank(1150, 450, 4, true));
                activeTanks.add(new enemyTank(1400, 275, 5, false));
                activeTanks.add(new enemyTank(1400, 625, 6, false));
                enemyNumb = 6;
                break;
            case 14:
                tankX = 800;
                tankY = 450;
                activeTanks.add(new enemyTank(200, 275, 1, false));
                activeTanks.add(new enemyTank(200, 625, 2, false));
                activeTanks.add(new enemyTank(200, 450, 3, false));
                activeTanks.add(new enemyTank(1400, 450, 4, true));
                activeTanks.add(new enemyTank(1400, 275, 5, false));
                activeTanks.add(new enemyTank(1400, 625, 6, false));
                enemyDeathsToTurnOnAI = 4;
                enemyNumb = 6;
                break;
            case 15:
                tankX = 200;
                tankY = 175;
                activeTanks.add(new enemyTank(200, 625, 1, false));
                activeTanks.add(new enemyTank(800, 175, 2, false));
                activeTanks.add(new enemyTank(800, 450, 3, true));
                enemyNumb = 3;
                //reset god mode
                godMODEDispalyed = false;
                godModeBulletSpeed = bulletSpeed;
                godModeCounter = 0;
                break;
            default://keep playing this level
                tankX = 800;
                tankY = 450;
                activeTanks.add(new enemyTank(1400, 175, 1, false));
                activeTanks.add(new enemyTank(1400, 275, 2, false));
                activeTanks.add(new enemyTank(1400, 450, 3, false));
                activeTanks.add(new enemyTank(1400, 625, 4, false));
                activeTanks.add(new enemyTank(1400, 725, 5, false));
                activeTanks.add(new enemyTank(200, 175, 6, false));
                activeTanks.add(new enemyTank(200, 275, 7, false));
                activeTanks.add(new enemyTank(200, 450, 8, false));
                activeTanks.add(new enemyTank(200, 625, 9, false));
                activeTanks.add(new enemyTank(200, 725, 10, false));
                enemyNumb = 10;
                godModeBulletSpeed += .5;//speed increase each round
                godModeCounter++;
                recoverTime *= .9;//recover time of bullets
                triggerSpeed *= .9;
                tankSpeed +=0.2;
                enemyDeathsToTurnOnAI = enemyNumb - godModeCounter;//number of AI increase each round
                break;
        }

        //if level greater than or equal to 16 you are in god mode
        if (level >= 16) {
            godMODE = true;
        } else {
            //if big mode is false (in the small mode) eveything is /2
            godMODE = false;
            godModeBulletSpeed = 5;
            bulletSpeed = 5;
            recoverTime = 600000000;
            triggerSpeed = 300000000;
        }
    }

    //constructor
    gamePanel() throws IOException {

        //normal size 1600X900
        //load all the images
        imgURL = getClass().getResource("titlePage.png");
        startScreen = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(startScreen, 0);

        imgURL = getClass().getResource("backgroundImage.png");
        backgroundImage = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(backgroundImage, 0);

        imgURL = getClass().getResource("levelComplete.png");
        levelComplete = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(levelComplete, 0);

        imgURL = getClass().getResource("levelFailed.png");
        levelFailed = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(levelFailed, 0);

        imgURL = getClass().getResource("gameover.png");
        gameover = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(gameover, 0);

        imgURL = getClass().getResource("paused.png");
        paused = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(paused, 0);

        imgURL = getClass().getResource("Instructions.png");
        instructionsScreen = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(instructionsScreen, 0);

        imgURL = getClass().getResource("highScores.png");
        highScoreScreen = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(highScoreScreen, 0);

        imgURL = getClass().getResource("loosingPage.png");
        loosingScreen = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(loosingScreen, 0);

        imgURL = getClass().getResource("X.png");
        blackX = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(blackX, 0);

        imgURL = getClass().getResource("redX.png");
        redX = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(redX, 0);

        imgURL = getClass().getResource("tankBase.png");
        tankBase = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(tankBase, 0);

        imgURL = getClass().getResource("enemyTankBase.png");
        enemyTankBase = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(enemyTankBase, 0);
        
        imgURL = getClass().getResource("bulletBillA.png");
        bulletBillA = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(bulletBillA, 0);

        imgURL = getClass().getResource("brick.png");
        brick = Toolkit.getDefaultToolkit().getImage(imgURL);
        mediaTracker.addImage(brick, 0);

        imgURL = this.getClass().getResource("goldBulletBill.png");
        bullet = ImageIO.read(imgURL);
        mediaTracker.addImage(bullet, 0);

        //wait for them to load
        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException ex) {
            Logger.getLogger(gamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        //store width and heigh of background
        width = backgroundImage.getWidth(this);
        height = backgroundImage.getHeight(this);

        //add mouse and keyboard inputs
        addKeyListener(
                this);
        addMouseMotionListener(
                this);
        addMouseListener(
                this);

        //set the side of the panel to the background size
        setSize(width, height);

        //set it to visible
        setVisible(
                true);
        //get focouse
        setFocusable(
                true);
        requestFocus();
    }

    @Override
    public void paintComponent(Graphics g) {

        //if the game has started draw all the game elements
        if (gameStarted == true) {
            g.drawImage(backgroundImage, 0, 0, this);//draw background always

            //draw ennemy tanks
            //cycle through tank array draw an "X" if that tank is dead draw the tank if it is not
            tankitr = activeTanks.iterator();

            while (tankitr.hasNext()) {
                enemyTank theTank = (enemyTank) tankitr.next();

                if (theTank.dead == true) {
                    g.drawImage(redX, (int) (theTank.enemyX - (redX.getWidth(this) / 2)), (int) (theTank.enemyY - (redX.getHeight(this) / 2)), this);
                } else {
                    g.drawImage(enemyTankBase, (int) (theTank.enemyX - (enemyTankBase.getWidth(this) / 2)), (int) (theTank.enemyY - (enemyTankBase.getHeight(this) / 2)), this);
                }
            }
            
            brickitr = activeBricks.iterator();
            
            while(brickitr.hasNext()){
                brick theBrick = (brick) brickitr.next();
                    g.drawImage(brick, (int) (theBrick.brickX - (brick.getWidth(this)/2)),(int) (theBrick.brickY - (brick.getWidth(this)/2)), this);
            }

            //draw my tank
            if (!gameOver) {

                //if level faild or game over draw an "X" else draw the thank
                if (!levelFail) {
                    g.drawImage(tankBase, tankX - (tankBase.getWidth(this) / 2), tankY - (tankBase.getHeight(this) / 2), this);

                    //draw the number of bullets in mag
                    for (int b = 1; b < bulletMag - bulletsFired + 1; b++) {
                        g.drawImage(bulletBillA, 50 + (25 * b) - (bulletBillA.getWidth(this) / 2), 800 - (bulletBillA.getHeight(this) / 2), this);
                    }
                } else {
                    g.drawImage(blackX, tankX - (blackX.getWidth(this) / 2), tankY - (blackX.getHeight(this) / 2), this);
                }

                //if level faild or game over draw an "X" else draw the thank
            } else {
                g.drawImage(blackX, tankX - (blackX.getWidth(this) / 2), tankY - (blackX.getHeight(this) / 2), this);
            }

            //draw the bullet
            //cycle through bullet array and draw the bullets
            bulletitr = activeBullets.iterator();

            while (bulletitr.hasNext()) {
                bullet theBullet = (bullet) bulletitr.next();

                //rotate the bullet
                AffineTransform affineTransform = new AffineTransform();
                affineTransform.rotate(theBullet.bulletAngle, bullet.getWidth() / 2, bullet.getHeight() / 2);
                AffineTransformOp opRotated = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);

                //bulletRotated is a reotated version of the bullet
                BufferedImage bulletRotated = opRotated.filter(bullet, null);

                //draw the rotated bullet
                g.drawImage(bulletRotated, (int) theBullet.bulletX - (bulletRotated.getWidth(this) / 2), (int) theBullet.bulletY - (bulletRotated.getHeight(this) / 2), this);
            }

            //draw paused sign
            //if the game is paused
            if (pause) {
                g.drawImage(paused, 0, 0, this);
            }

            //draw game over sign
            //if the game is over display game over sign
            if (gameOver) {
                g.drawImage(gameover, 0, 0, this);
            }

            //draw level won sign
            //if you beat the level, draw the level won sign and also display how many lives and what level you have just beaten
            if (levelWon) {

                g.drawImage(levelComplete, 0, 0, this);
                g.setFont(new Font("Impact", Font.BOLD, fontSizeA));
                g.setColor(Color.RED);
                g.drawString(slevel, 1000, 450);
                g.drawString(slives, 600, 650);
                //if you are in god mode display the words "god mode"
                if (godMODE == true) {
                    g.setFont(new Font("Impact", Font.BOLD, fontSizeA));
                    g.setColor(Color.RED);
                    g.drawString("GOD MODE", 300, 300);
                }
            }

            //draw level failed sign
            //if you failed the level, draw the level failed sign and also display how many lives
            if (levelFail == true) {

                g.drawImage(levelFailed, 0, 0, this);
                g.setFont(new Font("Impact", Font.BOLD, fontSizeA));
                g.setColor(Color.RED);
                slives = "Lives:" + Integer.toString(lives);
                g.drawString(slives, 600, 650);

            }
            //if the game has not started
        } else //disply instructions on the instructions page
        if (instructions == true) {
            g.drawImage(instructionsScreen, 0, 0, this);
        } else {
            g.drawImage(startScreen, 0, 0, this);
        }

        //display the stats page
        //show how many level you go to and shows the looing screen
        if (statsPage == true) {

            g.drawImage(loosingScreen, 0, 0, this);
            g.setFont(new Font("Impact", Font.BOLD, fontSizeA));
            g.setColor(Color.RED);
            g.drawString(slevel, 1400, 150);
            y = 325;

            //writes out the highscores
            for (int j = 0; j < scores.length; j++) {
                highScoreLevel = Integer.toString(scores[j].level);
                y += 100;
                g.setFont(new Font("Impact", Font.BOLD, fontSizeA));
                g.setColor(Color.RED);
                g.drawString(scores[j].name, 500, y);
                g.drawString(highScoreLevel, 900, y);
            }

        }

        //display the high scores page
        if (displayHighScore == true) {
            //if in large mode

            g.drawImage(highScoreScreen, 0, 0, this);
            y = 325;

            //writes out the high scores
            for (int i = 0; i < scores.length; i++) {
                highScoreLevel = Integer.toString(scores[i].level);
                y += 100;//lower next line of text
                g.setFont(new Font("Impact", Font.BOLD, fontSizeA));
                g.setColor(Color.RED);
                g.drawString(scores[i].name, 500, y);
                g.drawString(highScoreLevel, 900, y);
            }
        }
    }

    //if a key was pressed
    @Override
    public void keyPressed(KeyEvent e) {
        //see what key it was
        keyPressed = (e.getKeyChar());
        switch (keyPressed) {
            case 'a'://if it was "a" and the game is still on, move left
                if (!gameOver && !levelWon && !levelFail) {
                    moveLeft = true;
                }
                break;
            case 'd'://if it was "d" and the game is still on, move right
                if (!gameOver && !levelWon && !levelFail) {
                    moveRight = true;
                }
                break;
            case 'w'://if it was "w" and the game is still on, move up
                if (!gameOver && !levelWon && !levelFail) {
                    moveUp = true;
                }
                break;
            case 's'://if it was "s" and the game is still on, move down
                if (!gameOver && !levelWon && !levelFail) {
                    moveDown = true;
                }
                break;
            case 'p'://if it was "p" pause or unpause the game depending if it was already paused or not
                if (pause == false && ppressed == false && gameStarted) {
                    pause = true;
                    ppressed = true;
                } else if (pause == true && ppressed == false && gameStarted) {
                    pause = false;
                    ppressed = true;
                }
                break;
            case 32://if it was space proced to the next page
                if (statsPage == true) {
                    statsPage = false;
                    lives = 3;
                    vaildName = false;
                    resetField();
                }
                if (gameOver == true) {
                    gameStarted = false;
                    gameOver = false;
                    statsPage = true;
                }
                cheatON = true;
                break;
            default:
                System.err.println("KEY NOT SUPPORTED");
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyReleased = (e.getKeyChar());
        switch (keyReleased) {
            //set to fale if relesed
            case 'a':
                moveLeft = false;
                break;
            case 'd':
                moveRight = false;
                break;
            case 'w':
                moveUp = false;
                break;
            case 's':
                moveDown = false;
                break;
            case 'p':
                ppressed = false;
                break;
            case 32:
                cheatON = false;
            default:
                //System.err.println("KEY NOT SUPPORTED");
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //shoot a bullet
        if (spawned == false && gameStarted == true && levelWon == false) {
            fire = true;
            spawned = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        spawned = false;

        //check to see if they have clicked on one of the "buttons"
        if (gameStarted == false) {

            if (instructions == true) {
                //go back from instructions page
                instructions = false;
            } else if (displayHighScore == true) {
                //go back from high scores
                displayHighScore = false;
            } else if (960 < e.getX() && e.getX() < 1500 && 610 < e.getY() && e.getY() < 700) {
                //clicked on instructions
                instructions = true;
            } else if (960 < e.getX() && e.getX() < 1500 && 740 < e.getY() && e.getY() < 820) {
                //clicked on high scores
                displayHighScore = true;
            } else if (1120 < e.getX() && e.getX() < 1300 && 485 < e.getY() && e.getY() < 575) {
                //clicked on play
                gameStarted = true;//start game
                pause = false;
                level = 1;//set level to one

                //if space is being held (cheat is on) jump to level 15
                if (cheatON == true) {
                    level = 15;
                }

                //rest field
                resetField();
            }

        }

        //if you have paused
        if (pause == true) {
            //if you decide to quit

            if (80 < e.getX() && e.getX() < 260 && 725 < e.getY() && e.getY() < 800) {
                pause = false;
                gameOver = true;
            }

        }

        //if you have won the level proced to next level
        if (levelWon == true) {
            //rest field
            resetField();
        }
        //if you have failed replay level
        if (levelFail == true) {
            levelFail = false;
            //rest field
            resetField();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }
}
