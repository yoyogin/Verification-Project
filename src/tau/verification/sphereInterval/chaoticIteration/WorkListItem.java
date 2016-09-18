package tau.verification.sphereInterval.chaoticIteration;

import tau.verification.sphereInterval.lattice.FactoidsConjunction;

public class WorkListItem {
    public FactoidsConjunction value;
    private String name;
    private static int workListItemCounter = 0; //TODO: consider creating a proper factory object so each method processing will start at index 0

    public static WorkListItem getFreshWorkListItem() {
        WorkListItem result = new WorkListItem("R[" + workListItemCounter + "]");
        workListItemCounter++;

        return result;
    }

    public static void clean() {
        workListItemCounter = 0;
    }

    private WorkListItem(String name) {
        this.name = name;
        this.value = null;
    }

    @Override
    public String toString() {
        return name;
    }
}