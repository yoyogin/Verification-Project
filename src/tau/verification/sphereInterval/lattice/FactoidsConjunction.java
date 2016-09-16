package tau.verification.sphereInterval.lattice;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A set of varToFactoid of the form 'x = Sphere'
 */
public class FactoidsConjunction {
    private Set<Factoid> factoids;

    public static FactoidsConjunction getFactoidsConjunction(FactoidsConjunction other) {
        return new FactoidsConjunction(other);
    }

    public static FactoidsConjunction getFactoidsConjunction() {
        return new FactoidsConjunction(false);
    }

    public static FactoidsConjunction getTop() {
        return new FactoidsConjunction(false);
    }

    public static FactoidsConjunction getBottom() {
        return new FactoidsConjunction(true);
    }

    private FactoidsConjunction(boolean isBottom) {
        this.factoids = isBottom ? null : new TreeSet<Factoid>();
    }

    private FactoidsConjunction(FactoidsConjunction other) {
        if(other.isBottom()) {
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
        if(!(obj instanceof FactoidsConjunction)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        FactoidsConjunction other = (FactoidsConjunction) obj;
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
        if(this.factoids == null) {
            return variables;
        }

        for (Factoid factoid : this.factoids) {
            variables.add(factoid.variable);
        }

        return variables;
    }

    public void update(
            JimpleLocal sphereVariable,
            IntConstant bottomLeft0,
            IntConstant bottomLeft1,
            IntConstant bottomLeft2,
            IntConstant edgeA,
            IntConstant edgeB,
            IntConstant edgeC,
            IntConstant radios) {
        Factoid newFactoid = new Factoid(
                sphereVariable,
                bottomLeft0,
                bottomLeft1,
                bottomLeft2,
                edgeA,
                edgeB,
                edgeC,
                radios);

        this.update(newFactoid);
    }

    public void update(Factoid factoid) {
        if(this.factoids == null && factoid.isBottom()) {
            return;
        } else if(this.factoids == null && !factoid.isBottom()) {
            this.factoids = new TreeSet<>();
        }

        this.factoids.add(factoid);

        return;
    }

    public Factoid getFactoid(JimpleLocal sphereVariable) {
        if(this.factoids == null) {
            return null;
        }

        for(Factoid factoid : this.factoids) {
            if (factoid.variable.equals(sphereVariable)) {
                return factoid;
            }
        }

        return null;
    }

    public boolean removeFactoid(JimpleLocal sphereVariable) {
        boolean result = false;
        for (Iterator<Factoid> iterator = factoids.iterator(); iterator.hasNext();) {
            Factoid factoid = iterator.next();
            if (factoid.variable.equals(sphereVariable)) {
                iterator.remove();
                result = true;
                break;
            }
        }

        return result;
    }

    public String toString() {
        if (this.factoids == null) {
            return "false (bottom)";
        }

        if (factoids.isEmpty()) {
            return "true (top)";
        }

        StringBuilder result = new StringBuilder("Conjunction (");
        Set<JimpleLocal> vars = getVariables();
        int size = vars.size();
        for (JimpleLocal variable : vars) {
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

    public static FactoidsConjunction upperBound(FactoidsConjunction first, FactoidsConjunction second) {
        if (first.isBottom()) {
            return second;
        }

        if (second.isBottom()) {
            return first;
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction();
        Set<JimpleLocal> variables = first.getVariables();
        variables.addAll(second.getVariables());

        for (JimpleLocal variable : variables) {
            Factoid firstFactoid = first.getFactoid(variable);
            Factoid secondFactoid = second.getFactoid(variable);
            Factoid jointFactoid = Factoid.getUpperBound(firstFactoid, secondFactoid);

            if (jointFactoid != null) {
                result.update(jointFactoid);
            }
        }

        return result;
    }

    public static FactoidsConjunction lowerBound(FactoidsConjunction first, FactoidsConjunction second) {
        if (first.isBottom() || second.isBottom()) {
            return FactoidsConjunction.getBottom();
        }

        FactoidsConjunction result = FactoidsConjunction.getFactoidsConjunction();
        Set<JimpleLocal> variables = first.getVariables();
        variables.addAll(second.getVariables());

        for (JimpleLocal variable : variables) {
            Factoid firstFactoid = first.getFactoid(variable);
            Factoid secondFactoid = second.getFactoid(variable);
            Factoid jointFactoid = Factoid.getLowerBound(firstFactoid, secondFactoid);

            if (jointFactoid != null) {
                result.update(jointFactoid);
            }
        }

        return result;
    }

    /**
     * The order relation of the lattice

     * @return true if first is less than or equals the second based on the Factoids Conjunction lattice order relation
     */
    public static boolean lessThanEquals(FactoidsConjunction first, FactoidsConjunction second) {
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