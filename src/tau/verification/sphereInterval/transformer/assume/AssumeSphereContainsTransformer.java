package tau.verification.sphereInterval.transformer.assume;

import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;

public class AssumeSphereContainsTransformer extends AssumeSphereBaseTransformer {
    public AssumeSphereContainsTransformer(
            JimpleLocal receiverVariable,
            JimpleLocal argumentVariable,
            boolean assumeValue) {
        super(receiverVariable, argumentVariable, assumeValue);
    }

    @Override
    public String toString() {
        String assumeExpressionDescription = String.format(
                "%s.contains(%s)",
                this.receiverVariable.toString(),
                this.argumentVariable.toString());

        return this.getDecoratedAssumeString(assumeExpressionDescription);
    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction factoidsConjunction) {
        if(factoidsConjunction.isBottom()) {
            return FactoidsConjunction.getBottom();
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction(factoidsConjunction);

        return result;
    }
}
