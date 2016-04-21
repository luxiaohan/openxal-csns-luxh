/*
 * MachineSimulatorDocument.java
 *
 * Created by Tom Pelaia on 9/19/11
 */

package xal.app.machinesimulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton.ToggleButtonModel;

import xal.extension.application.*;
import xal.extension.application.smf.*;
import xal.smf.*;
import xal.tools.xml.XmlDataAdaptor;
import xal.tools.data.*;
import xal.extension.bricks.WindowReference;
import xal.extension.widgets.apputils.SimpleProbeEditor;
import xal.model.probe.Probe;
import xal.service.pvlogger.apputils.browser.PVLogSnapshotChooser;
import xal.service.pvlogger.sim.PVLoggerDataSource;
import xal.sim.scenario.Scenario;


/**
 * MachineSimulatorDocument represents the document for the Machine Simulator application.
 * @author  t6p
 */
public class MachineSimulatorDocument extends AcceleratorDocument implements DataListener {
 	/** the data adaptor label used for reading and writing this document */
	static public final String DATA_LABEL = "MachineSimulatorDocument";
	/**the button to set using design mode*/
	final private ToggleButtonModel USE_DESIGN;
	/**the button to set using rf_design mode*/
	final private ToggleButtonModel USE_RF_DESIGN;
	/**the button to set using live mode*/
	final private ToggleButtonModel USE_CHANNEL;
	/**the button to set using pv logger*/
	final private ToggleButtonModel USE_PVLOGGER;
	/**the button to set using logged bends when using pv logger*/
	final private ToggleButtonModel USE_LOGGEDBEND;
	/**the button to set using read back value*/
	final private ToggleButtonModel USE_READ_BACK;
	/**the button to set using set value*/
	final private ToggleButtonModel USE_SET;
	/**the pvlogger chooser*/
	final private PVLogSnapshotChooser PV_LOG_CHOOSER;
	/**pvlogger chooser Dialog*/
	private JDialog pvLogSelector;
	/**the pvlogger data source*/
	private PVLoggerDataSource pvLoggerDataSource;
	/**pvlogger ID*/
	private long pvLoggerID;
	/** main window reference */
	final WindowReference WINDOW_REFERENCE;
    /** main model */
    final MachineModel MODEL;
    /** controller*/
    final MachineSimulatorController MACHINE_SIMULATOR_CONTROLLER;
   
    /** Empty Constructor */
    public MachineSimulatorDocument() {
        this( null );
    }
   
    /** 
     * Primary constructor 
     * @param url The URL of the file to load into the new document.
     */
    public MachineSimulatorDocument( final java.net.URL url ) {
    	setSource( url );
    	//initialize the buttons
		USE_DESIGN = new ToggleButtonModel();
		USE_RF_DESIGN = new ToggleButtonModel();
		USE_CHANNEL= new ToggleButtonModel();
		USE_PVLOGGER = new ToggleButtonModel();
		USE_LOGGEDBEND = new ToggleButtonModel();
		USE_READ_BACK= new ToggleButtonModel();
		USE_SET= new ToggleButtonModel();
		
		WINDOW_REFERENCE = getDefaultWindowReference( "MainWindow", this );
		
		PV_LOG_CHOOSER = new PVLogSnapshotChooser( mainWindow, true );
		
      // initialize the model here
      MODEL = new MachineModel();
		MACHINE_SIMULATOR_CONTROLLER = new MachineSimulatorController( this,WINDOW_REFERENCE );
		
		if ( url != null ) {
            System.out.println( "Opening document: " + url.toString() );
            final DataAdaptor documentAdaptor = XmlDataAdaptor.adaptorForUrl( url, false );
            update( documentAdaptor.childAdaptor( dataLabel() ) );
        }
		
		setHasChanges( false );
    }
    
    
    /** Make and configure the main window. */
    public void makeMainWindow() {
        mainWindow = (XalWindow)WINDOW_REFERENCE.getWindow();
		setHasChanges( false );
    }
    
    
	/** get the model */
	public MachineModel getModel() {
		return MODEL;
	}
	
