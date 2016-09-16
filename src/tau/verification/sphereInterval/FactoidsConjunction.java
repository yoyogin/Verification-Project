package tau.verification.sphereInterval;

import soot.Local;
import soot.jimple.NumericConstant;

import java.util.*;

/**
 * A set of varToFactoid of the form 'x = Sphere'
 */
public class FactoidsConjunction {
    private final Set<Factoid> factoids;

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
    public Set<Local> getVars() {
        HashSet<Local> vars = new HashSet<>();
        if(this.factoids == null) {
            return vars;
        }

        for (Factoid factoid : this.factoids) {
            vars.add(factoid.sphereVariable);
        }

        return vars;
    }

    /**
     * This functions ads Factoid to the set if the same factoid exactly(defined by hashcode and equals) is not contained in set
     *
     * @param sphereVariable
     * @param bottomLeft0
     * @param bottomLeft1
     * @param bottomLeft2
     * @param edgeA
     * @param edgeB
     * @param edgeC
     * @param radios
     * @return true iff factoid added
     */
    public boolean add(
            Local sphereVariable,
            NumericConstant bottomLeft0,
            NumericConstant bottomLeft1,
            NumericConstant bottomLeft2,
            NumericConstant edgeA,
            NumericConstant edgeB,
            NumericConstant edgeC,
            NumericConstant radios) {

        if(this.factoids == null) {
            return false;
        }

        Factoid factoid = new Factoid(
                sphereVariable,
                bottomLeft0,
                bottomLeft1,
                bottomLeft2,
                edgeA,
                edgeB,
                edgeC,
                radios);

        return this.factoids.add(factoid);
    }

    public boolean add(Factoid factoid) {
        if(this.factoids == null) {
            return false;
        }

        return this.factoids.add(factoid);
    }

    public FactoidsConjunction setFactoid(
            Local sphereVariable,
            NumericConstant bottomLeft0,
            NumericConstant bottomLeft1,
            NumericConstant bottomLeft2,
            NumericConstant edgeA,
            NumericConstant edgeB,
            NumericConstant edgeC,
            NumericConstant radios)
    {
        if(getFactoid(sphereVariable) != null)
        {
            removeVar(sphereVariable);
        }

        add(sphereVariable, bottomLeft0, bottomLeft1, bottomLeft2, edgeA, edgeB, edgeC, radios);
        return this;
    }


    public FactoidsConjunction setFactoid(Factoid factoid)
    {
        return setFactoid(factoid.sphereVariable, factoid.x0, factoid.y0, factoid.z0, factoid.edgeA, factoid.edgeB, factoid.edgeC, factoid.radios);
    }

    public Factoid getFactoid(Local sphereVariable) {
        if(this.factoids == null) {
            return null;
        }

        for(Factoid factoid : this.factoids) {
            if (factoid.sphereVariable.equals(sphereVariable)) {
                return factoid;
            }
        }

        return null;
    }

    public boolean removeVar(Local sphereVariable) {
        boolean result = false;
        for (Iterator<Factoid> iterator = factoids.iterator(); iterator.hasNext();) {
            Factoid factoid = iterator.next();
            if (factoid.sphereVariable.equals(sphereVariable)) {
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

        StringBuilder result = new StringBuilder("and(");
        Set<Local> vars = getVars();
        int size = vars.size();
        for (Local var : vars) {
            Factoid factoid = getFactoid(var);
            result.append(factoid.toString());
            --size;
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
}