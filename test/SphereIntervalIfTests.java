public class SphereIntervalIfTests {
    private void error(String message) {
        // dummy error function to be caught by analysis
    }

    public void ifContainsSimpleExample() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (x.contains(y)) {
            error("We were wrong, x does contain y!");
        } else {
            error("We were wrong, x does *not* contain y!");
        }
    }

    public void ifIsContainedSimpleExample() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (x.isContained(y)) {
            error("We were wrong, y does contain x!");
        } else {
            error("We were wrong, y doesn't *not* contain x!");
        }
    }
}