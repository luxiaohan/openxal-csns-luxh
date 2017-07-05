/*
 *  InjMatchDocument.java
 *
 *  xal.app.injpointmtch
 *
 *  Created on Mar 28, 2017  11:11:50 AM
 *
 *  Created by luxiaohan (luxh@ihep.ac.cn)
 *
 *  Copyright (c) 2017 China Spallation Neutron Source(CSNS). All Rights Reserved
 * 
 *  DongGuan,GuangDong 523803
 *
 */
package xal.app.injpointmtch;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;

import xal.extension.application.Commander;
import xal.extension.application.XalDocument;
import xal.extension.application.XalWindow;
import xal.extension.bricks.WindowReference;
import xal.tools.data.DataAdaptor;
import xal.tools.data.DataListener;
import xal.tools.xml.XmlDataAdaptor;

/**
 * The Document handle the sources to read and writ
 * @author luxiaohan
 * @version Mar 28, 2017,v 1.0
 * @sine JDK 1.8
 */
public class InjMatchDocument extends XalDocument implements DataListener {

	/** main window reference */
	final private WindowReference WINDOW_REFERENCE;

	/** the injection match model */
	final private InjMatchModel MODEL;

	/** the Controller */
	final private InjMatchController INJCONTROLLER;

	/** Empty Constructor */
	public InjMatchDocument() {
		this(null);
	}

	/**
	 * Creates a new instance of InjMatchDocument.
	 * 
	 * @param url
	 *            The URL of the file to load into the new document
	 */
	public InjMatchDocument(final java.net.URL url) {
		setSource(url);
		WINDOW_REFERENCE = getDefaultWindowReference("MainWindow", this);

		MODEL = new InjMatchModel();

		INJCONTROLLER = new InjMatchController(this, WINDOW_REFERENCE);

		if (url != null) {
			System.out.println("Opening document: " + url.toString());
			final DataAdaptor documentAdaptor = XmlDataAdaptor.adaptorForUrl(url, false);
			update(documentAdaptor.childAdaptor(dataLabel()));
		}

		setHasChanges(false);

	}

	/** get the model */
	public InjMatchModel getModel() {
		return MODEL;
	}
	
	
	/**
	 * Register custom actions for the commands of this application
	 * @param commander  The commander with which to register the custom commands.
	 */
	public void customizeCommands( Commander commander ) {
		//load default accelerator
		final AbstractAction loadDefault = new AbstractAction( "load-default" ) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setHasChanges( true );
System.out.println("I'm here");				
			}
		};
		
		commander.registerAction( loadDefault );
		
		//load default accelerator
		final AbstractAction loadSelected = new AbstractAction( "load-selected" ) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setHasChanges( true );
System.out.println("I'm ok");				
			}
		};
		
		commander.registerAction( loadSelected );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xal.tools.data.DataListener#dataLabel()
	 */
	@Override
	public String dataLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xal.tools.data.DataListener#update(xal.tools.data.DataAdaptor)
	 */
	@Override
	public void update(DataAdaptor adaptor) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xal.tools.data.DataListener#write(xal.tools.data.DataAdaptor)
	 */
	@Override
	public void write(DataAdaptor adaptor) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xal.extension.application.XalAbstractDocument#makeMainWindow()
	 */
	@Override
	public void makeMainWindow() {
		mainWindow = (XalWindow) WINDOW_REFERENCE.getWindow();
		setHasChanges(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * xal.extension.application.XalAbstractDocument#saveDocumentAs(java.net.
	 * URL)
	 */
	@Override
	public void saveDocumentAs(URL url) {
		// TODO Auto-generated method stub

	}

}
