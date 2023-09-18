import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int snakeBodyParts = 6;
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    public void restartGame() {
        snakeBodyParts = 6;
        applesEaten = 0;
        direction = 'R';

        for(int i = 0; i < snakeBodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        newApple();

        if (!running) {
            running = true;
            if (timer != null) {
                timer.stop();
            }
            timer = new Timer(DELAY, this);
            timer.start();
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        if(running) {
            for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++) {
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH,i*UNIT_SIZE);
            }
            if((applesEaten > 8) && (applesEaten%2 == 0)){
                g.setColor(Color.green);
                g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);
            } else if (((applesEaten > 10) && (applesEaten%5 == 0))){
                g.setColor(Color.white);
                g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);
            }else {
                g.setColor(Color.red);
                g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);
            }

            for (int i = 0; i < snakeBodyParts; i++){
                if(i == 0){
                    g.setColor(new Color(181, 245, 173));
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                } else if(i%2 == 0) {
                    g.setColor(new Color(5, 131, 10));
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                } else {
                    g.setColor(new Color(31, 115, 15));
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                }
            }
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free",Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten,(SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }
    }
    public void newApple(){
        appleX = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
    }
    public void move(){
        for(int i = snakeBodyParts; i > 0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }
    public void checkEatenApple() {
        if((x[0] == appleX) && (y[0] == appleY)){
            applesEaten++;
            newApple();
            if((applesEaten > 8) && (applesEaten%2 == 0)){
                snakeBodyParts += 2;
            } else if (((applesEaten > 10) && (applesEaten%5 == 0))) {
                snakeBodyParts = 7;
            } else {
                snakeBodyParts++;
            }
        }
    }
    public void checkCollision(){
        //checks if head touches body
        for(int i = snakeBodyParts; i > 0; i--){
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        //checks if head touches any border
        if(x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        if(!running){
            timer.stop();
        }
    }
    public void gameOver(Graphics g){
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free",Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten,(SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over",(SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free",Font.BOLD, 20));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press 'R' to play again.",(SCREEN_WIDTH - metrics3.stringWidth("Press 'R' to play again."))/2, SCREEN_HEIGHT/3);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkEatenApple();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') {
                        direction = 'L';
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') {
                        direction = 'R';
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') {
                        direction = 'U';
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') {
                        direction = 'D';
                    }
                }
                case KeyEvent.VK_R -> {
                    if (!running) {
                        restartGame();
                    }
                }
            }
        }
    }
}
