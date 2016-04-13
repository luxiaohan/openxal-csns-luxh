package xal.app.mtvtest; 

import javax.swing.*;
import xal.extension.application.*;
import xal.extension.application.smf.AcceleratorApplication;

import java.util.logging.*;

/**
 * Application adaptor for the PV Histogram application.
 * @author  t6p
 */
public class Main extends ApplicationAdaptor {    
    /**
     * Returns the text file suffixes of files this application can open.
     * @return Suffixes of readable files
     */
    public String[] readableDocumentTypes() {
        return writableDocumentTypes();
    }
    
    
    /**
     * Returns the text file suffixes of files this application can write.
     * @return Suffixes of writable files
     */
    public String[] writableDocumentTypes() {
        return new String[] {"mt"};
    }
    
    
    /**
     * Implement this method to return an instance of my custom document.
     * @return An instance of my custom document.
     */
    public XalDocument newEmptyDocument() {
        return new MyDocument();
    }
    
    
    /**
     * Implement this method to return an instance of my custom document 
     * corresponding to the specified URL.
     * @param url The URL of the file to open.
     * @return An instance of my custom document.
     */
    public XalDocument newDocument(java.net.URL url) {
        return new MyDocument( url );
    }
    
    
    /**
     * Specifies the name of my application.
     * @return Name of my application.
     */
    public String applicationName() {
        return "Magnet Tuner";
    }
    
    
    /** Capture the application launched event and print it */
    public void applicationFinishedLaunching() {
        System.out.println("Application finished launching...");
		Logger.getLogger("global").log( Level.INFO, "Application finished launching." );
    }
    
    
    /** The main method of the application. */
    static public void main(String[] args) {
        try {
            System.out.println("Launching application...");
			Logger.getLogger("global").log( Level.INFO, "Launching the application..." );
            AcceleratorApplication.launch( new Main() );
        }
        catch(Exception exception) {
            System.err.println( exception.getMessage() );
			Logger.getLogger("global").log( Level.SEVERE, "Error launching the application." , exception );
            exception.printStackTrace();
            JOptionPane.showMessageDialog(null, exception.getMessage(), exception.getClass().getName(), JOptionPane.WARNING_MESSAGE);
        }
    }
}
