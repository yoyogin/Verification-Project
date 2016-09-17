public class Sphere {
    public final int x;
    public final int y;
    public final int z;
    public final int radios;

    public Sphere(int x, int y, int z, int radios) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radios = (radios < 0) ? 0 : radios;
    }

    public Sphere addPoint(int x, int y, int z) {
        return new Sphere(
                this.x + x,
                this.y + y,
                this.z + z,
                this.radios);
    }

    public Sphere addRadios(int radios) {
        return new Sphere(
                this.x,
                this.y,
                this.z,
                newRadios);
    }

    public boolean isContainedIn(Sphere other) {

        double dist_x = (this.x - other.x) * (this.x - other.x);
        double dist_y = (this.y - other.y) * (this.y - other.y);
        double dist_z = (this.z - other.z) * (this.z - other.z);

        double sum = dist_x + dist_y + dist_z;
        double sqrt_sum = Math.sqrt(sum);


        return (sqrt_sum + this.radios) <= other.radios;
    }

    public boolean contains(Sphere other) {
        return other.isContainedIn(this);
    }
}