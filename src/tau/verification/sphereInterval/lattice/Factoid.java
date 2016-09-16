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
    public final IntConstant x0;
    public final IntConstant y0;
    public final IntConstant z0;
    public final IntConstant edgeA;
    public final IntConstant edgeB;
    public final IntConstant edgeC;
    public final IntConstant radios;

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
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.edgeA = edgeA;
        this.edgeB = edgeB;
        this.edgeC = edgeC;
        this.radios = radios;

        assert sphereVariable != null;
        assert x0 != null;
        assert y0 != null;
        assert z0 != null;
        assert edgeA != null;
        assert edgeB != null;
        assert edgeC != null;
        assert radios != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.sphereVariable.hashCode();
        result = prime * result + this.x0.hashCode();
        result = prime * result + this.y0.hashCode();
        result = prime * result + this.z0.hashCode();
        result = prime * result + this.edgeA.hashCode();
        result = prime * result + this.edgeB.hashCode();
        result = prime * result + this.edgeC.hashCode();
        result = prime * result + this.radios.hashCode();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof Factoid) {
            Factoid other = (Factoid) obj;
            result =
                this.sphereVariable == other.sphereVariable &&
                this.x0 == other.x0 &&
                this.y0 == other.y0 &&
                this.z0 == other.z0 &&
                this.edgeA == other.edgeA &&
                this.edgeB == other.edgeB &&
                this.edgeC == other.edgeC &&
                this.radios == other.radios;
        }

        return result;
    }

    @Override
    public String toString() {
        String result = String.format(
                "%s =(%s, %s, %s, %s, %s, %s, %s)",
                this.sphereVariable,
                this.x0,
                this.y0,
                this.z0,
                this.edgeA,
                this.edgeB,
                this.edgeC,
                this.radios);

        return result.toString();
    }

    private IntConstant getX1() {
        return (IntConstant) this.x0.add(this.edgeA);
    }

    private IntConstant getY1() {
        return (IntConstant) this.y0.add(this.edgeB);
    }

    private IntConstant getZ1() {
        return (IntConstant) this.z0.add(this.edgeC);
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

        // Note that the implementation below cover all cases
        //
        // if (first.contains(second)) {
        //      return first;
        // } else if (second.contains(first)) {
        //      return second;
        // } else {
        //      return 'Sphere Interval which contains both first and second';
        // }

        IntConstant minX0 = (first.x0.lessThanOrEqual(second.x0).equivTo(1)) ? first.x0 : second.x0;
        IntConstant minY0 = (first.y0.lessThanOrEqual(second.x0).equivTo(1)) ? first.y0 : second.y0;
        IntConstant minZ0 = (first.z0.lessThanOrEqual(second.z0).equivTo(1)) ? first.z0 : second.z0;

        IntConstant maxX1 = (first.getX1().lessThanOrEqual(second.getX1()).equivTo(1)) ? second.getX1() : first.getX1();
        IntConstant maxY1 = (first.getY1().lessThanOrEqual(second.getY1()).equivTo(1)) ? second.getY1() : first.getY1();
        IntConstant maxZ1 = (first.getZ1().lessThanOrEqual(second.getZ1()).equivTo(1)) ? second.getZ1() : first.getZ1();

        IntConstant maxRadios = (first.radios.lessThanOrEqual(second.radios).equivTo(1)) ? second.radios : first.radios;

        Factoid joint = new Factoid(
                first.sphereVariable, // first.sphereVariable == second.sphereVariable
                minX0,
                minY0,
                minZ0,
                (IntConstant) maxX1.subtract(minX0),
                (IntConstant) maxY1.subtract(minY0),
                (IntConstant) maxZ1.subtract(minZ0),
                maxRadios);

        return joint;
    }

    public static Factoid getLowerBound(Factoid first, Factoid second) {
        if (first == null || second == null) {
            return null;
        }

        if(!first.sphereVariable.equals(second.sphereVariable)) {
//            return null; // TODO: does it makes sense to return bottom in this case?
            throw new IllegalArgumentException("Sphere variables are different");
        }

        // Note that the implementation below cover all cases
        //
        // if (first.contains(second)) {
        //      return first;
        // } else if (second.contains(first)) {
        //      return second;
        // } else {
        //      return 'Sphere Interval which contains both first and second';
        // }

        // TODO: refactor code copying
        // X
        IntConstant jointX0;
        IntConstant jointEdgeA;
        if (first.x0.lessThanOrEqual(second.x0).equivTo(1)) {
            if(second.x0.lessThanOrEqual(first.getX1()).equivTo(1)) {
                jointX0 = second.x0;
                if(second.getX1().lessThanOrEqual(first.getX1()).equivTo(1)) {
                    jointEdgeA = second.getX1();
                } else {
                    jointEdgeA = (IntConstant) first.getX1().subtract(second.x0);
                }
            } else {
                return null; // there is no intersection on X axis
            }
        } else {
            if(first.x0.lessThanOrEqual(second.getX1()).equivTo(1)) {
                jointX0 = first.x0;
                if(first.getX1().lessThanOrEqual(second.getX1()).equivTo(1)) {
                    jointEdgeA = first.getX1();
                } else {
                    jointEdgeA = (IntConstant) second.getX1().subtract(first.x0);
                }
            } else {
                return null; // there is no intersection on X axis
            }
        }

        // Y
        IntConstant jointY0;
        IntConstant jointEdgeB;
        if (first.y0.lessThanOrEqual(second.y0).equivTo(1)) {
            if(second.y0.lessThanOrEqual(first.getY1()).equivTo(1)) {
                jointY0 = second.y0;
                if(second.getY1().lessThanOrEqual(first.getY1()).equivTo(1)) {
                    jointEdgeB = second.getY1();
                } else {
                    jointEdgeB = (IntConstant) first.getY1().subtract(second.y0);
                }
            } else {
                return null; // there is no intersection on Y axis
            }
        } else {
            if(first.y0.lessThanOrEqual(second.getY1()).equivTo(1)) {
                jointY0 = first.y0;
                if(first.getY1().lessThanOrEqual(second.getY1()).equivTo(1)) {
                    jointEdgeB = first.getY1();
                } else {
                    jointEdgeB = (IntConstant) second.getY1().subtract(first.y0);
                }
            } else {
                return null; // there is no intersection on Y axis
            }
        }

        // Z
        IntConstant jointZ0;
        IntConstant jointEdgeC;
        if (first.z0.lessThanOrEqual(second.z0).equivTo(1)) {
            if(second.z0.lessThanOrEqual(first.getZ1()).equivTo(1)) {
                jointZ0 = second.z0;
                if(second.getZ1().lessThanOrEqual(first.getZ1()).equivTo(1)) {
                    jointEdgeC = second.getZ1();
                } else {
                    jointEdgeC = (IntConstant) first.getZ1().subtract(second.z0);
                }
            } else {
                return null; // there is no intersection on Z axis
            }
        } else {
            if(first.z0.lessThanOrEqual(second.getZ1()).equivTo(1)) {
                jointZ0 = first.z0;
                if(first.getZ1().lessThanOrEqual(second.getZ1()).equivTo(1)) {
                    jointEdgeC = first.getZ1();
                } else {
                    jointEdgeC = (IntConstant) second.getZ1().subtract(first.z0);
                }
            } else {
                return null; // there is no intersection on Z axis
            }
        }

        IntConstant minRadios = (first.radios.lessThanOrEqual(second.radios).equivTo(1)) ? first.radios : second.radios;

        Factoid joint = new Factoid(
                first.sphereVariable, // first.sphereVariable == second.sphereVariable
                jointX0,
                jointY0,
                jointZ0,
                jointEdgeA,
                jointEdgeB,
                jointEdgeC,
                minRadios);

        return joint;
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