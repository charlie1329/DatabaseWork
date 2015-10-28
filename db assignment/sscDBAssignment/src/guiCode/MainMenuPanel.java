package guiCode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import operations.RegisterAndReport;

/**this class represents the main menu of the database system
 * 
 * @author Charlie Street
 *
 */
public class MainMenuPanel extends JPanel
{
	private JLabel titleLabel;
	private JButton studentEntry;
	private JButton tutorEntry;
	private JButton studentReport;
	private JButton lecturerReport;
	
	private JFrame parent;
	private RegisterAndReport register;
	
	/**constructor sets up components
	 * 
	 * @param parent the parent frame
	 * @param register the register object for db ops
	 */
	public MainMenuPanel(JFrame parent, RegisterAndReport register)
	{
		this.parent = parent;
		this.register = register;
		
		this.titleLabel = new JLabel("University Database System");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.PLAIN, 24));
		
		this.studentEntry = new JButton("Student Registration");//initialising buttons
		this.studentEntry.addActionListener(e -> newRegisterStudent());
		
		this.tutorEntry = new JButton("Tutor Registration");
		this.tutorEntry.addActionListener(e -> newRegisterTutor());
		
		
		this.studentReport = new JButton("Student Report");
		this.studentReport.addActionListener(e -> newStudentReport());
		
		
		this.lecturerReport = new JButton("Lecturer Report");
		this.lecturerReport.addActionListener(e -> newLecturerReport());
		
		BorderLayout border = new BorderLayout();//setting layout manager
		setLayout(border);
		
		JPanel button1 = new JPanel();//creating new panels
		button1.add(studentEntry);
		button1.add(tutorEntry);
		
		add(button1,BorderLayout.WEST);
		
		JPanel button2 = new JPanel();
		button2.add(studentReport);
		button2.add(lecturerReport);
		
		add(button2, BorderLayout.EAST);
		
		add(this.titleLabel, BorderLayout.NORTH);
		
	}
	
	/**changes panel to new student register form
	 * 
	 */
	public void newRegisterStudent()
	{
		this.parent.getContentPane().remove(this);
		this.parent.getContentPane().revalidate();
		this.parent.getContentPane().repaint();
		StudentEntryPanel entry = new StudentEntryPanel(this.parent, this.register);
		this.parent.add(entry);
	}
	
	/**will open up the tutor registration part of the program
	 * 
	 */
	public void newRegisterTutor()
	{
		this.parent.getContentPane().remove(this);
		this.parent.getContentPane().revalidate();
		this.parent.getContentPane().repaint();
		TutorEntryForm entry = new TutorEntryForm(this.parent, this.register);
		this.parent.add(entry);
	}
	
	/**changes panel to new student report panel
	 * 
	 */
	public void newStudentReport()
	{
		this.parent.getContentPane().remove(this);
		this.parent.getContentPane().revalidate();
		this.parent.getContentPane().repaint();
		StudentReportPanel report = new StudentReportPanel(this.parent, this.register);
		this.parent.add(report);
	}
	
	/**changes panel to new lecturer report panel
	 * 
	 */
	public void newLecturerReport()
	{
		this.parent.getContentPane().remove(this);
		this.parent.getContentPane().revalidate();
		this.parent.getContentPane().repaint();
		LecturerReportPanel report = new LecturerReportPanel(this.parent, this.register);
		this.parent.add(report);
	}
	
}
