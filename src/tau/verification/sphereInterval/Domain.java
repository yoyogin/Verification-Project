package tau.verification.sphereInterval;

import tau.verification.sphereInterval.function.Function;
import soot.Local;
import tau.verification.sphereInterval.chaoticIteration.WorkListItem;
import tau.verification.sphereInterval.transformer.TransformerSwitch;

import java.util.List;
import java.util.Set;

// TODO: consider refinement by equalities

/**
 * Implementation of the sphere interval abstract domain.
 */
public class Domain {
    private TransformerSwitch matcher = new TransformerSwitch();

    public Domain() {
    }

    public FactoidsConjunction getBottom() {
        return FactoidsConjunction.getBottom();
    }

    public FactoidsConjunction getTop() {
        return FactoidsConjunction.getTop();
    }

    public FactoidsConjunction upperBound(FactoidsConjunction first, FactoidsConjunction second) {
        if (first.isBottom()) {
            return second;
        }

        if (second.isBottom()) {
            return first;
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction();
        Set<Local> vars = first.getVars();
        vars.addAll(second.getVars());

        for (Local var : vars) {
            Factoid firstFactoid = first.getFactoid(var);
            Factoid secondFactoid = second.getFactoid(var);
            Factoid jointFactoid = Factoid.getUpperBound(firstFactoid, secondFactoid);

            if (jointFactoid != null) {
                result.add(jointFactoid);
            }
        }

        return result;
    }

    public FactoidsConjunction lowerBound(FactoidsConjunction first, FactoidsConjunction second) {
        if (first.isBottom() || second.isBottom()) {
            return FactoidsConjunction.getBottom();
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction();
        Set<Local> vars = first.getVars();
        vars.addAll(second.getVars());

        for (Local var : vars) {
            Factoid firstFactoid = first.getFactoid(var);
            Factoid secondFactoid = second.getFactoid(var);
            Factoid jointFactoid = Factoid.getLowerBound(firstFactoid, secondFactoid);

            if (jointFactoid != null) {
                result.add(jointFactoid);
            }
        }

        return result;
    }

    /**
     * The order relation of the domain
     */
    public boolean lessThanEquals(FactoidsConjunction first, FactoidsConjunction second) {
        if (first.isBottom()) {
            return true;
        }

        if (second.isBottom() && !first.isBottom()) {
            return false;
        }

        Set<Local> vars = first.getVars();
        vars.addAll(second.getVars());
        for (Local var : vars) {
            Factoid firstFactoid = first.getFactoid(var);
            Factoid secondFactoid = second.getFactoid(var);
            if (secondFactoid == null) {
                continue;
            } else if (firstFactoid != null && secondFactoid.contains(firstFactoid)) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }



    /**
     * Returns an operation that always returns the top element.
     */
    public Function getTopOperation() {
        return new Function(){
            @Override
            public byte arguments() {
                return 0;
            }

            @Override
            public FactoidsConjunction invoke() {
                return getTop();
            }

            @Override
            public String toString() {
                return getTop().toString();
            }

            @Override
            public String invocationToString(List<WorkListItem> arguments) {
                return getTop().toString();
            }
        };
    }
}