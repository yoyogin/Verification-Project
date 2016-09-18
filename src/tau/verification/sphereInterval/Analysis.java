package tau.verification.sphereInterval;

import soot.*;
import soot.jimple.InvokeStmt;
import tau.verification.sphereInterval.chaoticIteration.ChaoticIteration;
import tau.verification.sphereInterval.chaoticIteration.EquationSystem;
import tau.verification.sphereInterval.chaoticIteration.EquationsSystemBuilder;
import tau.verification.sphereInterval.chaoticIteration.WorkListItem;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.transformer.BaseTransformer;
import tau.verification.sphereInterval.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class Analysis extends BodyTransformer {
    public static final boolean IS_WIDENING_NARROWING_OPTIMIZATION = true;

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
        this.ignoreMethodList = Arrays.asList(new String[] { "Note", "Success", "Error", "<init>", "addPoint", "addRadios", "setPoint", "setRadios", "isContainedIn", "contains","isRadiosLessOrEqualThan" });
    }

    @Override
    protected void internalTransform(Body body, String phaseName, Map options) {
        String methodName = body.getMethod().getName();
        if(ignoreMethodList.contains(methodName)){
            return;
        }

        if(IS_WIDENING_NARROWING_OPTIMIZATION)
        {
            System.out.println(">>>>> Analyzing method with widening/narrowing optimization'" + methodName + "' <<<<<");

            System.out.println("\nBuilding Equation System from '" + methodName + "' body");
            EquationsSystemBuilder equationsSystemBuilder = new EquationsSystemBuilder(body);
            System.out.println("Equation system body = \n" + equationsSystemBuilder.getEquationSystemBodyDescription());
            EquationSystem optimizingEquationSystem = equationsSystemBuilder.build();



            System.out.println("Running Widening Chaotic Iteration on equation system");
            ChaoticIteration chaoticIteration = new ChaoticIteration();
            chaoticIteration.iterate(optimizingEquationSystem,true);

            optimizingEquationSystem.getOptimizingEquation().updateTransformer(new BaseTransformer(2) {
                @Override
                public FactoidsConjunction invoke(FactoidsConjunction firstFactoidsConjunction, FactoidsConjunction secondFactoidsConjunction) {
                    return FactoidsConjunction.narrow(firstFactoidsConjunction, secondFactoidsConjunction);
                }
                @Override
                public String toString() {
                    return "Narrowing";
                }
            });

            System.out.println("Running Narrowing Chaotic Iteration on equation system");
            chaoticIteration.iterate(optimizingEquationSystem,false);

            System.out.println("\n>>>>> Report for method '" + methodName + "' <<<<<");
            printReport(equationsSystemBuilder.getWorkListItemToUnit());

        }else{

            System.out.println(">>>>> Analyzing method '" + methodName + "' <<<<<");

            System.out.println("\nBuilding Equation System from '" + methodName + "' body");
            EquationsSystemBuilder equationsSystemBuilder = new EquationsSystemBuilder(body);
            System.out.println("Equation system body = \n" + equationsSystemBuilder.getEquationSystemBodyDescription());
            EquationSystem equationSystem = equationsSystemBuilder.build();

            System.out.println("Running Chaotic Iteration on equation system");
            ChaoticIteration chaoticIteration = new ChaoticIteration();
            chaoticIteration.iterate(equationSystem);

            System.out.println("\n>>>>> Report for method '" + methodName + "' <<<<<");
            printReport(equationsSystemBuilder.getWorkListItemToUnit());
        }

    }

    private void printReport(Map<WorkListItem, Unit> equationToUnit) {
        Collection<String> messages = new HashSet<>();

        for (Map.Entry<WorkListItem, Unit> entry : equationToUnit.entrySet()) {
            WorkListItem workList = entry.getKey();
            Unit unit = entry.getValue();

            if (unit instanceof InvokeStmt) {
                InvokeStmt invokeStmt = (InvokeStmt) unit;
                boolean isInvocationReachable = workList.value.evaluateConjunction();
                String methodName = invokeStmt.getInvokeExpr().getMethod().getName();
                boolean isNoteInvocation = methodName.equals("Note");
                boolean isSuccessInvocation = methodName.equals("Success");
                boolean isErrorInvocation = methodName.equals("Error");

                if (!(isInvocationReachable && (isSuccessInvocation || isErrorInvocation || isNoteInvocation))) {
                    continue;
                }

                StringBuilder stringBuilder = new StringBuilder(methodName + " ");
                stringBuilder.append(invokeStmt.getInvokeExpr().getArg(0));
                messages.add(stringBuilder.toString());
            }
        }

        System.out.println("Found " + messages.size() + " messages");
        if (!messages.isEmpty()) {
            System.out.println(StringUtils.collectionWithSeparatorToString(messages, "\n"));
        }

        System.out.println("\n");
    }
}