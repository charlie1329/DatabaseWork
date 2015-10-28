package guiCode;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import operations.RegisterAndReport;

/**this class represents a JPanel for use with lecturer reports
 * 
 * @author Charlie Street
 *
 */
public class LecturerReportPanel extends JPanel
{
	private JLabel lecturerIDLabel;
	private JTextField lecturerField;
	private JButton search;
	
	private JPanel display;
	private JScrollPane scroll;
	
	private JFrame parent;
	private RegisterAndReport report;
	
	private JButton mainMenu;
	
	private int current; //used for keeping track of students in lecturer report
	
	/**constructor sets up all gui layout etc. and action listeners to create the panel
	 * 
	 * @param parent the parent JFrame, making it easy to switch panels
	 * @param report the object carrying out all the work on the database
	 */
	public LecturerReportPanel(JFrame parent, RegisterAndReport report)
	{
		BorderLayout border = new BorderLayout();//setting border layout
		setLayout(border);
		
		this.parent = parent;
		this.report = report;
		
		this.lecturerIDLabel = new JLabel("Lecturer ID: ");//creating components of Button Bar
		this.lecturerField = new JTextField();
		this.lecturerField.setColumns(20);
		this.search = new JButton("Search");
		this.search.addActionListener(e -> getReport());
		
		JPanel buttonBar = new JPanel();//creating panel for data entry
		buttonBar.add(this.lecturerIDLabel);
		buttonBar.add(this.lecturerField);
		buttonBar.add(this.search);
		add(buttonBar,BorderLayout.NORTH);//adding button bar to north of panel
		
		this.display = new JPanel();
		this.scroll = new JScrollPane(this.display);//making the display panel scroll-able
		add(this.scroll,BorderLayout.CENTER);
		
		this.mainMenu = new JButton("Main Menu");//setting up main menu button
		this.mainMenu.addActionListener(e -> destroy());
		add(this.mainMenu,BorderLayout.SOUTH);
		
		this.current = 1;
	}
	
	/**method will return whether the report is empty or not
	 * 
	 * @param report the result of the report
	 * @return whether the report given is empty
	 */
	private boolean emptyResult(ArrayList<String[]> report)
	{
		for(int i = 0; i < report.size(); i++)
		{
			for(int j = 0; j < report.get(i).length; j++)
			{
				if(report.get(i)[j] != null)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/**this method will retrieve the report for the lecturer from the database
	 * 
	 */
	public void getReport()
	{
		try
		{
			String currentID = this.lecturerField.getText();
			if(currentID.equals(""))
			{
				JOptionPane.showMessageDialog(this.parent,"No Lecturer ID Entered","No Lecturer ID Entered", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				int currentIDInt = Integer.parseInt(currentID);
				ArrayList<String[]> result = report.lecturerReport(currentIDInt);
				if(emptyResult(result))
				{
					JOptionPane.showMessageDialog(this.parent,"Not a valid lecturer ID","Invalid Lecturer ID", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					String output = "<html> Lecturer Report For: " + result.get(0)[1] + " " + result.get(0)[2] + " " + //using html tags for text formatting
									result.get(0)[3] + " ID: " + result.get(0)[0] + 
									"<br/><br/> First Year Tutees: <br/><br/>";
					
					this.current = 1;//counter for array list being reinitialised
					output += getStudentsForYear(result,"1");//first year tutees
					
					output += "Second Year Tutees: <br/><br/>";//second year tutees
					output += getStudentsForYear(result,"2");
					
					output += "Third Year Tutees: <br/><br/>";//third year tutees
					output += getStudentsForYear(result,"3");
					
					output += "Fourth Year Tutees: <br/><br/>";//fourth year tutees
					output += getStudentsForYear(result,"4");
					
					output += "Fifth Year Tutees: <br/><br/>";//fifth year tutees
					output += getStudentsForYear(result,"5");
					
					
					output += "<html>";
					
					JLabel out = new JLabel(output);//all information to display
					this.display.removeAll();//adding to panel
					this.display.add(out);
					this.display.revalidate();
					this.display.repaint();
					
				}
			}
		}
		catch(SQLException e)//if database error
		{
			JOptionPane.showMessageDialog(this.parent,e.getCause(),"SQL Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this.parent,"Lecturer ID should be an integer","Number Format Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**gets students for a given year, if none it says so
	 * 
	 * @param result the results passed to the class
	 * @param year the year specified
	 * @param current the current counter
	 * @return the string for that year
	 */
	private String getStudentsForYear(ArrayList<String[]> result, String year)
	{
		String output = "";
		while(this.current < result.size() && result.get(this.current)[5].equals(year))
		{
			output += getStudentString(result,this.current);
			this.current++;
		}
		
		if(output.equals(""))//ie no results
		{
			output += "(None)<br/><br/>";
		}
		
		return output;
		
	}
	
	/**method gets a students report as a string
	 * used for outputting all students
	 */
	public String getStudentString(ArrayList<String[]> result, int current)
	{
		return result.get(current)[0] + " " + result.get(current)[1] + " " + (result.get(current)[2] == null ? "(no last name)" : result.get(current)[2]) + "<br/>" +
				"Date Of Birth: " + result.get(current)[3] + "<br/>" + //result.get(current)[2] has an if statement to allow for null values in last name which may occur
				"Student ID: " + result.get(current)[4] + "<br/>" + 
				"Year Of Study: " + result.get(current)[5] + "<br/>" + 
				"Registration Type: " + result.get(current)[6] + "<br/>" + 
				"Email: " + result.get(current)[7] + "<br/>" + 
				"Address: " + result.get(current)[8] + "<br/>" + 
				"Emergency Name: " + result.get(current)[9] + "<br/>" +
				"Emergency Email: " + result.get(current)[10] + "<br/>" + 
				"Emergency Address: " + result.get(current)[11] + "<br/><br/>";
	}
	
	/**this method will get rid of the current panel, to bring up a new instance of the main menu
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
