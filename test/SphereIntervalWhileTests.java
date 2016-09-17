public class SphereIntervalWhileTests {
    private void error(String message) {
        // dummy error function to be caught by analysis
    }

    public void whileAddSimpleExample() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        while(!y.contains(x)) {
            y.addRadios(1);
        }

        if(!y.contains(x) {
            error("The analysis is incorrect - we expected y to contain x");
        } else {
            error("The analysis is correct y does contain x");
        }
    }
}