package tau.verification.sphereInterval.chaoticIteration;

import tau.verification.sphereInterval.FactoidsConjunction;
import tau.verification.sphereInterval.util.StringUtils;

import java.util.*;

public class EquationSystem {
    private LinkedHashMap<WorkListItem, Equation> workListItemToEquation = new LinkedHashMap<>();
    private HashMap<WorkListItem, Set<WorkListItem>> workListItemToDependentWorkListItems = new HashMap<>();

    public void addEquation(Equation equation) {
        WorkListItem lhsWorkListItem = equation.getLhsWorkListItem();

        assert !workListItemToEquation.containsKey(lhsWorkListItem);
        workListItemToEquation.put(lhsWorkListItem, equation);

        for (WorkListItem rhsWorkListItem : equation.getRhsWorkListItems()) {
            Set<WorkListItem> dependentWorkListItems = workListItemToDependentWorkListItems.get(rhsWorkListItem);

            if (dependentWorkListItems == null) {
                dependentWorkListItems = new HashSet<>();
                workListItemToDependentWorkListItems.put(rhsWorkListItem, dependentWorkListItems);
            }

            dependentWorkListItems.add(lhsWorkListItem);
        }
    }

    public Equation getEquation(WorkListItem workListItem) {
        return workListItemToEquation.get(workListItem);
    }

    public Collection<Equation> getEquations() {
        return workListItemToEquation.values();
    }

    public Collection<WorkListItem> getWorkListItems() {
        return workListItemToEquation.keySet();
    }

    public Set<WorkListItem> getDependentWorkListItems(WorkListItem workListItem) {
        Set<WorkListItem> result = workListItemToDependentWorkListItems.get(workListItem);
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

    @Override
    public String toString() {
        return StringUtils.collectionWithSeparatorToString(workListItemToEquation.values(), "\n");
    }
}