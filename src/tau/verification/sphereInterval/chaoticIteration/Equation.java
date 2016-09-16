package tau.verification.sphereInterval.chaoticIteration;

import tau.verification.sphereInterval.function.Function;

import java.util.ArrayList;
import java.util.List;

public class Equation {
    private final WorkListItem lhsWorkListItem;
    private final Function function;
    private final ArrayList<WorkListItem> rhsWorkListItems;

    private final String unitDescription;

    public Equation(WorkListItem lhsWorkListItem, Function function, String unitDescription) {
        this.lhsWorkListItem = lhsWorkListItem;
        this.function = function;
        this.rhsWorkListItems = new ArrayList<>();
        this.unitDescription = unitDescription;
    }

    public Equation(WorkListItem lhsWorkListItem, Function function, WorkListItem argument, String unitDescription) {
        this.lhsWorkListItem = lhsWorkListItem;
        this.function = function;

        this.rhsWorkListItems = new ArrayList<>();
        this.rhsWorkListItems.add(argument);

        this.unitDescription = unitDescription;
    }

    public Equation(WorkListItem lhsWorkListItem, Function function, WorkListItem arg1, WorkListItem arg2, String unitDescription) {
        this.lhsWorkListItem = lhsWorkListItem;
        this.function = function;

        this.rhsWorkListItems = new ArrayList<>();
        this.rhsWorkListItems.add(arg1);
        this.rhsWorkListItems.add(arg2);

        this.unitDescription = unitDescription;
    }

    public WorkListItem getLhsWorkListItem() {
        return lhsWorkListItem;
    }

    public List<WorkListItem> getRhsWorkListItems() {
        return rhsWorkListItems;
    }

    public void evaluate() {
        this.lhsWorkListItem.value = this.function.invoke(rhsWorkListItems); //TODO: consider placing logging here
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(lhsWorkListItem);
        stringBuilder.append(" = ");
        stringBuilder.append(function.invocationToString(rhsWorkListItems));
        stringBuilder.append("   <--->   ");
        stringBuilder.append(unitDescription);

        return stringBuilder.toString();
    }
}