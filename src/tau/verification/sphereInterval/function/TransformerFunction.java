package tau.verification.sphereInterval.function;

import tau.verification.sphereInterval.FactoidsConjunction;

public abstract class TransformerFunction extends Function {
    @Override
    public final int arguments() {
        return 1;
    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction input) {
        throw new UnsupportedOperationException();
    }
}