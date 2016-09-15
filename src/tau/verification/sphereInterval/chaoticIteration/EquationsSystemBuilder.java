package tau.verification.sphereInterval.chaoticIteration;

import tau.verification.sphereInterval.chaoticIteration.Equation;
import tau.verification.sphereInterval.function.Function;
import tau.verification.sphereInterval.function.TransformerFunction;
import tau.verification.sphereInterval.Domain;
import tau.verification.sphereInterval.FactoidsConjunction;
import tau.verification.sphereInterval.chaoticIteration.EquationSystem;
import tau.verification.sphereInterval.chaoticIteration.WorkListItem;
import soot.Body;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import tau.verification.sphereInterval.transformer.TransformerSwitch;

import java.util.*;

public class EquationsSystemBuilder {
    private Body body;
    private Domain domain;
    private WorkListItem entryWorkListItem = null;
    private UnitGraph unitGraph;
    private TransformerSwitch transformerSwitch;

    private Map<Unit, List<WorkListItem>> unitToInputWorkListItems = new HashMap<>();
    private Map<Unit, WorkListItem> unitToOutputWorkListItem = new HashMap<>();
    private Map<Unit, WorkListItem> ifStmtToAssumeFalseWorkListItem = new HashMap<>();
    private Map<Unit, WorkListItem> unitToLoopJoinWorkListItem = new HashMap<>();

    private Map<Equation, Unit> equationToUnit = new HashMap<>();
    private Set<Unit> loopHeads = new HashSet<>();

    public EquationsSystemBuilder(Body body, Domain domain) {
        this.body = body;
        this.domain = domain;
        this.transformerSwitch = new TransformerSwitch();
        this.unitGraph = new ExceptionalUnitGraph(body);
    }

    public EquationSystem build() {
        findAllLoopHeads();
        generateWorkListItemsBasedOnUnitGraph();

        return createEquations();
    }

    public Map<Equation, Unit> getEquationToUnit() {
        return this.equationToUnit;
    }

    private EquationSystem createEquations() {
        EquationSystem equationSystem = new EquationSystem();

        // Add an equation to initialize the entry variable to top.
        Equation setTopToEntryWorkListItem = new Equation(this.entryWorkListItem, this.domain.getTopOperation(), "Entry Work List Item");
        equationSystem.addEquation(setTopToEntryWorkListItem);
        this.equationToUnit.put(setTopToEntryWorkListItem, unitGraph.getHeads().get(0));

        for (Unit unit : body.getUnits()) {
            List<WorkListItem> inputWorkListItems = unitToInputWorkListItems.get(unit);
            if (inputWorkListItems.size() == 2) {
                WorkListItem workListItem1 = inputWorkListItems.get(0);
                WorkListItem workListItem2 = inputWorkListItems.get(1);
                WorkListItem joinWorkListItem = unitToLoopJoinWorkListItem.get(unit);
                Function joinFunction = new Function() {
                    @Override
                    public byte arguments() {
                        return 2;
                    }

                    @Override
                    public FactoidsConjunction invoke(FactoidsConjunction first, FactoidsConjunction second) {
                        return domain.upperBound(first, second);
                    }

                    @Override
                    public String toString() {
                        return "Join Function";
                    }
                };

                Equation joinEquation = new Equation(joinWorkListItem, joinFunction, workListItem1, workListItem2, unit.toString());
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
                TransformerFunction assumeTrueTransformer = transformerSwitch.getIfTransformer(ifStmt, true);

                Equation assumeTrueEquation = new Equation(assumeTrueWorkListItem, assumeTrueTransformer, inputWorkListItem, unit.toString());
                equationSystem.addEquation(assumeTrueEquation);
                equationToUnit.put(assumeTrueEquation, unit);

                WorkListItem assumeFalseWorkListItem = ifStmtToAssumeFalseWorkListItem.get(ifStmt);
                TransformerFunction assumeFalseTransformer = transformerSwitch.getIfTransformer(ifStmt, false);

                Equation assumeFalseEquation = new Equation(assumeFalseWorkListItem, assumeFalseTransformer, inputWorkListItem, unit.toString());
                equationSystem.addEquation(assumeFalseEquation);
                equationToUnit.put(assumeFalseEquation, unit);
            } else {
                WorkListItem lhsVar = unitToOutputWorkListItem.get(unit);
                TransformerFunction unitTransformer = transformerSwitch.getStatmentTransformer((Stmt) unit);
                Equation unitEquation = new Equation(lhsVar,unitTransformer, inputWorkListItem, unit.toString());
                equationSystem.addEquation(unitEquation);
                equationToUnit.put(unitEquation, unit);
            }
        }

        return equationSystem;
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