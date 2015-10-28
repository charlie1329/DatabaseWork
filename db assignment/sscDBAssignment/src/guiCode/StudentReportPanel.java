package guiCode;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import operations.RegisterAndReport;

/**this class will provide the panel for a student report
 * 
 * @author Charlie Street
 *
 */
public class StudentReportPanel extends JPanel 
{
	private JLabel studentIDLabel;
	private JTextField studentText;
	private JButton getReport;
	
	private JButton mainMenu;
	
	private JScrollPane scrollPanel;
	private JPanel reportPanel;
	
	private JFrame parent;
	private RegisterAndReport report;
	
	/**this constructor sets up the interface and action listeners as well as the layout 
	 * 
	 * @param parent the parent frame, useful for changing panels
	 * @param report the object to carry out the work on the database
	 */
	public StudentReportPanel(JFrame parent, RegisterAndReport report)
	{
		BorderLayout border = new BorderLayout();//setting new layout
		setLayout(border);
		
		this.parent = parent;
		this.report = report;
		
		this.studentIDLabel = new JLabel("Student ID: ");
		this.studentText = new JTextField();
		this.studentText.setColumns(20);//forcing to be a set width
		this.getReport = new JButton("Search");
		this.getReport.addActionListener(e -> getReport());
		
		JPanel buttonBar = new JPanel();//panel of label, button and search button
		buttonBar.add(this.studentIDLabel);
		buttonBar.add(this.studentText);
		buttonBar.add(this.getReport);
		add(buttonBar,BorderLayout.NORTH);
				
		this.reportPanel = new JPanel();
		this.scrollPanel = new JScrollPane(this.reportPanel);//allowing scrolling of panel
		add(this.scrollPanel,BorderLayout.CENTER);
		
		this.mainMenu = new JButton("Main Menu");
		this.mainMenu.addActionListener(e -> destroy());
		add(this.mainMenu,BorderLayout.SOUTH);
	}
	
	/**method checks if report is empty i.e invalid student id
	 * 
	 * @param currentReport the report returned by the db operation
	 * @return whether it is empty
	 */
	private boolean isEmptyReport(String[] currentReport)
	{
		for(int i = 0; i < currentReport.length; i++)
		{
			if(currentReport[i] != null)
			{
				return false;
			}
		}
		return true;
	}
	
	/**this method will attempt to retrieve the report for the user
	 * 
	 */
	public void getReport()
	{
		try
		{
			String currentID = this.studentText.getText();
			if(currentID.equals(""))
			{
				JOptionPane.showMessageDialog(this.parent,"No Student ID Entered","No Student ID Entered", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				int currentIDInt = Integer.parseInt(currentID);//needs to be int
				String[] currentReport = this.report.studentReport(currentIDInt);
				if(isEmptyReport(currentReport))//if no result
				{
					JOptionPane.showMessageDialog(this.parent,"Not a valid student ID","Invalid Student ID", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					//label to show the report (using HTML tags to format text)
					JLabel reportLabel = new JLabel("<html>Student Report For: " + currentReport[0] + " " + currentReport[1] + " " + (currentReport[2] == null ? "(no last name)" : currentReport[2]) + "<br/>" +
													"Date Of Birth: " + currentReport[3] + "<br/>" + //currentReport[2] has if statment as it may be null so I want to account for such an event
													"Student ID: " + currentReport[4] + "<br/>" + 
													"Year Of Study: " + currentReport[5] + "<br/>" + 
													"Registration Type: " + currentReport[6] + "<br/>" + 
													"Email: " + currentReport[7] + "<br/>" + 
													"Address: " + currentReport[8] + "<br/><br/>" + 
													"Emergency Contact: " + "<br/>" +
													"Name: " + currentReport[9] + "<br/>" +
													"Email: " + currentReport[10] + "<br/>" + 
													"Address: " + currentReport[11] + "<html>");
					this.reportPanel.removeAll();//adding to panel
					this.reportPanel.add(reportLabel);
					this.reportPanel.revalidate();
					this.reportPanel.repaint();
				}
			}
		}
		catch(SQLException e)//if database goes wrong
		{
			JOptionPane.showMessageDialog(this.parent,e.getCause(),"SQL Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(NumberFormatException e)//if wrong form of input
		{
			JOptionPane.showMessageDialog(this.parent,"Student ID should be an integer","Number Format Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**this method will destroy the current panel and take the user back to the main menu
	 * 
	 */
	public void destroy()
	{
		this.parent.getContentPane().remove(this);
		this.parent.getContentPane().removeAll();
		this.parent.getContentPane().revalidate();
		this.parent.getContentPane().repaint();
		MainMenuPanel main = new MainMenuPanel(parent,report);
		this.parent.add(main);
	}
	
	
}
