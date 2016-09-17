package tau.verification.sphereInterval.transformer.assume;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.lattice.SphereInterval;

/**
 * Created by Lior on 16/09/18.
 */
public class AssumeIsRadiosLessOrEqualThanTransformer extends AssumeSphereBaseTransformer  {

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
                    this.argumentVariable == null ? this.argumentConstant .toString() : this.argumentVariable.toString());



        return this.getDecoratedAssumeString(assumeExpressionDescription);
    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction factoidsConjunction){

        if(factoidsConjunction.isBottom()) {
            return FactoidsConjunction.getBottom();
        }

        Factoid receiverFactoid = factoidsConjunction.getFactoid(receiverVariable);
        if(receiverFactoid == null ) {
            return factoidsConjunction;
        }

        IntConstant radios;

        if(argumentConstant != null)
        {
            radios = ((IntConstant)(receiverFactoid.sphereInterval.radios.lessThan(argumentConstant))).value == 1? receiverFactoid.sphereInterval.radios : argumentConstant;

        }else
        {
            Factoid argumentFactoid = factoidsConjunction.getFactoid(argumentVariable);
            if(argumentFactoid == null ) {
                return factoidsConjunction;
            }

            radios = ((IntConstant)(receiverFactoid.sphereInterval.radios.lessThan(argumentFactoid.sphereInterval.radios))).value == 1? receiverFactoid.sphereInterval.radios : argumentFactoid.sphereInterval.radios;
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
        Factoid resultFactoid = new Factoid(receiverVariable,si);


        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction(factoidsConjunction);
        result.update(resultFactoid);
        return result;


    }
}
