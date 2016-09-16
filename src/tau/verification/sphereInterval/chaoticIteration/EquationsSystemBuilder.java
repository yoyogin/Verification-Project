package tau.verification.sphereInterval.chaoticIteration;

import soot.Body;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import tau.verification.sphereInterval.lattice.LatticeOperations;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;
import tau.verification.sphereInterval.transformer.BaseTransformer;
import tau.verification.sphereInterval.transformer.TransformerSwitch;

import java.util.*;

public class EquationsSystemBuilder {
    private Body body;
    private LatticeOperations latticeOperations;
    private WorkListItem entryWorkListItem = null;
    private UnitGraph unitGraph;
    private TransformerSwitch transformerSwitch;

    private Map<Unit, List<WorkListItem>> unitToInputWorkListItems = new HashMap<>();
    private Map<Unit, WorkListItem> unitToOutputWorkListItem = new HashMap<>();
    private Map<Unit, WorkListItem> ifStmtToAssumeFalseWorkListItem = new HashMap<>();
    private Map<Unit, WorkListItem> unitToLoopJoinWorkListItem = new HashMap<>();

    private Map<Equation, Unit> equationToUnit = new HashMap<>();
    private Set<Unit> loopHeads = new HashSet<>();

    public EquationsSystemBuilder(Body body, LatticeOperations latticeOperations) {
        this.body = body;
        this.latticeOperations = latticeOperations;
        this.transformerSwitch = new TransformerSwitch();
        this.unitGraph = new ExceptionalUnitGraph(body);
    }

    public EquationSystem build() {
        findAllLoopHeads();
        generateWorkListItemsBasedOnUnitGraph();

        return createEquations();
    }

    public String getEquationSystemBodyDescription() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Unit unit : body.getUnits()) {
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
                        return latticeOperations.getTop();
                    }

                    @Override
                    public String invocationToString(List<WorkListItem> arguments) {
                        return "Get Top function";
                    }
                },
                "Entry Work List Item");
        equationSystem.addEquation(setTopToEntryWorkListItem);
        this.equationToUnit.put(setTopToEntryWorkListItem, unitGraph.getHeads().get(0));

        for (Unit unit : body.getUnits()) {
            List<WorkListItem> inputWorkListItems = unitToInputWorkListItems.get(unit);
            if (inputWorkListItems.size() == 2) {
                WorkListItem workListItem1 = inputWorkListItems.get(0);
                WorkListItem workListItem2 = inputWorkListItems.get(1);
                WorkListItem joinWorkListItem = unitToLoopJoinWorkListItem.get(unit);
                BaseTransformer joinTransformer = new BaseTransformer(2 /* numberOfArguments */) {
                    @Override
                    public FactoidsConjunction invoke(FactoidsConjunction first, FactoidsConjunction second) {
                        return latticeOperations.upperBound(first, second);
                    }

                    @Override
                    public String toString() {
                        return "Join BaseTransformer";
                    }
                };

                Equation joinEquation = new Equation(joinWorkListItem, joinTransformer, workListItem1, workListItem2, getUnitDescription(unit));
                equationSystem.addEquation(joinEquation);
                equationToUnit.put(joinEquation, unit);
            }
            //TODO: handle cases of more than two? (e.g. loop continue)

            WorkListItem inputWorkListItem = inputWorkListItems.size() == 1
                    ? inputWorkListItems.get(0)
                    : unitToLoopJoinWorkListItem.get(unit);

            if (unit instanceof IfStmt) {
                IfStmt ifStmt = (IfStmt) unit;

                WorkListItem assumeTrueWorkListItem = unitToOutputWorkListItem.get(unit);
                BaseTransformer assumeTrueTransformer = transformerSwitch.getIfTransformer(ifStmt, true);

                Equation assumeTrueEquation = new Equation(assumeTrueWorkListItem, assumeTrueTransformer, inputWorkListItem, getUnitDescription(unit));
                equationSystem.addEquation(assumeTrueEquation);
                equationToUnit.put(assumeTrueEquation, unit);

                WorkListItem assumeFalseWorkListItem = ifStmtToAssumeFalseWorkListItem.get(ifStmt);
                BaseTransformer assumeFalseTransformer = transformerSwitch.getIfTransformer(ifStmt, false);

                Equation assumeFalseEquation = new Equation(assumeFalseWorkListItem, assumeFalseTransformer, inputWorkListItem, getUnitDescription(unit));
                equationSystem.addEquation(assumeFalseEquation);
                equationToUnit.put(assumeFalseEquation, unit);
            } else {
                WorkListItem lhsVar = unitToOutputWorkListItem.get(unit);
                BaseTransformer unitTransformer = transformerSwitch.getStatmentTransformer((Stmt) unit);
                Equation unitEquation = new Equation(lhsVar,unitTransformer, inputWorkListItem, getUnitDescription(unit));
                equationSystem.addEquation(unitEquation);
                equationToUnit.put(unitEquation, unit);
            }
        }

        return equationSystem;
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
        List<WorkListItem> inputWorkListItems = unitToInputWorkListItems.get(unit);
        if (inputWorkListItems == null) {
            inputWorkListItems = new ArrayList<>();
            unitToInputWorkListItems.put(unit, inputWorkListItems);
        }

        if(!inputWorkListItems.contains(workListItem)) {
            inputWorkListItems.add(workListItem);
        }
    }

    private void findAllLoopHeads() {
        LoopFinder loopFinder = new LoopFinder();
        loopFinder.transform(body);
        Collection<Loop> loops = loopFinder.loops();
        for (Loop loop : loops) {
            loopHeads.add(loop.getHead());
        }
    }

    private void generateWorkListItemsBasedOnUnitGraph() {
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
                List<Unit> successors = unitGraph.getSuccsOf(unit);
                for (Unit target : successors) {
                    addInputWorkListItemToUnit(target, outputWorkListItem);
                }
            }

            if (unitGraph.getPredsOf(unit).isEmpty()) {
                addInputWorkListItemToUnit(unit, entryWorkListItem);
            }

            // In a case of a loop we need another variable to join predecessors
            if (unitGraph.getPredsOf(unit).size() > 1) {
                outputWorkListItem = WorkListItem.getFreshWorkListItem();
                unitToLoopJoinWorkListItem.put(unit, outputWorkListItem);
            }
        }
    }
}