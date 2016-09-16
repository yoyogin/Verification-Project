package tau.verification.sphereInterval.transformer;

import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;

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

        Factoid rhsFactoid = result.getFactoid(rhs);
        if(rhsFactoid == null) {
            return result;
        }

        //TODO: consider whether we should validate that both a truly sphere variables? can we assume that this is guaranteed by the Java language compiler?
        //result.add(new Factoid(lhs, rhsFactoid.getAbstractSphere()));

        return result;
    }
}