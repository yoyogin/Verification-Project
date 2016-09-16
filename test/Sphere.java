public class Sphere {
    public int x;
    public int y;
    public int z;
    private int radios;

    public Sphere(int x, int y, int z, int radios) {
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
        this.radios += radios;
    }

    public void setPoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRadios(int radios) {
        this.radios = radios;
    }

    public boolean isContained(Sphere other) {

        double dist_x = (this.x-other.x) * (this.x-other.x);
        double dist_y = (this.y-other.y) * (this.y-other.y);
        double dist_z = (this.z-other.z) * (this.z-other.z);

        double sum = dist_x + dist_y + dist_z;
        double sqrt_sum = Math.sqrt(sum);


        return sqrt_sum + this.radios <=other.radios;
    }

    public boolean contains(Sphere other) {
        return other.isContained(this);

    }
}