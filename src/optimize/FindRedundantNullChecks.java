package optimize;

import joeq.Class.jq_Class;
import joeq.Main.Helper;
import flow.Flow;

public class FindRedundantNullChecks {

    /*
     * args is an array of class names
     * method should print out a list of quad ids of redundant null checks
     * for each function as described on the course webpage
     */
    public static void main(String[] args) {
        //fill me in
		String[] mySolverArgs  = new String[3];
		mySolverArgs[0] = "submit.MySolver";
		mySolverArgs[1] = "flow.ConstantProp"; //TODO: change this to FindRedundantNullChecks;
		for (int i = 0; i < args.size(); i++) {
			mySolverArgs[2] = args[i];
			Flow.main(mySolverArgs);
		}	
    }
}
