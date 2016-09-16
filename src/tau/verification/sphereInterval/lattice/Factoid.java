package tau.verification.sphereInterval.lattice;

import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;

/**
 * The abstraction for each Sphere variable
 * 1. Variable
 * 2. Center point interval - a cuboid with edges parallel to coordinate system.
 * 3. Radios length
 */
public class Factoid implements Comparable<Factoid> {
    public final JimpleLocal sphereVariable;
    public final AbstractSphere abstractSphere;

    public Factoid(
            JimpleLocal sphereVariable,
            IntConstant x0,
            IntConstant y0,
            IntConstant z0,
            IntConstant edgeA,
            IntConstant edgeB,
            IntConstant edgeC,
            IntConstant radios) {
        this.sphereVariable = sphereVariable;
        this.abstractSphere = new AbstractSphere(x0, y0, z0, edgeA, edgeB, edgeC, radios);
    }

    public Factoid(
            JimpleLocal sphereVariable,
            AbstractSphere abstractSphere) {
        this.sphereVariable = sphereVariable;
        this.abstractSphere = abstractSphere;

        // We're maintaining the same pointer on purpose for 'x = y' assignments
        assert this.abstractSphere == abstractSphere;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.sphereVariable.hashCode();
        result = prime * result + this.abstractSphere.hashCode();

        return result;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if (object instanceof Factoid) {
            Factoid other = (Factoid) object;
            result =
                this.sphereVariable.equals(other.sphereVariable) &&
                this.abstractSphere.equals(other.abstractSphere);
        }

        return result;
    }

    @Override
    public String toString() {
        String result = String.format(
                "%s = %s",
                this.sphereVariable,
                this.abstractSphere.toString());

        return result.toString();
    }

    public boolean contains(Factoid other) {
        return getLowerBound(this, other).equals(other);
    }

    // TODO: what about infinity? (i.e. does IntConstant wraps around? or reach infy on Wrap?)
    public static Factoid getUpperBound(Factoid first, Factoid second) {
        if (first == null || second == null) {
            return null;
        }

        if(!first.sphereVariable.equals(second.sphereVariable)) {
//            return null; // TODO: does it makes sense to return bottom in this case?
            throw new IllegalArgumentException("Sphere variables are different");
        }

        Factoid result = new Factoid(
                first.sphereVariable, // first.sphereVariable == second.sphereVariable
                AbstractSphere.getUpperBound(first.abstractSphere, second.abstractSphere));

        return result;
    }

    public static Factoid getLowerBound(Factoid first, Factoid second) {
        assert first != null && second != null;

        if(!first.sphereVariable.equals(second.sphereVariable)) {
//            return null; // TODO: does it makes sense to return bottom in this case?
            throw new IllegalArgumentException("Sphere variables are different");
        }

        Factoid result = new Factoid(
                first.sphereVariable, // first.sphereVariable == second.sphereVariable
                AbstractSphere.getLowerBound(first.abstractSphere, second.abstractSphere));

        return result;
    }

    @Override
    public int compareTo(Factoid other) {
        if (sphereVariable.getNumber() != other.sphereVariable.getNumber()) {
            return other.sphereVariable.getNumber() - sphereVariable.getNumber();
        } else {
            return -1; //TODO: is this correct? we always want the latest
        }
    }
}