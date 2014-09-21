/*
 * Tam_Ryan, May_2013, Final Project, ICS3U, Lim_Mister
 */
package tam_ryan_final_project;

import java.io.*;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author Ryan
 */
public class Tanks_Deploy {

    public static void main(String[] args) {

        //set up window
        JFrame myFrame = new JFrame();
        //make a JPanel
        gamePanel myPanel = null;
        try {
            myPanel = new gamePanel();
        } catch (IOException ex) {
            Logger.getLogger(Tanks_Deploy.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Set the title
        myFrame.setTitle("Tanks");
        //make it show up
        myFrame.setVisible(true);
        //set size
        myFrame.setSize(myPanel.width, myPanel.height + 20);
        //close program when you click the "x"
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //make it not resizable
        myFrame.setResizable(false);
        //put JPanel into the JFrame
        myFrame.setContentPane(myPanel);

        Iterator bulletitr;//bullet array
        Iterator bulletitr2;//same bullet array but for detecting bullet bullet collision
        Iterator tankitr;//enemy tank array
        Iterator tankitr2;//same enemy tanks but for detecting tank tank collision

        //keep track of time in milliseconds
        time time = new time(System.nanoTime());

        //create highscores file if it does not exist
        File highScores = new File("highScores_(encrypted).dat");

        //if it doesn't exist
        if (!highScores.exists()) {

            //create a new file
            try {
                highScores.createNewFile();

                //catch read write error
            } catch (IOException e) {
                myPanel.highscoreUnavalible = true;
            }
        }

        //Read the contents of the file
        FileReader in;
        BufferedReader readFile;
        try {
            //set up reader
            in = new FileReader(highScores);
            readFile = new BufferedReader(in);

            String line;//the current line of text
            char temp;//the char value of that character (used for encyrption)
            String decrypted;//decrypted string that is assembled
            int a;//location of the space/index of the space that is used to speparate the score and name
            int b;//level number

            //for each line
            for (int i = 0; i < myPanel.scores.length; i++) {
                line = readFile.readLine();//read the line

                //if the line has something
                if (line != null) {
                    decrypted = "";//reset the "assembly string"
                    a = line.indexOf(" ");//find the break between score and name

                    //for each charcter up untill the break/i.e. each character of the name
                    for (int o = 0; o < a; o++) {
                        temp = line.charAt(o);//take its char value
                        temp -= myPanel.decryptionKey;//subtract the decryption number
                        decrypted += String.valueOf(temp);//take the new char-convert to string then add to the string
                    }

                    //take the string segment that represents the level-convert to int//take than number divide by 42 and then square root//subtract the decrypotion key
                    b = (int) Math.sqrt(Integer.parseInt(line.substring(a + 1, line.length())) / 42) - myPanel.decryptionKey;

                    //add score decrypted score to the score array
                    myPanel.scores[i] = (new highScores(decrypted, b));
                } else {

                    //if there is nothing on the line give it a place holder
                    myPanel.scores[i] = (new highScores("null", -1));
                }
            }

            //close the reader
            readFile.close();
            in.close();

            //catch a read write error
        } catch (IOException e) {
            //if there is a problem set highscore to unavaible
            myPanel.highscoreUnavalible = true;
        }

        //request focus
        myPanel.requestFocus();

        //main game loop
        while (true) {

            //before the game has started i.e. still on start screen/highscore/...
            if (myPanel.gameStarted == false) {

////////////////////////////////////////////////////////////
//High Scores
////////////////////////////////////////////////////////////
                //if on the stats page
                if (myPanel.statsPage == true) {

                    //compare the players score to the leader board
                    for (int k = 0; k < myPanel.scores.length; k++) {

                        //if his score is higher than one of them
                        if (myPanel.level >= myPanel.scores[k].level) {

                            //get the players name/ keep asking if he give an invalid name
                            while (myPanel.vaildName == false) {

                                //input box asking for a name
                                myPanel.name = JOptionPane.showInputDialog(null, "Enter Name \n No Spaces! \n Under 5 Characters!");

                                //if the player doenst put a name in
                                if (myPanel.name == null || myPanel.name.equals("")) {

                                    //call the player rando
                                    myPanel.name = "rando";
                                }

                                //if the player entered in a name make sure it is a valid name
                                if ((myPanel.name.indexOf(" ")) == -1 && myPanel.name.length() <= 5) {

                                    //if it is stop asking for name
                                    myPanel.vaildName = true;

                                    //shift everyone elses name down
                                    for (int i = myPanel.scores.length - 1; i > k; i--) {
                                        myPanel.scores[i].name = myPanel.scores[i - 1].name;
                                        myPanel.scores[i].level = myPanel.scores[i - 1].level;
                                    }

                                    //put the players name in that slot
                                    myPanel.scores[k] = (new highScores(myPanel.name, myPanel.level));
                                }
                            }
                        }
                    }

                    //write the score to the highscore file
                    FileWriter out;
                    BufferedWriter writeFile;
                    try {

                        //set up writer
                        out = new FileWriter(highScores);
                        writeFile = new BufferedWriter(out);

                        char temp;//the char value of that character (used for encyrption)
                        String encrypted;//encrypted string that is assembled
                        int c;//encrypted level number

                        //for each high score
                        for (int j = 0; j < myPanel.scores.length; j++) {

                            //reset the assembly line
                            encrypted = "";

                            //for each character
                            for (int p = 0; p < myPanel.scores[j].name.length(); p++) {
                                temp = myPanel.scores[j].name.charAt(p);//convert to char
                                temp += myPanel.decryptionKey;//add the encryption key to the char
                                encrypted += String.valueOf(temp);//turn it back to a string and add it the the assembly
                            }
                            //encrypt the level number by adding the key, squaring then X 42
                            c = (int) Math.pow(myPanel.scores[j].level + myPanel.decryptionKey, 2) * 42;

                            //write the encrypted content and start new line
                            writeFile.write(encrypted + " " + String.valueOf(c));
                            writeFile.newLine();
                        }

                        //close the writer
                        writeFile.close();
                        out.close();

                        //catch read write error
                    } catch (IOException ex) {
                        //if there is a problem set highscore to unavaible
                        myPanel.highscoreUnavalible = true;
                    }
                }

                //if the game has begun run the game logic
            } else {
                //update time
                time.nano = System.nanoTime() - time.start;

                //if the game is not paused
                if (myPanel.pause == false) {

////////////////////////////////////////////////////////////
//Moving my Tank Around
////////////////////////////////////////////////////////////
                    
                    //if the tank is not past the boundary and not hit a tank that has hit the wall the key has been presed then move the tank
                    if (myPanel.moveRight && !myPanel.hitRight) {
                        if (myPanel.tankX < myPanel.width - myPanel.boundary) {
                            myPanel.tankX += myPanel.tankSpeed;
                        }
                    }
                    if (myPanel.moveLeft && !myPanel.hitLeft) {
                        if (myPanel.tankX > myPanel.boundary) {
                            myPanel.tankX -= myPanel.tankSpeed;
                        }
                    }
                    if (myPanel.moveUp && !myPanel.hitUp) {
                        if (myPanel.tankY > myPanel.boundary) {
                            myPanel.tankY -= myPanel.tankSpeed;
                        }
                    }
                    if (myPanel.moveDown && !myPanel.hitDown) {
                        if (myPanel.tankY < myPanel.height - myPanel.boundary) {
                            myPanel.tankY += myPanel.tankSpeed;
                        }
                    }

////////////////////////////////////////////////////////////
//Me Shooting
////////////////////////////////////////////////////////////
                    
                    //if the presed the button and there are still bullets in my mag shoot a bullet and update the time that tha bullet was last fired
                    if (myPanel.fire == true) {
                        if (!myPanel.gameOver && (myPanel.bulletsFired < myPanel.bulletMag) && time.nano - myPanel.lastFiredTime > myPanel.triggerSpeed) {
                            myPanel.activeBullets.add(new bullet(myPanel.tankX, myPanel.tankY, myPanel.mouseX, myPanel.mouseY, true, -1));
                            myPanel.bulletsFired++;
                            myPanel.lastFiredTime = time.nano;
                        }
                        myPanel.fire = false;
                    }
                    //if a certain time has elapsed since the last bullet fire add one to the mag unless the mag is full
                    if (time.nano - myPanel.lastFiredTime > myPanel.recoverTime && myPanel.bulletsFired > 0) {
                        myPanel.bulletsFired--;
                        myPanel.lastFiredTime = time.nano;
                    }

////////////////////////////////////////////////////////////
//Moving enemey Tank Around
////////////////////////////////////////////////////////////
                    
                    //go through all the tanks
                    tankitr = myPanel.activeTanks.iterator();

                    while (tankitr.hasNext()) {
                        enemyTank theTank = (enemyTank) tankitr.next();
                        
                        //if the tank is not dead and is a intellegent (AI/moving tank)
                        if (theTank.dead == false && theTank.intelligence == true) {
                            
                            //calcuate distance from me
                            theTank.tankDistance = Math.sqrt(Math.pow((theTank.enemyX - myPanel.tankX), 2) + Math.pow((theTank.enemyY - myPanel.tankY), 2));

                            //if greater set it to not ark mode, else it is following the tank in a ark (ark mode)
                            if (theTank.tankDistance > myPanel.trackDistance) {
                                theTank.ark = false;
                            } else if (theTank.tankDistance < myPanel.unTrackWidth && theTank.ark == false) {
                                theTank.ark = true;
                            }
                            
                            //caculate the angle between my tank and the enemy tank
                            theTank.tankAngle = Math.atan2((myPanel.tankY - theTank.enemyY), (myPanel.tankX - theTank.enemyX));
                            
                            //if not in ark mode and the tank has not been told to stop
                            if (theTank.ark == false && theTank.stop == false) {
                                //move the tank closer to my tank
                                theTank.enemyX += myPanel.enemySpeed * Math.cos(theTank.tankAngle);
                                theTank.enemyY += myPanel.enemySpeed * Math.sin(theTank.tankAngle);
                            }
                            //if it is in ark mode (following in a circle
                            if (theTank.ark == true) {
                                //animate tank in a circle
                                theTank.enemyX += theTank.leftRight * myPanel.enemySpeed * Math.cos(theTank.tankAngle - (Math.PI / 2));
                                theTank.enemyY += theTank.leftRight * myPanel.enemySpeed * Math.sin(theTank.tankAngle - (Math.PI / 2));
                            }
                        } else {
                            //if there is a problem just stop the tank
                            theTank.stop = true;
                        }
                    }

////////////////////////////////////////////////////////////
//Enemy Tank Aiming and Shooting
////////////////////////////////////////////////////////////
                    
                    //go through all the tanks
                    tankitr = myPanel.activeTanks.iterator();

                    while (tankitr.hasNext()) {
                        enemyTank theTank = (enemyTank) tankitr.next();

                        //find the angle to my tank
                        theTank.tankAngle = Math.atan2((myPanel.tankY - theTank.enemyY), (myPanel.tankX - theTank.enemyY));

                        //SHOOT
                        //generate random number and if that number== a certain value and the tank is not dead shoot a bullet
                        //the range of random number increases and the amount of bullets on the field increase effectivly lower the probabiblity of a shot
                        if (((int) (Math.random() * (myPanel.activeBullets.size() + 1) * 30)) == 5 && theTank.dead == false && (theTank.bulletsFired < theTank.bulletMag)) {
                            myPanel.activeBullets.add(new bullet((int) theTank.enemyX, (int) theTank.enemyY, myPanel.tankX, myPanel.tankY, false, theTank.tankNumber));
                            theTank.bulletsFired++;
                        }

                        //time delay on shot
                        if (time.nano - theTank.lastFiredTime > 750000000 && theTank.bulletsFired > 0) {
                            theTank.bulletsFired--;
                            theTank.lastFiredTime = time.nano;
                        }
                    }

                    //if killed all enimies level = won :]
                    if (myPanel.enemyDeaths >= myPanel.enemyNumb && myPanel.gameOver == false && myPanel.levelFail == false) {
                        //stop the tank movement
                        myPanel.levelWon = true;
                        myPanel.moveLeft = false;
                        myPanel.moveRight = false;
                        myPanel.moveUp = false;
                        myPanel.moveDown = false;
                    }
                    
                    //if a the critical value is reached. turn all the tanks from stationary to moving
                    if (myPanel.enemyDeaths >= myPanel.enemyDeathsToTurnOnAI && myPanel.enemyDeathsToTurnOnAI != 0) {
                        tankitr = myPanel.activeTanks.iterator();

                        while (tankitr.hasNext()) {
                            enemyTank theTank = (enemyTank) tankitr.next();
                            theTank.intelligence = true;//turn them to smart
                        }
                    }

////////////////////////////////////////////////////////////
//Enemy Tank Enemy Tank Collision Detection (acutally prevention)/THE AI
////////////////////////////////////////////////////////////
                    
                    //go through all the tanks
                    tankitr = myPanel.activeTanks.iterator();

                    while (tankitr.hasNext()) {
                        enemyTank theTank = (enemyTank) tankitr.next();

                        //compare to all the other tanks
                        tankitr2 = myPanel.activeTanks.iterator();

                        while (tankitr2.hasNext()) {
                            enemyTank theTank2 = (enemyTank) tankitr2.next();

                            //distance between two tanks being compared
                            theTank2.distanceFromEnemy = Math.sqrt(Math.pow((theTank.enemyX - theTank2.enemyX), 2) + Math.pow((theTank.enemyY - theTank2.enemyY), 2));

                            //if both tanks are are dead
                            if (theTank2.dead == false && theTank.dead == false) {
                                //and the distance is above a certain value
                                if (theTank2.distanceFromEnemy > myPanel.tankTankClear) {
                                    theTank2.flip = false;//both tanks don't need to be flipped/have not been fliped/have cleared each other
                                    theTank.flip = false;
                                }
                                //if the are less than a certain distance (and not 0 beacuse 0 would mean you are comparing the tank to itself)
                                if (theTank2.distanceFromEnemy < myPanel.tankTankMin && theTank2.distanceFromEnemy != 0) {
                                    //they have not been fliped
                                    if (theTank2.flip == false) {
                                        theTank.leftRight = theTank.leftRight * -1;//reverse the direction of the arks
                                        theTank2.leftRight = theTank.leftRight * -1;

                                        theTank2.flip = true;//they have been fliped
                                        theTank.flip = true;
                                    }
                                }
                                //if the tank has hit a boundary reverse the ark / bounce off the wall
                                //lower the tank a few pixes to prevent it from constantly flipping in and out
                                if (theTank2.enemyY < myPanel.boundary) {
                                    theTank2.leftRight = theTank.leftRight * -1;
                                    theTank2.enemyY = myPanel.boundary + 2;
                                }
                                if (theTank2.enemyY > myPanel.height - myPanel.boundary) {
                                    theTank2.leftRight = theTank.leftRight * -1;
                                    theTank2.enemyY = myPanel.height - myPanel.boundary - 2;
                                }
                                if (theTank2.enemyX < myPanel.boundary) {
                                    theTank2.leftRight = theTank.leftRight * -1;
                                    theTank2.enemyX = myPanel.boundary + 2;
                                }
                                if (theTank2.enemyX > myPanel.width - myPanel.boundary) {
                                    theTank2.leftRight = theTank.leftRight * -1;
                                    theTank2.enemyX = myPanel.width - myPanel.boundary - 2;
                                }
                            }
                            //if the are not dead and they are not in ark mode and in track mode
                            if (theTank2.ark == false && theTank2.dead == false && theTank.dead == false) {
                                //and theire distance is too close
                                if (theTank2.distanceFromEnemy < myPanel.tankStopMin && theTank2.distanceFromEnemy != 0 && theTank2.stop == false && theTank.stop == false) {
                                    theTank2.stop = true;//stop the tank
                                }
                                //if the diestance is great enough
                                if (theTank2.distanceFromEnemy > myPanel.tankStopClear) {
                                    theTank2.stop = false;//let the tank run as usual
                                }
                            } else {
                                theTank2.stop = false;//if it is dead or there is problem stop the tank
                            }
                        }
                    }

////////////////////////////////////////////////////////////
//Enemy Tank and My Tank Collision Detection
////////////////////////////////////////////////////////////
                    
                    //go through all the tanks
                    tankitr = myPanel.activeTanks.iterator();

                    while (tankitr.hasNext()) {
                        enemyTank theTank = (enemyTank) tankitr.next();

                        //if it alive
                        if (theTank.dead == false) {
                            //and it has over laping coridinates on the right side
                            if ((myPanel.tankX - theTank.enemyX) > -myPanel.tankSize && (myPanel.tankX - theTank.enemyX) < 0)//hit on right side of me
                            {
                                //you have not hit a tank on the left (make sure it doesn't treat it as a hit on the left and right)
                                myPanel.hitLeft = false;
                                
                                //if you are at the same heigh as the tank, and you are moving, push the tank to a side
                                if (Math.abs(myPanel.tankY - theTank.enemyY) <= myPanel.tankSize) {
                                    if (myPanel.moveRight) {
                                        if (theTank.enemyX < myPanel.width - myPanel.boundary) {
                                            theTank.enemyX += myPanel.tankSpeed;
                                        } else {
                                            myPanel.hitRight = true;
                                        }
                                    }
                                //if not you have not hit the tank
                                } else {
                                    myPanel.hitRight = false;
                                }
                                //same thing as above but for left side
                            } else if ((myPanel.tankX - theTank.enemyX) < myPanel.tankSize && (myPanel.tankX - theTank.enemyX) > 0)//hit on left side of me
                            {
                                myPanel.hitRight = false;
                                if (Math.abs(myPanel.tankY - theTank.enemyY) <= myPanel.tankSize) {
                                    if (myPanel.moveLeft) {
                                        if (theTank.enemyX > myPanel.boundary) {
                                            theTank.enemyX -= myPanel.tankSpeed;
                                        } else {
                                            myPanel.hitLeft = true;
                                        }
                                    }
                                } else {
                                    myPanel.hitLeft = false;
                                }
                            }
                            //same thing from up side
                            if ((myPanel.tankY - theTank.enemyY) < myPanel.tankSize && (myPanel.tankY - theTank.enemyY) > 0)//hit on Up side of me
                            {
                                myPanel.hitDown = false;
                                if (Math.abs(myPanel.tankX - theTank.enemyX) <= myPanel.tankSize) {
                                    if (myPanel.moveUp) {
                                        if (theTank.enemyY > myPanel.boundary) {
                                            theTank.enemyY -= myPanel.tankSpeed;
                                        } else {
                                            myPanel.hitUp = true;
                                        }
                                    }
                                } else {
                                    myPanel.hitUp = false;
                                }
                                //same thing for down side
                            } else if ((myPanel.tankY - theTank.enemyY) > -myPanel.tankSize && (myPanel.tankY - theTank.enemyY) < 0)//hit on Down side of me
                            {
                                myPanel.hitUp = false;
                                if (Math.abs(myPanel.tankX - theTank.enemyX) <= myPanel.tankSize) {
                                    if (myPanel.moveDown) {
                                        if (theTank.enemyY < myPanel.height - myPanel.boundary) {
                                            theTank.enemyY += myPanel.tankSpeed;
                                        } else {
                                            myPanel.hitDown = true;
                                        }
                                    }
                                } else {
                                    myPanel.hitDown = false;
                                }
                            //if you have not hit then you have not hit
                            } else {
                                myPanel.hitLeft = false;
                                myPanel.hitRight = false;
                                myPanel.hitUp = false;
                                myPanel.hitDown = false;
                            }
                        }
                    }

////////////////////////////////////////////////////////////
//Bullet Collision Detection
////////////////////////////////////////////////////////////
                    
                    //go through all the bullets
                    bulletitr = myPanel.activeBullets.iterator();

                    while (bulletitr.hasNext()) {

                        bullet theBullet = (bullet) bulletitr.next();

                        //Upgrades speed if in god mode
                        if (myPanel.godMODE == true) {
                            myPanel.bulletSpeed = myPanel.godModeBulletSpeed;
                        }

                        //Detects if bullet has hit wall and if so it bounces it
                        if (theBullet.bulletX > myPanel.width - (myPanel.boundary - 10)) {
                            theBullet.bulletAngle = Math.PI - theBullet.bulletAngle;
                            theBullet.bounces += 1;
                            theBullet.fired = true;
                        }
                        if (theBullet.bulletX < (myPanel.boundary - 10)) {
                            theBullet.bulletAngle = Math.PI - theBullet.bulletAngle;
                            theBullet.bounces += 1;
                            theBullet.fired = true;
                        }
                        if (theBullet.bulletY < (myPanel.boundary - 10)) {
                            theBullet.bulletAngle = 2 * (Math.PI) - theBullet.bulletAngle;
                            theBullet.bounces += 1;
                            theBullet.fired = true;
                        }
                        if (theBullet.bulletY > myPanel.height - (myPanel.boundary - 10)) {
                            theBullet.bulletAngle = 2 * (Math.PI) - theBullet.bulletAngle;
                            theBullet.bounces += 1;
                            theBullet.fired = true;
                        }
                        if (myPanel.levelWon != true) {
                            //Move the bullet
                            theBullet.bulletX += myPanel.bulletSpeed * Math.cos(theBullet.bulletAngle);
                            theBullet.bulletY += myPanel.bulletSpeed * Math.sin(theBullet.bulletAngle);
                        }

                        //Check this bullet against all the enemies on the field. If there is a collision remove bullet and kill tank
                        tankitr = myPanel.activeTanks.iterator();

                        //compare agains all tanks
                        while (tankitr.hasNext()) {
                            enemyTank theTank = (enemyTank) tankitr.next();

                            if (theTank.dead == false) {

                                //distance from the tank
                                theTank.distanceFromBullet = Math.sqrt(Math.pow((theTank.enemyX - theBullet.bulletX), 2) + Math.pow((theTank.enemyY - theBullet.bulletY), 2));

                                //if the bullet was fried from this tank check to see the bullet has left the tank (this is to prevent the tank from killing it self the instant it fires)
                                if (theBullet.tankNumber == theTank.tankNumber) {
                                    //if it is greater than a certain distance that means it has cleared
                                    if (theTank.distanceFromBullet > myPanel.tankBulletClear) {
                                        theBullet.leftEnemy = true;
                                    }
                                    //if it has cleared and is with a ceratin range tank is dead, add one to the death toll
                                    if (theBullet.leftEnemy == true && theTank.distanceFromBullet < myPanel.tankBulletMin) {
                                        theTank.dead = true;
                                        myPanel.enemyDeaths++;
                                        try {
                                            //remove bullet
                                            bulletitr.remove();
                                        } catch (IllegalStateException e) {
                                        }
                                    }
                                 //if it wasn't fired from this tank then just usally collision detection
                                } else {
                                    //if withing the distance kill tank, add to death toll, remove bullet
                                    if (theTank.distanceFromBullet < myPanel.tankBulletMin) {
                                        theTank.dead = true;
                                        myPanel.enemyDeaths++;
                                        try {
                                            //remove bullet
                                            bulletitr.remove();
                                        } catch (IllegalStateException e) {
                                        }
                                    }
                                }
                            }
                        }

                        //Check to see if this bullet has killed my Tank. If so game over :[
                        theBullet.distanceFromTank = Math.sqrt(Math.pow((myPanel.tankX - theBullet.bulletX), 2) + Math.pow((myPanel.tankY - theBullet.bulletY), 2));

                        //if the bullet has reached a ceratin distacne away from me than it has cleared (again to prevent the my bullets from killing me when i first fire)
                        if (theBullet.distanceFromTank >= myPanel.tankBulletClear) {
                            theBullet.fired = true;
                        }
                        //if it has cleared me run collision detection
                        if (theBullet.fired == true) {
                            //if it is within a ceratin distance it has hit me
                            if (theBullet.distanceFromTank < myPanel.tankBulletMin) {
                                //if i still have lives i have failed the level and i lose anther life
                                if (myPanel.lives > 0 && myPanel.levelFail == false) {
                                    myPanel.levelFail = true;
                                    myPanel.lives--;
                                }
                                //else i lost
                                if (myPanel.lives <= 0) {
                                    myPanel.levelFail = false;
                                    myPanel.gameOver = true;
                                    //reset the game varibales
      
                                        myPanel.tankSpeed = 2;
                                    
                                    myPanel.bulletMag = 6;
                                    myPanel.recoverTime = 750000000;
                                    myPanel.godMODE = false;
                                }
                                //stop the tank
                                myPanel.moveLeft = false;
                                myPanel.moveRight = false;
                                myPanel.moveUp = false;
                                myPanel.moveDown = false;
                                try {
                                    //remove the bullet
                                    bulletitr.remove();
                                } catch (IllegalStateException e) {
                                }
                            }
                        }

                        //If the bullet has excceded the maximum amount of bounces it has been allowed, it is removed
                        if (theBullet.bounceMAX <= theBullet.bounces) {
                            try {
                                bulletitr.remove();
                            } catch (IllegalStateException e) {
                            }
                        }

                        //If the bullet has hit another bullet
                        if (theBullet.hitBullet == true) {
                            try {
                                //remove the bullet
                                bulletitr.remove();
                            } catch (IllegalStateException e) {
                            }
                        }

                        //compare this bullet to all the other bullets
                        bulletitr2 = myPanel.activeBullets.iterator();

                        while (bulletitr2.hasNext()) {
                            bullet theBullet2 = (bullet) bulletitr2.next();
                            //if the distance is less than a certain value
                            theBullet.distanceFromBullet = Math.sqrt(Math.pow((theBullet.bulletX - theBullet2.bulletX), 2) + Math.pow((theBullet.bulletY - theBullet2.bulletY), 2));
                            //and it not 0 (if it is 0 it has been comapared to itself) then the bullet have collided
                            if (theBullet.distanceFromBullet != 0 && theBullet.distanceFromBullet < myPanel.bulletBulletMin) {
                                theBullet.hitBullet = true;
                                theBullet2.hitBullet = true;
                            }
                        }
                    }
                }
            }

////////////////////////////////////////////////////////////
//GOD Mode
////////////////////////////////////////////////////////////
            
            //if god mode reached double tank speed, pop opean congragulation panel, display cheat, increase mag and relode speed
            if (myPanel.level == 16 && myPanel.godMODEDispalyed == false) {
                JOptionPane.showMessageDialog(null, "Congrats You have now reached GOD MODE \n Your tank speed and rate of fire have been increased! \n The speed of bullets will also keep increasing \n Cheat: Hold Space when you press PLAY to directly enter Level 15");
                myPanel.godMODEDispalyed = true;
                myPanel.tankSpeed = myPanel.tankSpeed * 2;
                myPanel.bulletMag = 10;
                myPanel.recoverTime = 250000000;
                myPanel.godMODE = true;
            }

////////////////////////////////////////////////////////////
//30 FPS
////////////////////////////////////////////////////////////
            myPanel.repaint();

            try {
                Thread.sleep(17);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tanks_Deploy.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

////////////////////////////////////////////////////////////
//Debug
////////////////////////////////////////////////////////////
            if(myPanel.printLine) {
                System.out.println("\n");
                System.out.format("%5s %2s %5s %2s", "tankX:", myPanel.tankX, "tankY:", myPanel.tankY);
                System.out.format("%10s %2s %5s %2s", "mouseX:", myPanel.mouseX, "mouseY:", myPanel.mouseY);
                System.out.format("%10s %5s", "Time:", time.nano);
            }
        }
    }
}
