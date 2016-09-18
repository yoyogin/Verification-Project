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

    public SphereInterval(
            IntConstant x0,
            IntConstant y0,
            IntConstant z0,
            IntConstant edgeA,
            IntConstant edgeB,
            IntConstant edgeC,
            IntConstant radios) {


        if(     x0== null &&
                y0 == null &&
                z0 == null &&
                edgeA == null &&
                edgeB == null &&
                edgeC == null &&
                radios == null
            )
        {
            this.isBottom = true;
        }else
        {
            this.isBottom = false;
        }

        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.edgeA = edgeA;
        this.edgeB = edgeB;
        this.edgeC = edgeC;


        if(radios != null)
        {
            this.radios = (radios.lessThan(IntConstant.v(0)).equals(IntConstant.v(1))) ? IntConstant.v(0) : radios;
        }else
        {
            this.radios = radios;
        }
    }

    public SphereInterval(SphereInterval other) {
        if(other.isBottom){
            this.isBottom = true;

            this.x0 = null;
            this.y0 = null;
            this.z0 = null;
            this.edgeA = null;
            this.edgeB = null;
            this.edgeC = null;
            this.radios = null;

            return;
        }

        this.isBottom = false;

        assert other.x0 != null;
        assert other.y0 != null;
        assert other.z0 != null;
        assert other.edgeA != null;
        assert other.edgeB != null;
        assert other.edgeC != null;

        this.x0 = other.x0;
        this.y0 = other.y0;
        this.z0 = other.z0;
        this.edgeA = other.edgeA;
        this.edgeB = other.edgeB;
        this.edgeC = other.edgeC;
        this.radios = other.radios;
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
                            handleInfinity(this.x0),
                            handleInfinity(this.y0),
                            handleInfinity(this.z0),
                            handleInfinity(this.edgeA),
                            handleInfinity(this.edgeB),
                            handleInfinity(this.edgeC),
                            handleInfinity(this.radios));
        }

        return String.format("%s", sphereIntervalDescription);
    }

    private String handleInfinity(IntConstant constant)
    {
        if(constant.value == Integer.MAX_VALUE)
        {
            return "Infinity";
        }
        else if(constant.value == Integer.MIN_VALUE)
        {
            return "-Infinity";
        }
        return constant.toString();
    }

    public boolean contains(SphereInterval other) {
        return SphereInterval.getLowerBound(this, other).equals(other);
    }

    private IntConstant getX1() {
        if(this.edgeA.value == Integer.MAX_VALUE)
        {
            return IntConstant.v(Integer.MAX_VALUE);
        }
        return (IntConstant) this.x0.add(this.edgeA);
    }

    private IntConstant getY1() {
        if(this.edgeB.value == Integer.MAX_VALUE)
        {
            return IntConstant.v(Integer.MAX_VALUE);
        }
        return (IntConstant) this.y0.add(this.edgeB);
    }

    private IntConstant getZ1() {
        if(this.edgeC.value == Integer.MAX_VALUE)
        {
            return IntConstant.v(Integer.MAX_VALUE);
        }
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

        if(first.isBottom && second.isBottom) {
            return SphereInterval.getBottom();
        } else if(first.isBottom && !second.isBottom) {
            return new SphereInterval(
                    second.x0,
                    second.y0,
                    second.z0,
                    second.edgeA,
                    second.edgeB,
                    second.edgeC,
                    second.radios);
        } else if (!first.isBottom && second.isBottom) {
            return new SphereInterval(
                    first.x0,
                    first.y0,
                    first.z0,
                    first.edgeA,
                    first.edgeB,
                    first.edgeC,
                    first.radios);
        }

        IntConstant minX0 = (first.x0.lessThanOrEqual(second.x0).equals(IntConstant.v(1))) ? first.x0 : second.x0;
        IntConstant minY0 = (first.y0.lessThanOrEqual(second.x0).equals(IntConstant.v(1))) ? first.y0 : second.y0;
        IntConstant minZ0 = (first.z0.lessThanOrEqual(second.z0).equals(IntConstant.v(1))) ? first.z0 : second.z0;

        IntConstant maxX1 = (first.getX1().lessThanOrEqual(second.getX1()).equals(IntConstant.v(1))) ? second.getX1() : first.getX1();
        IntConstant maxY1 = (first.getY1().lessThanOrEqual(second.getY1()).equals(IntConstant.v(1))) ? second.getY1() : first.getY1();
        IntConstant maxZ1 = (first.getZ1().lessThanOrEqual(second.getZ1()).equals(IntConstant.v(1))) ? second.getZ1() : first.getZ1();

        IntConstant maxRadios = (first.radios.lessThanOrEqual(second.radios).equals(IntConstant.v(1))) ? second.radios : first.radios;

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

        if(first.isBottom || second.isBottom) {
            return SphereInterval.getBottom();
        }

        // TODO: refactor code copying
        // X
        IntConstant jointX0;
        IntConstant jointEdgeA;
        if (first.x0.lessThanOrEqual(second.x0).equals(IntConstant.v(1))) {
            if(second.x0.lessThanOrEqual(first.getX1()).equals(IntConstant.v(1))) {
                jointX0 = second.x0;
                if(second.getX1().lessThanOrEqual(first.getX1()).equals(IntConstant.v(1))) {
                    jointEdgeA = (IntConstant)second.getX1().subtract(jointX0);
                } else {
                    jointEdgeA = (IntConstant) first.getX1().subtract(jointX0);
                }
            } else {
                return SphereInterval.getBottom();
            }
        } else {
            if(first.x0.lessThanOrEqual(second.getX1()).equals(IntConstant.v(1))) {
                jointX0 = first.x0;
                if(first.getX1().lessThanOrEqual(second.getX1()).equals(IntConstant.v(1))) {
                    jointEdgeA = (IntConstant)first.getX1().subtract(jointX0);
                } else {
                    jointEdgeA = (IntConstant) second.getX1().subtract(jointX0);
                }
            } else {
                return SphereInterval.getBottom();
            }
        }

        // Y
        IntConstant jointY0;
        IntConstant jointEdgeB;
        if (first.y0.lessThanOrEqual(second.y0).equals(IntConstant.v(1))) {
            if(second.y0.lessThanOrEqual(first.getY1()).equals(IntConstant.v(1))) {
                jointY0 = second.y0;
                if(second.getY1().lessThanOrEqual(first.getY1()).equals(IntConstant.v(1))) {
                    jointEdgeB = (IntConstant)second.getY1().subtract(jointY0);
                } else {
                    jointEdgeB = (IntConstant) first.getY1().subtract(second.getY1());
                }
            } else {
                return SphereInterval.getBottom();
            }
        } else {
            if(first.y0.lessThanOrEqual(second.getY1()).equals(IntConstant.v(1))) {
                jointY0 = first.y0;
                if(first.getY1().lessThanOrEqual(second.getY1()).equals(IntConstant.v(1))) {
                    jointEdgeB = (IntConstant)first.getY1().subtract(jointY0);
                } else {
                    jointEdgeB = (IntConstant)second.getY1().subtract(jointY0);
                }
            } else {
                return SphereInterval.getBottom();
            }
        }

        // Z
        IntConstant jointZ0;
        IntConstant jointEdgeC;
        if (first.z0.lessThanOrEqual(second.z0).equals(IntConstant.v(1))) {
            if(second.z0.lessThanOrEqual(first.getZ1()).equals(IntConstant.v(1))) {
                jointZ0 = second.z0;
                if(second.getZ1().lessThanOrEqual(first.getZ1()).equals(IntConstant.v(1))) {
                    jointEdgeC = (IntConstant)second.getZ1().subtract(jointZ0);
                } else {
                    jointEdgeC = (IntConstant) first.getZ1().subtract(jointZ0);
                }
            } else {
                return SphereInterval.getBottom();
            }
        } else {
            if(first.z0.lessThanOrEqual(second.getZ1()).equals(IntConstant.v(1))) {
                jointZ0 = first.z0;
                if(first.getZ1().lessThanOrEqual(second.getZ1()).equals(IntConstant.v(1))) {
                    jointEdgeC = (IntConstant) first.getZ1().subtract(jointZ0);
                } else {
                    jointEdgeC = (IntConstant) second.getZ1().subtract(jointZ0);
                }
            } else {
                return SphereInterval.getBottom();
            }
        }

        IntConstant minRadios = (first.radios.lessThanOrEqual(second.radios).equals(IntConstant.v(1))) ? first.radios : second.radios;

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


    public static SphereInterval widen(SphereInterval first,SphereInterval second)
    {
        if(first.isBottom)
        {
            return second;
        }
        return new SphereInterval(
                getLowerWidening(first.x0,second.x0),
                getLowerWidening(first.y0,second.y0),
                getLowerWidening(first.z0,second.z0),
                getUpperWidening(first.edgeA,second.edgeA),
                getUpperWidening(first.edgeB,second.edgeB),
                getUpperWidening(first.edgeC,second.edgeC),
                getUpperWidening(first.radios,second.radios)
        );
    }

    public static SphereInterval narrow(SphereInterval first,SphereInterval second)
    {
        if(second.isBottom)
        {
            return first;
        }
        return new SphereInterval(
                getUpperNarrowing(first.x0,second.x0),
                getUpperNarrowing(first.y0,second.y0),
                getUpperNarrowing(first.z0,second.z0),
                getLowerNarrowing(first.edgeA,second.edgeA),
                getLowerNarrowing(first.edgeB,second.edgeB),
                getLowerNarrowing(first.edgeC,second.edgeC),
                getLowerNarrowing(first.radios,second.radios)
        );
    }

    private static IntConstant getLowerWidening(IntConstant v1,IntConstant v2)
    {
        if(((IntConstant)v1.lessThanOrEqual(v2)).value == 1)
        {
            return v1;
        }
        return IntConstant.v(Integer.MIN_VALUE);
    }

    private static IntConstant getUpperWidening(IntConstant v1,IntConstant v2)
    {
        if(((IntConstant)v1.greaterThanOrEqual(v2)).value == 1)
        {
            return v1;
        }
        return IntConstant.v(Integer.MAX_VALUE);
    }

    private static IntConstant getUpperNarrowing(IntConstant v1,IntConstant v2)
    {
        if(((IntConstant)v1.equalEqual(IntConstant.v(Integer.MIN_VALUE))).value == 1)
        {
            return v2;
        }
        return v1;
    }

    private static IntConstant getLowerNarrowing(IntConstant v1,IntConstant v2)
    {
        if(((IntConstant)v1.equalEqual(IntConstant.v(Integer.MAX_VALUE))).value == 1)
        {
            return v2;
        }
        return v1;
    }
}
