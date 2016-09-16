package tau.verification.sphereInterval.function;

import tau.verification.sphereInterval.FactoidsConjunction;

public abstract class TransformerFunction extends Function {
    @Override
    public final byte arguments() {
        return 1;
    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction input) {
        throw new UnsupportedOperationException();
    }
}