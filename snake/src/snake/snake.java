
//아직 제작중입니다

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
    private int position_x;//x위치
    private int position_y;//y위치

    public Player(int x,int y) {
        this.position_x = x;
        this.position_y = y;
    	x_direction = 2;
    	y_direction = 2;
    }

    public int get_x() {
        return this.position_x;
    }

    public int get_y() {
        return this.position_y ;
    }
    public void set_x(int position) {
        this.position_x = position;
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
	private Player[] p;
	int eaten;
	inital_move(Player[] p,int eaten){
		this.p = p;
		this.eaten = eaten;
	}
	public void run(){
		try{
			while(true){
				
				if(this.p[0].get_x_direction() == 2) {
					this.p[0].move_x(1);
				}
				else if(this.p[0].get_x_direction() == 1){
					this.p[0].move_x(-1);
				}
				
				else if(this.p[0].get_y_direction() == 2) {
					this.p[0].move_y(1);

				}
				else if(this.p[0].get_y_direction() == 1){
					this.p[0].move_y(-1);
				}
				Thread.sleep(10000);
			}
		}
		catch(InterruptedException e){
			
		}
	}
}
class game extends JPanel {
    public Player[] playerobj;
    private boolean Start_screen;//시작 화면인지 확인
    public int eaten;

    game() {
    	Start_screen = true;
    	
    	
        setPreferredSize(new Dimension(700,700));
        setBackground(new Color(255, 255, 255));
        setFocusable(true);
        addKeyListener(new KeyInput());
        playerobj = new Player[30];
        for(int i=0;i<30;i++) {
        	playerobj[i] = new Player(350-30*i,350);
        }
        eaten = 10;
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
            if (key == KeyEvent.VK_LEFT) {
            	playerobj[0].x_change_direction(1);
            	playerobj[0].y_change_direction(0);
            }
            if (key == KeyEvent.VK_RIGHT) {
            	playerobj[0].x_change_direction(2);
            	playerobj[0].y_change_direction(0);
            }
            if (key == KeyEvent.VK_UP) {
            	playerobj[0].y_change_direction(1);
            	playerobj[0].x_change_direction(0);
            }
            if (key == KeyEvent.VK_DOWN) {
            	playerobj[0].y_change_direction(2);
            	playerobj[0].x_change_direction(0);
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
            Thread R1 =new Thread(new inital_move(playerobj,eaten));
            R1.start();
			repaint();
			for(int i=this.eaten;i>0;i--) {
				playerobj[i].set_x(playerobj[i-1].get_x());
				playerobj[i].set_y(playerobj[i-1].get_y());

			}
			for(int i=0;i<eaten;i++) {
				g.fillRect(playerobj[i].get_x(), playerobj[i].get_y(), 30, 30);
			}
    		
    	}

    }

}

public class snake{
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