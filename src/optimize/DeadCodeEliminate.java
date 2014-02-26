package optimize;

import joeq.Class.jq_Class;
import joeq.Main.Helper;
import flow.Flow;
import java.util.*;
import joeq.Compiler.Quad.Operand.RegisterOperand;

// some useful things to import. add any additional imports you need.
import joeq.Compiler.Quad.*;

public class DeadCodeEliminate implements Flow.Analysis {

    private Set<Integer> quadIDs;    

    public Set<String> getInSet(int quadID) {

          return null;

    }

        
    public Set<String> getOutSet(int quadID) {

          return null;

    }

    public void preprocess(ControlFlowGraph cfg) {

        

    }

    public void postprocess(ControlFlowGraph cfg) {
		QuadIterator qit = new QuadIterator(cfg);
		while (qit.hasNext()) {

                  Quad q = qit.next();
                  if (quadIDs.contains(q.getID())) qit.remove();

		}
		Iterator itr = quadIDs.iterator();
		while (itr.hasNext()) {
			Integer i = itr.next();
			System.out.printf(" "+i.intValue());
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
        
        public void visitQuad(Quad q, Set<String> outSet) {

          boolean dont_remove = true;

          for (RegisterOperand def : q.getDefinedRegisters()) { 

            if (outSet.contains(def.toString())) dont_remove = false;

          }

          
          for (RegisterOperand use : q.getUsedRegisters()) { 

            if (outSet.contains(use.toString())) dont_remove = false;

          }

          if (dont_remove == false) {
            
            quadIDs.add(q.getID());

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
    }
}
