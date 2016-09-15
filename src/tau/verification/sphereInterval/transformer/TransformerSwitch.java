package tau.verification.sphereInterval.transformer;

import tau.verification.sphereInterval.function.IdTransformer;
import tau.verification.sphereInterval.function.TransformerFunction;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;

public class TransformerSwitch extends AbstractStmtSwitch {
    public TransformerFunction getStatmentTransformer(Stmt stmt) {
        return new IdTransformer();
    }

    public TransformerFunction getIfTransformer(IfStmt stmt, boolean trueOrFalse) {
        return new IdTransformer();
    }
}