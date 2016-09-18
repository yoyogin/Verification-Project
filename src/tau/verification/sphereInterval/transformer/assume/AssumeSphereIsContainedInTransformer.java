package tau.verification.sphereInterval.transformer.assume;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsMapping;
import tau.verification.sphereInterval.lattice.SphereInterval;

public class AssumeSphereIsContainedInTransformer extends AssumeSphereBaseTransformer {
    public final JimpleLocal receiverVariable;
    public final JimpleLocal argumentVariable;

    public AssumeSphereIsContainedInTransformer(
            JimpleLocal receiverVariable,
            JimpleLocal argumentVariable,
            boolean assumeValue) {
        super(assumeValue);

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
    public FactoidsMapping invoke(FactoidsMapping factoidsMapping) {
        if (assumeContains == false) {
            //TODO handle false by returning bottom if 100% contained in.
            //We are able to check it only for trivial cases like dot spheres
            return factoidsMapping;
        }

        if (factoidsMapping.isBottom()) {
            return FactoidsMapping.getBottom();
        }

        Factoid receiverFactoid = factoidsMapping.getFactoid(receiverVariable);
        Factoid argumentFactoid = factoidsMapping.getFactoid(argumentVariable);
        if (receiverFactoid == null || argumentFactoid == null) {
            return factoidsMapping;
        }

        // Create a meet with SI element by algorithm described documentation
        SphereInterval meetWithElement = new SphereInterval(
                (IntConstant) argumentFactoid.sphereInterval.x0.subtract(argumentFactoid.sphereInterval.radios),
                (IntConstant) argumentFactoid.sphereInterval.y0.subtract(argumentFactoid.sphereInterval.radios),
                (IntConstant) argumentFactoid.sphereInterval.z0.subtract(argumentFactoid.sphereInterval.radios),
                (IntConstant) argumentFactoid.sphereInterval.edgeA.add(argumentFactoid.sphereInterval.radios.multiply(IntConstant.v(2))),
                (IntConstant) argumentFactoid.sphereInterval.edgeB.add(argumentFactoid.sphereInterval.radios.multiply(IntConstant.v(2))),
                (IntConstant) argumentFactoid.sphereInterval.edgeC.add(argumentFactoid.sphereInterval.radios.multiply(IntConstant.v(2))),
                argumentFactoid.sphereInterval.radios);

        SphereInterval meetWithElementResult = SphereInterval.getLowerBound(receiverFactoid.sphereInterval, meetWithElement);
        Factoid assumedContainsFactoid = new Factoid(receiverFactoid.variable, meetWithElementResult);

        FactoidsMapping result = FactoidsMapping.getFactoidsConjunction(factoidsMapping);
        result.update(assumedContainsFactoid);

        return result;
    }
}
