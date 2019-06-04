package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;


import mod.Player;
import mod.Track;
import mod.Converter;
import mod.Modifier;

/**
 * Handles all UI elements which allow the user to interact with the program.
 * Interacts directly with the classes Converter, Modifier and Player. Uses the Track class to store audio files.
 * The main elements are various buttons which cause different actions (play a track, cut files etc.) in the classes mentioned above.
 * A JList shows the elements currently loaded or being modified.
 * @author Tobias Haider
 */

public class UserInterface extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1634782744123000578L;
	private final String STANDARD_PATH_STRING = "..\\resources";	//Standard Path String
	private final JList<Track> jTrackList;	//JList visualizing the ListModel
	private final DefaultListModel<Track> trackList;	//ListModel containing Tracks
	private final Player player;	//Player object used to play Tracks
	private final Waveform waveform;	//JPanel representing a waveform 				
	private JButton play, skip, save, remove, cut, fadeIn, fadeOut, addSil, volume, autocut, concat, add, substract ;
	private File dir; //Directory with mp3 files
	
	/**
	 * Constructor for the user interface
	 * Initializes all final variables of the class.
	 * Sets up the components(buttons, panels etc.) by calling different init methods.
	 */
	public UserInterface() {
		super("cut&play");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		trackList = new DefaultListModel<Track>();
		jTrackList = new JList<Track>(trackList);
		player = new Player();
		waveform = new Waveform();
		initDir(STANDARD_PATH_STRING);
		initTrackList();
		initButtons();
		initMenubar();
		configUi();
	}

	/**
	 * Sets the directory.
	 * @param path A string representing path for the directory
	 */
	public void initDir(String path) {	//Enter main path to music file location
		dir = new File(path);
	}

	/**
	 * Configures different UI elements (multiple panels and a JList).
	 * The buttons, which are already intitialized are added to the corresponding panels.
	 */
	private void configUi() { 
		this.setSize(800, 400);	//Configure window
		this.setLocation(300,200);

        //Configure SelectionListener
        jTrackList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                final List<Track> selectedValuesList = jTrackList.getSelectedValuesList();
                if(selectedValuesList.size() > 0) {
                    player.setTrack(selectedValuesList.get(0));
                    waveform.createWaveForm(player.getTrack().getData());
                }
                player.stop();
            }
        });
        
        JPanel playerPanel = new JPanel();	//Configure player panel 
        playerPanel.setLayout(new GridLayout(2,2));
        playerPanel.add(play);
        playerPanel.add(skip);
        playerPanel.add(save);
        playerPanel.add(remove);
        
		JToolBar toolbar = new JToolBar(); 	
		
		toolbar.add(playerPanel);
		toolbar.add(waveform);
		
		JPanel modPanel = new JPanel();	//Configure modification panel
		modPanel.setLayout(new GridLayout(3,3));
		
		modPanel.add(cut);
		modPanel.add(fadeIn);
		modPanel.add(fadeOut);
		modPanel.add(addSil);
		modPanel.add(volume);
		modPanel.add(autocut);
		modPanel.add(concat);
		modPanel.add(add);
		modPanel.add(substract);
		
		getContentPane().add(new JScrollPane(jTrackList), BorderLayout.CENTER);	//Configure position of elements
		getContentPane().add(toolbar, BorderLayout.SOUTH);
		getContentPane().add(modPanel, BorderLayout.EAST);
	}
	/**
	 * The ListModel is filled with tracks from the current directory.
	 * Using static functions from the Converter class, .mp3 and .wav Files can be loaded.
	 */
	public void initTrackList() {	
		  try {
			  File[] directoryListing = dir.listFiles();
			  if (directoryListing != null) {
				  Track t = null;
				  	for (File child : directoryListing) {
				  		if(child.getPath().endsWith("wav")) {
				  			t = Converter.getTrackFromWav(child);
				  		}
				  		if(child.getPath().endsWith("mp3")) {
				  			t = Converter.getTrackFromMP3(child);
				  		}
				  		if(t != null) {
					  		trackList.addElement(t);
				  		}
				  	}
			    }
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
	}
	
	/**
	 * All buttons are initialized.
	 * The command String and the ActionListener are set.
	 */
	private void initButtons() {	
		play = new JButton("Play/Pause");
		play.setActionCommand("play");
		play.addActionListener(this);
		skip = new JButton("Skip to");
		skip.setActionCommand("skip");
		skip.addActionListener(this);
		save = new JButton("Save");
		save.setActionCommand("save");
		save.addActionListener(this);
		remove = new JButton("Remove");
		remove.setActionCommand("remove");
		remove.addActionListener(this);
		cut = new JButton("Cut");
		cut.setActionCommand("cut");
		cut.addActionListener(this);
		fadeIn = new JButton("Fade-in");
		fadeIn.setActionCommand("fadeIn");
		fadeIn.addActionListener(this);
		fadeOut = new JButton("Fade-out");
		fadeOut.setActionCommand("fadeOut");
		fadeOut.addActionListener(this);
		addSil = new JButton("Add silent seconds");
		addSil.setActionCommand("addSil");
		addSil.addActionListener(this);
		volume = new JButton("Modify volume");
		volume.setActionCommand("volume");
		volume.addActionListener(this);
		autocut = new JButton("Autocut");
		autocut.setActionCommand("autocut");
		autocut.addActionListener(this);
		concat = new JButton("Concatenate");
		concat.setActionCommand("concat");
		concat.addActionListener(this);
		add = new JButton("Add waveform");
		add.setActionCommand("add");
		add.addActionListener(this);
		substract = new JButton("Subtract waveform");
		substract.setActionCommand("subtract");
		substract.addActionListener(this);
	}
	
	/**
	 * A JMenuBar is added to the user interface.
	 * The items "Exit" and "Load new Files" with their ActionListeners are added to the menubar.
	 */
	private void initMenubar() {
		JMenuBar menubar = new JMenuBar(); 
		setJMenuBar(menubar);
		JMenu fileMenu = new JMenu("File"); 
		menubar.add(fileMenu); 
		JMenuItem exitMI = new JMenuItem("Exit");
		fileMenu.add(exitMI); 
		exitMI.addActionListener(a -> {
			dispose();
		});
		JMenuItem load = new JMenuItem("Load new Files");
		fileMenu.add(load); 
		load.addActionListener(a -> {
			 JFileChooser chooser = new JFileChooser();
			 chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		        chooser.setCurrentDirectory(dir);
		        int rValue = chooser.showOpenDialog(null);
		        if(rValue == JFileChooser.APPROVE_OPTION) {
			        dir = chooser.getSelectedFile();
			        initTrackList();
		        }
		});
		JMenuItem clear = new JMenuItem("Clear list");
		fileMenu.add(clear); 
		clear.addActionListener(a -> {
			trackList.clear();
		});
	}
	
	/**
	 * Defines the behaviour of the program when buttons are clicked.
	 * Depending on the buttons pressed, the ActionEvent contains a certain command String.
	 * Statements in this function use static functions from the Converter, if Tracks should be loaded or saved to a file.
	 * Static functions from the Modifier are used, if Tracks should be modified.
	 * The Player class is notified if tracks should be played, stopped etc.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if(command.equals("play")) { //play track using player
			if(player.isPlaying()) {
				player.stop();
			}
			else {
				player.play();
			}
			return;
		}
		if(command.equals("skip")) {	//Set new startpoint
			player.setPosition(Float.parseFloat(popUpWindow("Enter start point in seconds.")));
			player.stop();
			return;
		}
		if(command.equals("save")) {	//Save track in a file
			Converter.save(player.getTrack(), popUpWindow("Enter the file name (without .mp3)"), dir);
			return;
		}
		if(command.equals("remove")) {	//Remove track from list
			player.setTrack(null);
			int index = jTrackList.getSelectedIndex();
			if(index == 0) {
				jTrackList.setSelectedIndex(1);
			}
			else {
				jTrackList.setSelectedIndex(index - 1);
			}
			trackList.removeElementAt(index);
			return;
		}
		if(command.equals("cut")) {	//Call cut function
			player.setTrack(Modifier.cut(player.getTrack(), 
									Float.parseFloat(popUpWindow("Enter start in seconds")), 
									Float.parseFloat(popUpWindow("Enter end in senconds"))
									));
			jTrackList.setSelectedIndex(trackList.indexOf(player.getTrack()));
			trackList.addElement(player.getTrack());
			return;
		}
		if(command.equals("fadeIn")) {	//Call fadeIn function
			player.setTrack(Modifier.fadeIn(player.getTrack(), 
										Float.parseFloat(popUpWindow("Enter end in seconds"))
										));
			jTrackList.setSelectedIndex(trackList.indexOf(player.getTrack()));
			trackList.addElement(player.getTrack());
			return;
		}
		if(command.equals("fadeOut")) {	//Call fadeOut function
			player.setTrack(Modifier.fadeOut(player.getTrack(), 
										Float.parseFloat(popUpWindow("Enter start in seconds"))
										));
			jTrackList.setSelectedIndex(trackList.indexOf(player.getTrack()));
			trackList.addElement(player.getTrack());
			return;
		}
		if(command.equals("addSil")) {	//Call amplify function
			player.setTrack(Modifier.addSil(player.getTrack(),
										Float.parseFloat(popUpWindow("Enter the position in seconds")),
										Float.parseFloat(popUpWindow("Enter seconds"))
										));
			jTrackList.setSelectedIndex(trackList.indexOf(player.getTrack()));
			trackList.addElement(player.getTrack());
			return;
		}
		if(command.equals("volume")) {	//Call volume function
			player.setTrack(Modifier.volume(player.getTrack(), 
										Integer.parseInt(popUpWindow("Enter percentage"))
										));
			jTrackList.setSelectedIndex(trackList.indexOf(player.getTrack()));
			trackList.addElement(player.getTrack());
			return;
		}
		if(command.equals("autocut")) {	//Call autocut function
			int perc = Integer.parseInt(popUpWindow("Enter threshold in percent"));
			if(perc > 100 || perc < 0) {
				return;
			}
			player.setTrack(Modifier.autoCut(player.getTrack(),
                                        perc,
                                        Float.parseFloat(popUpWindow("Enter the min duration for autocutting"))
                                        ));
			jTrackList.setSelectedIndex(trackList.indexOf(player.getTrack()));
			trackList.addElement(player.getTrack());
			return;
		}
		
		if(command.equals("concat")) {	//Call concat function
			List<Track> list = getTrackList(popUpWindow("Enter indices seperated by a ',' of the files you want to concatenate."));
			if(!list.isEmpty()) {
				trackList.addElement(Modifier.concat(list));
			}
			return;
		}
		if(command.equals("add")) {	//Call add function
			List<Track> list = getTrackList(popUpWindow("Enter indices seperated by a ',' of the files you want to add."));
			if(!list.isEmpty()) {
				trackList.addElement(Modifier.add(list));
			}
			return;
		}
		if(command.equals("subtract")) {	//Call substract function
			List<Track> list = getTrackList(popUpWindow("Enter index of a file you want to substract from the selected one."));
			if(!list.isEmpty()) {
				trackList.addElement(Modifier.subtract(list));
			}
			return;
		}
	}
	
	/**
	 * Assistive function simply showing a dialog window and returning the String entered by the user.
	 * @param msg Message string shown in the dialog window
	 * @return User input String
	 */
	private String popUpWindow(String msg) {	//input dialog with message
		return JOptionPane.showInputDialog(this, msg);
	}

	/**
	 * Adds Tracks with the indices from the string to a selection list.
	 * @param sel User input String in csv style
	 * @return List of Tracks selected tracks in special order defined by the input String
	 */
	private List<Track> getTrackList(String sel) {
		List<Integer> selList = new ArrayList<Integer>();
		List<Track> selTracks = new ArrayList<Track>();
	
		for(String s : sel.split(",")) {
			selList.add(Integer.parseInt(s));
		}
		
		for(int i = 0; i < selList.size(); i++) {
			for(int j = 0; j < trackList.size(); j++) {
				if(trackList.get(j).getId() == selList.get(i)) {
					selTracks.add(trackList.get(j));
				}
			}
		}
		return selTracks;
	}
}
