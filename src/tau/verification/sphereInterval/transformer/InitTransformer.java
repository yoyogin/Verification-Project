package tau.verification.sphereInterval.transformer;

import soot.Local;
import soot.jimple.IntConstant;
import soot.jimple.NumericConstant;
import tau.verification.sphereInterval.FactoidsConjunction;
import tau.verification.sphereInterval.function.TransformerFunction;

public class InitTransformer extends TransformerFunction {

    public final Local sphereVariable;
    public final Local sphereOtherVariablePointer;
    public final Local sphereOtherVariableRadios;
    public final NumericConstant x0;
    public final NumericConstant y0;
    public final NumericConstant z0;
    public final NumericConstant edgeA;
    public final NumericConstant edgeB;
    public final NumericConstant edgeC;
    public final NumericConstant radios;

    public InitTransformer(
            Local sphereVariable,
            NumericConstant x0,
            NumericConstant y0,
            NumericConstant z0,
            NumericConstant edgeA,
            NumericConstant edgeB,
            NumericConstant edgeC,
            NumericConstant radios) {
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

    public InitTransformer(
            Local sphereVariable,
            NumericConstant x0,
            NumericConstant y0,
            NumericConstant z0,
            NumericConstant radios) {
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

    public InitTransformer(
            Local sphereVariable,
            Local sphereOtherVariablePointer,
            Local sphereOtherVariableRadios)
    {
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

    public InitTransformer(
            Local sphereVariable,
            Local sphereOtherVariablePointer,
            NumericConstant radios)
    {
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

    public InitTransformer(
            Local sphereVariable,
            Local sphereOtherVariableRadios,
            NumericConstant x0,
            NumericConstant y0,
            NumericConstant z0,
            NumericConstant edgeA,
            NumericConstant edgeB,
            NumericConstant edgeC) {
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
