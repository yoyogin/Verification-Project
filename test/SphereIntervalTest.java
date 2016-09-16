public class SphereIntervalTest {
    private void error(String message) {
    }

    public void ifExample() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (x.contains(y)) {
            error("We were wrong, x does contain y!");
        } else {
            error("We were wrong, x doesn't contain y!");
        }
    }
}