public class SphereIntervalIfTests {
    public void ifContainsSimpleTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (x.contains(y)) {
            Report.Success("x does contain y");
        } else {
            Report.Error("x should contain y");
        }
    }

    public void ifIsContainedInSimpleTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (x.isContainedIn(y)) {
            Report.Error("x isn't contained in y");
        } else {
            Report.Sucess("x isn't contained in y");
        }
    }

    public void ifContainsNegatedSimpleTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (!x.contains(y)) {
            Report.Error("x should contain y");
        } else {
            Report.Success("x does contain y");
        }
    }

    public void ifIsContainedInNegatedSimpleTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (!x.isContainedIn(y)) {
            Report.Sucess("x isn't contained in y");
        } else {
            Report.Error("x isn't contained in y");
        }
    }
}