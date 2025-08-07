import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import javax.swing.*;



public class SnakeGameP extends JPanel implements ActionListener,KeyListener{
    private class Tile{
        int x;
        int y;
        Tile(int x,int y){
            this.x=x;
            this.y=y;
        }
    }
    int boardWidth;
    int boardHeight;
    int tileSize = 25;
    Tile snakeHead;
    ArrayList<Tile> snakeBody ;
    Tile food ;
    Random random;
    Timer gameLoop;
    int velocityx;
    int velocityy;
    boolean gameOver = false;
    private int highScore;
    private final String highScoreFile = "highscore.txt";
    int speed =120;

    
    
    
         //constructor
         SnakeGameP(int boardWidth, int boardHeight){
            this.boardHeight = boardHeight;
            this.boardWidth = boardWidth;
            setPreferredSize(new Dimension(this.boardWidth,this.boardHeight));
            setBackground(Color.black);
            setFocusable(true);
            addKeyListener(this);
            

            loadHighScore();
            playSound("sound_effects/start.wav");
            
            //snake
            snakeHead = new Tile(5,5);
            snakeBody = new ArrayList<>();
            // food 
            food = new Tile(20, 20);


            // random number for the food 
            random = new Random();
            placeFood();

            ///game loop 
            velocityx=1;
            velocityy=0;
            gameLoop = new Timer(speed, (ActionListener) this);
            gameLoop.start();
           
            
            
        }

   

    // Load high score when the game starts
    private void loadHighScore() {
    try {
        File file = new File(highScoreFile);
        if (!file.exists()) {
            file.createNewFile(); // Create file if it doesn't exist
            highScore = 0;
            return;
        }
        
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        if (line == null || line.isEmpty()) {
            highScore = 0; // Handle empty file
        } else {
            highScore = Integer.parseInt(line.trim());
        }
        reader.close();
    } catch (IOException | NumberFormatException e) {
        highScore = 0; // Default to 0 in case of an error
    }
    }

