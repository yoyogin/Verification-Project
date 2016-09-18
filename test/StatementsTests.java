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


}