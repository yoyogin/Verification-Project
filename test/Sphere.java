public class Sphere {
    private int x;
    private int y;
    private int z;
    private int radios;

    public Sphere(int x, int y, int z, int radios) {
//        assert radios >= 0;

        this.x = x;
        this.y = y;
        this.z = z;
        this.radios = radios;
    }

    public Sphere(Sphere other) {
        if(this != other) {
            this.x = other.x;
            this.y = other.y;
            this.z = other.z;
            this.radios = other.radios;
        }
    }

    public void addPoint(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void addRadios(int radios) {
//        assert radios >= 0;

        this.radios += radios;
    }

    public void setPoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRadios(int radios) {
//        assert radios >= 0;

        this.radios = radios;
    }

    public boolean isContained(Sphere other) {
        //calculate distance between both center points

        // TODO
        return false;
    }

    public boolean contains(Sphere other) {
        return other.isContained(this);

    }
}