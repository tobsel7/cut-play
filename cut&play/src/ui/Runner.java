package ui;

	/**
	 * Runner class for the application
	 * @author Tobias Haider
	 */
public class Runner {
	
	/**
	 * Main method for the application
	 * Simply creating a UserInterface object
	 * @param args Main method arguments
	 */
	public static void main(String[] args) {
		UserInterface ui = new UserInterface();
		ui.setVisible(true);
	}
}
