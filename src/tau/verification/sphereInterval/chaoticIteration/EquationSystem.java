package tau.verification.sphereInterval.chaoticIteration;

import tau.verification.sphereInterval.FactoidsConjunction;
import tau.verification.sphereInterval.util.StringUtils;

import java.util.*;

public class EquationSystem {
    private LinkedHashMap<WorkListItem, Equation> workListItemToEquation = new LinkedHashMap<>();
    private HashMap<WorkListItem, Set<Equation>> workListItemToDependentEquations = new HashMap<>();

    public void addEquation(Equation equation) {
        WorkListItem workListItem = equation.getLhsWorkListItem();

        assert !workListItemToEquation.containsKey(workListItem);
        workListItemToEquation.put(workListItem, equation);

        for (WorkListItem argVar : equation.getRhsWorkListItems()) {
            Set<Equation> containingEquations = workListItemToDependentEquations.get(argVar);
            if (containingEquations == null) {
                containingEquations = new HashSet<>();
                workListItemToDependentEquations.put(argVar, containingEquations);
            }
            containingEquations.add(equation);
        }
    }

    public Collection<Equation> getEquations() {
        return workListItemToEquation.values();
    }

    public Set<Equation> getDependentEquations(WorkListItem var) {
        Set<Equation> result = workListItemToDependentEquations.get(var);
        if (result == null) {
            return Collections.emptySet();
        }

        return result;
    }

    public void resetWorkListItems(FactoidsConjunction value) {
        for (Equation equation : workListItemToEquation.values()) {
            equation.getLhsWorkListItem().value = value;
        }
    }

    /**
     * @return all equations that don't depend on other work list items
     */
    public Set<Equation> getConstantEquations() {
        Set<Equation> result = new HashSet<>();
        for (Equation equation : workListItemToEquation.values()) {
            if (equation.getRhsWorkListItems().isEmpty())
                result.add(equation);
        }

        return result;
    }

    @Override
    public String toString() {
        return StringUtils.collectionWithSeparatorToString(workListItemToEquation.values(), "\n");
    }
}