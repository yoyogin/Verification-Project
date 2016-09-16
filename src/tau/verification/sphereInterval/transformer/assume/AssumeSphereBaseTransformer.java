package tau.verification.sphereInterval.transformer.assume;

import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.transformer.BaseTransformer;

/**
 * A transformer for assume statements.
 */
public class AssumeSphereBaseTransformer extends BaseTransformer {
    public final JimpleLocal receiverVariable;
    public final JimpleLocal argumentVariable;
    public final boolean assumeValue;

    public AssumeSphereBaseTransformer(
            JimpleLocal receiverVariable,
            JimpleLocal argumentVariable,
            boolean assumeValue) {
        super(1 /* numberOfArguments */);

        this.receiverVariable = receiverVariable;
        this.argumentVariable = argumentVariable;
        this.assumeValue = assumeValue;
    }

    protected String getDecoratedAssumeString(String assumeExpressionDescription) {
        if (this.assumeValue) {
            return assumeExpressionDescription;
        } else {
            return "!" + assumeExpressionDescription;
        }
    }
}