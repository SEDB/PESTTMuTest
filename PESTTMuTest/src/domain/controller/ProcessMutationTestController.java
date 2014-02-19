package domain.controller;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import domain.constants.Messages;
import ui.dialog.ProcessMessage;
import domain.events.RunAllMutationsEvent;
import domain.events.RunRandomMutationsEvent;
import domain.events.StartProcessMutationTestEvent;
import domain.mutation.Mutation;
import domain.util.InfoProjectHelper;

public class ProcessMutationTestController {
	private MutationOperatorsController operatorsController;
	private MutationsController mutationsController;
	private GroundStringController groundStringController;
	private ProjectController projectController;
	private ControllerRunningTest controllerRunningTest;

	public ProcessMutationTestController() {
		operatorsController = new MutationOperatorsController();
		groundStringController = new GroundStringController(
				operatorsController.getManagerMutationOperators());
		projectController = new ProjectController(groundStringController);
		mutationsController = new MutationsController();
		controllerRunningTest = new ControllerRunningTest();
	}

	public void startProcessMutationTest(Object[] elements) {
		new StartProcessMutationTestEvent().execute(elements,
				operatorsController, groundStringController, projectController,
				mutationsController);
	}

	public void runRandomMutations() {
		new RunRandomMutationsEvent().execute(
				projectController.getProjectNameSelected(),
				mutationsController, projectController, groundStringController,
				controllerRunningTest);
	}

	public void runAllMutations() {
		new RunAllMutationsEvent().execute(
				projectController.getProjectNameSelected(),
				mutationsController, projectController, groundStringController,
				controllerRunningTest);
	}

	public void analyseProject() {
		groundStringController.initializeListGroundString();
		projectController.analyseProject(projectController
				.getProjectNameSelected());
	}

	public void verifyChangesOperators(Object[] elements) {
		boolean result = operatorsController.verifyChangesOperators(elements);
		if (!result) {
			ProcessMessage.INSTANCE.showInformationMessage("Info",
					Messages.NOT_SELECT_ELEMENTS_TREE);
		}
	}

	public List<Mutation> getMutantsToDisplay() {
		ASTNode groundString = groundStringController.getSelectedGroundString();
		List<Mutation> mutations = operatorsController
				.getMutations(groundString);
		return mutationsController.getMutantsToDisplay(groundString, mutations);
	}

	/**
	 * @return the operatorsController
	 */
	public MutationOperatorsController getOperatorsController() {
		return operatorsController;
	}

	/**
	 * @return the mutationsController
	 */
	public MutationsController getMutationsController() {
		return mutationsController;
	}

	/**
	 * @return the groundStringController
	 */
	public GroundStringController getGroundStringController() {
		return groundStringController;
	}

	/**
	 * @return the projectController
	 */
	public ProjectController getProjectController() {
		return projectController;
	}

	public String getFullyQualifiedName(ASTNode node) {
		return InfoProjectHelper.getFullyQualifiedName(node);
	}

}