    // Save the new high score if it's greater than the previous one
    private void saveHighScore() {
    try {
        FileWriter writer = new FileWriter(highScoreFile);
        writer.write(String.valueOf(highScore));
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    // Call this when the game is over
    private void updateHighScore() {
    int currentScore = snakeBody.size();
    if (currentScore > highScore) {
        highScore = currentScore;
        saveHighScore();
    }
    }
        public void  paintComponent(Graphics g){
            super.paintComponent(g);
            draw(g);
        }
        public void draw(Graphics g){

           // Grid 
            // for(int i=0;i<boardHeight/tileSize;i++){
            //     g.setColor(Color.white);
            //     g.drawLine(i*tileSize,0, i*tileSize,boardHeight);
            //     g.drawLine(0, i*tileSize,boardWidth, i*tileSize);
            // }


            ///food
            g.setColor(Color.red);
            g.fill3DRect(food.x*tileSize,food.y*tileSize,tileSize,tileSize,true);


            /// snake
            g.setColor(Color.pink);
            //g.fillRect(snakeHead.x*tileSize,snakeHead.y*tileSize,tileSize,tileSize);
            g.fill3DRect(snakeHead.x*tileSize,snakeHead.y*tileSize,tileSize,tileSize,true);

            //snake body 
            g.setColor(Color.green);
            for(int i=0;i<snakeBody.size();i++){
                Tile snakePart = snakeBody.get(i);
               // g.fillRect(snakePart.x*tileSize,snakePart.y*tileSize,tileSize,tileSize);
               g.fillRect(snakePart.x*tileSize,snakePart.y*tileSize,tileSize,tileSize);

            }

            //score represent
            
            if (gameOver) {
                g.setColor(Color.red);
                g.drawString("GAME OVER!  ", boardWidth / 2 - 40, boardHeight / 2);
                g.drawString( "YOUR SCORE IS " + String.valueOf(snakeBody.size()), boardWidth / 2 - 60, boardHeight / 2 + 20);
                g.drawString("HIGH SCORE: " + highScore, boardWidth / 2 - 53, boardHeight / 2 + 40);
                g.drawString("TO RESTART THE GAME ,PRESS R", boardWidth / 2 - 95, boardHeight / 2 + 60);
                g.setColor(Color.black);
                g.fill3DRect(food.x*tileSize,food.y*tileSize,tileSize,tileSize,true);
                
                snakeBody.clear();
    
            }
            else{
                g.drawString(" Score :" +String.valueOf(snakeBody.size()),tileSize,tileSize);
                g.drawString("High Score: " + highScore, tileSize, tileSize + 20);

            }
        }
        
        public void placeFood() {
            do {
                food.x = random.nextInt(boardWidth / tileSize);
                food.y = random.nextInt(boardHeight / tileSize);
            } while (isFoodOnSnake(food));
        }
        
        private boolean isFoodOnSnake(Tile food) {
            if (collision(snakeHead, food)) return true;
            for (Tile part : snakeBody) {
                if (collision(part, food)) return true;
            }
            return false;
        }
        
        public void move(){
            // eat food 
            if(collision(snakeHead, food)){
                    snakeBody.add(new Tile(food.x, food.y));
                    placeFood();
                    speed-=2;
                    gameLoop.setDelay(speed);
                    playSound("sounds/eat.wav");

            }

            
            
           

            //snake body movement 
            // for (int i=snakeBody.size()-1; i>=0; i--) {
            //     Tile snakePart = snakeBody.get(i);
            //     if(i==0){
            //         snakePart.x = snakeHead.x;
            //         snakePart.y = snakeHead.y;
            //   }
            //     else{
            //         Tile preSnakePart = snakeBody.get(i-1);
            //         snakePart.x=preSnakePart.x;
            //         snakePart.y=preSnakePart.y;
            //     }
            // }

            // Move the snake body (Optimized)
    if (!snakeBody.isEmpty()) {
        snakeBody.add(0, new Tile(snakeHead.x, snakeHead.y));
        snakeBody.remove(snakeBody.size() - 1);
    }

             //snake head movement 
             snakeHead.x+=velocityx;
             snakeHead.y+=velocityy;


            for(int i=0;i<snakeBody.size();i++){
                Tile snakePart = snakeBody.get(i);

                // collision of snake it self ;
                if(collision(snakeHead, snakePart)){
                    gameOver = true;
                    playSound("sound_effect/start.wav");

                }

                // collision with walls 
            //     if (snakeHead.x < 1 || snakeHead.x >= (boardWidth / tileSize)-1 || 
            //         snakeHead.y < 1 || snakeHead.y >= (boardHeight / tileSize)-1) {
            //     gameOver = true;
            // }
            if (snakeHead.x < 0 || snakeHead.x >= (boardWidth / tileSize) || 
                    snakeHead.y < 0 || snakeHead.y >= (boardHeight / tileSize)) {
                gameOver = true;
            }
            }
        }

        public boolean collision(Tile a,Tile b){
            return a.x==b.x && a.y==b.y; 
        }

        @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
       if(gameOver){
        //playSound("sound_effect/start.wav");

        updateHighScore(); 
        gameLoop.stop();
       }
    }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && velocityy!=1){
                    velocityx=0;
                    velocityy=-1;
                    playSound("sound_effects/keyboard.wav");
            }
            else if((key== KeyEvent.VK_DOWN || key== KeyEvent.VK_S) && velocityy!=-1){
                velocityx=0;
                velocityy=1;
                playSound("sound_effects/keyboard.wav");
            }
            else if((key== KeyEvent.VK_LEFT || key== KeyEvent.VK_A) && velocityx!=1){
                playSound("sound_effects/keyboard.wav");
                velocityx=-1;
                velocityy=0;
            }
            else if((key== KeyEvent.VK_RIGHT  || key== KeyEvent.VK_D) && velocityx!=-1){
                playSound("sound_effects/keyboard.wav");
                velocityx=1;
                velocityy=0;
            }
            else if (key == KeyEvent.VK_R && gameOver) {
                playSound("sound_effects/keyboard.wav");
                restartGame();
                speed=120;
            }
            
        }
        private void restartGame() {
            playSound("sound_effects/keyboard.wav");
            snakeHead = new Tile(5, 5);
            snakeBody.clear();
            placeFood();
            velocityx = 1;
            velocityy = 0;
            gameOver = false;
            gameLoop.start();
        }
        

            // sound effects:
        public static void playSound(String filePath) {
            try {
                File soundFile = new File(filePath);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // do not need this !
        //                  V
        @Override
        public void keyTyped(KeyEvent e) {
           
        }        
        @Override
        public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub
            // throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
        }
}
