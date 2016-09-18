package tau.verification.sphereInterval.lattice;

import soot.jimple.internal.JimpleLocal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A set of varToFactoid of the form 'x = Sphere'
 */
public class FactoidsMapping {
    private Set<Factoid> factoids;

    public static FactoidsMapping getFactoidsConjunction(FactoidsMapping other) {
        return new FactoidsMapping(other);
    }

    public static FactoidsMapping getFactoidsConjunction() {
        return new FactoidsMapping(false);
    }

    public static FactoidsMapping getTop() {
        return new FactoidsMapping(false);
    }

    public static FactoidsMapping getBottom() {
        return new FactoidsMapping(true);
    }

    public boolean evaluateConjunction() {
        for (Factoid factoid : this.factoids) {
            if (factoid.isBottom()) {
                return false;
            }
        }

        return true;
    }

    private FactoidsMapping(boolean isBottom) {
        this.factoids = isBottom ? null : new TreeSet<Factoid>();
    }

    private FactoidsMapping(FactoidsMapping other) {
        if (other.isBottom()) {
            this.factoids = null;
        } else {
            this.factoids = new TreeSet<>();
            this.factoids.addAll(other.factoids);
        }
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result;
        result += ((this.factoids == null) ? 0 : this.factoids.hashCode());

        return result;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof FactoidsMapping)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        FactoidsMapping other = (FactoidsMapping) obj;
        if (this.factoids == null || other.factoids == null) {
            return factoids == null && other.factoids == null;
        } else {
            return this.factoids.containsAll(other.factoids) && other.factoids.containsAll(this.factoids);
        }
    }

    /**
     * Returns a (mutable) copy of the set of local variables appearing in any factoid in the conjunction.
     */
    public Set<JimpleLocal> getVariables() {
        HashSet<JimpleLocal> variables = new HashSet<>();
        if (this.factoids == null) {
            return variables;
        }

        for (Factoid factoid : this.factoids) {
            variables.add(factoid.variable);
        }

        return variables;
    }

    public void update(Factoid factoid) {
        if (this.factoids == null) {
            this.factoids = new TreeSet<>();
        }

        this.removeFactoidByVariable(factoid.variable);
        this.factoids.add(factoid);
    }

    public Factoid getFactoid(JimpleLocal sphereVariable) {
        if (this.factoids == null) {
            return null;
        }

        for (Factoid factoid : this.factoids) {
            if (factoid.variable.equals(sphereVariable)) {
                return factoid;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        if (this.factoids == null) {
            return "false (bottom)";
        }

        if (factoids.isEmpty()) {
            return "true (top)";
        }

        StringBuilder result = new StringBuilder("Conjunction (");
        Set<JimpleLocal> variables = this.getVariables();
        int size = variables.size();
        for (JimpleLocal variable : variables) {
            Factoid factoid = getFactoid(variable);
            result.append(factoid.toString());
            size--;
            if (size > 0) {
                result.append(", ");
            }
        }
        result.append(")");

        return result.toString();
    }

    public boolean isBottom() {
        return this.factoids == null;
    }

    public boolean isTop() {
        return (this.factoids != null) && (this.factoids.isEmpty());
    }

    public void removeFactoidByVariable(JimpleLocal variable) {
        for (Iterator<Factoid> iterator = factoids.iterator(); iterator.hasNext(); ) {
            Factoid factoid = iterator.next();
            if (factoid.variable.equals(variable)) {
                iterator.remove();
                break;
            }
        }
    }

    public static FactoidsMapping upperBound(FactoidsMapping first, FactoidsMapping second) {
        if (first.isBottom()) {
            return second;
        }

        if (second.isBottom()) {
            return first;
        }

        FactoidsMapping result = FactoidsMapping.getFactoidsConjunction();
        Set<JimpleLocal> variables = first.getVariables();
        variables.addAll(second.getVariables());

        for (JimpleLocal variable : variables) {
            Factoid firstFactoid = first.getFactoid(variable);
            Factoid secondFactoid = second.getFactoid(variable);

            if (firstFactoid == null || secondFactoid == null) {
                continue;
            }

            result.update(Factoid.getUpperBound(firstFactoid, secondFactoid));
        }

        return result;
    }

    public static FactoidsMapping widen(FactoidsMapping first, FactoidsMapping second) {
        if (first.isBottom()) {
            return second;
        }


        FactoidsMapping result = FactoidsMapping.getFactoidsConjunction();
        Set<JimpleLocal> variables = first.getVariables();
        variables.addAll(second.getVariables());

        for (JimpleLocal variable : variables) {
            Factoid firstFactoid = first.getFactoid(variable);
            Factoid secondFactoid = second.getFactoid(variable);

            if (firstFactoid == null || secondFactoid == null) {
                continue;
            }

            result.update(Factoid.widen(firstFactoid, secondFactoid));
        }

        return result;
    }

    public static FactoidsMapping narrow(FactoidsMapping first, FactoidsMapping second) {
        if (second.isBottom()) {
            return first;
        }


        FactoidsMapping result = FactoidsMapping.getFactoidsConjunction();
        Set<JimpleLocal> variables = first.getVariables();
        variables.addAll(second.getVariables());

        for (JimpleLocal variable : variables) {
            Factoid firstFactoid = first.getFactoid(variable);
            Factoid secondFactoid = second.getFactoid(variable);

            if (firstFactoid == null || secondFactoid == null) {
                continue;
            }

            result.update(Factoid.narrow(firstFactoid, secondFactoid));
        }

        return result;
    }


    public static FactoidsMapping lowerBound(FactoidsMapping first, FactoidsMapping second) {
        if (first.isBottom() || second.isBottom()) {
            return FactoidsMapping.getBottom();
        }

        FactoidsMapping result = FactoidsMapping.getFactoidsConjunction();
        Set<JimpleLocal> variables = first.getVariables();
        variables.addAll(second.getVariables());

        for (JimpleLocal variable : variables) {
            Factoid firstFactoid = first.getFactoid(variable);
            Factoid secondFactoid = second.getFactoid(variable);

            if (firstFactoid == null || secondFactoid == null) {
                continue;
            }

            result.update(Factoid.getLowerBound(firstFactoid, secondFactoid));
        }

        return result;
    }

    /**
     * The order relation of the lattice
     *
     * @return true if first is less than or equals the second based on the Factoids Conjunction lattice order relation
     */
    public static boolean lessThanEquals(FactoidsMapping first, FactoidsMapping second) {
        if (first.isBottom()) {
            return true;
        }

        if (second.isBottom() && !first.isBottom()) {
            return false;
        }

        Set<JimpleLocal> variables = first.getVariables();
        variables.addAll(second.getVariables());
        for (JimpleLocal variable : variables) {
            Factoid firstFactoid = first.getFactoid(variable);
            Factoid secondFactoid = second.getFactoid(variable);
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