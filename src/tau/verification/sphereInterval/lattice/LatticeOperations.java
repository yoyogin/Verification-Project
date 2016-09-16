package tau.verification.sphereInterval.lattice;

import soot.jimple.internal.JimpleLocal;
import tau.verification.sphereInterval.lattice.Factoid;
import tau.verification.sphereInterval.lattice.FactoidsConjunction;

import java.util.Set;

// TODO: consider refinement by equalities

/**
 * Implementation of the sphere interval abstract domain.
 */
public class LatticeOperations {
    public LatticeOperations() {
    }

    public static FactoidsConjunction getBottom() {
        return FactoidsConjunction.getBottom();
    }

    public static FactoidsConjunction getTop() {
        return FactoidsConjunction.getTop();
    }

    public static FactoidsConjunction upperBound(FactoidsConjunction first, FactoidsConjunction second) {
        if (first.isBottom()) {
            return second;
        }

        if (second.isBottom()) {
            return first;
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction();
        Set<JimpleLocal> vars = first.getVars();
        vars.addAll(second.getVars());

        for (JimpleLocal var : vars) {
            Factoid firstFactoid = first.getFactoid(var);
            Factoid secondFactoid = second.getFactoid(var);
            Factoid jointFactoid = Factoid.getUpperBound(firstFactoid, secondFactoid);

            if (jointFactoid != null) {
                result.add(jointFactoid);
            }
        }

        return result;
    }

    public static FactoidsConjunction lowerBound(FactoidsConjunction first, FactoidsConjunction second) {
        if (first.isBottom() || second.isBottom()) {
            return FactoidsConjunction.getBottom();
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction();
        Set<JimpleLocal> vars = first.getVars();
        vars.addAll(second.getVars());

        for (JimpleLocal var : vars) {
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
    public static boolean lessThanEquals(FactoidsConjunction first, FactoidsConjunction second) {
        if (first.isBottom()) {
            return true;
        }

        if (second.isBottom() && !first.isBottom()) {
            return false;
        }

        Set<JimpleLocal> vars = first.getVars();
        vars.addAll(second.getVars());
        for (JimpleLocal var : vars) {
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
}