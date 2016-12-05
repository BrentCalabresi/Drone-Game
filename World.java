package Project4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by BAC on 11/23/2016.
 */
public class World extends JPanel {

    private final double GRAVITY = 1;
    private final int REFRESH_RATE = 10;//refresh rate in ms
    private boolean worldCreated = false;
    private Rectangle bounds,landingZone;
    private Rectangle[] top = new Rectangle[20];
    private Rectangle[] bottom = new Rectangle[20];//rocks will be Rectangles stored in arrays
    private Rectangle[] obstacles;
    private boolean up,left,right;
    private int worldComplexity = 100,score = 0;
    private static JFrame frame = new JFrame("Drone Navigation");
    private String endGame;

    private Drone drone = new Drone(0,0,0,3,100,0, 250,0, GRAVITY, 0,false,0);
    private Timer timer = new Timer(REFRESH_RATE, new TimerHandler());

    public World(){
        setLayout(null);
        setBackground(Color.gray);
        setPreferredSize(new Dimension(1280, 600));
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyHandler());

        timer.start();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        //handles rotation of image
        BufferedImage droneIMG = (BufferedImage) drone.getDroneImage();
        //droneIMG.getScaledInstance(200,200,Image.SCALE_SMOOTH);

        drone.setX(calculateNextPosition(drone).x);
        drone.setY(calculateNextPosition(drone).y+GRAVITY);
        AffineTransform at = AffineTransform.getTranslateInstance((int)drone.getX(),(int)drone.getY());
        at.rotate(Math.toRadians(drone.getPitch()),drone.width/2,drone.height/2);
        g2d.drawImage(droneIMG,at,null);

        AffineTransform atx = AffineTransform.getTranslateInstance(bounds.getX(),bounds.getY());
        atx.rotate(Math.toRadians(drone.getPitch()),drone.width/2,drone.height/2);
        //g2d.draw(bounds);

