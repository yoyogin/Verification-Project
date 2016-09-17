package tau.verification.sphereInterval.transformer.assume;

import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.transformer.BaseTransformer;

/**
 * A transformer for assume statements.
 */
public class AssumeSphereBaseTransformer extends BaseTransformer {
    public final boolean assumeContains;

    public AssumeSphereBaseTransformer(boolean assumeContains) {
        super(1 /* numberOfArguments */);

        this.assumeContains = assumeContains;
    }

    protected String getDecoratedAssumeString(String assumeExpressionDescription) {
        if (this.assumeContains) {
            return assumeExpressionDescription;
        } else {
            return "!" + assumeExpressionDescription;
        }
    }
}