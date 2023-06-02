package snake;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import static snake.Snake.SIZE;

class gameSpectate extends JPanel {
    private ArrayList<Player> playerobj;
    private int eaten;
    private Food food;
    private GameStatus state;//시작 화면인지 확인
    private final Socket socket;

    gameSpectate() {
        state = GameStatus.NOT_STARTED;
        setPreferredSize(new Dimension(600, 600));
        setBackground(new Color(255, 255, 255));
        setFocusable(true);
        try {
            socket = new Socket("localhost", 5000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        running(g);
        Toolkit.getDefaultToolkit().sync();
        g.setColor(Color.BLACK);
    }

    private void receive() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Object data = ois.readObject();
            Class<?> dataClass = data.getClass();
            if (dataClass == ArrayList.class) {
                playerobj = (ArrayList<Player>) data;
//                System.out.println("playerobj received");
            } else if (dataClass == Food.class) {
                food = (Food) data;
//                System.out.println("food received");
            } else if (dataClass == Integer.class) {
                eaten = (Integer) data;
//                System.out.println("eaten received");
            } else if (dataClass == GameStatus.class) {
                state = (GameStatus) data;
//                System.out.println("state received");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void running(Graphics g) {
        //폰트 지정
        Font font = new Font("Arial", Font.BOLD, 30);
        g.setFont(font);
        receive();
        receive();
        receive();
        receive();
        g.drawString("Spectating", 0, 600-SIZE);
        //시작화면인지 체크
        if (state == GameStatus.NOT_STARTED) {
            g.drawString("SNAKE GAME", 180, 200);
            g.drawString("Waiting  Player  to  Start  Game.", 70, 400);
        }
        //게임 진행중
        else if (state == GameStatus.RUNNING) {
            g.drawString("Eaten: " + Integer.toString(eaten - 4), 10, 30);
            g.drawRect(0, 0, Snake.WIDTH * SIZE, Snake.HEIGHT * SIZE);
            g.setColor(Color.BLUE);
            for (Player player : playerobj.subList(0, eaten - 1)) {
                g.fillRect(player.get_x(), player.get_y(), SIZE, SIZE);
            }
            System.out.println("Length of snake: " + playerobj.size());
            g.setColor(Color.RED);
            g.fillOval(food.get_x(), food.get_y(), SIZE, SIZE);
        } else if (state == GameStatus.GAME_OVER) {
            g.drawString("GAME OVER", 200, 200);
            g.drawString("Waiting  Player  to  Start  Again.", 70, 400);
        }
        try{
            Thread.sleep(70);
            repaint();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

public class Spectator {
    public static void main(String[] args) {
        init();
    }

    static void init() {
        //기초 설정
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        gameSpectate snakeGame = new gameSpectate();
        frame.getContentPane().add(snakeGame);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
