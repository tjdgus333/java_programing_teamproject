package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import static snake.Snake.*;

enum GameStatus {
    NOT_STARTED, RUNNING, GAME_OVER
}

//snake 본체
class Player implements Serializable {
    private int x_direction;//x축 현재 방향
    private int y_direction;//y축 현재 방향
    private int position_x;//x위치
    private int position_y;//y위치

    public Player(int x, int y) {
        this.position_x = x;
        this.position_y = y;
        x_direction = 2;
        y_direction = 0;
    }

    public int get_x() {
        return this.position_x;
    }

    public void set_x(int position) {
        this.position_x = position;
    }

    public int get_y() {
        return this.position_y;
    }

    public void set_y(int position) {
        this.position_y = position;
    }

    public void move_x(int num) {
        this.position_x += num;
    }

    public void move_y(int num) {
        this.position_y += num;
    }

    public int get_x_direction() {
        return this.x_direction;
    }

    public int get_y_direction() {
        return this.y_direction;
    }

    public void x_change_direction(int a) {
        this.x_direction = a;
    }

    public void y_change_direction(int a) {
        this.y_direction = a;
    }

}

class Food implements Serializable {
    private int x_p = 0;
    private int y_p = 0;
    private boolean foodsetting;

    public Food() {
        foodsetting = false;
    }

    public void set_food() {
        Random random = new Random();
        int rand_pos;
        rand_pos = random.nextInt(Snake.WIDTH) * SIZE;
        this.x_p = rand_pos;
        rand_pos = random.nextInt(Snake.HEIGHT) * SIZE;
        this.y_p = rand_pos;
        this.foodsetting = true;
    }

    public boolean get_foodsetting() {
        return this.foodsetting;
    }

    public void set_foodsetting() {
        this.foodsetting = false;
    }

    public int get_x() {
        return this.x_p;
    }

    public int get_y() {
        return this.y_p;
    }
}

class initial_move extends Thread {
    //private int eaten;
    private final ArrayList<Player> p;

    initial_move(ArrayList<Player> p/*, int eaten*/) {
        this.p = p;
        //this.eaten = eaten;
    }

    public void run() {
        if (this.p.get(0).get_x_direction() == 2) {
            this.p.get(0).move_x(SIZE);
        } else if (this.p.get(0).get_x_direction() == 1) {
            this.p.get(0).move_x(-SIZE);
        } else if (this.p.get(0).get_y_direction() == 2) {
            this.p.get(0).move_y(SIZE);
        } else if (this.p.get(0).get_y_direction() == 1) {
            this.p.get(0).move_y(-SIZE);
        }
    }
}

class game extends JPanel {
    private ArrayList<Player> playerobj;
    private int eaten;
    private Food food;
    private GameStatus state;//시작 화면인지 확인
    private final Socket socket;

    game() {
        state = GameStatus.NOT_STARTED;

        setPreferredSize(new Dimension(900, 700));
        setBackground(new Color(255, 255, 255));
        setFocusable(true);
        addKeyListener(new KeyInput());
        playerobj = new ArrayList<Player>();
        for (int i = 0; i < 4; i++) {
            playerobj.add(new Player((Snake.WIDTH * SIZE) / 2 - SIZE * i, (Snake.HEIGHT * SIZE) / 2));
        }
        eaten = 4;
        food = new Food();
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

    public byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            bos.close();
            bytes = bos.toByteArray();
        } catch (Exception ex) {
            System.out.println("error");
        }
        return bytes;
    }

