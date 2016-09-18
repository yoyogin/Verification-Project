package tau.verification.sphereInterval.transformer;

import tau.verification.sphereInterval.chaoticIteration.WorkListItem;
import tau.verification.sphereInterval.lattice.FactoidsMapping;

import java.util.List;

public abstract class BaseTransformer {
    private int argumentsCount;

    public BaseTransformer(int argumentsCount) {
        this.argumentsCount = argumentsCount;
    }

    public FactoidsMapping invoke(List<WorkListItem> workListItems) {
        switch (this.argumentsCount) {
            case 0:
                return invoke();
            case 1:
                FactoidsMapping argument = workListItems.get(0).value;
                return invoke(argument);
            case 2:
                FactoidsMapping firstArgument = workListItems.get(0).value;
                FactoidsMapping secondArgument = workListItems.get(1).value;
                return invoke(firstArgument, secondArgument);
            default:
                throw new UnsupportedOperationException();
        }
    }

    public FactoidsMapping invoke() {
        throw new UnsupportedOperationException();
    }

    public FactoidsMapping invoke(FactoidsMapping factoidsMapping) {
        throw new UnsupportedOperationException();
    }

    public FactoidsMapping invoke(
            FactoidsMapping firstFactoidsMapping,
            FactoidsMapping secondFactoidsMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public String invocationToString(List<WorkListItem> arguments) {
        StringBuilder result = new StringBuilder(this.toString() + "(");

        for (int i = 0; i < this.argumentsCount; i++) {
            result.append(arguments.get(i));
            if (i < arguments.size() - 1)
                result.append(", ");
        }
        result.append(")");

        return result.toString();
    }
}