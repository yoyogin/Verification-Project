package tau.verification.sphereInterval.function;

import tau.verification.sphereInterval.FactoidsConjunction;
import tau.verification.sphereInterval.chaoticIteration.WorkListItem;

import java.util.List;

public final class IdTransformer extends TransformerFunction {
    public IdTransformer() {
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