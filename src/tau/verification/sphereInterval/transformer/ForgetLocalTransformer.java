package tau.verification.sphereInterval.transformer;

import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;

/**
 * A transformer to be used in cases where the state of the variable has changed
 * but we don't know how to handle the change therefore we should clean the the
 * knowledge that we gained about that variable (i.e. set it back to bottom)
 */
public class ForgetLocalTransformer extends BaseTransformer {
    protected JimpleLocal lhs;

    public ForgetLocalTransformer(JimpleLocal lhs) {
        super(1 /* numberOfArguments */);
        this.lhs = lhs;
    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction input) {
        if (input.isBottom()) {
            return FactoidsConjunction.getBottom();
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction(input);
        result.update(Factoid.getBottom(this.lhs));

        return result;
    }
}
