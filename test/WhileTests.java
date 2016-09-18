public class WhileTests {
    public void addRadiosSingleStepTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        while(!y.contains(x)) {
            y = y.addRadios(1);
        }
    }

    public void addRadioSevenStepsTest() {
        Sphere x = new Sphere(0, 0, 0, 7);
        Sphere y = new Sphere(0, 0, 0, 0);

        while(!y.contains(x)) {
            y = y.addRadios(1);
        }
    }

    public void infiniteTest() {
        Sphere x = new Sphere(0, 0, 0, 7);
        Sphere y = new Sphere(0, 0, 0, 0);

        while(!y.contains(x)) {
            //code that doesn't change x nor y
        }
    }

    public void updateInWhileBodyTest() {
        Sphere x = new Sphere(0, 0, 0, 7);
        Sphere y = new Sphere(0, 0, 0, 3);

        while(!y.contains(x)) {
            y = new Sphere(0, 0, 0, 13);
        }
    }
}