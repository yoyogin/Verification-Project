package tau.verification.sphereInterval;

import tau.verification.sphereInterval.chaoticIteration.EquationsSystemBuilder;
import tau.verification.sphereInterval.chaoticIteration.ChaoticIteration;
import tau.verification.sphereInterval.chaoticIteration.EquationSystem;
import tau.verification.sphereInterval.chaoticIteration.Equation;
import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.InvokeStmt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class Analysis extends BodyTransformer {
    private Domain domain;

    public Analysis() {
        this.domain = new Domain();
    }

    @Override
    protected void internalTransform(Body body, String phaseName, Map options) {
        System.out.println(">>>>> Analyzing method '" + body.getMethod().getName() + "' <<<<<");

        System.out.println("\nBuilding Equation System from '" + body.getMethod().getName() + "' body");
        EquationsSystemBuilder equationsSystemBuilder = new EquationsSystemBuilder(body, domain);
        EquationSystem equationSystem = equationsSystemBuilder.build();

        System.out.println("Running Chaotic Iteration on equation system");
        ChaoticIteration chaoticIteration = new ChaoticIteration();
        chaoticIteration.iterate(equationSystem, domain);

        System.out.println("\n>>>>> Error report for method '" + body.getMethod().getName() + "' <<<<<\n\n");
        reportErrors(equationsSystemBuilder.getEquationToUnit());
    }

    private void reportErrors(Map<Equation, Unit> equationToUnit) {
        Collection<Unit> errors = new HashSet<>();

        for (Map.Entry<Equation, Unit> entry : equationToUnit.entrySet()) {
            Equation equation = entry.getKey();
            Unit unit = entry.getValue();

            if (unit instanceof InvokeStmt) {
                InvokeStmt invokeStmt = (InvokeStmt) unit;
                boolean isInvocationReachable = !equation.getLhsWorkListItem().value.equals(domain.getBottom());
                boolean isErrorInvocation = invokeStmt.getInvokeExpr().getMethod().getName().equals("error");

                if (isInvocationReachable && isErrorInvocation) {
                    errors.add(unit);
                }
            }
        }

        System.out.println("Found " + errors.size() + " errors");
        if (!errors.isEmpty()) {
            System.out.println(errors); //TODO: pretty print
        }
    }
}