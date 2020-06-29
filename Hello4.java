package Test;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

class corona {
	private String time;
	private int infection;
	private int disinfection;
	private int surviving;

	String getTime() {
		return time;
	}

	int getInfection() {
		return infection;
	}

	int getDisinfection() {
		return disinfection;
	}

	int getSurviving() {
		return surviving;
	}

	void setTime(String time) {
		this.time = time;
	}

	void setInfection(int infection) {
		this.infection = infection;
	}

	void setDisinfection(int disinfection) {
		this.disinfection = disinfection;
	}

	void setSurviving(int surviving) {
		this.surviving = surviving;
	}
}

class coronaClass {
	String DB_DRIVER = "org.h2.Driver";
	String DB_CONNECTION = "jdbc:h2:~/test"; // database name
	String DB_USER = "sa"; // user id
	String DB_PASSWORD = ""; // passward
	Connection Conn = null;
	Statement stmt = null;

	void connectDB() {
		try {
			Class.forName(DB_DRIVER);
			Conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void closeDB() {
		try {
			stmt.close();
			Conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	ArrayList<corona> getAll() {
		connectDB();
		ArrayList<corona> alp = new ArrayList<corona>();

		try {
			//Conn.setAutoCommit(false);
			stmt = Conn.createStatement();
			String sql;
			sql = "select * from corona;";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				corona p = new corona();
				p.setTime(rs.getString("time"));
				p.setInfection(rs.getInt("infection"));
				p.setDisinfection(rs.getInt("disinfection"));
				p.setSurviving(rs.getInt("surviving"));
				alp.add(p);
			}
		} catch (SQLException e) {
			System.out.println("Exception Message " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
			return alp;
		}

	}

	void input(String sql) {
		connectDB();
		try {
			stmt = Conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println("Exception Message " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
	}

	void show() {
		ArrayList<corona> showAP;
		showAP = getAll();
		System.out.println("시간 : " + showAP.get(showAP.size() - 1).getTime() + " / " + "확진자 수 : "
				+ showAP.get(showAP.size() - 1).getInfection() + " / " + "자가격리자 수 : "
				+ showAP.get(showAP.size() - 1).getDisinfection() + " / " + "잔존하는 바이러스  : "
				+ showAP.get(showAP.size() - 1).getSurviving());
	}
}

public class Hello4 extends JFrame implements Runnable {

	public static void main(String[] args) {
		new Hello4();
		new Information();
	}

	int f_width = 1500;
	int f_height = 900;
	Image buffImage;
	Graphics buffg;
	Image[] vi;
	Thread th;
	private ArrayList<virus> vip = new ArrayList<virus>(); //크기가 증가하는 빨간색 바이러스
	private ArrayList<virus2> vip2 = new ArrayList<virus2>(); // 크기증가를 멈춘 바이러스.(선택을당한)
	private ArrayList<virus3> vip3 = new ArrayList<virus3>(); // 크기증가가 미미한  잔존하는 바이러스
	private ArrayList<virus2> vip4 = new ArrayList<virus2>(); // 이동을 당한 바이러스.
	private ArrayList<Point> drag = new ArrayList<Point>(); //드래그를 위한 시작점
	private Image backImg = new ImageIcon("Images/Globalmap.png").getImage();
	private Image Check = new ImageIcon("Images/Check.png").getImage();
	private Image logo1 = new ImageIcon("Images/logo1.png").getImage();
	private Image logo2 = new ImageIcon("Images/logo2.png").getImage();
	private int Option = 0;//버튼 상태
	private int hwak;
	private int jaga;
	private int janjon;

	class virus {//크기가 증가하는
		int x;
		int y;
		int w;

		virus(Point p) {
			this.x = p.x;
			this.y = p.y;
			this.w = 5;
		}

		virus(Point p, int w) {
			this.x = p.x;
			this.y = p.y;
			this.w = w;
		}

		public void setX(int x) {
			this.x = x;
		}

		public void setY(int y) {
			this.y = y;
		}

		public void setW(int w) {
			this.w = w;
		}

		int bigger() {
			w += 3;
			return w;
		}

	}

	class virus2 {//크기 증가가 없는
		int x;
		int y;
		int w;

		virus2(Point p, int w) {
			this.x = p.x;
			this.y = p.y;
			this.w = w;
		}

		public void setX(int x) {
			this.x = x;
		}

		public void setY(int y) {
			this.y = y;
		}
	}

	class virus3 {// 크기증가가 미미한
		int x;
		int y;
		int w;

		virus3(Point p) {
			this.x = p.x;
			this.y = p.y;
			this.w = 5;
		}

		virus3(Point p, int w) {
			this.x = p.x;
			this.y = p.y;
			this.w = w;
		}

		int bigger() {
			w += 1;
			return w;
		}
	}

	Hello4() {
		vi = new Image[3];
		vi[0] = new ImageIcon("Images/virus.png").getImage();
		vi[1] = new ImageIcon("Images/virus2.png").getImage(); //배열에 바이러스이미지 담기
		vi[2] = new ImageIcon("Images/virus3.png").getImage();
		setTitle("코로나 바이러스 가시화 시스템");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(f_width, f_height);

		addMouseListener(new MyMouse());
		addMouseMotionListener(new MyMouse());
		th = new Thread(this);
		th.start();
		setVisible(true);

	}

	public void run() {
		int n = 0;
		while (true) {
			n++;
			try {
				repaint();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			if (n == 60) {
				if (Option == 1) {
					n = 0;
				} else {
					Option = 2;
					n = 0;
				}
			}
		}
	}

	public void paint(Graphics g) {
		buffImage = createImage(f_width, f_height);
		buffg = buffImage.getGraphics();
		update(g);

	}

	public void update(Graphics g) {
		Draw();
		g.drawImage(buffImage, 0, 0, this);
	}

	public void Draw() {
		buffg.drawImage(backImg, 0, 0, f_width, f_height, this);

		for (virus data : vip) {
			buffg.drawImage(vi[0], data.x - data.bigger() / 2, data.y - data.bigger() / 2, data.bigger(), data.bigger(),
					this);
		}

		hwak = vip.size();

		for (virus2 data : vip2) {
			buffg.drawImage(vi[1], data.x - data.w / 2, data.y - data.w / 2, data.w, data.w, this);
		}

		jaga = vip2.size();

		for (virus3 data : vip3) {
			buffg.drawImage(vi[2], data.x - data.bigger() / 2, data.y - data.bigger() / 2, data.bigger(), data.bigger(),
					this);
		}

		janjon = vip3.size();

		for (virus2 data : vip4) {
			buffg.drawImage(vi[1], data.x - data.w / 2, data.y - data.w / 2, data.w, data.w, this);
		}

		setTitle("코로나 바이러스 가시화 시스템 - 확진자  : " + hwak + "명, 자가격리자 : " + jaga + "명, 잔존하는 바이러스 : " + janjon);

		if (Option == 0) {
			buffg.drawImage(logo1, (f_width - 300) / 2, 50, 300, 30, this);
		}

		if (Option == 1) {
			buffg.drawImage(Check, (f_width - 50) / 2, f_height - 100, 50, 50, this);
			buffg.drawImage(logo2, (f_width - 400) / 2, 50, 400, 30, this);
		}

		if (Option == 2) {
			SimpleDateFormat format1 = new SimpleDateFormat("MM월dd일HH시mm분");
			Calendar time = Calendar.getInstance();
			String format_time1 = format1.format(time.getTime()); //format_time1(String) : 2018-10-26 22:28:15
			coronaClass coro = new coronaClass();
			coro.input("insert into corona values('" + format_time1 + "'," + hwak + "," + jaga + "," + janjon + ");");
			Option = 0;
			System.out.println(format_time1 + " 기록완료.");
		}
	}

	class MyMouse extends MouseAdapter {

		private Point start;

		public void mousePressed(MouseEvent e) {

			//마우스 버튼이 눌렸을때
			start = e.getPoint();

			if (Option == 0) { //기본상태
				for (virus data : vip) {
					if ((data.x - data.bigger() / 4 <= start.x && start.x <= data.x + data.bigger() / 4)
							&& (data.y - data.bigger() / 4 <= start.y && start.y <= data.y + data.bigger() / 4)) {
						Point p1 = new Point(data.x, data.y);
						virus2 a = new virus2(p1, data.bigger());
						vip2.add(a);
						vip.remove(vip.indexOf(data));
						return;
					}

				}

				for (virus2 data2 : vip2) { //멈춰진 바이러스 클릭시 동작하는 루틴
					if ((data2.x - data2.w / 4 <= start.x && start.x <= data2.x + data2.w / 4)
							&& (data2.y - data2.w / 4 <= start.y && start.y <= data2.y + data2.w / 4)) {
						String[] buttonText = { "제거", "이동" };
						int result = JOptionPane.showOptionDialog(null, "어떤 동작을 원하십니까", "선택 창",
								JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttonText,
								buttonText[0]);
						if (result == JOptionPane.CLOSED_OPTION) { //사용자가 둘다 클릭하지 않고 창을 닫은 경우.

						} else if (result == 0) { //에를 클릭한 경우(제거를 클릭)
							vip2.remove(vip2.indexOf(data2));
						} else { //아니오를 클릭한 경우(이동을 클릭)
							vip2.remove(vip2.indexOf(data2));
							Point p2 = new Point(data2.x, data2.y);
							virus3 a2 = new virus3(p2, data2.w);
							vip3.add(a2); //크기가 미세하게 변하는 바이러스에 추가

							virus2 a4 = new virus2(p2, data2.w);
							vip4.add(a4);
							Option = 1;

						}

						return;
					}

				}

				for (virus3 data : vip3) {
					if ((data.x - data.w / 4 <= start.x && start.x <= data.x + data.w / 4)
							&& (data.y - data.w / 4 <= start.y && start.y <= data.y + data.w / 4)) {
						vip3.remove(vip3.indexOf(data));
						return;
					}
				}

				virus p4 = new virus(start);
				vip.add(p4);

			}

			if (Option == 1) {//선택상태
				if (((f_width - 50) / 2 - 50 <= start.x && start.x <= (f_width - 50) / 2 + 50)
						&& (f_height - 100 <= start.y && start.y <= f_height - 50)) {
					for (virus2 data : vip4) {
						Point p3 = new Point(data.x, data.y);
						virus a3 = new virus(p3, data.w);
						vip.add(a3);

					}
					vip4.clear();
					Option = 0;
					return;
				}
			}
		}

		public void mouseClicked(MouseEvent e) {

		}

		public void mouseDragged(MouseEvent e) {
			Point end = new Point();
			end = e.getPoint();
			if (Option == 1) {
				for (virus2 data : vip4) {
					if ((data.x - data.w / 4 <= end.x && end.x <= data.x + data.w / 4)
							&& (data.y - data.w / 4 <= end.y && end.y <= data.y + data.w / 4)) {
						data.setX(end.x);
						data.setY(end.y);
						drag.add(end);
					}
				}

			}
		}

		public void mouseReleased(MouseEvent e) {
			for (int i = 0; i < drag.size(); i += 50) {
				Point p = new Point(drag.get(i));
				virus3 p1 = new virus3(p);
				vip3.add(p1);
			}
			drag.clear();
		}

	}

}

class Information extends JFrame {

	private JLabel info1;//확진자
	private JLabel info2;//자가격리자
	private JLabel info3;//잔존하는 바이러스
	private JLabel info4;//수정시간
	private JButton update;

	Font font = new Font("GOTHIC", Font.BOLD, 15);

	Information() {

		setTitle("인원현황");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new GridLayout(4, 2));

		JLabel gul1 = new JLabel("확진자 :");//확진자글씨
		gul1.setFont(font);
		JLabel gul2 = new JLabel("자가격리자 :");//자가격리자 글씨
		gul2.setFont(font);
		JLabel gul3 = new JLabel("잔존 바이러스 :");//생존바이러스
		gul3.setFont(font);
		info1 = new JLabel();
		info1.setFont(font);
		info2 = new JLabel();
		info2.setFont(font);
		info3 = new JLabel();
		info3.setFont(font);
		info4 = new JLabel();
		info4.setFont(font);
		update = new JButton("UPDATE");
		update.setFont(font);

		update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				coronaClass p = new coronaClass();
				ArrayList<corona> pList = p.getAll();
				int i = pList.size() - 1;
				info1.setText(Integer.toString(pList.get(i).getInfection()));
				info2.setText(Integer.toString(pList.get(i).getDisinfection()));
				info3.setText(Integer.toString(pList.get(i).getSurviving()));
				info4.setText(pList.get(i).getTime());
			}
		});

		c.add(gul1);
		c.add(info1);
		c.add(gul2);
		c.add(info2);
		c.add(gul3);
		c.add(info3);
		c.add(info4);
		c.add(update);
		setLocation(1500, 0);
		setVisible(true);
		setSize(400, 590);

	}
}