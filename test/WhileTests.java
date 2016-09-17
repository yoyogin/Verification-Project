public class WhileTests {
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

        Report.Note("This test should not have Error nor Success - indication of a successful pass")

        while(!y.contains(x)) {
            //code that doesn't change x nor y
        }

        Report.Error("The analysis should not be able to resolve the (infinte) loop");
    }

    public void misleadingUpdateInWhileBodyTest() {
        Sphere x = new Sphere(0, 0, 0, 7);
        Sphere y = new Sphere(0, 0, 0, 3);

        Report.Note("The analysis should not be able to resolve the loop becuase Join(Sphere(0,0,0,7), Sphere(0,0,0,1)) == Sphere(0,0,0,7)");

        while(!y.contains(x)) {
            x = new Sphere(0, 0, 0, 1);
        }

        Report.Error("The analysis should not be able to resolve the misleading loop");
    }

    public void updateInWhileBodyTest() {
        Sphere x = new Sphere(0, 0, 0, 7);
        Sphere y = new Sphere(0, 0, 0, 3);

        while(!y.contains(x)) {
            y = new Sphere(0, 0, 0, 13);
        }

        Report.Success("The analysis resolved the loop becuase Join(Sphere(0,0,0,3), Sphere(0,0,0,13)) == Sphere(0,0,0,13)");
    }
}