public class IsRadiosLessOrEqualThanTransformerTest{

//    public void optimizationTest() {
//        Sphere x = new Sphere(0, 0, 0,0 );
//
//        while(x.isRadiosLessOrEqualThan(1000))
//        {
//            x = x.addRadios(1);
//        }
//
//    }

//    public void containTest() {
//        Sphere y = new Sphere(0, 0, 0,5);
//        Sphere x = new Sphere(0, 0, 0,10);
//
//        while(x.isContainedIn(y))
//        {
//            x = x.addRadios(1);
//            //maximal possible radios here is 6
//        }
//        //maximal possible radios here is 10
//    }

    public void containTest() {
        Sphere y = new Sphere(2,2, 2,5);
        Sphere x = new Sphere(2, 2, 2,2);

        while(x.isContainedIn(y))
        {
            x = x.addPoint(1,1,1);
            //maximal possible radios here is 6
        }
        //maximal possible radios here is 10
    }
}