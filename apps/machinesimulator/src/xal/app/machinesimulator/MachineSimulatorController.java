/**
 * 
 */
package xal.app.machinesimulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;

import xal.extension.bricks.WindowReference;
import xal.extension.widgets.plot.FunctionGraphsJPanel;
import xal.extension.widgets.swing.KeyValueFilteredTableModel;
import xal.tools.data.KeyValueAdaptor;

/**
 * @author luxiaohan
 * controller for binding the MachineSimulator model to the user interface 
 */
public class MachineSimulatorController {
	
	 /** main window reference */
	 final private WindowReference WINDOW_REFERENCE;
	
     /** simulated states table model */
    final KeyValueFilteredTableModel<MachineSimulationRecord> STATES_TABLE_MODEL;
     /** main model */
     final MachineModel MODEL;
     public List<MachineSimulationRecord> _allRecords;
      
     /**result of MachineSimulatoion */
     public MachineSimulation _MachineSimulation;
      /** the document for the Machine Simulator application*/
     final private MachineSimulatorDocument _Document;
     /** the plotter*/
     public MachineSimulatorPlotter _MachineSimulatorPlotter;
       
     final KeyValueAdaptor KEY_VALUE_ADAPTOR;
     
     final HashMap<String, List<Double>> PLOT_DATA;
       /** initialize the data list of parameters*/
     public List<Double> _position=new ArrayList<Double>();
     public List<Double> kineticenery=new ArrayList<Double>();     
     public List<Double> betax=new ArrayList<Double>();
     public List<Double> betay=new ArrayList<Double>();
     public List<Double> betaz=new ArrayList<Double>();
     public List<Double> alphax=new ArrayList<Double>();
     public List<Double> alphay=new ArrayList<Double>();
     public List<Double> alphaz=new ArrayList<Double>();
     public List<Double> gammax=new ArrayList<Double>();
     public List<Double> gammay=new ArrayList<Double>();
     public List<Double> gammaz=new ArrayList<Double>();
     public List<Double> emittancex=new ArrayList<Double>();
     public List<Double> emittancey=new ArrayList<Double>();
     public List<Double> emittancez=new ArrayList<Double>();
     public List<Double> beamsizex=new ArrayList<Double>();
     public List<Double> beamsizey=new ArrayList<Double>();
     public List<Double> beamsizez=new ArrayList<Double>();
     public List<Double> betatronphasex=new ArrayList<Double>();
     public List<Double> betatronphasey=new ArrayList<Double>();
     public List<Double> betatronphasez=new ArrayList<Double>();

        
	/**constructor */
	public  MachineSimulatorController(final MachineSimulatorDocument document,final WindowReference windowReference) {
		// TODO Auto-generated constructor stub
		_Document=document;
		WINDOW_REFERENCE=windowReference;
		STATES_TABLE_MODEL = new KeyValueFilteredTableModel<MachineSimulationRecord>();
		PLOT_DATA = new HashMap<String,List<Double>>();
		KEY_VALUE_ADAPTOR= new KeyValueAdaptor();
        // initialize the model here
        MODEL = _Document.getModel();

//        RECORDS=_MachineSimulation.getSimulationRecords();
         configureTableWindow(WINDOW_REFERENCE);
	}
	
	
    /** configure the Table window */
    private void configureTableWindow( final WindowReference windowReference ) {
        STATES_TABLE_MODEL.setColumnClassForKeyPaths( Double.class, "position", "probeState.kineticEnergy" );

        STATES_TABLE_MODEL.setColumnName( "elementID", "Element" );
        STATES_TABLE_MODEL.setColumnName( "probeState.kineticEnergy", "Kinetic Energy" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.0.beta", "<html>&beta;<sub>x</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.0.alpha", "<html>&alpha;<sub>x</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.0.gamma", "<html>&gamma;<sub>x</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.0.emittance", "<html>&epsilon;<sub>x</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.0.envelopeRadius", "<html>&sigma;<sub>x</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "betatronPhase.toArray.0", "<html>&phi;<sub>x</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.1.beta", "<html>&beta;<sub>y</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.1.alpha", "<html>&alpha;<sub>y</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.1.gamma", "<html>&gamma;<sub>y</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.1.emittance", "<html>&epsilon;<sub>y</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.1.envelopeRadius", "<html>&sigma;<sub>y</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "betatronPhase.toArray.1", "<html>&phi;<sub>y</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.2.beta", "<html>&beta;<sub>z</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.2.alpha", "<html>&alpha;<sub>z</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.2.gamma", "<html>&gamma;<sub>z</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.2.emittance", "<html>&epsilon;<sub>z</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "twissParameters.2.envelopeRadius", "<html>&sigma;<sub>z</sub></html>" );
        STATES_TABLE_MODEL.setColumnName( "betatronPhase.toArray.2", "<html>&phi;<sub>z</sub></html>" );
        
        final JTable statesTable = (JTable)windowReference.getView( "States Table" );
        statesTable.setModel( STATES_TABLE_MODEL );
        
        final JTextField statesTableFilterField = (JTextField)windowReference.getView( "States Table Filter Field" );
        STATES_TABLE_MODEL.setInputFilterComponent( statesTableFilterField );
        STATES_TABLE_MODEL.setMatchingKeyPaths( "elementID" );
        
        final FunctionGraphsJPanel twissParametersplot = (FunctionGraphsJPanel) windowReference.getView("States Plot");
        _MachineSimulatorPlotter=new MachineSimulatorPlotter(twissParametersplot);

        // handle the parameter selections of Table view
        final JCheckBox kineticEnergyCheckbox = (JCheckBox)windowReference.getView( "Kinetic Energy Checkbox" );
        
        final JCheckBox xSelectionCheckbox = (JCheckBox)windowReference.getView( "X Selection Checkbox" );
        final JCheckBox ySelectionCheckbox = (JCheckBox)windowReference.getView( "Y Selection Checkbox" );
        final JCheckBox zSelectionCheckbox = (JCheckBox)windowReference.getView( "Z Selection Checkbox" );
        
        final JCheckBox betaCheckbox = (JCheckBox)windowReference.getView( "Beta Checkbox" );
        final JCheckBox alphaCheckbox = (JCheckBox)windowReference.getView( "Alpha Checkbox" );
        final JCheckBox gammaCheckbox = (JCheckBox)windowReference.getView( "Gamma Checkbox" );
        final JCheckBox emittanceCheckbox = (JCheckBox)windowReference.getView( "Emittance Checkbox" );
        final JCheckBox beamSizeCheckbox = (JCheckBox)windowReference.getView( "Beam Size Checkbox" );
        final JCheckBox betatronPhaseCheckbox = (JCheckBox)windowReference.getView( "Betatron Phase Checkbox" );
                
        final ActionListener PARAMETER_HANDLER = new ActionListener() {
            public void actionPerformed( final ActionEvent event ) {                
                // array of standard parameters to display
                final String[] standardParameterKeys = new String[] { "elementID", "position" };
                // array of optional scalar parameters to display
                final List<String> scalarParameterNames = new ArrayList<String>();
                if ( kineticEnergyCheckbox.isSelected() )  scalarParameterNames.add( "probeState.kineticEnergy" );
                final String[] scalarParameterKeys = new String[ scalarParameterNames.size() ];
                int scalarParameterIndex = 0;
                for ( final String scalarParameterName : scalarParameterNames ) {
                    scalarParameterKeys[ scalarParameterIndex++ ] = scalarParameterName;
                }
                STATES_TABLE_MODEL.setColumnClassForKeyPaths( Double.class, scalarParameterKeys );
                
                // Add each selected plan to the list of planes to display and associate each plane with its corresponding twiss array index
                final List<String> planes = new ArrayList<String>(3);
                if ( xSelectionCheckbox.isSelected() )  planes.add( "0" );
                if ( ySelectionCheckbox.isSelected() )  planes.add( "1" );
                if ( zSelectionCheckbox.isSelected() )  planes.add( "2" );
                
                // Add each selected twiss parameter name to the list of parameters to display
                final List<String> twissParameterNames = new ArrayList<String>();
                if ( betaCheckbox.isSelected() )  twissParameterNames.add( "beta" );
                if ( alphaCheckbox.isSelected() )  twissParameterNames.add( "alpha" );
                if ( gammaCheckbox.isSelected() )  twissParameterNames.add( "gamma" );
                if ( emittanceCheckbox.isSelected() )  twissParameterNames.add( "emittance" );
                if ( beamSizeCheckbox.isSelected() )  twissParameterNames.add( "envelopeRadius" );
                
                int vectorParameterBaseCount = twissParameterNames.size();
                if ( betatronPhaseCheckbox.isSelected() )  vectorParameterBaseCount++;
  //                   System.out.println(vectorParameterBaseCount);
                
                // construct the full vector parameter keys from each pair of selected planes and vector parameter names
                final String[] vectorParameterKeys = new String[ planes.size() * vectorParameterBaseCount ];
                int vectorParameterIndex = 0;
                for ( final String plane : planes ) {
                    for ( final String twissParameter : twissParameterNames ) {
//                        vectorParameterKeys[ vectorParameterIndex++ ] = "twiss." + plane + "." + twissParameter;
                        vectorParameterKeys[ vectorParameterIndex++ ] = "twissParameters." + plane + "." + twissParameter;
                    }
                    
                    if ( betatronPhaseCheckbox.isSelected() ) {
                        vectorParameterKeys[ vectorParameterIndex++ ] = "betatronPhase.toArray." + plane;
                    }
                }
                STATES_TABLE_MODEL.setColumnClassForKeyPaths( Double.class, vectorParameterKeys );
                
                final String[] parameterKeys = new String[standardParameterKeys.length + scalarParameterKeys.length + vectorParameterKeys.length];
                // add standard parameters at the start
                System.arraycopy( standardParameterKeys, 0, parameterKeys, 0, standardParameterKeys.length );
                // append optional scalar parameters after standard parameters
                System.arraycopy( scalarParameterKeys, 0, parameterKeys, standardParameterKeys.length, scalarParameterKeys.length );
                // append vector parameters after scalar parameters
                System.arraycopy( vectorParameterKeys, 0, parameterKeys, scalarParameterKeys.length + standardParameterKeys.length, vectorParameterKeys.length );
                STATES_TABLE_MODEL.setKeyPaths( parameterKeys );
                
                final String[] parameterKeysForPlot=new String[parameterKeys.length-1];
                System.arraycopy(parameterKeys, 1, parameterKeysForPlot, 0, parameterKeys.length-1);
                getParametersData(_allRecords, parameterKeysForPlot);
            }
        };
        
        kineticEnergyCheckbox.addActionListener( PARAMETER_HANDLER );
        
        xSelectionCheckbox.addActionListener( PARAMETER_HANDLER );
        ySelectionCheckbox.addActionListener( PARAMETER_HANDLER );
        zSelectionCheckbox.addActionListener( PARAMETER_HANDLER );
        
        betaCheckbox.addActionListener( PARAMETER_HANDLER );
        alphaCheckbox.addActionListener( PARAMETER_HANDLER );
        gammaCheckbox.addActionListener( PARAMETER_HANDLER );
        emittanceCheckbox.addActionListener( PARAMETER_HANDLER );
        beamSizeCheckbox.addActionListener( PARAMETER_HANDLER );
        
        betatronPhaseCheckbox.addActionListener( PARAMETER_HANDLER );
        
        // perform the initial parameter display configuration
       // PARAMETER_HANDLER.actionPerformed( null );
        
        
        // configure the run button
        final JButton runButton = (JButton)windowReference.getView( "Run Button" );
        runButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent event ) {
                System.out.println( "running the model..." );
                final MachineSimulation simulation = MODEL.runSimulation();
                _allRecords=simulation.getSimulationRecords();
                STATES_TABLE_MODEL.setRecords( simulation.getSimulationRecords() );
            }
        });

		
		final JCheckBox phaseSlipCheckbox = (JCheckBox)windowReference.getView( "Phase Slip Checkbox" );
		phaseSlipCheckbox.setSelected( MODEL.getSimulator().getUseRFGapPhaseSlipCalculation() );
		phaseSlipCheckbox.addActionListener( new ActionListener() {
			public void actionPerformed( final ActionEvent event ) {
				MODEL.getSimulator().setUseRFGapPhaseSlipCalculation( phaseSlipCheckbox.isSelected() );
			}
		});
    }
    
    /** get the parameter data from Machinesimulation*/  
    public void getParametersData(List<MachineSimulationRecord> records,String[] keyPaths){ 
    	PLOT_DATA.clear();
    	for(final String keyPath:keyPaths){
    		PLOT_DATA.put(keyPath, new ArrayList<Double>(records.size()));
    		for(final MachineSimulationRecord record:records){
    			PLOT_DATA.get(keyPath).add((Double)KEY_VALUE_ADAPTOR.valueForKeyPath(record,keyPath));    			
    		}
    	}
    	
    	
    }
    
    

}


 
