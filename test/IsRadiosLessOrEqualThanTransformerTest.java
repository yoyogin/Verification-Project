public class IsRadiosLessOrEqualThanTransformerTest{

    public void lessOrEqualToVariable() {
        Sphere x = new Sphere(0, 0, 0,0 );
        Sphere y = new Sphere(0, 0, 0, 100);

        while(x.isRadiosLessOrEqualThan(50))
        {
            x = x.addRadios(1);
        }

    }
}