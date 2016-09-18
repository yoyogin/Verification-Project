package tau.verification.sphereInterval.transformer;

import soot.jimple.*;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.transformer.assume.AssumeIsRadiosLessOrEqualThanTransformer;
import tau.verification.sphereInterval.transformer.assume.AssumeSphereContainsTransformer;
import tau.verification.sphereInterval.transformer.assume.AssumeSphereIsContainedInTransformer;
import tau.verification.sphereInterval.transformer.statement.*;

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

        if (!(className.equals("Sphere"))) {
            return new IdTransformer();
        }

        JimpleLocal receiverVariable = (JimpleLocal) ifExpressionStmt.getBaseBox().getValue();
        List arguments = ifExpressionStmt.getArgs();

        if (arguments.size() != 1) {
            return new IdTransformer();
        }

        if ((arguments.get(0) instanceof JimpleLocal)) {
            JimpleLocal argumentVariable = (JimpleLocal) arguments.get(0);

            String methodName = ifExpressionStmt.getMethod().getName();
            if (methodName.equals("contains")) {
                return new AssumeSphereContainsTransformer(receiverVariable, argumentVariable, assumeValue);
            } else if (methodName.equals("isContainedIn")) {
                return new AssumeSphereIsContainedInTransformer(receiverVariable, argumentVariable, assumeValue);
            } else if (methodName.equals("isRadiosLessOrEqualThan")) {
                return new AssumeIsRadiosLessOrEqualThanTransformer(receiverVariable, argumentVariable, assumeValue);
            }
        } else if (arguments.get(0) instanceof IntConstant) {
            IntConstant argumentConstant = (IntConstant) arguments.get(0);

            String methodName = ifExpressionStmt.getMethod().getName();

            if (methodName.equals("isRadiosLessOrEqualThan")) {
                return new AssumeIsRadiosLessOrEqualThanTransformer(receiverVariable, argumentConstant, assumeValue);
            }
        }

        return new IdTransformer();
    }

    /**
     * Examples:
     * > specialinvoke temp$0.<Sphere: void <init>(int,int,int,int)>(0, 0, 0, 1)
     * > virtualinvoke this.<SphereIntervalTest: void error(java.lang.String)>("Cannot prove that x.contains(y)")
     */
    @Override
    public void caseInvokeStmt(InvokeStmt stmt) {
        assert transformer == null;

        String className = stmt.getInvokeExpr().getMethod().getDeclaringClass().toString();
        if (!(className.equals("Sphere"))) {
            return;
        }

        String methodName = stmt.getInvokeExpr().getMethod().getName();

        if (methodName.equals("<init>")) {
            sphereConstructorInvokeStmt(stmt);
        }

        return;
    }

    private void sphereConstructorInvokeStmt(InvokeStmt stmt) {
        if (!(stmt.getInvokeExpr() instanceof JSpecialInvokeExpr)) {
            return;
        }

        JSpecialInvokeExpr specialInvokeExpr = (JSpecialInvokeExpr) stmt.getInvokeExpr();

        JimpleLocal receiverVariable = (JimpleLocal) specialInvokeExpr.getBaseBox().getValue();

        List arguments = specialInvokeExpr.getArgs();
        if (arguments.size() == 4) {
            IntConstant x = (IntConstant) arguments.get(0);
            IntConstant y = (IntConstant) arguments.get(1);
            IntConstant z = (IntConstant) arguments.get(2);
            IntConstant radios = (IntConstant) arguments.get(3);

            transformer = new SphereConstructorTransformer(receiverVariable, x, y, z, radios);
        }
    }

    /**
     * We are interested in assignments in which the rhs is a sphere variable //TODO: should we make sure that the lhs is of type sphere?
     * <p>
     * Note: that for Sphere variable 'Assignment from constructor' we look for the accompanying constructor call //TODO is this a valid assumption?
     * <p>
     * Examples:
     * > temp$0 = new Sphere
     * > y = temp$1
     * > temp$2 = virtualinvoke x.<Sphere: boolean contains(Sphere)>(y)
     */
    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        assert transformer == null;

        if (!(stmt.getLeftOp() instanceof JimpleLocal)) {
            return;
        }

        JimpleLocal lhs = (JimpleLocal) stmt.getLeftOp();

        Class rhsClass = stmt.getRightOp().getClass();
        if (rhsClass.equals(JimpleLocal.class)) {
            assignLocalToLocalTransformerResolution(lhs, (JimpleLocal) stmt.getRightOp());
        } else if (rhsClass.equals(JVirtualInvokeExpr.class)) {
            assignVirtualInvokeToLocalTransformerResolution(lhs, (JVirtualInvokeExpr) stmt.getRightOp());
        }

        if (transformer == null) {
            // Best practice - lets forget it what we know about it
            transformer = new ForgetLocalTransformer(lhs);
        }
    }

    private void assignVirtualInvokeToLocalTransformerResolution(JimpleLocal lhs, JVirtualInvokeExpr rhs) {
        String className = rhs.getMethod().getDeclaringClass().toString();
        if (!(className.equals("Sphere"))) {
            return;
        }

        String methodName = rhs.getMethod().getName();

        if (methodName.equals("addRadios")) {
            sphereAddRadiosInvokeStmt(lhs, rhs);
        } else if (methodName.equals("addPoint")) {
            sphereAddPointInvokeStmt(lhs, rhs);
        }
    }

    private void sphereAddPointInvokeStmt(JimpleLocal lhs, JVirtualInvokeExpr virtualInvokeExpr) {
        JimpleLocal rhsVariable = (JimpleLocal) virtualInvokeExpr.getBaseBox().getValue();

        List arguments = virtualInvokeExpr.getArgs();
        if (arguments.size() != 3) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            if (!(arguments.get(i) instanceof IntConstant)) {
                return;
            }
        }

        IntConstant additionToX0 = (IntConstant) arguments.get(0);
        IntConstant additionToY0 = (IntConstant) arguments.get(1);
        IntConstant additionToZ0 = (IntConstant) arguments.get(2);
        transformer = new AssignLocalToSphereAddPointTransformer(lhs, rhsVariable, additionToX0, additionToY0, additionToZ0);
    }

    private void sphereAddRadiosInvokeStmt(JimpleLocal lhs, JVirtualInvokeExpr virtualInvokeExpr) {
        JimpleLocal rhsVariable = (JimpleLocal) virtualInvokeExpr.getBaseBox().getValue();

        List arguments = virtualInvokeExpr.getArgs();
        if (arguments.size() != 1) {
            return;
        }

        if (!(arguments.get(0) instanceof IntConstant)) {
            return;
        }

        IntConstant additionToRadios = (IntConstant) arguments.get(0);
        transformer = new AssignLocalToSphereAddRadiosTransformer(lhs, rhsVariable, additionToRadios);
    }

    private void assignLocalToLocalTransformerResolution(JimpleLocal lhs, JimpleLocal rhs) {
        if (lhs.equals(rhs)) {
            transformer = new IdTransformer();
        } else {
            transformer = new AssignLocalToLocalTransformer(lhs, rhs);
        }
    }
}
