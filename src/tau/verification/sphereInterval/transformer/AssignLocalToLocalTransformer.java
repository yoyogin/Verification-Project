package tau.verification.sphereInterval.transformer;

import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.FactoidsConjunction;

public class AssignLocalToLocalTransformer extends BaseTransformer {
    protected final JimpleLocal lhs;
    protected final JimpleLocal rhs;

    public AssignLocalToLocalTransformer(JimpleLocal lhs, JimpleLocal rhs) {
        super(1 /* numberOfArguments */);

        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction factoidsConjunction) {
        if (factoidsConjunction.isBottom()) {
            return FactoidsConjunction.getBottom();
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction(factoidsConjunction);
        result.removeVar(lhs);

        //TODO: handle 'pointer' assignment in case both are truly sphere variables

        return result;
    }
}