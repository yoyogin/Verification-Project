package tau.verification.sphereInterval;

import soot.PackManager;
import soot.Transform;

public class Main {
    public static void main(String[] args) {
        PackManager
                .v()
                .getPack("jtp")
                .add(new Transform("jtp.Analysis",
                     new Analysis()));

        soot.Main.main(args);
    }
}