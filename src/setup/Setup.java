package setup;

import java.io.File; 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import coreLogic.AppEnvironment;
import coreObjects.User;
import gui.MainScreen;

/** Class to start the Quiz Application
 * <p>
 * Coordinates the starting of the application, checks if there is an existing
 * Serialized file for info from a previous session. Other wise creates a new
 * application
 * 
 * @author Hugo Phibbs
 * @version 30/6/21
 * @since 25/6/21
 *
 */
public class Setup {
	
	/** String for the absolute working directory for this application, where session files are stored for each user */
	private String workingDirectory;
	/** AppEnvironment object for this application */
	private AppEnvironment appEnvironment;
	
	/** Constructor for Setup class
	 * <p>
	 * Sets the working directory for this application, i.e. where to load and write files for serialization.
	 * 
	 * @param sessionsDirectory
	 */
	public Setup(String workingDirectory){
		this.workingDirectory = workingDirectory;
	}	

	// ************************ Loading a Session ***************************** // 
	
	/** Loads a session saved for a User with name userName
	 * <p>
	 * Rethrows FileNotFoundException, this can then be handled by the GUI
	 * 
	 * @param userName String for a User's name, that a user wants to load a session for
	 */
	public void loadSession(String userName) throws FileNotFoundException {
		String sessionFilePath = sessionFilePath(userName);
		try {
			loadSessionHelper(sessionFilePath);
		}
		catch (FileNotFoundException fnfe) {
			throw new FileNotFoundException(String.format("Session for %s doesn't exist!", userName));
		} 
		catch (IOException ioe) {
	        ioe.printStackTrace();
	        return;
		} 
		catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
	        return;
	    }
	}
	
	/** Helper Method for loadSession(String)
	 * <p>
	 * Does the actual loading of a file part 
	 *  
	 * @param sessionFilePath String for the file path of a session
	 * @throws FileNotFoundException If a session wasn't found
	 * @throws IOException If an I/O exception occurs
	 * @throws ClassNotFoundException If a class isn't found
	 */
	private void loadSessionHelper(String sessionFilePath) throws FileNotFoundException, IOException, ClassNotFoundException I{
		FileInputStream fileIn = new FileInputStream(sessionFilePath);
		ObjectInputStream objIn = new ObjectInputStream(fileIn);
		appEnvironment = (AppEnvironment) objIn.readObject();
		objIn.close();
		fileIn.close();
	}

	// *********************** Deleting a Session **************************** // 
	
	/** Deletes a session saved for a User with name userName
	 * 
	 * @param userName String for a User's name, that a user wants to delete a session for
	 */
	public void deleteSession(String userName) {
		String sessionFilePath = sessionFilePath(userName);
		File file = new File(sessionFilePath);
		file.delete();
	}

	// *********************** Creating a Session *************************** // 
	
	/** Creates a session for a user
	 * 
	 * @param userName String for the name of a User object that a user wants to create a session for
	 */
	public void createSession(String userName) {
		String sessionFilePath = sessionFilePath(userName);
		if (!canLoadSession(sessionFilePath)) {
			try {
				createSessionHelper(sessionFilePath, userName);
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		else {
			throw new IllegalArgumentException("Session already exists under this name!");
		}
	}
	
	/** Helper method for createSession(String)
	 * <p>
	 * Does the actual writing to a new file part
	 *
	 * @param sessionFilePath String for the file path of a session to be created
	 * @param userName String for the name of a user to create a Session for
	 */
	private void createSessionHelper(String sessionFilePath, String userName) throws IOException{
		FileOutputStream fileOut = new FileOutputStream(sessionFilePath);
		ObjectOutputStream objOut= new ObjectOutputStream(fileOut);
		
		appEnvironment = new AppEnvironment(new User(userName));
		
		objOut.writeObject(appEnvironment);
		objOut.close();
		fileOut.close();
		
	}
	
	/** Returns the file directory that a serialized session
	 *  would be saved under using userName
	 * 
	 * @param userName String for a user's name
	 * @return String for the name of a file 
	 */
	private String sessionFilePath(String userName) {
		return String.format("%s\\%sQuizSession.ser", workingDirectory, userName);
	}
	
	/** Finds out if a session can be loaded that was saved for a userName as per file defined by
	 * sessionFilePath(String)
	 * <p>
	 * Attempts to load a file, if a FileNotFoundException is caught, the returns false
	 * 
	 * @param userName String for the name a User's session to see if it exists
	 */
	@SuppressWarnings("resource")
	private static boolean canLoadSession(String sessionFilePath) {
		try {
			new FileInputStream(sessionFilePath);
			return true; // If reaches here, exception wasn't thrown
		} 
		catch (FileNotFoundException fnfe) {
			return false;
		}
	}
	
	/** Handles the finishing of the setup of the application
	 * <p>
	 * Creates a new MainScreen
	 * 	 
	 * @param appEnvironment AppEnvironment object for the application
	 */
	public void onSetupFinished() {
		MainScreen mainScreen = new MainScreen(appEnvironment);
		//mainScreen.show(); // TODO remove later once Screen fully implemented
	}
}
