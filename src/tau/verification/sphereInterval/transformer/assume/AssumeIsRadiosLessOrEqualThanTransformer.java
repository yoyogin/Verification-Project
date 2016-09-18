package tau.verification.sphereInterval.transformer.assume;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsMapping;
import tau.verification.sphereInterval.lattice.SphereInterval;

public class AssumeIsRadiosLessOrEqualThanTransformer extends AssumeSphereBaseTransformer {

    public final JimpleLocal receiverVariable;
    public final JimpleLocal argumentVariable;
    public final IntConstant argumentConstant;

    public AssumeIsRadiosLessOrEqualThanTransformer(
            JimpleLocal receiverVariable,
            JimpleLocal argumentVariable,
            boolean assumeValue) {
        super(assumeValue);

        this.receiverVariable = receiverVariable;
        this.argumentVariable = argumentVariable;
        this.argumentConstant = null;
    }

    public AssumeIsRadiosLessOrEqualThanTransformer(
            JimpleLocal receiverVariable,
            IntConstant argumentConstant,
            boolean assumeValue) {
        super(assumeValue);

        this.receiverVariable = receiverVariable;
        this.argumentConstant = argumentConstant;
        this.argumentVariable = null;
    }

    @Override
    public String toString() {
        String assumeExpressionDescription = String.format(
                "%s.isRadiosLessOrEqualThan(%s)",
                this.receiverVariable.toString(),
                this.argumentVariable == null ? this.argumentConstant.toString() : this.argumentVariable.toString());

        return this.getDecoratedAssumeString(assumeExpressionDescription);
    }

    @Override
    public FactoidsMapping invoke(FactoidsMapping factoidsMapping) {
        if (assumeContains == false) {
            // we cant handle false assume
            return factoidsMapping;
        }

        if (factoidsMapping.isBottom()) {
            return FactoidsMapping.getBottom();
        }

        Factoid receiverFactoid = factoidsMapping.getFactoid(receiverVariable);
        if (receiverFactoid == null) {
            return factoidsMapping;
        }

        IntConstant radios;

        if (argumentConstant != null) {
            radios = (((IntConstant) (receiverFactoid.sphereInterval.radios.lessThanOrEqual(argumentConstant))).value == 1)
                    ? receiverFactoid.sphereInterval.radios
                    : argumentConstant;

        } else {
            Factoid argumentFactoid = factoidsMapping.getFactoid(argumentVariable);
            if (argumentFactoid == null) {
                return factoidsMapping;
            }

            radios = (((IntConstant) (receiverFactoid.sphereInterval.radios.lessThanOrEqual(argumentFactoid.sphereInterval.radios))).value == 1)
                    ? receiverFactoid.sphereInterval.radios
                    : argumentFactoid.sphereInterval.radios;
        }

        SphereInterval si = new SphereInterval(
                receiverFactoid.sphereInterval.x0,
                receiverFactoid.sphereInterval.y0,
                receiverFactoid.sphereInterval.z0,
                receiverFactoid.sphereInterval.edgeA,
                receiverFactoid.sphereInterval.edgeB,
                receiverFactoid.sphereInterval.edgeC,
                radios
        );
        Factoid resultFactoid = new Factoid(receiverVariable, si);

        FactoidsMapping result = FactoidsMapping.getFactoidsConjunction(factoidsMapping);
        result.update(resultFactoid);

        return result;
    }
}
