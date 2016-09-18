public class IfTests {
    public void containsTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (x.contains(y)) {
            x = x.addPoint(2, 2, 2);
        } else {
            y = y.addPoint(1, 1, 1);
        }
    }

    public void isContainedInTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (x.isContainedIn(y)) {
            x = x.addPoint(2, 2, 2);
            y = y.addPoint(1, 1, 1);
        }
    }

    public void containsNegatedTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (!x.contains(y)) {
            x = x.addPoint(2, 2, 2);
            y = y.addPoint(1, 1, 1);
        }
    }

    public void isContainedInNegatedTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (!x.isContainedIn(y)) {
            x = x.addPoint(2, 2, 2);
            y = y.addPoint(1, 1, 1);
        }
    }
}