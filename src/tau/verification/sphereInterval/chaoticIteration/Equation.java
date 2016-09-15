package tau.verification.sphereInterval.chaoticIteration;

import tau.verification.sphereInterval.function.Function;
import tau.verification.sphereInterval.chaoticIteration.WorkListItem;

import java.util.ArrayList;
import java.util.List;

public class Equation {
    private final WorkListItem lhsWorkListItem;
    private final Function function;
    private final ArrayList<WorkListItem> rhsWorkListItems;

    private final String originCodeFromUnit;

    public Equation(WorkListItem lhsWorkListItem, Function function, String originCodeFromUnit) {
        this.lhsWorkListItem = lhsWorkListItem;
        this.function = function;
        this.rhsWorkListItems = new ArrayList<>();
        this.originCodeFromUnit = originCodeFromUnit;
    }

    public Equation(WorkListItem lhsWorkListItem, Function function, WorkListItem argument, String originCodeFromUnit) {
        this.lhsWorkListItem = lhsWorkListItem;
        this.function = function;

        this.rhsWorkListItems = new ArrayList<>();
        this.rhsWorkListItems.add(argument);

        this.originCodeFromUnit = originCodeFromUnit;
    }

    public Equation(WorkListItem lhsWorkListItem, Function function, WorkListItem arg1, WorkListItem arg2, String originCodeFromUnit) {
        this.lhsWorkListItem = lhsWorkListItem;
        this.function = function;

        this.rhsWorkListItems = new ArrayList<>();
        this.rhsWorkListItems.add(arg1);
        this.rhsWorkListItems.add(arg2);

        this.originCodeFromUnit = originCodeFromUnit;
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
        stringBuilder.append(" >> ");
        stringBuilder.append(originCodeFromUnit);

        return stringBuilder.toString();
    }
}