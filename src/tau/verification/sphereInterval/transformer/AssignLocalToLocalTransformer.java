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

        Factoid rhsFactoid = factoidsConjunction.getFactoid(rhs);
        if(rhsFactoid == null) {
            ForgetLocalTransformer forgetLocalTransformer = new ForgetLocalTransformer(lhs);
            return forgetLocalTransformer.invoke(factoidsConjunction);
        }

        if(rhsFactoid.isBottom()) {
            ForgetLocalTransformer forgetLhsLocalTransformer = new ForgetLocalTransformer(lhs);
            ForgetLocalTransformer forgetRhsLocalTransformer = new ForgetLocalTransformer(rhs);
            return forgetLhsLocalTransformer.invoke(forgetRhsLocalTransformer.invoke(factoidsConjunction));
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction(factoidsConjunction);
        result.removeFactoid(lhs);
        result.update(new Factoid(lhs, rhsFactoid.sphereInterval));

        return result;
    }
}