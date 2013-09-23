/**
 * 
 */
package ui.display.views.table.structure;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;

import domain.mutation.IMutationOperators;

import ui.constants.TableViewers;

/**
 * @author sheilla
 * 
 */
public class OpMutationApplicableView extends AbstractTableViewer implements
		Observer {
	private Composite parent;
	private IWorkbenchPartSite site;
	private TableViewer opMutationApplTableViewer;

	public OpMutationApplicableView(Composite parent, IWorkbenchPartSite site) {
		this.parent = parent;
		this.site = site;
		Activator.getDefault().addObserverGroundStringController(this);
	}

	public TableViewer create() {
		opMutationApplTableViewer = createViewTable(parent, site,
				TableViewers.OPMUTATIONAPPLTABLE);
		createColumnsOpMutationApplicableView();
		opMutationApplTableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						Activator.getDefault().verifyChangesOperators();
						IStructuredSelection selection = (IStructuredSelection) opMutationApplTableViewer
								.getSelection();
						IMutationOperators operator = (IMutationOperators) selection
								.getFirstElement();
						Activator.getDefault()
								.setSelectedIMutOperator(operator);
					}
				});

		return opMutationApplTableViewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (opMutationApplTableViewer.getElementAt(0) != null) {
			Object allElements = opMutationApplTableViewer.getInput();
			opMutationApplTableViewer.remove(allElements);

		}
		if (Activator.getDefault().getSelectedGroundString() != null) {

			List<IMutationOperators> list = Activator.getDefault()
					.getOperatorsApplicable();
			opMutationApplTableViewer.setInput(list);
			editTableStyle(opMutationApplTableViewer);
		}

	}

	public void createColumnsOpMutationApplicableView() {
		TableViewerColumn col = createColumnsHeaders(opMutationApplTableViewer,
				TableViewers.COLUMN_OP_MUTATION_APPL_TABLE,
				TableViewers.COLUMN_WIDTH);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				cell.setText(cell.getElement().toString());
			}
		});
	}

	public void dispose() {
		Activator.getDefault().deleteObserverGroundStringController(this);
	}

}
