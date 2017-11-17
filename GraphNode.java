package edu.uwec.math.research.SHDiagClassVersion;
/******************************************************************************************************
 * Semi-Hypercube Diagnostics
 ******************************************************************************************************
 * 
 * Designed by: Mitchell L Lemons
 * Designed on: 25 November 2014
 * Designed for: Research on the algebra associated with the Hasse graphs of semi-hypercubes.
 * 		Research Partner: Austin D Riedl
 * 		Research Mentor: Colleen M Duffy
 * 
 * Abstract:
 * This program is designed to be used to create the Hasse graphs of automorphisms on n-dimensional
 * semi-hypercubes. From these Hasse graphs, we can then produce the generating function for these
 * graphs. It is our hope that these functions will help in the finding of a generating formula
 * for the generating functions, and hence understand what the algebra of the automorphisms on these
 * n-dimensional semi-hypercubes are.
 * 
 * Within This Class:
 * This is the class that holds all of the information needed for the points on the Hasse graph,
 * including the face and its dimension. Other than that, there is not much to this class.
 * 
 ******************************************************************************************************
 *
 ******************************************************************************************************
 */


public class GraphNode {
	String name;
	int dim;
	
	/**<h1>Graph Node</h1><p>
	 * 
	 * This function constructs the graph node with the given face and dimension.
	 * 
	 * @param s - Face
	 * @param d - Dimension
	 */
	public GraphNode(String s, int d){
		name=s;
		dim=d;
	}
	
	/**<h1>Get Name</h1><p>
	 * 
	 * @return The name of the GraphNode.
	 */
	public String getName(){
		return name;
	}
	
	/**<h1>Get Dimension</h1><p>
	 * 
	 * @return The dimension of the GraphNode.
	 */
	public int getDim(){
		return dim;
	}

	/**<h1>Equals</h1><p>
	 * 
	 * This function compares two GraphNodes based on the names.
	 * 
	 * @param that - Second GraphNode
	 * @return A true/false statement regarding the equality of the names.
	 */
	public boolean equals(GraphNode that){
		return this.name.equals(that.getName());
	}
	
	@Override
	public String toString(){
		return name+": dim "+dim;
	}
	
}

