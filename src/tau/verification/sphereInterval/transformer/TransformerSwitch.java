package tau.verification.sphereInterval.transformer;

import soot.jimple.*;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.transformer.assume.AssumeSphereContainsTransformer;
import tau.verification.sphereInterval.transformer.assume.AssumeSphereIsContainedTransformer;
import tau.verification.sphereInterval.transformer.statement.AssignLocalToLocalTransformer;
import tau.verification.sphereInterval.transformer.statement.ForgetLocalTransformer;
import tau.verification.sphereInterval.transformer.statement.IdTransformer;
import tau.verification.sphereInterval.transformer.statement.SphereConstructorTransformer;

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

    public BaseTransformer getAssumeTransformer(JVirtualInvokeExpr ifExpressionStmt, boolean assumeValue) {
        transformer = null;

        String className = ifExpressionStmt.getMethod().getDeclaringClass().toString();

        if(!(className.equals("Sphere"))) {
            return new IdTransformer();
        }

        JimpleLocal receiverVariable = (JimpleLocal) ifExpressionStmt.getBaseBox().getValue();
        List arguments = ifExpressionStmt.getArgs();

        if(arguments.size() != 1) {
            return new IdTransformer();
        }

        if(!(arguments.get(0) instanceof JimpleLocal)) {
            return new IdTransformer();
        }

        JimpleLocal argumentVariable = (JimpleLocal) arguments.get(0);

        String methodName = ifExpressionStmt.getMethod().getName();
        if(methodName.equals("contains")) {
            return new AssumeSphereContainsTransformer(receiverVariable, argumentVariable, assumeValue);
        } else if (methodName.equals("isContained")) {
            return new AssumeSphereIsContainedTransformer(receiverVariable, argumentVariable, assumeValue);
        } else {
            return new IdTransformer();
        }
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

        JimpleLocal recieverVariable = (JimpleLocal) specialInvokeExpr.getBaseBox().getValue();
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
     * We are interested in assignments in which the rhs is a sphere variable //TODO: should we make sure that the lhs is of type sphere?
     *
     * Note: that for Sphere variable 'Assignment from constructor' we look for the accompanying constructor call //TODO is this a valid assumption?
     *
     * Examples:
     * > temp$0 = new Sphere
     * > y = temp$1
     * > temp$2 = virtualinvoke x.<Sphere: boolean contains(Sphere)>(y)
     */
    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        assert transformer == null;

        if(!(stmt.getLeftOp() instanceof JimpleLocal)) {
            return;
        }

        JimpleLocal lhs = (JimpleLocal) stmt.getLeftOp();

        Class rhsClass = stmt.getRightOp().getClass();
        if (rhsClass.equals(JimpleLocal.class)) {
            assignLocalToLocalTransformerResolution(lhs, (JimpleLocal) stmt.getRightOp());
        } else {
            // Best practice - lets forget it what we know about it
            transformer = new ForgetLocalTransformer(lhs);
        }
    }

    private void assignLocalToLocalTransformerResolution(JimpleLocal lhs, JimpleLocal rhs) {
        if(lhs.equals(rhs)) {
            transformer = new IdTransformer();
        } else {
            transformer = new AssignLocalToLocalTransformer(lhs, rhs);
        }
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