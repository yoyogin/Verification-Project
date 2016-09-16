package tau.verification.sphereInterval.transformer;

import soot.Local;
import soot.jimple.*;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.function.IdTransformer;
import tau.verification.sphereInterval.function.TransformerFunction;

import java.util.List;

public class TransformerSwitch extends AbstractStmtSwitch {
    TransformerFunction transformer = null;

    public TransformerFunction getStatmentTransformer(Stmt stmt) {
        transformer = null;

        stmt.apply(this);
        if (transformer == null) {
            transformer = new IdTransformer();
        }

        return transformer;
    }

    public TransformerFunction getIfTransformer(IfStmt stmt, boolean trueOrFalse) {
        transformer = null;

        //matchAssume(stmt, trueOrFalse);
        if (transformer == null) {
            transformer = new IdTransformer();
        }

        return transformer;
    }

    /**
     *
     * Examples:
     * >
     */
    @Override
    public void caseBreakpointStmt(BreakpointStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * > specialinvoke temp$0.<Sphere: void <init>(int,int,int,int)>(0, 0, 0, 1)
     */
    @Override
    public void caseInvokeStmt(InvokeStmt stmt) {
        assert transformer == null;

        if(!(stmt.getInvokeExpr() instanceof JSpecialInvokeExpr)) {
            // TODO: decide what to do
            return;
        }

        JSpecialInvokeExpr specialInvokeExpr = (JSpecialInvokeExpr) stmt.getInvokeExpr();

        String className = specialInvokeExpr.getMethod().getDeclaringClass().toString();
        String methodName = specialInvokeExpr.getMethod().getName();

        if(!(className.equals("Sphere") && methodName.equals("<init>"))) {
            // TODO: decide what to do
            return;
        }

        JimpleLocal recieverVariable = (JimpleLocal) specialInvokeExpr.getBaseBox().getValue(); //TODO: do this without casting
        List arguments = specialInvokeExpr.getArgs();
        if(arguments.size() == 4) {
            IntConstant x = (IntConstant) arguments.get(0);
            IntConstant y = (IntConstant) arguments.get(1);
            IntConstant z = (IntConstant) arguments.get(2);
            IntConstant radios = (IntConstant) arguments.get(3);

            transformer = new SphereConstructorTransformer(recieverVariable, x, y, z, radios);
        }


        return;
    }

    /**
     * It seems that these are not of our interest.
     *
     * Note: that for Sphere variable 'Assignment from constructor' we look for the accompanying constructor call //TODO is this a valid assumption?
     *
     * Examples:
     * > temp$0 = new Sphere
     * >
     */
    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     * It seems that these are not of our interest
     * Examples:
     * > this := @this: SphereIntervalTest
     *
     */
    @Override
    public void caseIdentityStmt(IdentityStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * >
     */
    @Override
    public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * >
     */
    @Override
    public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * >
     */
    @Override
    public void caseGotoStmt(GotoStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * >
     */
    @Override
    public void caseIfStmt(IfStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * >
     */
    @Override
    public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * >
     */
    @Override
    public void caseNopStmt(NopStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * >
     */
    @Override
    public void caseRetStmt(RetStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * >
     */
    @Override
    public void caseReturnStmt(ReturnStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * >
     */
    @Override
    public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * >
     */
    @Override
    public void caseTableSwitchStmt(TableSwitchStmt stmt) {
        assert transformer == null;

        return;
    }

    /**
     *
     * Examples:
     * >
     */

    @Override
    public void caseThrowStmt(ThrowStmt stmt) {
        assert transformer == null;

        return;
    }
}