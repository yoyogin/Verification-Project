package tau.verification.sphereInterval.transformer;

import tau.verification.sphereInterval.chaoticIteration.WorkListItem;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;

import java.util.List;

public abstract class BaseTransformer {
    private int argumentsCount;

    public BaseTransformer(int argumentsCount) {
        this.argumentsCount = argumentsCount;
    }

    public FactoidsConjunction invoke(List<WorkListItem> workListItems) {
        switch (this.argumentsCount) {
            case 0:
                return invoke();
            case 1:
                FactoidsConjunction argument = workListItems.get(0).value;
                return invoke(argument);
            case 2:
                FactoidsConjunction firstArgument = workListItems.get(0).value;
                FactoidsConjunction secondArgument = workListItems.get(1).value;
                return invoke(firstArgument, secondArgument);
            default:
                throw new UnsupportedOperationException();
        }
    }

    public FactoidsConjunction invoke() {
        throw new UnsupportedOperationException();
    }

    public FactoidsConjunction invoke(FactoidsConjunction factoidsConjunction) {
        throw new UnsupportedOperationException();
    }

    public FactoidsConjunction invoke(
            FactoidsConjunction firstFactoidsConjunction,
            FactoidsConjunction secondFactoidsConjunction) {
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