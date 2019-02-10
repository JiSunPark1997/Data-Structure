import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	Node poly;
	
	public Polynomial(){
		poly=null;
	}
	
	
	public Polynomial(BufferedReader b) throws IOException{
		String line;
		StringTokenizer tokenizer;
		float coeff;
		int degree;
		poly=null;
		while ((line = b.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			coeff = Float.parseFloat(tokenizer.nextToken());
			degree = Integer.parseInt(tokenizer.nextToken());
			poly = new Node(coeff, degree, poly);
		}
	}
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	public static Node add(Node poly1, Node poly2) {
	      Node sum = new Node(0, 0, null);
	        if (poly1 == null) {
	            return poly2;
	        }
	        if (poly2 == null) {
	            return poly1;
	        }
	        if (poly1 != null || poly2 != null) {
	            if (poly1.term.degree == poly2.term.degree) {
	            	sum.term.degree = poly1.term.degree;
	            	sum.term.coeff = poly1.term.coeff + poly2.term.coeff;
	                if (sum.term.coeff == 0) {
	                	sum.term = null;
	                }
	            } else if (poly1.term.degree < poly2.term.degree) {
	            	sum.next = poly1;
	                poly1 = poly1.next;
	            } else if (poly2.term.degree < poly1.term.degree) {
	            	sum = poly2;
	                poly2 = poly2.next;
	            }
	            Node p1 = poly1;
	            Node p2 = poly2;
	            Node sumRef = sum;
	            while (p1 != null || p2 != null) {
	                if ((p1.next != null && p2.next != null) && p1.term.degree == p2.term.degree) {
	                    sumRef.term.degree = p1.term.degree;
	                    sumRef.term.coeff = p1.term.coeff + p2.next.term.coeff;
	 
	                    p1 = p1.next;
	                    p2 = p2.next;
	 
	                } else if ((p1.next != null && p2.next != null) && p1.term.degree < p2.term.degree) {
	                    sumRef.term = p1.term;
	                    p1 = p1.next;
	                } else if ((p2.next != null && p2.next != null) && p2.term.degree < p1.term.degree) {
	                    sumRef.term = p2.term;
	                    p2 = p2.next;
	                }
	 
	                if (sumRef.term.coeff != 0) {
	                    sumRef = sumRef.next;
	                }
	 
	            }
	 
	        }
	 
	        System.out.println(sum);
	        return sum;
	}
	 
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		if (poly1 == null || poly2 == null)
	        return null;
	    Node polynomial1 = poly1;
	    Node polynomial2 = poly2;
	    Node terms = null;
	    float coeffProd;
	    int expSum;
	    int maxDegree = 0;
	    while (polynomial1 != null) {
	        while (polynomial2 != null) {
	            coeffProd = polynomial1.term.coeff * polynomial2.term.coeff;
	            expSum = polynomial1.term.degree + polynomial2.term.degree;
	            terms = new Node(coeffProd, expSum, terms);
	            if (expSum > maxDegree)
	                maxDegree = expSum;
	            polynomial2 = polynomial2.next;
	        }
	        polynomial1 = polynomial1.next;
	        polynomial2 = poly2;
	    }
	    Node combine = null;
	    for (int i = 0; i<= maxDegree; i++) {
	        Node temp = terms;
	        float sum = 0;
	        while (temp != null) {
	            if (temp.term.degree == i)
	            	sum+=temp.term.coeff;
	            temp = temp.next;
	        }
	        if (sum != 0)
	        	combine = new Node(sum, i, combine);
	    }
	    return combine;
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		float value=0;
		Node current=poly;
		while(current!=null){
			value+=current.term.coeff*(float)Math.pow(x, current.term.degree);
			current=current.next;
		}
		return value;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
