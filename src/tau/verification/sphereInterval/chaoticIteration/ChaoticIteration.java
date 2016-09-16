package tau.verification.sphereInterval.chaoticIteration;

import tau.verification.sphereInterval.lattice.LatticeOperations;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.util.StringUtils;

import java.util.*;

public class ChaoticIteration {
    private EquationSystem equationSystem;
    private LatticeOperations latticeOperations;
    private int iterationCounter;

    /**
     * Solves an equation equationSystem using chaotic iteration
     */
    public void iterate(EquationSystem equationSystem, LatticeOperations latticeOperations) {
        this.equationSystem = equationSystem;
        this.latticeOperations = latticeOperations;
        this.iterationCounter = 0;

        System.out.println("Solving equation system = \n" + equationSystem);
        this.equationSystem.resetWorkListItems(latticeOperations.getBottom());

        System.out.println("Begin Chaotic Iterations");
        internalIterate();

        System.out.println("Reached fixed-point after " + iterationCounter + " iterations");
        printSolution(equationSystem);
    }

    /**
     * Starts chaotic iteration computation
     */
    private void internalIterate() {
        Set<WorkListItem> workList = new LinkedHashSet<>(equationSystem.getWorkListItems());

        while (!workList.isEmpty()) {
            iterationCounter++;
            printWorkList(workList);

            WorkListItem currentWorkListItem = removeItemFromWorkList(workList);
            Equation currentEquation = equationSystem.getEquation(currentWorkListItem);

            System.out.println("Iteration " + iterationCounter + ": working on equation " + currentEquation.toString());

            System.out.println("\t\t\t" + currentEquation.getLhsWorkListItem() + " : " + currentEquation.getLhsWorkListItem().value);
            for (WorkListItem arg : currentEquation.getRhsWorkListItems()) {
                System.out.println("\t\t\t" + arg + " : " + arg.value);
            }

            FactoidsConjunction previousValue = currentEquation.getLhsWorkListItem().value;
            currentEquation.evaluate();
            System.out.println("\t\t\t updated " + currentEquation.getLhsWorkListItem() + " : " + currentEquation.getLhsWorkListItem().value);


            if (latticeOperations.lessThanEquals(currentEquation.getLhsWorkListItem().value, previousValue)) {
                // evaluate takes a conjunction upwards so there
                // was no change to the value in this iteration
                continue;
            }


            Collection<WorkListItem> newWorkListItemsToWorkingSet = new ArrayList<>();
            for (WorkListItem dependentWorkListItem : equationSystem.getDependentWorkListItems(currentWorkListItem)) {
                assert dependentWorkListItem != currentWorkListItem; // would get us to infinite loop...
                if (dependentWorkListItem == currentWorkListItem) {
                    continue;
                }

                newWorkListItemsToWorkingSet.add(dependentWorkListItem);
            }

            System.out.println("\t\t\tAdding the following to work list" + newWorkListItemsToWorkingSet);
            workList.addAll(newWorkListItemsToWorkingSet);
        }
    }

    private WorkListItem removeItemFromWorkList(Set<WorkListItem> workList) {
        Iterator<WorkListItem> iterator = workList.iterator();

        assert iterator.hasNext(); // we shouldn't get to this point
        if(!iterator.hasNext()) {
            return null;
        }

        WorkListItem result = iterator.next();
        iterator.remove();

        return result;
    }

    private void printWorkList(Collection<WorkListItem> workList) {
        StringBuilder result = new StringBuilder("\t\t\tworkSet = { ");
        result.append(StringUtils.collectionWithSeparatorToString(workList, ", "));
        result.append("}");

        System.out.println(result.toString());
    }

    private void printSolution(EquationSystem system) {
        StringBuilder result = new StringBuilder("Solution = {\n");

        for (Equation equation : system.getEquations()) {
            result.append("\t" + equation.getLhsWorkListItem() + " : " + equation.getLhsWorkListItem().value + "\n");
        }
        result.append("}");

        System.out.println(result.toString());
    }
}