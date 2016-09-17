package tau.verification.sphereInterval.transformer.assume;

import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;

public class AssumeSphereIsContainedInTransformer extends AssumeSphereBaseTransformer {
    public final JimpleLocal receiverVariable;
    public final JimpleLocal argumentVariable;

    public AssumeSphereIsContainedInTransformer(
            JimpleLocal receiverVariable,
            JimpleLocal argumentVariable,
            boolean assumeValue) {
        super(receiverVariable, argumentVariable, assumeValue);

        this.receiverVariable = receiverVariable;
        this.argumentVariable = argumentVariable;
    }

    @Override
    public String toString() {
        String assumeExpressionDescription = String.format(
                "%s.isContainedIn(%s)",
                this.receiverVariable.toString(),
                this.argumentVariable.toString());

        return this.getDecoratedAssumeString(assumeExpressionDescription);
    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction factoidsConjunction) {
        if(factoidsConjunction.isBottom()) {
            return FactoidsConjunction.getBottom();
        }

        Factoid receiverFactoid = factoidsConjunction.getFactoid(receiverVariable);
        Factoid argumentFactoid = factoidsConjunction.getFactoid(argumentVariable);
        if(receiverFactoid == null || argumentFactoid == null) {
            return factoidsConjunction;
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction(factoidsConjunction);
        if (!(this.assumeContains == argumentFactoid.sphereInterval.contains(receiverFactoid.sphereInterval))) {
            result.update(Factoid.getBottom(receiverFactoid.variable));
            result.update(Factoid.getBottom(argumentFactoid.variable));
        }

        return result;
    }
}
