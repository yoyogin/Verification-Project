package tau.verification.sphereInterval.transformer;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.FactoidsConjunction;

public class SphereConstructorTransformer extends BaseTransformer {

    public final JimpleLocal sphereVariable;
    public final JimpleLocal sphereOtherVariablePointer;
    public final JimpleLocal sphereOtherVariableRadios;
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
            IntConstant edgeA,
            IntConstant edgeB,
            IntConstant edgeC,
            IntConstant radios) {
        super(1 /* numberOfArguments */);

        this.sphereVariable = sphereVariable;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.edgeA = edgeA;
        this.edgeB = edgeB;
        this.edgeC = edgeC;
        this.radios = radios;
        this.sphereOtherVariablePointer = null;
        this.sphereOtherVariableRadios = null;

        assert sphereVariable != null;
        assert x0 != null;
        assert y0 != null;
        assert z0 != null;
        assert edgeA != null;
        assert edgeB != null;
        assert edgeC != null;
        assert radios != null;
    }

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

        this.sphereOtherVariablePointer = null;
        this.sphereOtherVariableRadios = null;

        assert sphereVariable != null;
        assert x0 != null;
        assert y0 != null;
        assert z0 != null;
        assert radios != null;
    }

    public SphereConstructorTransformer(
            JimpleLocal sphereVariable,
            JimpleLocal sphereOtherVariablePointer,
            JimpleLocal sphereOtherVariableRadios) {
        super(1 /* numberOfArguments */);

        this.sphereVariable = sphereVariable;
        this.sphereOtherVariableRadios = sphereOtherVariableRadios;
        this.sphereOtherVariablePointer =sphereOtherVariablePointer;

        this.x0 = null;
        this.y0 = null;
        this.z0 = null;
        this.edgeA = null;
        this.edgeB = null;
        this.edgeC = null;
        this.radios = null;

    }

    public SphereConstructorTransformer(
            JimpleLocal sphereVariable,
            JimpleLocal sphereOtherVariablePointer,
            IntConstant radios) {
        super(1 /* numberOfArguments */);

        this.sphereVariable = sphereVariable;
        this.sphereOtherVariableRadios = null;
        this.sphereOtherVariablePointer =sphereOtherVariablePointer;

        this.x0 = null;
        this.y0 = null;
        this.z0 = null;
        this.edgeA = null;
        this.edgeB = null;
        this.edgeC = null;
        this.radios = radios;

    }

    public SphereConstructorTransformer(
            JimpleLocal sphereVariable,
            JimpleLocal sphereOtherVariableRadios,
            IntConstant x0,
            IntConstant y0,
            IntConstant z0,
            IntConstant edgeA,
            IntConstant edgeB,
            IntConstant edgeC) {
        super(1 /* numberOfArguments */);

        this.sphereVariable = sphereVariable;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.edgeA = edgeA;
        this.edgeB = edgeB;
        this.edgeC = edgeC;
        this.radios = null;
        this.sphereOtherVariablePointer = null;
        this.sphereOtherVariableRadios = sphereOtherVariableRadios;

    }

    @Override
    public FactoidsConjunction invoke(FactoidsConjunction input) {
        return FactoidsConjunction
                .getFactoidsConjunction(input)
                .setFactoid(sphereVariable, x0, y0, z0, edgeA, edgeB, edgeC, radios);
    }
}
