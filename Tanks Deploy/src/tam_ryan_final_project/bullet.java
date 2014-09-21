/*
 * Tam_Ryan, May_2013, Final Project, ICS3U, Lim_Mister
 */
package tam_ryan_final_project;

/**
 *
 * @author Ryan
 */
public class bullet {

    int bounces = 0;
    int bounceMAX = 2;
    int tankNumber;//which tank it came from
    double bulletX;
    double bulletY;
    double bulletAngle;
    double bulletRadius = 5; //speed
    double distanceFromTank;
    double distanceFromBullet;
    boolean fired = false;
    boolean leftEnemy = false;
    boolean hitBullet = false;

    //the constructor for the class
    bullet(int x, int y, int x2, int y2, boolean a, int numb) {
        //assign values for the variables created in the class
        bulletX = x;
        bulletY = y;
        leftEnemy = a;//if it came from an enemy or not
        bulletAngle = Math.atan2((y2 - y), (x2 - x));
        tankNumber = numb;
    }
}
