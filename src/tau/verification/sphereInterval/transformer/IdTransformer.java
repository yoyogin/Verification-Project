package tau.verification.sphereInterval.transformer;

import tau.verification.sphereInterval.lattice.FactoidsMapping;

public final class IdTransformer extends BaseTransformer {
    public IdTransformer() {
        super(1 /* numberOfArguments */);
    }

    @Override
    public FactoidsMapping invoke(FactoidsMapping factoidsMapping) {
        return factoidsMapping;
    }

    @Override
    public String toString() {
        return "Id";
    }
}