        landingZone = new Rectangle(this.getWidth()-275,this.getHeight()-25,275,25);
        g.setColor(Color.RED);
        ((Graphics2D) g).fill(landingZone);
        g.setColor(Color.DARK_GRAY);
        generateWorld(g2d,worldComplexity);
        g.fillRect(0,0,100,70);//background for lives/fuel
        g.setColor(Color.lightGray);
        g.setFont(new Font("Helvetica",Font.BOLD,32));
        g.drawString("Lives: "+(int)drone.getLives(),5,25);
        g.drawString("Fuel: "+(int)drone.getFuel(),5,60);
        g.setColor(Color.green);
        g.drawString("Score: "+score,this.getWidth()-200,25);

    }

    protected void generateWorld(Graphics2D g2d,int complexity){

        Random gen = new Random();

        if (worldCreated == false){//only calculate rectangle points once

            int start = 0;
            for (int i =0;i<top.length;i++){
                top[i] = new Rectangle(start,0,gen.nextInt(75)+25,gen.nextInt(complexity)+50);
                start +=50;
            }
            int start2 = 0;
            int height;
            for (int i =0;i<bottom.length;i++){
                height = gen.nextInt(complexity)+25;
                bottom[i] = new Rectangle(start2,this.getHeight()-height,gen.nextInt(75)+25,height);
                start2 +=50;
            }
            //Obstacles
            obstacles = new Rectangle[complexity/10];
            for (int i =0;i<obstacles.length;i++){
                obstacles[i]=new Rectangle(gen.nextInt(this.getWidth()-250)+100,gen.nextInt(this.getHeight()-complexity+50)+50,gen.nextInt(25)+25,gen.nextInt(25)+10);
            }

        }

        for (int i =0; i < top.length; i++){//Rectangles need to be drawn at every refresh,
            g2d.fill(top[i]);               //but new points don't need to be calculated
            g2d.fill(bottom[i]);

        }

        for (int i =0; i <obstacles.length;i++)
            g2d.fill(obstacles[i]);
        worldCreated = true;
    }

    //calculates location of plane at next refresh point
    protected Point calculateNextPosition(Drone drone){
        double x2,y2;
        double x1 = drone.getX();
        double y1 = drone.getY();

        drone.setVelocity(drone.getVelocity()+drone.getAcceleration());
        drone.setaY(drone.getAcceleration()*Math.sin(drone.getPitch()));
        if (drone.getVelocity() < 0)
            drone.setVelocity(0);
        x2 = drone.getVelocity()*Math.cos(Math.toRadians(drone.getPitch()));
        y2 = drone.getVelocity()*Math.sin(Math.toRadians(drone.getPitch()));


        Point p2 = new Point((int)(x1+x2),(int)(y1+y2));

        return p2;
    }

    protected void pause(int time){//temporarily freezes screen after drone crashes
        up = false;
        right = false;
        left = false;
        drone.setVelocity(0);
        drone.setAcceleration(0);
        drone.setPitch(0);
        drone.setFuel(100);

        try{
            Thread.sleep(time);
        }catch (InterruptedException e){}
    }

    protected class TimerHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            repaint();
            bounds = new Rectangle((int)drone.getX()+5,(int)drone.getY()+5,drone.getDroneImage().getWidth(null)-5,drone.getDroneImage().getHeight(null)-5);
            if (drone.getFuel() < 0){
                pause(0x7d0);
                drone.setLives(drone.getLives()-1);
                drone.setX(5);//start from beginning
                drone.setY(250);
                drone.setPitch(0);

                drone.setFuel(100);
            }
            for (int i =0; i< bottom.length;i++){
                try{
                    if (bounds.intersects(bottom[i]) || bounds.intersects(top[i]) || bounds.intersects(obstacles[i])){//checks to see if drone is touching the wall
                        drone.setLives(drone.getLives()-1);
                        drone.setX(5);//start from beginning
                        drone.setY(250);
                        drone.setPitch(0);
                        pause(0x7d0);
                        drone.setFuel(100);
                    }
                 }catch (NullPointerException | ArrayIndexOutOfBoundsException ex){}
            }
            if (drone.getLives() <= 0)
                endGame = "You Lost";
            if (worldComplexity > 200)
                endGame = "Congratulations, You Won!";
            //Game Over
            if (drone.getLives() <= 0 || worldComplexity > 200) {

                JOptionPane pane = new JOptionPane();
                int user = JOptionPane.YES_NO_CANCEL_OPTION;
                pane.showMessageDialog(null, endGame + "\n" + "Press OK to Close");
                if (user == 1)
                    System.exit(5);
            }

            //Successful Completion
            try{
                if (bounds.intersects(landingZone)){

                    if (drone.getVelocity() < 5){//checks if drone lands too quickly
                        worldCreated = false;
                        score+= 50+drone.getFuel()+drone.getLives()*10;
                        worldComplexity += 50;
                        drone.setX(5);
                        drone.setY(250);
                        pause(2000);
                        drone.setPitch(0);
                        drone.setFuel(100);
                        drone.setLives(3);

                    }
                    else{
                        pause(2000);
                        drone.startOver();
                        getGraphics().drawString("Your Landing Speed Was Too Fast!",500,250);
                    }
                }
            }catch (NullPointerException NEX){}

           // System.out.println(drone.getAcceleration()+" "+drone.getVelocity());
//            System.out.println(bounds);
//            System.out.println(drone.getX()+", "+drone.getY());
        }
    }

        protected class KeyHandler implements KeyListener{

            @Override
            public void keyPressed(KeyEvent e) {
                drone.setFuel(drone.getFuel()-1);
                switch (e.getKeyCode()){
                    case KeyEvent.VK_RIGHT: right = true;
                        break;
                    case KeyEvent.VK_LEFT: left = true;
                        break;
                    case KeyEvent.VK_UP:{ drone.setThrusting(true);
                        up = true;
                        break;
                    }
                }
                if (up)
                    drone.setAcceleration(.025);
                if (right)
                    drone.setPitch(drone.getPitch()+10);
                if (left)
                    drone.setPitch(drone.getPitch()-10);
            }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()){
                case KeyEvent.VK_RIGHT: right = false;
                    break;
                case KeyEvent.VK_LEFT: left = false;
                    break;
                case KeyEvent.VK_UP:{
                    up = false;
                    drone.setThrusting(false);

                }
                if (up == false){
                    if (drone.getAcceleration() > 0)
                        drone.setAcceleration(-.05);
                    else
                        drone.setAcceleration(0);
                    break;
                }
            }
        }
        @Override
        public void keyTyped(KeyEvent e) {
        }
    }//end KeyHandler Class


    public static void main(String[] args) {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        World canvas = new World();
        frame.add(canvas);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
