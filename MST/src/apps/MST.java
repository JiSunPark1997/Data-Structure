package apps;

import structures.*;
import java.util.ArrayList;
import java.util.Iterator;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		 PartialTreeList list = new PartialTreeList();
		   Vertex[] vertex= graph.vertices;
		   PartialTree tree;
		   boolean[] visited = new boolean[vertex.length];
		   PartialTree.Arc arc = null;
		   int count = 0;
		   for(int i =0 ; i < vertex.length;i++)
		   {
		       Vertex.Neighbor x = vertex[i].neighbors;
		       tree = new PartialTree(vertex[i]);
		       visited[i] = true;
		       System.out.println(vertex[i]);
		       MinHeap<PartialTree.Arc> P = tree.getArcs(); 
		      
		       while(x!= null)
		       {
		       arc = new PartialTree.Arc(vertex[i], x.vertex,x.weight);
		       P.insert(arc);
		       P.siftDown(count);
		       x = x.next;
		       }
		       if(visited[i] == true)
		       {
		    	   list.append(tree);
		       }
		       count++;
		      
		   }
		  
		       return list;
	}
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		ArrayList<PartialTree.Arc> list = new ArrayList();  
	       while(ptlist.size()>1){ 
	           PartialTree partialTree = ptlist.remove();
	           PartialTree.Arc pArc = null;
	           PartialTree.Arc pArc2 = partialTree.getArcs().getMin();
	           Vertex v1 = pArc2.v1; 
	           Vertex v2 = pArc2.v2;
	           check(partialTree,v1,v2,pArc,pArc2); 
	           pArc = partialTree.getArcs().deleteMin(); 
	           System.out.println(pArc +" "+"is a component of the MST");
	           PartialTree partialTree2 = ptlist.removeTreeContaining(pArc.v2); 
	           partialTree.merge(partialTree2);
	            list.add(pArc);
	            ptlist.append(partialTree);
	       }     
	       return list;
	   }
	   private static void check(PartialTree partialTree, Vertex v1,Vertex v2, PartialTree.Arc pArc, PartialTree.Arc pArc2)
	   {
	    while(compare(v2, partialTree) == true)
	       {
	    	pArc = partialTree.getArcs().deleteMin();       
	       v1 = pArc2.v1; 
	       v2 = pArc2.v2;
	       }
	   }
	   private static boolean compare(Vertex v2, PartialTree partialTree)
	   {
	        while(v2 != null)
	        {
	            if(partialTree.getRoot() == v2)
	            {
	                return true;
	            }
	            if(v2.equals(v2.parent))
	            {
	                return false;
	            }
	            v2 = v2.parent;
	        }
	        return false;
	    }
	}


	    
