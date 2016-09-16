package tau.verification.sphereInterval.chaoticIteration;

import soot.Body;
import soot.BooleanType;
import soot.Unit;
import soot.jimple.*;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.transformer.BaseTransformer;
import tau.verification.sphereInterval.transformer.TransformerSwitch;

import java.util.*;

public class EquationsSystemBuilder {
    private Body body;
    private WorkListItem entryWorkListItem = null;
    private UnitGraph unitGraph;
    private TransformerSwitch transformerSwitch;

    private Map<Unit, List<WorkListItem>> unitToInputWorkListItems = new HashMap<>();
    private Map<Unit, WorkListItem> unitToOutputWorkListItem = new HashMap<>();
    private Map<Unit, WorkListItem> ifStmtToAssumeFalseWorkListItem = new HashMap<>();
    private Map<Unit, WorkListItem> unitToJoinWorkListItem = new HashMap<>();

    private Map<Equation, Unit> equationToUnit = new HashMap<>();

    public EquationsSystemBuilder(Body body) {
        this.body = body;
        this.transformerSwitch = new TransformerSwitch();
        this.unitGraph = new ExceptionalUnitGraph(body);
    }

    public EquationSystem build() {
        generateWorkListItemGraph();
        return createEquations();
    }

    public String getEquationSystemBodyDescription() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Unit unit : this.body.getUnits()) {
            stringBuilder.append(getUnitDescription(unit));
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public Map<Equation, Unit> getEquationToUnit() {
        return this.equationToUnit;
    }

    private EquationSystem createEquations() {
        EquationSystem equationSystem = new EquationSystem();

        // Add an equation to initialize entry variable to top
        Equation setTopToEntryWorkListItem = new Equation(
                this.entryWorkListItem,
                new BaseTransformer(0 /* numberOfArguments */) {
                    @Override
                    public FactoidsConjunction invoke() {
                        return FactoidsConjunction.getTop();
                    }

                    @Override
                    public String invocationToString(List<WorkListItem> arguments) {
                        return "Top transformer";
                    }
                },
                "Entry work list item");
        equationSystem.addEquation(setTopToEntryWorkListItem);
        this.equationToUnit.put(setTopToEntryWorkListItem, this.unitGraph.getHeads().get(0));

        for (Unit unit : this.body.getUnits()) {
            List<WorkListItem> inputWorkListItems = this.unitToInputWorkListItems.get(unit);
            if (inputWorkListItems.size() == 2) {
                WorkListItem workListItem1 = inputWorkListItems.get(0);
                WorkListItem workListItem2 = inputWorkListItems.get(1);
                WorkListItem joinWorkListItem = this.unitToJoinWorkListItem.get(unit);
                BaseTransformer joinTransformer = new BaseTransformer(2 /* numberOfArguments */) {
                    @Override
                    public FactoidsConjunction invoke(FactoidsConjunction first, FactoidsConjunction second) {
                        return FactoidsConjunction.upperBound(first, second);
                    }

                    @Override
                    public String toString() {
                        return "Join";
                    }
                };

                Equation joinEquation = new Equation(joinWorkListItem, joinTransformer, workListItem1, workListItem2, getUnitDescription(unit));
                equationSystem.addEquation(joinEquation);
                this.equationToUnit.put(joinEquation, unit);
            }
            assert inputWorkListItems.size() > 2; //TODO: handle cases of more than two? (e.g. loop continue)

            WorkListItem inputWorkListItem = inputWorkListItems.size() == 1
                    ? inputWorkListItems.get(0)
                    : this.unitToJoinWorkListItem.get(unit);

            if (unit instanceof IfStmt) {
                IfStmt ifStmt = (IfStmt) unit;

                WorkListItem assumeTrueWorkListItem = this.unitToOutputWorkListItem.get(unit);
                BaseTransformer assumeTrueTransformer = this.transformerSwitch.getAssumeTransformer(this.getIfSphereVirtualInvokeExpr(ifStmt), true);

                Equation assumeTrueEquation = new Equation(assumeTrueWorkListItem, assumeTrueTransformer, inputWorkListItem, getUnitDescription(unit));
                equationSystem.addEquation(assumeTrueEquation);
                this.equationToUnit.put(assumeTrueEquation, unit);

                WorkListItem assumeFalseWorkListItem = this.ifStmtToAssumeFalseWorkListItem.get(ifStmt);
                BaseTransformer assumeFalseTransformer = this.transformerSwitch.getAssumeTransformer(this.getIfSphereVirtualInvokeExpr(ifStmt), false);

                Equation assumeFalseEquation = new Equation(assumeFalseWorkListItem, assumeFalseTransformer, inputWorkListItem, getUnitDescription(unit));
                equationSystem.addEquation(assumeFalseEquation);
                this.equationToUnit.put(assumeFalseEquation, unit);
            } else {
                WorkListItem lhsWorkListItem = this.unitToOutputWorkListItem.get(unit);
                BaseTransformer unitTransformer = this.transformerSwitch.getStatmentTransformer((Stmt) unit);
                Equation unitEquation = new Equation(lhsWorkListItem,unitTransformer, inputWorkListItem, getUnitDescription(unit));
                equationSystem.addEquation(unitEquation);
                this.equationToUnit.put(unitEquation, unit);
            }
        }

        return equationSystem;
    }

    private JVirtualInvokeExpr getIfSphereVirtualInvokeExpr(IfStmt ifStmt) {
        List<Unit> ifPreds = this.unitGraph.getPredsOf(ifStmt);
        if(ifPreds.size() != 1) {
            assert false; // we don't expect this case for our test files
            return null;
        }

        if(!(ifPreds.get(0) instanceof AssignStmt)){
            assert false; // we don't expect this case for our test files
            throw null;
        }

        AssignStmt assignStmt = (AssignStmt) ifPreds.get(0);

        if(!(assignStmt.getLeftOp() instanceof JimpleLocal)) {
            assert false; // we don't expect this case for our test files
            throw null;
        }

        JimpleLocal lhs = (JimpleLocal) assignStmt.getLeftOp();

        if(!(ifStmt.getCondition() instanceof EqExpr)) {
            assert false; // we don't expect this case for our test files
            throw null;
        }

        EqExpr expr = (EqExpr) ifStmt.getCondition();

        if(!(expr.getOp1() instanceof JimpleLocal)) {
            assert false; // we don't expect this case for our test files
            throw null;
        }

        if(!(expr.getOp1().equals(lhs))) {
            // making sure that the variable the if use is the same as the
            // variable we believe the Sphere conditional expression is assigned to
            assert false; // we don't expect this case for our test files
            throw null;
        }

        if(!(lhs.getType() instanceof BooleanType)) {
            assert false; // we don't expect this case for our test files
            throw null;
        }

        if(!(lhs.getName().startsWith("temp$"))) {
            assert false; // we don't expect this case for our test files
            throw null;
        }

        if(!(assignStmt.getRightOp() instanceof JVirtualInvokeExpr)) {
            assert false; // we don't expect this case for our test files
            throw null;
        }

        return (JVirtualInvokeExpr) assignStmt.getRightOp();
    }

    private String getUnitDescription(Unit unit) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("(");
        stringBuilder.append(Integer.toString(unit.hashCode()));
        stringBuilder.append(") ");
        stringBuilder.append(unit.toString());

        return stringBuilder.toString();
    }

    private void addInputWorkListItemToUnit(Unit unit, WorkListItem workListItem) {
        List<WorkListItem> inputWorkListItems = this.unitToInputWorkListItems.get(unit);
        if (inputWorkListItems == null) {
            inputWorkListItems = new ArrayList<>();
            this.unitToInputWorkListItems.put(unit, inputWorkListItems);
        }

        if(!inputWorkListItems.contains(workListItem)) {
            inputWorkListItems.add(workListItem);
        }
    }

    private void generateWorkListItemGraph() {
        this.entryWorkListItem = WorkListItem.getFreshWorkListItem();

        // Traverse the nodes of the unit graph, attach a single work list items for output of each unit and resolve all input units
        for (Unit unit : this.body.getUnits()) {
            WorkListItem outputWorkListItem = WorkListItem.getFreshWorkListItem();
            this.unitToOutputWorkListItem.put(unit, outputWorkListItem); // attach a work list item to the unit

            // 'if' statements need a work list item for the 'assume false' as well
            if (unit instanceof IfStmt) {
                IfStmt ifStmt = (IfStmt) unit;

                Unit trueTarget = ifStmt.getTarget();
                WorkListItem assumeTrueInputWorkListItem = outputWorkListItem;
                addInputWorkListItemToUnit(trueTarget, assumeTrueInputWorkListItem);

                ArrayList<Unit> successors = new ArrayList<>(this.unitGraph.getSuccsOf(unit));
                successors.remove(trueTarget);

                assert successors.size() == 1; //TODO: check an if() {} else if() {} else {} instance

                Unit falseTarget = successors.get(0);
                WorkListItem assumeFalseInputWorkListItem = WorkListItem.getFreshWorkListItem();
                this.ifStmtToAssumeFalseWorkListItem.put(unit, assumeFalseInputWorkListItem);
                addInputWorkListItemToUnit(falseTarget, assumeFalseInputWorkListItem);

            } else if (unit instanceof GotoStmt) {
                GotoStmt gotoStmt = (GotoStmt) unit;
                Unit target = gotoStmt.getTarget();
                addInputWorkListItemToUnit(target, outputWorkListItem);
            } else {
                List<Unit> successors = this.unitGraph.getSuccsOf(unit);
                for (Unit target : successors) {
                    addInputWorkListItemToUnit(target, outputWorkListItem);
                }
            }

            if (this.unitGraph.getPredsOf(unit).isEmpty()) {
                addInputWorkListItemToUnit(unit, this.entryWorkListItem);
            }

            // In a case of branching (loop, if) we need another variable to join predecessors
            if (this.unitGraph.getPredsOf(unit).size() > 1) {
                outputWorkListItem = WorkListItem.getFreshWorkListItem();
                this.unitToJoinWorkListItem.put(unit, outputWorkListItem);
            }
        }
    }
}