public class SphereIntervalWhileTests {
    public void addRadiosSingleStepTest() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        while(!y.contains(x)) {
            y.addRadios(1);
        }

        if(!y.contains(x)) {
            Report.Error("x should contain y");
        } else {
            Report.Success("y does contain x");
        }
    }

    public void addRadioSevenStepsTest() {
        Sphere x = new Sphere(0, 0, 0, 7);
        Sphere y = new Sphere(0, 0, 0, 0);

        while(!y.contains(x)) {
            y.addRadios(1);
        }

        if(!y.contains(x)) {
            Report.Error("x should contain y");
        } else {
            Report.Success("y does contain x");
        }
    }

    public void infiniteTest() {
        Sphere x = new Sphere(0, 0, 0, 7);
        Sphere y = new Sphere(0, 0, 0, 0);

        Report.Note("This test does not have errors or successes")

        while(!y.contains(x)) {
            //code that doesn't change x nor y
        }

        Report.Error("The analysis cannot resolve the (infinte) loop");
    }
}