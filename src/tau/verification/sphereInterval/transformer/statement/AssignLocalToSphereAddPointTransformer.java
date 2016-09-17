package tau.verification.sphereInterval.transformer.statement;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.transformer.BaseTransformer;

public class AssignLocalToSphereAddPointTransformer extends BaseTransformer {

    public final JimpleLocal lhsVariable;
    public final JimpleLocal rhsVariable;
    public final IntConstant additionToX0;
    public final IntConstant additionToY0;
    public final IntConstant additionToZ0;

    public AssignLocalToSphereAddPointTransformer(
            JimpleLocal lhsVariable,
            JimpleLocal rhsVariable,
            IntConstant additionToX0,
            IntConstant additionToY0,
            IntConstant additionToZ0) {
        super(1 /* numberOfArguments */);

        this.lhsVariable = lhsVariable;
        this.rhsVariable = rhsVariable;
        this.additionToX0 = additionToX0;
        this.additionToY0 = additionToY0;
        this.additionToZ0 = additionToZ0;
    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction factoidsConjunction) {
        if(factoidsConjunction.isBottom()) {
            return FactoidsConjunction.getBottom();
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction(factoidsConjunction);
        Factoid rhsFactoid = result.getFactoid(this.rhsVariable);
        if(rhsFactoid == null) {
            return result;
        }

        result.update(new Factoid(
                this.lhsVariable,
                (IntConstant) rhsFactoid.sphereInterval.x0.add(this.additionToX0),
                (IntConstant) rhsFactoid.sphereInterval.y0.add(this.additionToY0),
                (IntConstant) rhsFactoid.sphereInterval.z0.add(this.additionToZ0),
                rhsFactoid.sphereInterval.edgeA,
                rhsFactoid.sphereInterval.edgeB,
                rhsFactoid.sphereInterval.edgeC,
                rhsFactoid.sphereInterval.radios));

        return result;
    }
}