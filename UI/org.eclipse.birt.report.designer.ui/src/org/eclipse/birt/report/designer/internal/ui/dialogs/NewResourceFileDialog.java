/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.designer.internal.ui.dialogs;

import java.io.ByteArrayInputStream;

import org.eclipse.birt.report.designer.internal.ui.util.ExceptionHandler;
import org.eclipse.birt.report.designer.nls.Messages;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 *  
 */

public class NewResourceFileDialog extends ElementTreeSelectionDialog
{

	private Text text;

	private IContainer container = null;

	private ISelectionStatusValidator fValidator = null;

	private String fileName;

	/**
	 * The constructor.
	 * 
	 * @param parent
	 * @param labelProvider
	 * @param contentProvider
	 */
	public NewResourceFileDialog( Shell parent, ILabelProvider labelProvider,
			ITreeContentProvider contentProvider )
	{
		super( parent, labelProvider, contentProvider );

		setDoubleClickSelects( false );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea( Composite parent )
	{
		Composite rt = (Composite) super.createDialogArea( parent );

		Composite pane = new Composite( rt, 0 );
		pane.setLayout( new GridLayout( 2, false ) );
		pane.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

		Label lb = new Label( pane, 0 );
		lb.setText( Messages.getString( "NewResourceFileDialog.label.NewFile" ) );//$NON-NLS-1$

		text = new Text( pane, SWT.BORDER );
		text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		text.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				fileName = text.getText( );
				updateOKStatus( );
			}
		} );

		getTreeViewer( ).addSelectionChangedListener( new ISelectionChangedListener( ) {

			public void selectionChanged( SelectionChangedEvent event )
			{
				ISelection sel = event.getSelection( );

				if ( sel instanceof IStructuredSelection )
				{
					IStructuredSelection stsel = (IStructuredSelection) sel;

					Object obj = stsel.getFirstElement( );
					if ( obj instanceof IResource )
						text.setText( ( (IResource) obj ).getName( ) );
				}

			}
		} );

		return rt;
	}

	/**
	 * Returns the file name.
	 * 
	 * @return
	 */
	public String getFileName( )
	{
		return fileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#setValidator(org.eclipse.ui.dialogs.ISelectionStatusValidator)
	 */
	public void setValidator( ISelectionStatusValidator validator )
	{
		fValidator = validator;
	}

	private Object[] getExistItems( )
	{
		TreeItem[] items = getTreeViewer( ).getTree( ).getItems( );

		Object[] objs = new Object[items.length];
		for ( int i = 0; i < items.length; i++ )
		{
			objs[i] = items[i].getData( );
		}

		return objs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#updateOKStatus()
	 */
	protected void updateOKStatus( )
	{
		IStatus fCurrStatus;

		if ( fValidator != null )
		{
			fCurrStatus = fValidator.validate( getExistItems( ) );
		}
		else
		{
			fCurrStatus = new Status( IStatus.OK,
					PlatformUI.PLUGIN_ID,
					IStatus.OK,
					"", //$NON-NLS-1$
					null );
		}

		updateStatus( fCurrStatus );
	}

	public void setContainer( IContainer parentContainer )
	{
		this.container = parentContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open( )
	{
		int rt = super.open( );

		if ( rt == Window.OK && container != null )
		{
			IFile file = container.getFile( new Path( fileName ) );

			try
			{
				file.create( new ByteArrayInputStream( new byte[0] ),
						true,
						null );
			}
			catch ( CoreException e )
			{
				ExceptionHandler.handle( e );
			}
		}

		return rt;
	}
}