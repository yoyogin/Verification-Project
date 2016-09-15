package tau.verification.sphereInterval.chaoticIteration;

import tau.verification.sphereInterval.Domain;
import tau.verification.sphereInterval.FactoidsConjunction;
import tau.verification.sphereInterval.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class ChaoticIteration {
    private EquationSystem equationSystem;
    private Domain domain;
    private int iterationCounter;

    /**
     * Solves an equation equationSystem using chaotic iteration
     */
    public void iterate(EquationSystem system, Domain domain) {
        this.equationSystem = system;
        this.domain = domain;
        this.iterationCounter = 0;

        System.out.println("Solving equation equationSystem = \n" + system);
        equationSystem.resetWorkListItems(domain.getBottom());

        System.out.println("Begin Chaotic Iterations");
        internalIterate();

        System.out.println("Reached fixed-point after " + iterationCounter + " iterations");
        printSolution(system);
    }

    /**
     * Starts chaotic iteration computation
     */
    private void internalIterate() {
        Collection<Equation> initialEquations = equationSystem.getConstantEquations();
        if (initialEquations.isEmpty()) {
            initialEquations.addAll(equationSystem.getEquations());
        }

        LinkedList<Equation> workList = new LinkedList<>(initialEquations);
        while (!workList.isEmpty()) {
            iterationCounter++;
            printWorkList(workList);

            Equation equation = workList.remove();

            System.out.println("Iteration " + iterationCounter + ": working on equation " + equation.toString());

            System.out.println("\t\t\t" + equation.getLhsWorkListItem() + " : " + equation.getLhsWorkListItem().value);
            for (WorkListItem arg : equation.getRhsWorkListItems()) {
                System.out.println("\t\t\t" + arg + " : " + arg.value);
            }

            FactoidsConjunction previousValue = equation.getLhsWorkListItem().value;
            equation.evaluate();
            System.out.println("\t\t\t updated " + equation.getLhsWorkListItem() + " : " + equation.getLhsWorkListItem().value);


            if (domain.lessThanEquals(equation.getLhsWorkListItem().value, previousValue)) {
                // evaluate takes a conjunction upwards so there
                // was no change to the value in this iteration
                continue;
            }


            Collection<Equation> newEquationsToWorkList = new ArrayList<>();
            for (Equation nextEquation : equationSystem.getDependentEquations(equation.getLhsWorkListItem())) {
                assert nextEquation != equation; // would get us to infinite loop...
                if (nextEquation == equation) {
                    continue;
                }

                newEquationsToWorkList.add(nextEquation);
            }

            System.out.println("\t\t\tAdding the following to work list" + newEquationsToWorkList);
            workList.addAll(newEquationsToWorkList);
        }
    }

    private void printWorkList(Collection<Equation> workList) {
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