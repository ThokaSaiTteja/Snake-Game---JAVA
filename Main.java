import javax.swing.*;
public class Main {
    public static void main(String[] args) throws Exception {
        int boardHeight=600;
        int boardWidth=boardHeight;


        JFrame frame = new JFrame("Snakegame");
        
        frame.setSize(boardWidth,boardHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        
        SnakeGameP snakegame = new SnakeGameP(boardWidth,boardHeight);
        frame.add(snakegame);
        frame.pack();
        snakegame.requestFocusInWindow();
    }
}
