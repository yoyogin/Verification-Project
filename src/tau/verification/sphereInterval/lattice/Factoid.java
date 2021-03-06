package tau.verification.sphereInterval.lattice;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;

/**
 * An element of the map-lattice {JimpleLocal variable -> SphereInterval}
 */
public class Factoid implements Comparable<Factoid> {
    public final JimpleLocal variable;
    public final SphereInterval sphereInterval;

    private final static SphereInterval bottom = null; // null is the bottom element
    // private final static SphereInterval top = ? //TODO: depends on SphereInterval.top

    public Factoid(
            JimpleLocal variable,
            IntConstant x0,
            IntConstant y0,
            IntConstant z0,
            IntConstant edgeA,
            IntConstant edgeB,
            IntConstant edgeC,
            IntConstant radios) {
        if (variable == null) {
            assert false;
            throw new IllegalArgumentException();
        }

        this.variable = variable;
        this.sphereInterval = new SphereInterval(x0, y0, z0, edgeA, edgeB, edgeC, radios);
    }

    public Factoid(JimpleLocal variable, SphereInterval sphereInterval) {
        if (variable == null || sphereInterval == null) {
            assert false;
            throw new IllegalArgumentException();
        }

        this.variable = variable;
        this.sphereInterval = new SphereInterval(sphereInterval);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.variable.hashCode();
        result = prime * result + this.sphereInterval.hashCode();

        return result;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if (object instanceof Factoid) {
            Factoid other = (Factoid) object;
            result = this.variable.equals(other.variable) &&
                     this.sphereInterval.equals(other.sphereInterval);
        }

        return result;
    }

    @Override
    public String toString() {
        return String.format(
                "%s = %s",
                this.variable,
                this.sphereInterval.toString());
    }

    public boolean contains(Factoid other) {
        return Factoid.getLowerBound(this, other).equals(other);
    }

    @Override
    public int compareTo(Factoid other) {
        if (variable.getNumber() != other.variable.getNumber()) {
            return other.variable.getNumber() - variable.getNumber();
        } else {
            return -1; //TODO: is this correct? we always want the latest
        }
    }

    public boolean isBottom() {
        return this.sphereInterval.isBottom;
    }

    public static Factoid getBottom(JimpleLocal variable) {
        return new Factoid(variable, SphereInterval.getBottom());
    }

    public static Factoid getUpperBound(Factoid first, Factoid second) {
        assert first != null && second != null;

        if (!first.variable.equals(second.variable)) {
            assert false;
            throw new IllegalArgumentException("Factoids with different variables belong to different lattices");
        }

        Factoid result = new Factoid(
                first.variable, // first.variable == second.variable
                SphereInterval.getUpperBound(first.sphereInterval, second.sphereInterval));

        return result;
    }

    public static Factoid getLowerBound(Factoid first, Factoid second) {
        assert first != null && second != null;

        if (!first.variable.equals(second.variable)) {
            assert false;
            throw new IllegalArgumentException("Factoids with different variables belong to different lattices");
        }

        return new Factoid(
                first.variable, // first.variable == second.variable
                SphereInterval.getLowerBound(first.sphereInterval, second.sphereInterval));
    }

    public static Factoid widen(Factoid first, Factoid second) {
        assert first != null && second != null;

        if (!first.variable.equals(second.variable)) {
            assert false;
            throw new IllegalArgumentException("Factoids with different variables belong to different lattices");
        }

        return new Factoid(
                first.variable, // first.variable == second.variable
                SphereInterval.widen(first.sphereInterval, second.sphereInterval));
    }

    public static Factoid narrow(Factoid first, Factoid second) {
        assert first != null && second != null;

        if (!first.variable.equals(second.variable)) {
            assert false;
            throw new IllegalArgumentException("Factoids with different variables belong to different lattices");
        }

        return new Factoid(
                first.variable, // first.variable == second.variable
                SphereInterval.narrow(first.sphereInterval, second.sphereInterval));
    }


}