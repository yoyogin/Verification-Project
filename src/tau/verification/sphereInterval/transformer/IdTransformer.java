package tau.verification.sphereInterval.transformer;

import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.chaoticIteration.WorkListItem;

import java.util.List;

public final class IdTransformer extends BaseTransformer {
    public IdTransformer() {
        super(1 /* numberOfArguments */);
    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction input) {
        return input;
    }

    @Override
    public String toString() {
        return "Id";
    }

    @Override
    public String invocationToString(List<WorkListItem> arguments) {
        return arguments.get(0).toString();
    }
}