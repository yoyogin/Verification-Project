public class StatementsTests {
    public void addPointWithConstantsTest() {
        Sphere x = new Sphere(0, 0, 0, 0);
        x = x.addPoint(1, 1, 1);

        Report.Note("x should change state during this test")
    }

    public void addRadiosWithConstantTest() {
        Sphere x = new Sphere(0, 0, 0, 0);
        x = x.addRadios(3);

        Report.Note("x should change state during this test")
    }

    public void addRadiosWithFieldsTest() {
        Sphere x = new Sphere(0, 0, 0, 0);
        Sphere y = new Sphere(1, 1, 1, 1);

        x = x.addRadios(y.radios);

        Report.Note("x should be forgotten and y should not change state during this test")
    }

    public void addPointWithFieldsTest() {
        Sphere x = new Sphere(0, 0, 0, 0);
        Sphere y = new Sphere(1, 1, 1, 1);

        x = x.addPoint(y.x, y.y, y.z);

        Report.Note("x should be forgotten and y should not change state during this test")
    }
}