package tau.verification.sphereInterval.transformer.statement;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.transformer.BaseTransformer;

public class SphereAddPointTransformer extends BaseTransformer {

    public final JimpleLocal receiverVariable;
    public final IntConstant additionToX0;
    public final IntConstant additionToY0;
    public final IntConstant additionToZ0;

    public SphereAddPointTransformer(
            JimpleLocal receiverVariable,
            IntConstant additionToX0,
            IntConstant additionToY0,
            IntConstant additionToZ0) {
        super(1 /* numberOfArguments */);

        this.receiverVariable = receiverVariable;
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
        Factoid receiverFactoid = result.getFactoid(this.receiverVariable);
        if(receiverFactoid == null) {
            return result;
        }

        result.update(new Factoid(
                receiverVariable,
                (IntConstant) receiverFactoid.sphereInterval.x0.add(this.additionToX0),
                (IntConstant) receiverFactoid.sphereInterval.y0.add(this.additionToY0),
                (IntConstant) receiverFactoid.sphereInterval.z0.add(this.additionToZ0),
                receiverFactoid.sphereInterval.edgeA,
                receiverFactoid.sphereInterval.edgeB,
                receiverFactoid.sphereInterval.edgeC,
                receiverFactoid.sphereInterval.radios));

        return result;
    }
}