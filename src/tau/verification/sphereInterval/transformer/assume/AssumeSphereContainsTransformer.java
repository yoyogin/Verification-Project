package tau.verification.sphereInterval.transformer.assume;

import soot.jimple.IntConstant;
import soot.jimple.NumericConstant;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.transformer.statement.IdTransformer;

public class AssumeSphereContainsTransformer extends AssumeSphereBaseTransformer {
    public final JimpleLocal receiverVariable;
    public final JimpleLocal argumentVariable;

    public AssumeSphereContainsTransformer(
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

        Factoid receiverFactoid = factoidsConjunction.getFactoid(receiverVariable);
        Factoid argumentFactoid = factoidsConjunction.getFactoid(argumentVariable);
        if(receiverFactoid == null || argumentFactoid == null) {
            return factoidsConjunction;
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction(factoidsConjunction);
        // TODO: we're loosing here information on whether they're pointing to the same variable
        if(!(this.assumeValue == receiverFactoid.sphereInterval.contains(argumentFactoid.sphereInterval))) {
            result.update(Factoid.getBottom(argumentFactoid.variable));
        }

        return result;
    }
}