//    public <T> T toObject (byte[] bytes, Class<T> type) {
//        Object obj = null;
//        try {
//            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
//            ObjectInputStream ois = new ObjectInputStream (bis);
//            obj = ois.readObject();
//        }
//        catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        catch (ClassNotFoundException ex) {
//            ex.printStackTrace();
//        }
//        return type.cast(obj);
//    }

    private void send() {
        byte[] playerBytes = toByteArray(playerobj);
        byte[] foodBytes = toByteArray(food);
        byte[] eatenBytes = toByteArray(eaten);
        try {
            OutputStream os = socket.getOutputStream();
            os.write(playerBytes);
            os.flush();
            os.write(foodBytes);
            os.flush();
            os.write(eatenBytes);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private void receive() {
//        byte[] buffer = new byte[1024];
//        try{
//            InputStream is = socket.getInputStream();
//            int nReadSize = is.read(buffer);
//            if(nReadSize > 0){
//                receivedPlayer = toObject(buffer, ArrayList.class);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private void running(Graphics g) {
        //폰트 지정
        Font font = new Font("Arial", Font.BOLD, 30);
        g.setFont(font);

        //시작화면인지 체크
        if (state == GameStatus.NOT_STARTED) {
            g.drawString("SNAKE GAME", 225, 200);
            g.drawString("Press  Enter  to  begin", 150, 400);
        }
        //게임 진행중
        else if (state == GameStatus.RUNNING) {
            g.drawString(Integer.toString(eaten - 4), 10, 30);
            Thread R1 = new Thread(new initial_move(playerobj/*, eaten*/));
            R1.start();
            send();
            repaint();
            for (int i = eaten - 1; i > 0; i--) {
                playerobj.get(i).set_x(playerobj.get(i - 1).get_x());
                playerobj.get(i).set_y(playerobj.get(i - 1).get_y());
            }
            //충돌확인
            if (collision_food(food, playerobj.get(0))) {
                eaten++;
                if (eaten > 30) {
                    eaten = 30;//최대점수
                } else {
                    playerobj.add(new Player(playerobj.get(eaten - 2).get_x(), playerobj.get(eaten - 2).get_y()));
                }
                food.set_foodsetting();
            }
            if (!food.get_foodsetting()) {
                food.set_food();
            }

            g.drawRect(0, 0, Snake.WIDTH * SIZE, Snake.HEIGHT * SIZE);

            g.setColor(Color.BLUE);
            for (Player p : playerobj) {
                g.fillRect(p.get_x(), p.get_y(), SIZE, SIZE);
            }

            g.setColor(Color.RED);
            g.fillOval(food.get_x(), food.get_y(), SIZE, SIZE);

            try {
                Thread.sleep(100);
                checkGameOver();
            } catch (InterruptedException e) {
                System.out.println("error");
            }
        } else if (state == GameStatus.GAME_OVER) {
            g.drawString("GAME OVER", 225, 200);
            g.drawString("Press  Enter  to  Play  Again", 150, 400);
        }
    }

    private boolean collision_food(Food f, Player p) {
        return f.get_x() == p.get_x() && f.get_y() == p.get_y();
    }

    private void checkGameOver() {
        Player head = playerobj.get(0);
//        System.out.println("X : " + playerobj.get(0).get_x() + " Y : " + playerobj.get(0).get_y());
//        System.out.println("X : " + playerobj.get(1).get_x() + " Y : " + playerobj.get(1).get_y());
//        System.out.println("X : " + playerobj.get(2).get_x() + " Y : " + playerobj.get(2).get_y());
        boolean boundaryCheck = head.get_x() < 0 ||
                head.get_x() > (SIZE * Snake.WIDTH - SIZE) ||
                head.get_y() < 0 ||
                head.get_y() > (SIZE * Snake.HEIGHT - SIZE);
        boolean ateItself = false;
        for (Player p : playerobj.subList(1, eaten)) {
            if (head.get_x() == p.get_x() && head.get_y() == p.get_y()) {
                ateItself = true;
                break;
            }
        }
//        System.out.println("boundaryCheck : " + boundaryCheck + " ateItself : " + ateItself);
        if (boundaryCheck || ateItself) {
            state = GameStatus.GAME_OVER;
        }
    }

    //key 입력 받기
    class KeyInput extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (state == GameStatus.RUNNING) {
                if (key == KeyEvent.VK_LEFT && playerobj.get(0).get_x_direction() != 2) {
                    playerobj.get(0).x_change_direction(1);
                    playerobj.get(0).y_change_direction(0);
                }
                if (key == KeyEvent.VK_RIGHT && playerobj.get(0).get_x_direction() != 1) {
                    playerobj.get(0).x_change_direction(2);
                    playerobj.get(0).y_change_direction(0);
                }
                if (key == KeyEvent.VK_UP && playerobj.get(0).get_y_direction() != 2) {
                    playerobj.get(0).y_change_direction(1);
                    playerobj.get(0).x_change_direction(0);
                }
                if (key == KeyEvent.VK_DOWN && playerobj.get(0).get_y_direction() != 1) {
                    playerobj.get(0).y_change_direction(2);
                    playerobj.get(0).x_change_direction(0);
                }
            }
            if (state == GameStatus.NOT_STARTED && key == KeyEvent.VK_ENTER) {
                state = GameStatus.RUNNING;
            }
            if (state == GameStatus.GAME_OVER && key == KeyEvent.VK_ENTER) {
                playerobj = new ArrayList<Player>();
                for (int i = 0; i < 4; i++) {
                    playerobj.add(new Player((Snake.WIDTH * SIZE) / 2 - SIZE * i, (Snake.HEIGHT * SIZE) / 2));
                }
                eaten = 4;
                food = new Food();
                state = GameStatus.NOT_STARTED;
            }

            repaint();
        }
    }
}

public class Snake {
    static final int SIZE = 20;
    static final int WIDTH = 30;
    static final int HEIGHT = 30;

    public static void main(String[] args) {
        init();

    }

    static void init() {
        //기초 설정
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        game snakeGame = new game();
        frame.getContentPane().add(snakeGame);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}