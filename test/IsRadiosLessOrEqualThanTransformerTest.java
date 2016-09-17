public class IsRadiosLessOrEqualTransformerTest{
    public void lessOrEqualToVariable() {
        Sphere x = new Sphere(0, 0, 0, 1);
        Sphere y = new Sphere(0, 0, 0, 0);

        if (y.isRadiosLessOrEqualThan(x)) {
            Report.Success("x does contain y");
        }
    }
}