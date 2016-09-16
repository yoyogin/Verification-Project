package tau.verification.sphereInterval.lattice;

import soot.jimple.IntConstant;

public class SphereInterval {
    public final IntConstant x0;
    public final IntConstant y0;
    public final IntConstant z0;
    public final IntConstant edgeA;
    public final IntConstant edgeB;
    public final IntConstant edgeC;
    public final IntConstant radios;

    public final boolean isBottom;
    // Add if necessary public final boolean isTop;

    public SphereInterval(
            IntConstant x0,
            IntConstant y0,
            IntConstant z0,
            IntConstant edgeA,
            IntConstant edgeB,
            IntConstant edgeC,
            IntConstant radios) {
        this.isBottom = false;

        assert x0 != null;
        assert y0 != null;
        assert z0 != null;
        assert edgeA != null;
        assert edgeB != null;
        assert edgeC != null;

        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.edgeA = edgeA;
        this.edgeB = edgeB;
        this.edgeC = edgeC;
        this.radios = radios;
    }

    private SphereInterval() {
        this.isBottom = true;

        this.x0 = null;
        this.y0 = null;
        this.z0 = null;
        this.edgeA = null;
        this.edgeB = null;
        this.edgeC = null;
        this.radios = null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        if(this.isBottom) {
            result = prime * result + 0;
        } else {
            result = prime * result + this.x0.hashCode();
            result = prime * result + this.y0.hashCode();
            result = prime * result + this.z0.hashCode();
            result = prime * result + this.edgeA.hashCode();
            result = prime * result + this.edgeB.hashCode();
            result = prime * result + this.edgeC.hashCode();
            result = prime * result + this.radios.hashCode();
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if (object instanceof SphereInterval) {
            SphereInterval other = (SphereInterval) object;

            if(this.isBottom && other.isBottom) {
                result = true;
            } else if(this.isBottom && !other.isBottom) {
                result = false;
            } else {
                result =
                    this.x0.equals(other.x0) &&
                    this.y0.equals(other.y0) &&
                    this.z0.equals(other.z0) &&
                    this.edgeA.equals(other.edgeA) &&
                    this.edgeB.equals(other.edgeB) &&
                    this.edgeC.equals(other.edgeC) &&
                    this.radios.equals(other.radios);
            }
        }

        return result;
    }

    @Override
    public String toString() {
        String sphereIntervalDescription;

        if(this.isBottom) {
            sphereIntervalDescription = "bottom";
        } else {
            sphereIntervalDescription =
                    String.format(
                        "(%s, %s, %s, %s, %s, %s, %s)",
                        this.x0,
                        this.y0,
                        this.z0,
                        this.edgeA,
                        this.edgeB,
                        this.edgeC,
                        this.radios);
        }

        return String.format("(%d) %s", this.hashCode(), sphereIntervalDescription);
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

    public static SphereInterval getBottom() {
        return new SphereInterval();
    }

    // TODO: what about infinity? (i.e. does IntConstant wraps around? or reach infy on Wrap?)
    public static SphereInterval getUpperBound(SphereInterval first, SphereInterval second) {
        // Note that the implementation equals to the following:
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

        SphereInterval result = new SphereInterval(
                minX0,
                minY0,
                minZ0,
                (IntConstant) maxX1.subtract(minX0),
                (IntConstant) maxY1.subtract(minY0),
                (IntConstant) maxZ1.subtract(minZ0),
                maxRadios);

        return result;
    }

    public static SphereInterval getLowerBound(SphereInterval first, SphereInterval second) {
        // Note that the implementation equals to the following:
        //
        // if (first.contains(second)) {
        //      return second;
        // } else if (second.contains(first)) {
        //      return first;
        // } else {
        //      return 'Sphere interval which is contained in both';
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
                return SphereInterval.getBottom();
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
                return SphereInterval.getBottom();
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
                return SphereInterval.getBottom();
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
                return SphereInterval.getBottom();
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
                return SphereInterval.getBottom();
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
                return SphereInterval.getBottom();
            }
        }

        IntConstant minRadios = (first.radios.lessThanOrEqual(second.radios).equivTo(1)) ? first.radios : second.radios;

        SphereInterval result = new SphereInterval(
                jointX0,
                jointY0,
                jointZ0,
                jointEdgeA,
                jointEdgeB,
                jointEdgeC,
                minRadios);

        return result;
    }
}
