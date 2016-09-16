package tau.verification.sphereInterval.transformer;

import soot.Value;
import soot.jimple.*;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JimpleLocal;

import java.util.List;

public class TransformerSwitch extends AbstractStmtSwitch {
    BaseTransformer transformer = null;

    public BaseTransformer getStatmentTransformer(Stmt stmt) {
        transformer = null;

        stmt.apply(this);
        if (transformer == null) {
            transformer = new IdTransformer();
        }

        return transformer;
    }

    public BaseTransformer getIfTransformer(IfStmt stmt, boolean trueOrFalse) {
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
     * > virtualinvoke this.<SphereIntervalTest: void error(java.lang.String)>("Cannot prove that x.contains(y)")
     */
    @Override
    public void caseInvokeStmt(InvokeStmt stmt) {
        assert transformer == null;

        if(!(stmt.getInvokeExpr() instanceof JSpecialInvokeExpr)) {
            return;
        }

        JSpecialInvokeExpr specialInvokeExpr = (JSpecialInvokeExpr) stmt.getInvokeExpr();

        String className = specialInvokeExpr.getMethod().getDeclaringClass().toString();
        String methodName = specialInvokeExpr.getMethod().getName();

        if(!(className.equals("Sphere") && methodName.equals("<init>"))) {
            return;
        }

        JimpleLocal recieverVariable = (JimpleLocal) specialInvokeExpr.getBaseBox().getValue(); //TODO: do this without casting
        List arguments = specialInvokeExpr.getArgs();
        if(arguments.size() == 4) {
            //TODO: is it right to assume that all four would be IntConstants?
            IntConstant x = (IntConstant) arguments.get(0);
            IntConstant y = (IntConstant) arguments.get(1);
            IntConstant z = (IntConstant) arguments.get(2);
            IntConstant radios = (IntConstant) arguments.get(3);

            transformer = new SphereConstructorTransformer(recieverVariable, x, y, z, radios);
        }


        return;
    }

    /**
     * We are interested in assignments in which the rhs is a sphere variable //TODO: should we make sure that the lhs is of type sphere?
     *
     * Note: that for Sphere variable 'Assignment from constructor' we look for the accompanying constructor call //TODO is this a valid assumption?
     *
     * Examples:
     * > temp$0 = new Sphere
     * > y = temp$1
     */
    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        assert transformer == null;

        Value lhs = stmt.getLeftOp();
        if(!(lhs instanceof JimpleLocal)) {
            return;
        }

        Value rhs = stmt.getRightOp();
        if (!(rhs instanceof JimpleLocal)) {
            transformer = new ForgetLocalTransformer((JimpleLocal) lhs);
            return;
        }

        if(lhs.equals(rhs)) {
            transformer = new IdTransformer();
        } else {
            transformer = new AssignLocalToLocalTransformer((JimpleLocal) lhs, (JimpleLocal) rhs);
        }

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