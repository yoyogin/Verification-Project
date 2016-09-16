package tau.verification.sphereInterval.transformer.assume;

import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.transformer.BaseTransformer;

/**
 * A transformer for assume statements.
 */
public class AssumeSphereBaseTransformer extends BaseTransformer {
    public final boolean assumeValue;

    public AssumeSphereBaseTransformer(
            JimpleLocal receiverVariable,
            JimpleLocal argumentVariable,
            boolean assumeValue) {
        super(1 /* numberOfArguments */);

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