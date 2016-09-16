package tau.verification.sphereInterval.transformer;

import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.chaoticIteration.WorkListItem;

import java.util.List;

public abstract class BaseTransformer {
    private int argumentsCount;

    public BaseTransformer(int argumentsCount) {
        this.argumentsCount = argumentsCount;
    }

    public FactoidsConjunction invoke(List<WorkListItem> inputs) {
        switch (this.argumentsCount) {
        case 0:
            return invoke();
        case 1:
            FactoidsConjunction input0 = inputs.get(0).value;
            return invoke(input0);
        case 2:
            FactoidsConjunction input1 = inputs.get(0).value;
            FactoidsConjunction input2 = inputs.get(1).value;
            return invoke(input1, input2);
        default:
            throw new UnsupportedOperationException();
        }
    }

    public FactoidsConjunction invoke() {
        throw new UnsupportedOperationException();
    }

    public FactoidsConjunction invoke(FactoidsConjunction input) {
        throw new UnsupportedOperationException();
    }

    public FactoidsConjunction invoke(FactoidsConjunction input1, FactoidsConjunction input2) {
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