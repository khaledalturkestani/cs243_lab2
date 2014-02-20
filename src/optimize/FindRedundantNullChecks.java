package optimize;

import joeq.Class.jq_Class;
import joeq.Main.Helper;
import flow.Flow;
import java.util.*;
import joeq.Compiler.Quad.Operand.RegisterOperand;

// some useful things to import. add any additional imports you need.
import joeq.Compiler.Quad.*;

public class FindRedundantNullChecks implements Flow.Analysis {
	
    /**
     * Class for the dataflow objects in the ReachingDefs analysis.
     * You are free to change this class or move it to another file.
     */
    public static class VarSet implements Flow.DataflowObject {
        private Set<String> set;
        public static Set<String> universalSet;
        public VarSet() { set = new HashSet<String>(); }

        public void setToTop() { set = new HashSet<String>(); }
        public void setToBottom() { set = new HashSet<String>(universalSet); }

        public void meetWith(Flow.DataflowObject o) 
        {
            VarSet a = (VarSet)o;
            set.retainAll(a.set);
        }

        public void copy(Flow.DataflowObject o) 
        {
            VarSet a = (VarSet) o;
            set = new HashSet<String>(a.set);
        }

        @Override
        public boolean equals(Object o) 
        {
            if (o instanceof VarSet) 
            {
                VarSet a = (VarSet) o;
                return set.equals(a.set);
            }
            return false;
        }
        @Override
        public int hashCode() {
            return set.hashCode();
        }
        @Override
        public String toString() 
        {
            return set.toString();
        }

        public void genVar(String v) {set.add(v);}
        public void killVar(String v) {set.remove(v);}
    }

    private VarSet[] in, out;
    private VarSet entry, exit;

    public void preprocess(ControlFlowGraph cfg) {
        System.out.printf(cfg.getMethod().getName().toString());
        /* Generate initial conditions. */
        QuadIterator qit = new QuadIterator(cfg);
        int max = 0;
        while (qit.hasNext()) {
            int x = qit.next().getID();
            if (x > max) max = x;
        }
        max += 1;
        in = new VarSet[max];
        out = new VarSet[max];
        qit = new QuadIterator(cfg);

        Set<String> s = new HashSet<String>();
        VarSet.universalSet = s;

        /* Arguments are always there. */
        int numargs = cfg.getMethod().getParamTypes().length;
        for (int i = 0; i < numargs; i++) {
            s.add("R"+i);
        }

        while (qit.hasNext()) {
            Quad q = qit.next();
            for (RegisterOperand def : q.getDefinedRegisters()) {
                s.add(def.getRegister().toString());
            }
            for (RegisterOperand use : q.getUsedRegisters()) {
                s.add(use.getRegister().toString());
            }
        }

        entry = new VarSet();
		//entry.set = new HashSet<String>(VarSet.universalSet);
        exit = new VarSet();
        transferfn.val = new VarSet();
        for (int i=0; i<in.length; i++) {
            in[i] = new VarSet();
            out[i] = new VarSet();
        }
    }

    public void postprocess(ControlFlowGraph cfg) {
        /*System.out.println("entry: "+entry.toString());
        for (int i=1; i<in.length; i++) {
            System.out.println(i+" in:  "+in[i].toString());
            System.out.println(i+" out: "+out[i].toString());
        }
        System.out.println("exit: "+exit.toString());*/
		QuadIterator qit = new QuadIterator(cfg);
		while (qit.hasNext()) {
			Quad q = qit.next();
			Operator op = q.getOperator();
			if (op instanceof Operator.NullCheck) {
				//System.out.println("Got here!!");
	            for (RegisterOperand use : q.getUsedRegisters()) {
					//System.out.println(in[q.getID()]);
					//System.out.println("Use: "+use.toString());
					if (in[q.getID()].set.contains(use.getRegister().toString())) {
						//System.out.println("GOT HERE");
						System.out.printf(" "+q.getID());
					}
	            }
			}
		}
		System.out.printf("\n");
    }

    /* Is this a forward dataflow analysis? */
    public boolean isForward() { return true; }

    /* Routines for interacting with dataflow values. */

    public Flow.DataflowObject getEntry() 
    { 
        Flow.DataflowObject result = newTempVar();
        result.copy(entry); 
        return result;
    }
    public Flow.DataflowObject getExit() 
    { 
        Flow.DataflowObject result = newTempVar();
        result.copy(exit); 
        return result;
    }
    public Flow.DataflowObject getIn(Quad q) 
    {
        Flow.DataflowObject result = newTempVar();
        result.copy(in[q.getID()]); 
        return result;
    }
    public Flow.DataflowObject getOut(Quad q) 
    {
        Flow.DataflowObject result = newTempVar();
        result.copy(out[q.getID()]); 
        return result;
    }
    public void setIn(Quad q, Flow.DataflowObject value) 
    { 
        in[q.getID()].copy(value); 
    }
    public void setOut(Quad q, Flow.DataflowObject value) 
    { 
        out[q.getID()].copy(value); 
    }
    public void setEntry(Flow.DataflowObject value) 
    { 
        entry.copy(value); 
    }
    public void setExit(Flow.DataflowObject value) 
    { 
        exit.copy(value); 
    }

    public Flow.DataflowObject newTempVar() { return new VarSet(); }

    /* Actually perform the transfer operation on the relevant
     * quad. */

    private TransferFunction transferfn = new TransferFunction ();
    public void processQuad(Quad q) {
        transferfn.val.copy(in[q.getID()]);
        transferfn.visitQuad(q);
        out[q.getID()].copy(transferfn.val);
    }

    /* The QuadVisitor that actually does the computation */
    public static class TransferFunction extends QuadVisitor.EmptyVisitor {
        VarSet val;
        @Override
        public void visitQuad(Quad q) {
			for (RegisterOperand def : q.getDefinedRegisters()) {
                val.killVar(def.getRegister().toString());
            }
            Operator op = q.getOperator();
			if (op instanceof Operator.NullCheck) {
	            for (RegisterOperand use : q.getUsedRegisters()) {
	                val.genVar(use.getRegister().toString());
	            }
			}
        }
    }
	
    /*
     * args is an array of class names
     * method should print out a list of quad ids of redundant null checks
     * for each function as described on the course webpage
     */
    public static void main(String[] args) {
        //fill me in
		String[] mySolverArgs  = new String[3];
		mySolverArgs[0] = "submit.MySolver";
		mySolverArgs[1] = "optimize.FindRedundantNullChecks"; //TODO: change this to FindRedundantNullChecks;
		for (int i = 0; i < args.length; i++) {
			mySolverArgs[2] = args[i];
			Flow.main(mySolverArgs);
		}	
    }
}
