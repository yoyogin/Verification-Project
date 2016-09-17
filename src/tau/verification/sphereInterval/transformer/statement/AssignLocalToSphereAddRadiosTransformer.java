package tau.verification.sphereInterval.transformer.statement;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.transformer.BaseTransformer;

public class AssignLocalToSphereAddRadiosTransformer extends BaseTransformer {
    public final JimpleLocal lhsVariable;
    public final JimpleLocal rhsVariable;
    public final IntConstant additionToRadios;

    public AssignLocalToSphereAddRadiosTransformer(
            JimpleLocal lhsVariable,
            JimpleLocal rhsVariable,
            IntConstant additionToRadios) {
        super(1 /* numberOfArguments */);

        this.lhsVariable = lhsVariable;
        this.rhsVariable = rhsVariable;
        this.additionToRadios = additionToRadios;
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
                rhsFactoid.sphereInterval.x0,
                rhsFactoid.sphereInterval.y0,
                rhsFactoid.sphereInterval.z0,
                rhsFactoid.sphereInterval.edgeA,
                rhsFactoid.sphereInterval.edgeB,
                rhsFactoid.sphereInterval.edgeC,
                (IntConstant) rhsFactoid.sphereInterval.radios.add(this.additionToRadios)));

        return result;
    }
}