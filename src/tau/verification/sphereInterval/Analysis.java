package tau.verification.sphereInterval;

import soot.*;
import soot.jimple.InvokeStmt;
import tau.verification.sphereInterval.chaoticIteration.ChaoticIteration;
import tau.verification.sphereInterval.chaoticIteration.EquationSystem;
import tau.verification.sphereInterval.chaoticIteration.EquationsSystemBuilder;
import tau.verification.sphereInterval.chaoticIteration.WorkListItem;
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
        this.ignoreMethodList = Arrays.asList(new String[] { "Success", "Error", "<init>", "addPoint", "addRadios", "setPoint", "setRadios", "isContainedIn", "contains" });
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

        System.out.println("\n>>>>> Report success and error for method '" + methodName + "' <<<<<");
        reportSuccessAndError(equationsSystemBuilder.getWorkListItemToUnit());
    }

    private void reportSuccessAndError(Map<WorkListItem, Unit> equationToUnit) {
        Collection<String> successAndErrorMessages = new HashSet<>();

        for (Map.Entry<WorkListItem, Unit> entry : equationToUnit.entrySet()) {
            WorkListItem workList = entry.getKey();
            Unit unit = entry.getValue();

            if (unit instanceof InvokeStmt) {
                InvokeStmt invokeStmt = (InvokeStmt) unit;
                boolean isInvocationReachable = workList.value.evaluateConjunction();
                String methodName = invokeStmt.getInvokeExpr().getMethod().getName();
                boolean isSuccessInvocation = methodName.equals("Success");
                boolean isErrorInvocation = methodName.equals("Error");

                if (!(isInvocationReachable && (isSuccessInvocation || isErrorInvocation))) {
                    continue;
                }

                StringBuilder stringBuilder = new StringBuilder(methodName + " ");
                stringBuilder.append(invokeStmt.getInvokeExpr().getArg(0));
                successAndErrorMessages.add(stringBuilder.toString());
            }
        }

        System.out.println("Found " + successAndErrorMessages.size() + " Success and Error invocations");
        if (!successAndErrorMessages.isEmpty()) {
            System.out.println(StringUtils.collectionWithSeparatorToString(successAndErrorMessages, "\n"));
        }

        System.out.println("\n");
    }
}