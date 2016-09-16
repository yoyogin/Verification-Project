package tau.verification.sphereInterval;

import soot.Local;
import soot.jimple.NumericConstant;
import soot.jimple.internal.JimpleLocal;

/**
 * The abstraction for each Sphere variable
 * 1. Variable
 * 2. Center point interval - a cuboid with edges parallel to coordinate system.
 * 3. Radios length
 */
public class Factoid implements Comparable<Factoid> {
    public final JimpleLocal sphereVariable;
    public final NumericConstant x0;
    public final NumericConstant y0;
    public final NumericConstant z0;
    public final NumericConstant edgeA;
    public final NumericConstant edgeB;
    public final NumericConstant edgeC;
    public final NumericConstant radios;

    public Factoid(
            JimpleLocal sphereVariable,
            NumericConstant x0,
            NumericConstant y0,
            NumericConstant z0,
            NumericConstant edgeA,
            NumericConstant edgeB,
            NumericConstant edgeC,
            NumericConstant radios) {
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

    private NumericConstant getX1() {
        return this.x0.add(this.edgeA);
    }

    private NumericConstant getY1() {
        return this.y0.add(this.edgeB);
    }

    private NumericConstant getZ1() {
        return this.z0.add(this.edgeC);
    }

    public boolean contains(Factoid other) {
        return getLowerBound(this, other).equals(other);
    }

    // TODO: what about infinity? (i.e. does NumericConstant wraps around? or reach infy on Wrap?)
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

        NumericConstant minX0 = (first.x0.lessThanOrEqual(second.x0).equivTo(1)) ? first.x0 : second.x0;
        NumericConstant minY0 = (first.y0.lessThanOrEqual(second.x0).equivTo(1)) ? first.y0 : second.y0;
        NumericConstant minZ0 = (first.z0.lessThanOrEqual(second.z0).equivTo(1)) ? first.z0 : second.z0;

        NumericConstant maxX1 = (first.getX1().lessThanOrEqual(second.getX1()).equivTo(1)) ? second.getX1() : first.getX1();
        NumericConstant maxY1 = (first.getY1().lessThanOrEqual(second.getY1()).equivTo(1)) ? second.getY1() : first.getY1();
        NumericConstant maxZ1 = (first.getZ1().lessThanOrEqual(second.getZ1()).equivTo(1)) ? second.getZ1() : first.getZ1();

        NumericConstant maxRadios = (first.radios.lessThanOrEqual(second.radios).equivTo(1)) ? second.radios : first.radios;

        Factoid joint = new Factoid(
                first.sphereVariable, // first.sphereVariable == second.sphereVariable
                minX0,
                minY0,
                minZ0,
                maxX1.subtract(minX0),
                maxY1.subtract(minY0),
                maxZ1.subtract(minZ0),
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
        NumericConstant jointX0;
        NumericConstant jointEdgeA;
        if (first.x0.lessThanOrEqual(second.x0).equivTo(1)) {
            if(second.x0.lessThanOrEqual(first.getX1()).equivTo(1)) {
                jointX0 = second.x0;
                if(second.getX1().lessThanOrEqual(first.getX1()).equivTo(1)) {
                    jointEdgeA = second.getX1();
                } else {
                    jointEdgeA = first.getX1().subtract(second.x0);
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
                    jointEdgeA = second.getX1().subtract(first.x0);
                }
            } else {
                return null; // there is no intersection on X axis
            }
        }

        // Y
        NumericConstant jointY0;
        NumericConstant jointEdgeB;
        if (first.y0.lessThanOrEqual(second.y0).equivTo(1)) {
            if(second.y0.lessThanOrEqual(first.getY1()).equivTo(1)) {
                jointY0 = second.y0;
                if(second.getY1().lessThanOrEqual(first.getY1()).equivTo(1)) {
                    jointEdgeB = second.getY1();
                } else {
                    jointEdgeB = first.getY1().subtract(second.y0);
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
                    jointEdgeB = second.getY1().subtract(first.y0);
                }
            } else {
                return null; // there is no intersection on Y axis
            }
        }

        // Z
        NumericConstant jointZ0;
        NumericConstant jointEdgeC;
        if (first.z0.lessThanOrEqual(second.z0).equivTo(1)) {
            if(second.z0.lessThanOrEqual(first.getZ1()).equivTo(1)) {
                jointZ0 = second.z0;
                if(second.getZ1().lessThanOrEqual(first.getZ1()).equivTo(1)) {
                    jointEdgeC = second.getZ1();
                } else {
                    jointEdgeC = first.getZ1().subtract(second.z0);
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
                    jointEdgeC = second.getZ1().subtract(first.z0);
                }
            } else {
                return null; // there is no intersection on Z axis
            }
        }

        NumericConstant minRadios = (first.radios.lessThanOrEqual(second.radios).equivTo(1)) ? first.radios : second.radios;

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
}