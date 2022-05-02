import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class WinCalendar extends JFrame {
	
	private JPanel contentPane;
	private final JPanel panel = new JPanel();
	private final JTextField txtYear = new JTextField();
	private final JLabel lblYear = new JLabel("년");
	private final JLabel lblMonth = new JLabel("월");
	private final JTextField txtMonth = new JTextField();
	private final JButton btnCreate = new JButton("생성");
	private final JPanel panelCal = new JPanel();
	private boolean[] bSchedule = new boolean[31];
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WinCalendar frame = new WinCalendar();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public WinCalendar() {
		setTitle("2022년 달력");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 558, 355);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		contentPane.add(panel, BorderLayout.NORTH);
		txtYear.setHorizontalAlignment(SwingConstants.RIGHT);
		txtYear.setText("2022");
		
		panel.add(txtYear);
		txtYear.setColumns(10);
		
		panel.add(lblYear);
		txtMonth.setHorizontalAlignment(SwingConstants.RIGHT);
		txtMonth.setText("5");
		
		panel.add(txtMonth);
		txtMonth.setColumns(10);
		
		panel.add(lblMonth);
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//해당 년월을 입력한 후, 버튼 클릭
				try {
					LoadSchedule();
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				//원래의 버튼들을 삭제한다
				Component[] componentList = panelCal.getComponents();
				for(Component c: componentList) {
					if(c instanceof JButton)
						panelCal.remove(c);
				}
				panelCal.revalidate();
				panelCal.repaint();
				
				int sum=0;
				int index=1; //1922년 1월1일의 위치
				int curYear = Integer.parseInt(txtYear.getText());
				int curMonth = Integer.parseInt(txtMonth.getText());
				int Months[] = {31,28,31,30,31,30,31,31,30,31,30,31};
				
				for(int year=1922; year<curYear; year++) {
					if(year%4==0 && year%100!=0 || year%400==0)
						sum = sum + 366;
					else
						sum = sum + 365;
				}
				for(int month=0; month<curMonth-1; month++) {
					if(curMonth==2 && (curYear%4==0 && curYear%100!=1 || curYear%400==0))
						sum = sum + ++Months[month];
					else
						sum = sum + Months[month];
				}
				index = sum%7;
				for(int i=1; i<index+1; i++) {
					JButton btn = new JButton(" ");
					panelCal.add(btn);
					btn.setVisible(false);
				}
					//월의 마지막 날짜에 맞춰 버튼을 생성
					for(int i=1; i<=Months[curMonth-1]; i++) {
						JButton btn = null;
						if(bSchedule[i-1])
							btn = new JButton(Integer.toString(i) + "*");
						else
							btn = new JButton(Integer.toString(i));
						panelCal.add(btn);
						panelCal.revalidate();
						
						btn.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								JButton btn1 = (JButton) e.getSource();
								String year = txtYear.getText();
								String month = txtMonth.getText();
								String day = btn1.getText();
								try {
									InsertSchedule(year, month, day);
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
								//Db연동
							private void InsertSchedule(String year, String month, String day) throws Exception {
								Class.forName("com.mysql.cj.jdbc.Driver");
								String temp = "jdbc:mysql://localhost/sampledb?user=root&password=1234";
								Connection con = DriverManager.getConnection(temp);
								Statement stmt = con.createStatement();
								String strDate = year + "-" + month + "-" + day;
								String sql = "SELECT * FROM scheduletbl WHERE mDate='" + strDate + "'";
								ResultSet rs = stmt.executeQuery(sql);
								if(rs.next()) {
									JOptionPane.showMessageDialog(null,rs.getString("schedult")+"\n[스케줄이 있습니다]");
								}else {
									String msg = JOptionPane.showInputDialog("스케줄을 입력하시오");
									sql = "INSERT INTO scheduletbl values('" + strDate+"','"+msg+"')";
									stmt.executeUpdate(sql);
								}
								rs.close();
								stmt.close();
								con.close();
							}
						});
					}
					}
					private void LoadSchedule() throws Exception{
						Class.forName("com.mysql.cj.jdbc.Driver");
						String temp = "jdbc:mysql://localhost/sampledb?user=root&password=1234";
						Connection con = DriverManager.getConnection(temp);
						Statement stmt = con.createStatement();
						String sql = "SELECT * FROM scheduletbl";
						ResultSet rs = stmt.executeQuery(sql);
						
						String year = txtYear.getText(); //2022
						String month = null;
						if(txtMonth.getText().length()<2) // 01, 02, ... ~12
							month = "0" + txtMonth.getText();
						//System.out.println(year + "," + month);
						while(rs.next()) {					//2022-05-05					
							if(year.equals(rs.getString("mDate").substring(0,4)) && 
									month.equals(rs.getString("mDate").substring(5,7))){
								int idx = Integer.parseInt(rs.getString("mDate").substring(8));
								bSchedule[idx-1]=true;
							}
						}						
					}
		});
		panel.add(btnCreate);
		
		contentPane.add(panelCal, BorderLayout.CENTER);
		panelCal.setLayout(new GridLayout(0, 7, 5, 5));
	}

}
