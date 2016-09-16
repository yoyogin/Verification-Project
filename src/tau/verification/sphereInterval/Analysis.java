package tau.verification.sphereInterval;

import soot.*;
import soot.jimple.InvokeStmt;
import tau.verification.sphereInterval.chaoticIteration.ChaoticIteration;
import tau.verification.sphereInterval.chaoticIteration.Equation;
import tau.verification.sphereInterval.chaoticIteration.EquationSystem;
import tau.verification.sphereInterval.chaoticIteration.EquationsSystemBuilder;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class Analysis extends BodyTransformer {
    private Collection<String> ignoreMethodList;

    public static void main(String[] args) {
        PackManager
                .v()
                .getPack("jtp")
                .add(new Transform("jtp.Analysis",
                        new Analysis()));

        soot.Main.main(args);
    }

    public Analysis() {
        this.ignoreMethodList = Arrays.asList(new String[] { "<init>", "addPoint", "addRadios", "setPoint", "setRadios", "isContained", "contains", "error" });
    }

    @Override
    protected void internalTransform(Body body, String phaseName, Map options) {
        String methodName = body.getMethod().getName();
        if(ignoreMethodList.contains(methodName)){
            return;
        }

        System.out.println(">>>>> Analyzing method '" + methodName + "' <<<<<");

        System.out.println("\nBuilding Equation System from '" + methodName + "' body");
        EquationsSystemBuilder equationsSystemBuilder = new EquationsSystemBuilder(body);
        System.out.println("Equation system body = \n" + equationsSystemBuilder.getEquationSystemBodyDescription());
        EquationSystem equationSystem = equationsSystemBuilder.build();

        System.out.println("Running Chaotic Iteration on equation system");
        ChaoticIteration chaoticIteration = new ChaoticIteration();
        chaoticIteration.iterate(equationSystem);

        System.out.println("\n>>>>> Error report for method '" + methodName + "' <<<<<\n\n");
        reportErrors(equationsSystemBuilder.getEquationToUnit());
    }

    private void reportErrors(Map<Equation, Unit> equationToUnit) {
        Collection<Unit> errors = new HashSet<>();

        for (Map.Entry<Equation, Unit> entry : equationToUnit.entrySet()) {
            Equation equation = entry.getKey();
            Unit unit = entry.getValue();

            if (unit instanceof InvokeStmt) {
                InvokeStmt invokeStmt = (InvokeStmt) unit;
                boolean isInvocationReachable = !equation.getLhsWorkListItem().value.equals(FactoidsConjunction.getBottom());
                boolean isErrorInvocation = invokeStmt.getInvokeExpr().getMethod().getName().equals("error");

                if (isInvocationReachable && isErrorInvocation) {
                    errors.add(unit);
                }
            }
        }

        System.out.println("Found " + errors.size() + " errors");
        if (!errors.isEmpty()) {
            System.out.println(StringUtils.collectionWithSeparatorToString(errors, "\n"));
        }
    }
}