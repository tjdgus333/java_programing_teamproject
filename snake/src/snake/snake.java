package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;

//snake 본체
class Player {
    private int x_direction;//x축 현재 방향
    private int y_direction;//y축 현재 방향
    private int position_x;
    private int position_y;

    public Player() {
        this.position_x = 50;
        this.position_y = 50;
    	x_direction = 0;
    	y_direction = 0;
    }

    public int get_x() {
        return this.position_x;
    }

    public int get_y() {
        return this.position_y ;
    }

    public void move_x(int num) {
        this.position_x += num;
    }

    public void move_y(int num) {
        this.position_y += num;
    }
    public int get_x_direction(){
    	return this.x_direction;
    }
    public int get_y_direction(){
    	return this.y_direction;
    }
    public void x_change_direction(int a) {
    	this.x_direction = a;
    }
    public void y_change_direction(int a) {
    	this.y_direction = a;
    }
    
}
class inital_move extends Thread{
	private Player p;
	inital_move(Player p){
		this.p = p;
	}
	public void run(){
		try{
			while(true){
				if(this.p.get_x_direction() == 2) {
					this.p.move_x(1);
				}
				else if(this.p.get_x_direction() == 1){
					this.p.move_x(-1);
				}
				
				else if(this.p.get_y_direction() == 2) {
					this.p.move_y(1);
				}
				else if(this.p.get_y_direction() == 1){
					this.p.move_y(-1);
				}
				Thread.sleep(10000);
				}
		}
		catch(InterruptedException e){
			
		}
	}
}
class game extends JPanel {
    public Player playerobj;
    private boolean Start_screen;


    game() {
    	Start_screen = true;
    	
    	
        setPreferredSize(new Dimension(700,700));
        setBackground(new Color(255, 255, 255));
        setFocusable(true);
        addKeyListener(new KeyInput());
        playerobj = new Player();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        running(g);
        g.setColor(Color.BLACK);
    }
    //key 입력 받기
    class KeyInput extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            //System.out.println("Key pressed");
            if (key == KeyEvent.VK_LEFT) {
            	playerobj.x_change_direction(1);
            	playerobj.y_change_direction(0);
            }
            if (key == KeyEvent.VK_RIGHT) {
            	playerobj.x_change_direction(2);
            	playerobj.y_change_direction(0);
            }
            if (key == KeyEvent.VK_UP) {
            	playerobj.y_change_direction(1);
            	playerobj.x_change_direction(0);
            }
            if (key == KeyEvent.VK_DOWN) {
            	playerobj.y_change_direction(2);
            	playerobj.x_change_direction(0);
            }
            if (key == KeyEvent.VK_ENTER) {
            	Start_screen = false;
            }
            
            repaint();
        }
    }

    private void running(Graphics g) {
    	//폰트 지정
    	Font font= new Font("Arial",Font.BOLD,30);
    	g.setFont(font);
    	//시작화면인지 체크
    	if(Start_screen == true) {
    		g.drawString("SNAKE GAME",225 ,200);
    		g.drawString("Press  any  key  to  begin", 150, 400);
    	}
    	//게임 진행중
    	else {
            Thread R1 =new Thread(new inital_move(playerobj));
            R1.start();
			repaint();
    		g.fillRect(playerobj.get_x(), playerobj.get_y(), 30, 30);
    		
    	}

    }

}

public class snake{
    public static void main(String[] args) {
    	init();
    	
    }

    static void init() {
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