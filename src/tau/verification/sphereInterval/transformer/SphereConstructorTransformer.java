package tau.verification.sphereInterval.transformer;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;

public class SphereConstructorTransformer extends BaseTransformer {

    public final JimpleLocal sphereVariable;
    public final IntConstant x0;
    public final IntConstant y0;
    public final IntConstant z0;
    public final IntConstant edgeA;
    public final IntConstant edgeB;
    public final IntConstant edgeC;
    public final IntConstant radios;

    public SphereConstructorTransformer(
            JimpleLocal sphereVariable,
            IntConstant x0,
            IntConstant y0,
            IntConstant z0,
            IntConstant radios) {
        super(1 /* numberOfArguments */);

        this.sphereVariable = sphereVariable;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.edgeA = IntConstant.v(0);
        this.edgeB = IntConstant.v(0);
        this.edgeC = IntConstant.v(0);
        this.radios = radios;
    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction factoidsConjunction) {
        if(factoidsConjunction.isBottom()) {
            return FactoidsConjunction.getBottom();
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction(factoidsConjunction);
        result.update(new Factoid(sphereVariable, x0, y0, z0, edgeA, edgeB, edgeC, radios));

        return result;
    }
}
