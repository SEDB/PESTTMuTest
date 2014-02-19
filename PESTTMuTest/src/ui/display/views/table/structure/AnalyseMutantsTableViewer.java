package ui.display.views.table.structure;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.annotation.PreDestroy;

import main.activator.Activator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;

import domain.mutation.Mutation;
import ui.constants.Images;
import ui.constants.TableViewers;

public class AnalyseMutantsTableViewer extends AbstractTableViewer implements
		Observer {
	private Composite parent;
	private IWorkbenchPartSite site;
	private TableViewer analyseMutantsTableViewer;

	public AnalyseMutantsTableViewer(Composite parent, IWorkbenchPartSite site) {
		super();
		this.parent = parent;
		this.site = site;
		Activator.getDefault().getMutationsController()
				.addObserverMutationTestResult(this);
	}

	public TableViewer create() {
		analyseMutantsTableViewer = createViewTable(parent, site,
				TableViewers.ANALYSEMUTANTSTABLE);
		createColumnsToAnalyseMutants();
		analyseMutantsTableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						Activator.getDefault().verifyChangesOperators();
						IStructuredSelection selection = (IStructuredSelection) analyseMutantsTableViewer
								.getSelection();
						Mutation mutation = Activator.getDefault()
								.getMutationsController().getSelectedMutation();
						if (selection.getFirstElement() != null
								&& (mutation == null || !mutation
										.equals((Mutation) selection
												.getFirstElement()))) {
							Activator
									.getDefault()
									.getMutationsController()
									.setSelectedMutation(
											(Mutation) selection
													.getFirstElement());
						}
					}
				});
		setSelections();
		return analyseMutantsTableViewer;
	}

	@Override
	public void update(Observable o, Object arg) {
		Set<Mutation> setMutation = Activator.getDefault()
				.getMutationsController().getMutantsTestResults();
		if (setMutation.size() == 0
				&& analyseMutantsTableViewer.getElementAt(0) != null) {
			analyseMutantsTableViewer.remove(analyseMutantsTableViewer
					.getInput());
		} else {
			analyseMutantsTableViewer.setInput(setMutation);
		}
	}

	private void createColumnsToAnalyseMutants() {
		String[] columnNames = new String[] { TableViewers.COLUMN_MUTANT,
				TableViewers.COLUMN_MUTANT_STATE,
				TableViewers.COLUMN_MUTATION_OP_APPL,
				TableViewers.COLUMN_GROUND_STRING,
				TableViewers.COLUMN_FULLY_QUALIFIED_NAME }; // the names of
		// columns.
		int[] columnWidths = new int[] { 200, 40, 200, 200, 200 }; // the width
																	// of
		// columns.

		// first column is for the mutant.
		TableViewerColumn col = createColumnsHeaders(analyseMutantsTableViewer,
				columnNames[0], columnWidths[0]);

		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(cell.getElement().toString());
				super.update(cell);
			}

		});

		// second column is for the mutant state.
		col = createColumnsHeaders(analyseMutantsTableViewer, columnNames[1],
				columnWidths[1]);

		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				Mutation mutation = (Mutation) cell.getElement();
				if (Activator.getDefault().getMutationsController()
						.isLiveMutant(mutation)) {
					cell.setImage(Images.getImage((Images.LIVEMUTANT)));
				} else {
					cell.setImage(Images.getImage((Images.KILLEDMUTANT)));
				}

				super.update(cell);
			}

		});

		// third column is for the mutation operator Applicable
		col = createColumnsHeaders(analyseMutantsTableViewer, columnNames[2],
				columnWidths[2]);

		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				Mutation mutation = (Mutation) cell.getElement();
				cell.setText(mutation.getMutationOperator().toString());
				super.update(cell);
			}

		});

		// fourth column is for the ground string.
		col = createColumnsHeaders(analyseMutantsTableViewer, columnNames[3],
				columnWidths[3]);

		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				Mutation mutation = (Mutation) cell.getElement();
				cell.setText(mutation.getASTNode().toString());
				super.update(cell);
			}

		});

		// fifth column is for the fully qualified name
		col = createColumnsHeaders(analyseMutantsTableViewer, columnNames[4],
				columnWidths[4]);

		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				Mutation mutation = (Mutation) cell.getElement();
				cell.setText(Activator.getDefault().getFullyQualifiedName(
						(mutation.getASTNode())));
				super.update(cell);
			}

		});
	}

	@PreDestroy
	public void dispose() {
		// image.dispose();
		// Activator.getDefault().deleteObserverMutationTestResult(this);
	}

	private void setSelections() {
		analyseMutantsTableViewer.getTable().addListener(SWT.Selection,
				new Listener() {

					public void handleEvent(Event event) {

						if (event.detail == SWT.CHECK) { // when user
															// enable/disable
															// mutant.
							for (TableItem item : analyseMutantsTableViewer
									.getTable().getItems()) {
								if (item == event.item) {
									if (item.getChecked()) {
										Activator.getDefault()
												.getMutationsController()
												.incrementEquivalentMutants();

									} else {
										Activator.getDefault()
												.getMutationsController()
												.decrementEquivalentMutants();
									}

								}
							}

						}
					}
				});
	}
}