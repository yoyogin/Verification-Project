package tau.verification.sphereInterval.transformer.statement;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.transformer.BaseTransformer;

public class SphereAddRadiosTransformer extends BaseTransformer {

    public final JimpleLocal receiverVariable;
    public final IntConstant additionToRadios;

    public SphereAddRadiosTransformer(
            JimpleLocal receiverVariable,
            IntConstant additionToRadios) {
        super(1 /* numberOfArguments */);

        this.receiverVariable = receiverVariable;
        this.additionToRadios = additionToRadios;
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
                receiverFactoid.sphereInterval.x0,
                receiverFactoid.sphereInterval.y0,
                receiverFactoid.sphereInterval.z0,
                receiverFactoid.sphereInterval.edgeA,
                receiverFactoid.sphereInterval.edgeB,
                receiverFactoid.sphereInterval.edgeC,
                (IntConstant) receiverFactoid.sphereInterval.radios.add(this.additionToRadios)));

        return result;
    }
}