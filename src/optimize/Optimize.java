package optimize;

import java.util.List;
import joeq.Class.jq_Class;
import joeq.Main.Helper;
import flow.Flow;

public class Optimize {
    /*
     * optimizeFiles is a list of names of class that should be optimized
     * if nullCheckOnly is true, disable all optimizations except "remove redundant NULL_CHECKs."
     */
    public static void optimize(List<String> optimizeFiles, boolean nullCheckOnly) {

       String[] mySolverArgs  = new String[3];
       mySolverArgs[0] = "submit.MySolver";
       mySolverArgs[1] = "optimize.FindRedundantNullChecks"; 
       for (int i = 0; i < optimizeFiles.size(); i++) {
            jq_Class classes = (jq_Class)Helper.load(optimizeFiles.get(i));
            // Run your optimization on each classes.
            mySolverArgs[2] = optimizeFiles.get(i);
	    Flow.main(mySolverArgs);
        }
    }
}
