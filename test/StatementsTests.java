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

    public void addRadiosWithLocal() {
        Sphere x = new Sphere(0, 0, 0, 0);
        int additionalRadios = 10;

        x = x.addRadios(additionalRadios);

        Report.Note("x should be forgotten")
    }

    public void addPointWithFieldsTest() {
        Sphere x = new Sphere(0, 0, 0, 0);
        int additionalZ = 10;

        x = x.addPoint(1, 2, additionalZ);

        Report.Note("x should be forgotten")
    }
}