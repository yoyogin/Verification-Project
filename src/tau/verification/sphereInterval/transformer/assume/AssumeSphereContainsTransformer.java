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

        FactoidsConjunction assumedFactoidsConjunction = FactoidsConjunction.getFactoidsConjunction();

        Factoid receiverFactoid = factoidsConjunction.getFactoid(receiverVariable);
        Factoid argumentFactoid = factoidsConjunction.getFactoid(argumentVariable);
        if(receiverFactoid == null || argumentFactoid == null) {
            return factoidsConjunction;
        }

        // TODO: we're loosing here information on whether they're pointing to the same variable
        if(this.assumeValue == receiverFactoid.contains(argumentFactoid)) {
            assumedFactoidsConjunction.update(new Factoid(
                    argumentVariable,
                    receiverFactoid.sphereInterval.x0,
                    receiverFactoid.sphereInterval.y0,
                    receiverFactoid.sphereInterval.z0,
                    receiverFactoid.sphereInterval.edgeA,
                    receiverFactoid.sphereInterval.edgeB,
                    receiverFactoid.sphereInterval.edgeC,
                    receiverFactoid.sphereInterval.radios));
        } else {
            assumedFactoidsConjunction.update(getFactoidNotContainedInReceiverFactoid(receiverFactoid));
        }

        return FactoidsConjunction.lowerBound(factoidsConjunction, assumedFactoidsConjunction);
    }

    private Factoid getFactoidNotContainedInReceiverFactoid(Factoid receiverFactoid) {
        Factoid result = null;

        Factoid x0Plus1 = new Factoid(
                argumentVariable,
                (IntConstant) receiverFactoid.sphereInterval.x0.add(IntConstant.v(1)),
                receiverFactoid.sphereInterval.y0,
                receiverFactoid.sphereInterval.z0,
                receiverFactoid.sphereInterval.edgeA,
                receiverFactoid.sphereInterval.edgeB,
                receiverFactoid.sphereInterval.edgeC,
                receiverFactoid.sphereInterval.radios);

        Factoid y0Plus1 = new Factoid(
                argumentVariable,
                receiverFactoid.sphereInterval.x0,
                (IntConstant) receiverFactoid.sphereInterval.y0.add(IntConstant.v(1)),
                receiverFactoid.sphereInterval.z0,
                receiverFactoid.sphereInterval.edgeA,
                receiverFactoid.sphereInterval.edgeB,
                receiverFactoid.sphereInterval.edgeC,
                receiverFactoid.sphereInterval.radios);

        result = Factoid.getUpperBound(x0Plus1, y0Plus1);

        Factoid z0Plus1 = new Factoid(
                argumentVariable,
                receiverFactoid.sphereInterval.x0,
                receiverFactoid.sphereInterval.y0,
                (IntConstant) receiverFactoid.sphereInterval.z0.add(IntConstant.v(1)),
                receiverFactoid.sphereInterval.edgeA,
                receiverFactoid.sphereInterval.edgeB,
                receiverFactoid.sphereInterval.edgeC,
                receiverFactoid.sphereInterval.radios);

        result = Factoid.getUpperBound(result, z0Plus1);

        Factoid edgeAPlus1 = new Factoid(
                argumentVariable,
                receiverFactoid.sphereInterval.x0,
                receiverFactoid.sphereInterval.y0,
                receiverFactoid.sphereInterval.z0,
                (IntConstant) receiverFactoid.sphereInterval.edgeA.add(IntConstant.v(1)),
                receiverFactoid.sphereInterval.edgeB,
                receiverFactoid.sphereInterval.edgeC,
                receiverFactoid.sphereInterval.radios);

        result = Factoid.getUpperBound(result, edgeAPlus1);

        Factoid edgeBPlus1 = new Factoid(
                argumentVariable,
                receiverFactoid.sphereInterval.x0,
                receiverFactoid.sphereInterval.y0,
                receiverFactoid.sphereInterval.z0,
                receiverFactoid.sphereInterval.edgeA,
                (IntConstant) receiverFactoid.sphereInterval.edgeB.add(IntConstant.v(1)),
                receiverFactoid.sphereInterval.edgeC,
                receiverFactoid.sphereInterval.radios);

        result = Factoid.getUpperBound(result, edgeBPlus1);

        Factoid edgeCPlus1 = new Factoid(
                argumentVariable,
                receiverFactoid.sphereInterval.x0,
                receiverFactoid.sphereInterval.y0,
                receiverFactoid.sphereInterval.z0,
                receiverFactoid.sphereInterval.edgeA,
                receiverFactoid.sphereInterval.edgeB,
                (IntConstant) receiverFactoid.sphereInterval.edgeC.add(IntConstant.v(1)),
                receiverFactoid.sphereInterval.radios);

        result = Factoid.getUpperBound(result, edgeCPlus1);

        Factoid radiosPlus1 = new Factoid(
                argumentVariable,
                receiverFactoid.sphereInterval.x0,
                receiverFactoid.sphereInterval.y0,
                receiverFactoid.sphereInterval.z0,
                receiverFactoid.sphereInterval.edgeA,
                receiverFactoid.sphereInterval.edgeB,
                receiverFactoid.sphereInterval.edgeC,
                (IntConstant) receiverFactoid.sphereInterval.radios.add(IntConstant.v(1)));

        return Factoid.getUpperBound(result, radiosPlus1);
    }
}
