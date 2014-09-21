/*
 * Tam_Ryan, May_2013, Final Project, ICS3U, Lim_Mister
 */
package tam_ryan_final_project;

/**
 *
 * @author Ryan
 */
public class enemyTank {

    int tankNumber;//what number it is
    int bulletsFired = 0;
    int bulletMag = 3;
    int leftRight = 1;//stores direction
    double enemyX = 500;
    double enemyY = 500;
    double tankAngle;
    double tankDistance;
    double tankRadius = 5;//speed
    double distanceFromEnemy;
    double distanceFromBullet;
    long lastFiredTime = 0;//last tme it fired a bullet
    boolean dead = false;
    boolean intelligence = false;
    boolean ark = false;//ark mode
    boolean flip = false;//has been fliped direction
    boolean stop = false;//if it is stopped

    enemyTank(int x, int y, int numb, boolean intel) {
        enemyX = x;
        enemyY = y;
        tankNumber = numb;
        intelligence = intel;//if it is AI or not
    }
}