	/**
	 * Register custom actions for the commands of this application
	 * @param commander  The commander with which to register the custom commands.
	 */
	public void customizeCommands( Commander commander ) {
		//initialize the pvLoggerID
		pvLoggerID = 0;
		
		//the action to configure the pvlogger data
	    final ActionListener CONFIG_PVLOGGER_DATA = new ActionListener() {
			public void actionPerformed( final ActionEvent event ) {
                if ( pvLoggerID != 0 ) {
    				MODEL.configPVLoggerData( pvLoggerDataSource, pvLoggerID, USE_PVLOGGER.isSelected() );
                }

			}
		};
		
		//run model action
		final AbstractAction runModelAction = new AbstractAction( "run-model" ) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				CONFIG_PVLOGGER_DATA.actionPerformed( null );
				MACHINE_SIMULATOR_CONTROLLER.runModel( mainWindow, null );
				setHasChanges( true );
			}
		};
		
		commander.registerAction( runModelAction );
		
		//probe editor
		final AbstractAction probeEditor = new AbstractAction( "probe-editor" ) {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				Probe<?> baseProbe = MODEL.getSimulator().getEntranceProbe();
				if ( baseProbe != null ) {
					final SimpleProbeEditor probeEditor = new SimpleProbeEditor( getMainWindow(), baseProbe );					
					baseProbe = probeEditor.getProbe();

					Probe<?> currentProbe = baseProbe.copy();
					currentProbe.initialize();
					MODEL.getSimulator().setEntranceProbe( currentProbe );
					
					setHasChanges( true );
                }
                else {
                    //Sequence has not been selected
                    displayError("Probe Editor Error", "You must select a sequence before attempting to edit the probe.");
                }
			}
		};
		
		commander.registerAction( probeEditor );
		

		
		//register select_pvlogger button
		final AbstractAction selectPVLogger = new AbstractAction( "select-pvlogger" ) {
			
			private static final long serialVersionUID = 1L;

			public void actionPerformed( final ActionEvent event ) {
				if ( pvLogSelector == null ) pvLogSelector = PV_LOG_CHOOSER.choosePVLogId();
				else pvLogSelector.setVisible( true );
				
				if ( pvLoggerID != PV_LOG_CHOOSER.getPVLogId() && PV_LOG_CHOOSER.getPVLogId() != 0 ) {
					pvLoggerID = PV_LOG_CHOOSER.getPVLogId();
					pvLoggerDataSource = new PVLoggerDataSource ( pvLoggerID );
					pvLoggerDataSource.setUsesLoggedBendFields( USE_LOGGEDBEND.isSelected() );
					CONFIG_PVLOGGER_DATA.actionPerformed( null );
					MODEL.modelScenarioChanged();

                    setHasChanges( true );
				}
				
				
			}
		 };
		commander.registerAction( selectPVLogger );
		
		
		// register use_design button
		USE_DESIGN.setSelected(true);
		USE_DESIGN.addActionListener( new ActionListener() {
			public void actionPerformed( final ActionEvent event ) {
				MODEL.setSynchronizationMode( Scenario.SYNC_MODE_DESIGN );
				
				setHasChanges( true );
			}
		});
		commander.registerModel( "use-design",USE_DESIGN );
		
		
		//register use_rf_design button 
		USE_RF_DESIGN.addActionListener( new ActionListener() {
			public void actionPerformed( final ActionEvent event ) {
				MODEL.setSynchronizationMode( Scenario.SYNC_MODE_RF_DESIGN );
				
				setHasChanges( true );
			}
		});
		commander.registerModel( "use-rf_design",USE_RF_DESIGN );
		
		//register use_channel button
		USE_CHANNEL.addActionListener( new ActionListener() {
			public void actionPerformed( final ActionEvent event ) {
				MODEL.setSynchronizationMode( Scenario.SYNC_MODE_LIVE );
				
				setHasChanges( true );
			}
		});
		commander.registerModel( "use-channel",USE_CHANNEL );
		
		//register use_pvlogger button
	    USE_PVLOGGER.setSelected( false );
	    USE_PVLOGGER.addActionListener( new ActionListener() {
	    	public void actionPerformed ( final ActionEvent event ) {
	    		if ( pvLoggerID != 0 ) {
	    			if ( USE_PVLOGGER.isSelected() ) USE_LOGGEDBEND.setEnabled( true );
		    		else USE_LOGGEDBEND.setEnabled( false );
		    		
		    		setHasChanges( true );
	    		}
	    		else {
	    			JOptionPane.showMessageDialog( mainWindow,
	    	         		"You need to select pvLoggerData first","Warning!",JOptionPane.PLAIN_MESSAGE);
	    			USE_PVLOGGER.setSelected( false );
	    		}
	    		
	    		

	    	}
	    });				
	    commander.registerModel( "use-pvlogger", USE_PVLOGGER );
	    
		//register use_loggedbend button
	    USE_LOGGEDBEND.setSelected( false );
	    USE_LOGGEDBEND.setEnabled( false );
	    USE_LOGGEDBEND.addActionListener( new ActionListener() {
	    	public void actionPerformed ( final ActionEvent event ) {
	    		if ( USE_LOGGEDBEND.isSelected() ) {
	    			if ( pvLoggerDataSource != null ) pvLoggerDataSource.setUsesLoggedBendFields( true );
	    		}
	    		else {
	    			if ( pvLoggerDataSource != null ) pvLoggerDataSource.setUsesLoggedBendFields( false );
	    		}
	    		
	    		setHasChanges( true );
	    	}
	    });				
	    commander.registerModel( "use-loggedbend", USE_LOGGEDBEND );
	    
		
		//register use_set button
		USE_SET.setSelected( true );
		USE_SET.addActionListener( new ActionListener() {
			public void actionPerformed( final ActionEvent event ) {
				MODEL.setUseFieldReadback( false );
				
				MODEL.modelScenarioChanged();
				
				setHasChanges( true );

			}
		});
		commander.registerModel( "fieldSet", USE_SET );
		

		//register use_read_back button
		USE_READ_BACK.addActionListener( new ActionListener() {
			public void actionPerformed( final ActionEvent event ) {
				MODEL.setUseFieldReadback( true );
				
				MODEL.modelScenarioChanged();
				
				setHasChanges( true );
				
			}
		});
		commander.registerModel( "fieldReadback",USE_READ_BACK );
		
		
		
	}

    
   /** Save the document to the specified URL.
     * @param url The URL to which the document should be saved.
     */
    public void saveDocumentAs( final URL url ) {
        writeDataTo( this, url );
    }
    
    
    /** Handle the accelerator changed event by displaying the elements of the accelerator in the main window. */
    public void acceleratorChanged() {
        try {        	
            MODEL.setSequence( null );
            setHasChanges( true );
        }
        catch ( Exception exception ) {
            exception.printStackTrace();
            displayError( "Error Setting Accelerator", "Simulator Configuration Exception", exception );
        }
	}
    
    
    /** Handle the selected sequence changed event by displaying the elements of the selected sequence in the main window. */
    public void selectedSequenceChanged() {
        try {        	
            MODEL.setSequence( getSelectedSequence() );
            if( USE_RF_DESIGN.isSelected() ) MODEL.setSynchronizationMode( Scenario.SYNC_MODE_RF_DESIGN );
            if( USE_CHANNEL.isSelected() ) MODEL.setSynchronizationMode( Scenario.SYNC_MODE_LIVE );
            if( USE_PVLOGGER.isSelected() && pvLoggerID != 0 ) MODEL.configPVLoggerData( pvLoggerDataSource, pvLoggerID, true );
            if( USE_READ_BACK.isSelected() ) MODEL.setUseFieldReadback( true );
            if( USE_SET.isSelected() ) MODEL.setUseFieldReadback( false );
            setHasChanges( true );

        }
        catch ( Exception exception ) {
            exception.printStackTrace();
            displayError( "Error Setting Sequence", "Simulator Configuration Exception", exception );
        }
	}
	
    
    /** provides the name used to identify the class in an external data source. */
    public String dataLabel() {
        return DATA_LABEL;
    }
    
    
    /** Instructs the receiver to update its data based on the given adaptor. */
    public void update( final DataAdaptor adaptor ) {
		if ( adaptor.hasAttribute( "acceleratorPath" ) ) {
			final String acceleratorPath = adaptor.stringValue( "acceleratorPath" );
			final Accelerator accelerator = applySelectedAcceleratorWithDefaultPath( acceleratorPath );
			
			if ( accelerator != null && adaptor.hasAttribute( "sequence" ) ) {
				final String sequenceID = adaptor.stringValue( "sequence" );
				setSelectedSequence( getAccelerator().findSequence( sequenceID ) );
			}
		}
		
        final DataAdaptor modelAdaptor = adaptor.childAdaptor( MachineModel.DATA_LABEL );
        if ( modelAdaptor != null )  MODEL.update( modelAdaptor );
        
        if ( MODEL.getSequence() != null ) {
            if ( MODEL.getSimulator().getUseFieldReadback() ) USE_READ_BACK.setSelected( true );      
            final String synchMode = MODEL.getSimulator().getScenario().getSynchronizationMode();
            if ( synchMode.equals( Scenario.SYNC_MODE_LIVE ) ) USE_CHANNEL.setSelected( true );
            else if ( synchMode.equals( Scenario.SYNC_MODE_RF_DESIGN ) ) USE_RF_DESIGN.setSelected( true );
            USE_PVLOGGER.setSelected( adaptor.hasAttribute( "usepvlogger" ) ? adaptor.booleanValue( "usepvlogger" ) : false );
            USE_LOGGEDBEND.setEnabled( adaptor.hasAttribute( "usepvlogger" ) ? adaptor.booleanValue( "usepvlogger" ) : false );
            USE_LOGGEDBEND.setSelected( adaptor.hasAttribute( "usebendloggedfields" ) ? adaptor.booleanValue( "usebendloggedfields" ) : false );
            
            pvLoggerID  = adaptor.hasAttribute( "pvLoggerID" ) ? adaptor.longValue( "pvLoggerID" ) : 0;
            if ( pvLoggerID != 0 ) {
				pvLoggerDataSource = new PVLoggerDataSource ( pvLoggerID );
				pvLoggerDataSource.setUsesLoggedBendFields( USE_LOGGEDBEND.isSelected() );
				MODEL.configPVLoggerData( pvLoggerDataSource, pvLoggerID, USE_PVLOGGER.isSelected() );
            }
            
            final DataAdaptor whAdaptor = modelAdaptor.childAdaptor( WhatIfConfiguration.DATA_LABEL );
            MODEL.restoreModelScenario( whAdaptor );
            }
        
        MACHINE_SIMULATOR_CONTROLLER.changeSimHistoryRecords( MODEL.getSimulationHistoryRecords() );

    }
    
    
    /** Instructs the receiver to write its data to the adaptor for external storage. */
    public void write( final DataAdaptor adaptor ) {   	
        adaptor.setValue( "version", "1.0.0" );
        adaptor.setValue( "date", new java.util.Date().toString() );
	     
        if( MODEL.getSequence() != null ) adaptor.writeNode( MODEL );
        
		if ( getAccelerator() != null ) {
			adaptor.setValue( "acceleratorPath", getAcceleratorFilePath() );
			
			final AcceleratorSeq sequence = getSelectedSequence();
			if ( sequence != null ) {
				adaptor.setValue( "sequence", sequence.getId() );
				adaptor.setValue( "usebendloggedfields", USE_LOGGEDBEND.isSelected() );
				adaptor.setValue( "usepvlogger", USE_PVLOGGER.isSelected() );
				adaptor.setValue( "pvLoggerID", pvLoggerID );
			}
		}
    }
}
