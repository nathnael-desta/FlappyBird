package FlappyBird;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JFrame implements ActionListener, KeyListener, MouseListener {

    public static FlappyBird flappyBird;
    public PaintInstantiator drawer; // to call the instance of painter
    public Rectangle bird;
    public int WIDTH = 600;
    public int HEIGHT = 600;
    public ArrayList<Rectangle> pillars; // change make simpler

    public Random rand = new Random();

    public int ticks , score;
    public int vDistance; // ticks is the clicks and vDistance is the motion of the bird default both 0
    public boolean gameOver;
    public boolean hasBegun;
    public String highScore = "0";
    public int space = 260;
    public int width = 75;
    public boolean up;
    public int count = 0;
    public int playOnce = 0;
    public AudioInputStream audioInputStream;
    public Clip clip;
    public Font flappyFontBase,
            flappyFontReal,
            flappyScoreFont,
            flappyMiniFont = null;
    public String totalHighScore = "0";
    public int lastHighScore = 0;
    File file = new File("src\\FlappyBird\\highscore.txt");
    JButton start;
    public boolean intersect;
    public Point clickedPoint = new Point(-1, -1);
    int speed = 7;
    ImageIcon blueNormal = new ImageIcon("src\\FlappyBird\\TightCroppedBirdNormal.png");
    ImageIcon blueTilted = new ImageIcon("src\\FlappyBird\\PressedTightCroppedBirdNormal.png");
    ImageIcon redNormal = new ImageIcon("src\\FlappyBird\\Red.png");
    ImageIcon redTilted = new ImageIcon("src\\FlappyBird\\pressedRed.png");
    ImageIcon yellowNormal = new ImageIcon("src\\FlappyBird\\Yellow.png");
    ImageIcon yellowTilted = new ImageIcon("src\\FlappyBird\\pressedYellow.png");
    Image birdNormal = blueNormal.getImage();
    Image birdTilted = blueTilted.getImage();
    public FlappyBird(){
        drawer = new PaintInstantiator();
        add(drawer); // draws the jpanel onto the frame

        Timer timer = new Timer(25, this); // fires one or more action events after a specified delay

        setTitle("Flappy Bird");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setResizable(false);
        setVisible(true);

        bird = new Rectangle(WIDTH / 2 - 10,HEIGHT / 2 - 10,35,24);

        pillars = new ArrayList<Rectangle>();

        for(int i = 0; i < 6; i++)
            addpillar(true);
        addKeyListener(this);
        addMouseListener(this);






        timer.start(); //starts sending actions to the action listener




    }

    class PaintInstantiator extends JPanel implements ActionListener{
        public void paintComponent(Graphics g) { // overridden from parent class
            //  is needed to draw something on JPanel other than drawing the background color
            super.paintComponent(g);
            FlappyBird.flappyBird.repaint(g);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == start){
                jump();
            }
        }


    }



    public void repaint(Graphics g){

        try {
            InputStream is = new BufferedInputStream(new FileInputStream("src\\FlappyBird\\flappy-font.ttf"));
            flappyFontBase = Font.createFont(Font.TRUETYPE_FONT, is);

            flappyScoreFont = flappyFontBase.deriveFont(Font.PLAIN, 60);
            flappyFontReal  = flappyFontBase.deriveFont(Font.PLAIN, 15);
            flappyMiniFont  = flappyFontBase.deriveFont(Font.PLAIN, 30);

        } catch (Exception e) {
            System.err.println("Could not load Flappy Font!");
            System.exit(-1);
        }

        ImageIcon bg = new ImageIcon("src\\FlappyBird\\bg.png");
        g.drawImage(bg.getImage(),0,0,WIDTH,HEIGHT,null);


        if (up){
            g.drawImage(birdTilted,bird.x,bird.y,bird.width,bird.height,null);

        } else {
            g.drawImage(birdNormal, bird.x, bird.y, bird.width, bird.height, null);
        }


        ImageIcon tube = new ImageIcon("src\\FlappyBird\\tube.png");
        ImageIcon bottomBar = new ImageIcon("src\\FlappyBird\\tubebottomtop.png");
        ImageIcon topBar = new ImageIcon("src\\FlappyBird\\tubebottomdown.png");

        for(int i  = 0; i < pillars.size(); i++){ // paints each pillar in the array
            if (pillars.get(i).y == 0){
                g.drawImage(tube.getImage(),pillars.get(i).x, pillars.get(i).y, pillars.get(i).width, pillars.get(i).height,null);
                g.drawImage(topBar.getImage(),pillars.get(i).x - 5, pillars.get(i).y + pillars.get(i).height - 25, pillars.get(i).width + 10, 25,null);
            } else{
                g.drawImage(tube.getImage(),pillars.get(i).x, pillars.get(i).y, pillars.get(i).width, pillars.get(i).height,null);
                g.drawImage(bottomBar.getImage(),pillars.get(i).x - 5, pillars.get(i).y, pillars.get(i).width + 10, 25,null);
            }
        }

        g.setColor(Color.WHITE);
        g.setFont(flappyScoreFont);



        if (!gameOver && hasBegun){
            g.setColor(new Color(128,72,28));
            g.drawString(String.valueOf(score), WIDTH / 2 - 25 , 100);
        }
        g.setFont(flappyMiniFont);
        g.setColor(Color.WHITE);
        g.drawString("high score", 20, 40);
        g.setFont(flappyScoreFont);
        g.drawString(highScore, 20, 100);


        ImageIcon gameDone = new ImageIcon("src\\FlappyBird\\gameover.png");
        if (gameOver){
            g.drawImage(gameDone.getImage(), 130, 200, null);
        }

        // highscore

        if (score > Integer.parseInt(highScore)){
            highScore = score + "";
        }

        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) // read the score file line by line
            {
                lastHighScore = Integer.parseInt(line.trim()); // parse each line as an int
                if (lastHighScore < score) {
                    lastHighScore = score;
                }
                line = reader.readLine();
            }
            reader.close();
            try {
                PrintWriter output = new PrintWriter(file);
                output.println(lastHighScore);
                output.close();
            } catch (FileNotFoundException ex) {
                System.out.println("file not found");
            }


        } catch (Exception ex){
            System.out.println("error");
        }

        g.setFont(flappyMiniFont);
        g.drawString("Best score", WIDTH - 200, 40);
        g.setFont(flappyScoreFont);
        g.drawString(lastHighScore + "", WIDTH - 85, 100);


        if(!hasBegun){
            ImageIcon logo = new ImageIcon("src\\FlappyBird\\finalflappywef.png");
            g.drawImage(logo.getImage(), 35, 600 / 2 - 175,530,153, null);

        }

        if(gameOver || !hasBegun) {
            ImageIcon start = new ImageIcon("src\\FlappyBird\\playbutton.png");
            g.drawImage(start.getImage(), 600 / 2 - 90, 600 / 2 - 10, null);
            g.drawImage(yellowNormal.getImage(),600 / 2 - 90,600 / 2 + 90,35,24,null);
            g.setFont(flappyMiniFont);
            g.setColor(new Color(250,159,82));
            g.drawString("E",600 / 2 - 80, 600 / 2 + 145);
            g.drawImage(blueNormal.getImage(),600 / 2 - 30,600 / 2 + 90,35,24,null);
            g.setFont(flappyMiniFont);
            g.setColor(new Color(250,159,82));
            g.drawString("M",600 / 2 - 25, 600 / 2 + 145);
            g.drawImage(redNormal.getImage(),600 / 2 + 30,600 / 2 + 90,35,24,null);
            g.setFont(flappyMiniFont);
            g.setColor(new Color(250,159,82));
            g.drawString("H",600 / 2 + 40, 600 / 2 + 145);
        }



    }

    public void addpillar(boolean begin){
        int height = 30 + rand.nextInt(285);
        int heightAboveGround = HEIGHT - height - 150;

        if(begin) { // if true then it is the beginning pillar and create one bottom and top bars
            pillars.add(new Rectangle(WIDTH + pillars.size() * 200, heightAboveGround, width, height)); // Bottom rectangle
            // it creates a rectangle on a really long image that begins at the end of the original space + the width of the rectangle +
            // 300 pixles for each rectangle in there for spacing (technically multiplied by 2 * 300 for the 2nd rect because top and bottom added)
            // for height it begins at the bottom with HEIGHT, goes to the grass with - 120, goes rectangle height up
            // then builds the same rectangle down
            pillars.add(new Rectangle(pillars.get(pillars.size() - 1).x, 0, width, HEIGHT - height - space)); // Top rectangle
            // position at the end of the image + width of the rectangle + number of rectangles added - 1 for the bottom rectangle * 300 (same distance as top)
            // for spacing,height = 0 top, normal rect width, begins at bottom - normal rect height - spacing in between
        } else { // if not beginning pillar just append it to the last pillar, we need this because we can't make an infinite image, to only add one when we don't die as far
            pillars.add(new Rectangle(pillars.get(pillars.size() - 1).x + 400, heightAboveGround, width, height));
            // pillars.size - 1 is the index of the last element, get returns the element at the last index, we get the x value of the last element
            // we add 600 to append a new rectangle in x position with usual spacing then makes the usual rectangle
            pillars.add(new Rectangle(pillars.get(pillars.size() - 1).x, 0, width, HEIGHT - height - space));

        }
    }


    public void jump(){
        jumpSound();
        if (gameOver){
            bird = new Rectangle(WIDTH / 2 - 10,HEIGHT / 2 - 10,35,24);
            pillars.clear();

            vDistance = 0;
            score = 0;

            for(int i = 0; i < 6; i++)
                addpillar(true);

            gameOver = false;
        }
        if (!hasBegun){
            hasBegun = true;
        } else if (!gameOver){
              if (vDistance > 0){
                  vDistance = 0;
            }

            vDistance -= 10;
        }
        count = 0;
    }

    public void actionPerformed(ActionEvent e){

        //movement of the pillars
        ticks++;


        if (hasBegun) {

            for (int i = 0; i < pillars.size(); i++) { // draws the beginning pillars
                Rectangle pillar = pillars.get(i);
                pillar.x -= speed;

                // each time it is called it decreases the x of each pillar by 8, then it is drawn and changed on the
                // panel with drawer.repaint(); below, everything begins with the actionlistener in timer
            }

            // this part is gravity

            // timer.begin() is calling this action listener constantly every 20ms making this added each time
            if (ticks % 2 == 0 && vDistance < 15) { // pressed twice and bird hasn't moved too much
                // vDistance max is 15 because it falls faster the longer it falls and 16 is the max fall speed
                vDistance += 2; // jump resets to 0
            }

            // now for the appended pillars
            for (int i = 0; i < pillars.size(); i++) {
                Rectangle pillar = pillars.get(i);

                if (pillar.x + pillar.width < 0) { // checks if the pillar is fully out of the screen and removes it
                    pillars.remove((pillar));
                    // removes both the bottom and top pillar

                    if (pillar.y == 0) { // since we don't want to add twice just add new appended pillar when top gets removed
                        addpillar(false);
                    }
                }

            }
        }
        if(!gameOver){
            if(playOnce == 0) {
                song();
            }
            playOnce += 1;
        }




        bird.y += vDistance; // adding in y makes it go downwards

        drawer.repaint();



        if ( bird.y < 0 ){
            vDistance -= vDistance;
        }


        // collision mechanism

        for(int i = 0; i < pillars.size(); i++){
            Rectangle pillar = pillars.get(i);
            boolean passLeft = bird.x + bird.width / 2 > pillar.x + pillar.width / 2 - 4;
            boolean passRight = bird.x + bird.width / 2 < pillar.x + pillar.width /2 + 4;
            boolean pass = passRight && passLeft;
            // middle more than left less than right

            if (pillar.intersects(bird)) {
                intersect = true;
                if (count == 0) {
                    hit();
                }
                count += 1;
                gameOver = true;
                if (bird.x <= pillar.x) {
                    bird.x = pillar.x - bird.width; // bird stops at pillar
                } else {
                    // falling on top or touches top
                    if (pillar.y != 0){
                        bird.y = pillar.y - bird.height;
                    } else if (bird.y < pillar.height){
                        bird.y = pillar.height; // fall on top of the bottom pillar
                    }
                }
            }
            if (!gameOver && pillar.y == 0 && pass){
                pointSound();
                score++;
            }
        }



        if (bird.y + vDistance >= HEIGHT - 150){ // to fall on the ground instead of going underground
            bird.y = HEIGHT - 150 - bird.height;
        }





    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            up = true;
        }
        }




    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            jump();
            up = false;
        }

    }

    private void playSound (String sound, boolean play) {

        // Path to sound file
        String soundURL =  sound + ".wav";

        // Try to load and play sound
        try {
            audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(soundURL));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            if (!sound.equals("song")) {
                clip.start();
            }
            if (sound.equals("song")){
                clip.start();
                clip.loop(50);

            }


        } catch (Exception e) {
            System.out.println("Count not load " + sound);
        }
    }

    public void jumpSound () {
        playSound("jumps",true);
    }
    public void pointSound () {
        playSound("point",true);
    }
    public void hit () {
        playSound("hit",true);
    }
    public void song () {
        playSound("song",true);
    }
    public void songEnd () {
        playSound("song",false);
    }



    @Override
    public void mouseClicked(MouseEvent e) {

    }

    private boolean isTouching (Rectangle r) {

        return r.contains(clickedPoint);
    }
    @Override
    public void mousePressed(MouseEvent e) {
        clickedPoint = e.getPoint();
        if(gameOver || !hasBegun) {
            if (isTouching(new Rectangle(600 / 2 - 110, 600 / 2 - 10, 156, 87))) {
                jump();
                gameOver = false;
            }

            if (isTouching(new Rectangle(214,419,40,28))){
                speed = 5;
                birdNormal = yellowNormal.getImage();
                birdTilted = yellowTilted.getImage();
                file = new File("src\\FlappyBird\\highscoreEasy.txt");
                jump();
                totalHighScore = "0";
                gameOver = false;
            }

            if (isTouching(new Rectangle(277,419,40,28))){
                speed = 6;
                birdNormal = blueNormal.getImage();
                birdTilted = blueTilted.getImage();

                jump();
                gameOver = false;
            }
            if (isTouching(new Rectangle(336,419,40,28))){
                speed = 9;
                file = new File("src\\FlappyBird\\highscoreHard.txt");
                totalHighScore = "0";
                birdNormal = redNormal.getImage();
                birdTilted = redTilted.getImage();

                jump();
                gameOver = false;
            }
        }


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }



    public static void main(String[] args){
        flappyBird = new FlappyBird();
    }
}


