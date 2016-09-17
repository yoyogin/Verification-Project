public class IfTests {
    public void containsTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (x.contains(y)) {
            Report.Success("x does contain y");
        } else {
            Report.Error("x should contain y");
        }
    }

    public void isContainedInTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (x.isContainedIn(y)) {
            Report.Error("x is not contained in y");
        } else {
            Report.Success("x is not contained in y");
        }
    }

    public void containsNegatedTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (!x.contains(y)) {
            Report.Error("x should contain y");
        } else {
            Report.Success("x does contain y");
        }
    }

    public void isContainedInNegatedTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (!x.isContainedIn(y)) {
            Report.Success("x is not contained in y");
        } else {
            Report.Error("x is not contained in y");
        }
    }
}