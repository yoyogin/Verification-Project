package tau.verification.sphereInterval.transformer;

import tau.verification.sphereInterval.lattice.FactoidsConjunction;

public final class IdTransformer extends BaseTransformer {
    public IdTransformer() {
        super(1 /* numberOfArguments */);
    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction factoidsConjunction) {
        return factoidsConjunction;
    }

    @Override
    public String toString() {
        return "Id";
    }
}