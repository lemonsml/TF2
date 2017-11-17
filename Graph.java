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
 * This is the class that is the Hasse graph. Points are created and added to the graph (that uses
 * a matrix), and connections are made. Once the graph connections are made, we can evaluate a value 
 * for the number of paths that can be taken from one graph node to another, using recursion.
 * 
 ******************************************************************************************************
 *
 ******************************************************************************************************
 */


import java.util.*;

public class Graph{
	ArrayList<ArrayList<Integer>> matrix;
	ArrayList<GraphNode> array;
	
	/**<h1>Graph</h1><p>
	 * 
	 * This function creates a graph with nothing in it.
	 */
	public Graph(){
		//creates new matrix and array
		matrix = new ArrayList<ArrayList<Integer>>();
		array = new ArrayList<GraphNode>();
	}
	
	/**<h1>Add Point</h1><p>
	 * 
	 * This function creates a GraphNode from the given face and dimension, and adds it to the graph.
	 * It adds the GraphNode to the array and expands the connection matrix.
	 * 
	 * @param it - String value of the face.
	 * @param dim - Dimension of the face.
	 */
	public void addPoint(String it, int dim){
		
		//creates the GraphNode
		GraphNode gn = new GraphNode(it, dim);
		
		//adds the GraphNode to the array
		array.add(gn);
		
		//expands the matrix for the new node
		matrix.add(new ArrayList<Integer>());
		for(int i = 0; i<matrix.size()-1;i++){
			matrix.get(i).add(0);
		}
		for(int i = 0; i<array.size();i++){
			matrix.get(matrix.size()-1).add(0);
		}
	}

	/**<h1>Build Connections</h1><p>
	 * 
	 * This function takes what ever points that are within the graph at the time and makes connections.
	 * Only faces that are contained within each other have connections.
	 * These connections are weighted based on the difference in dimensions of the two faces.
	 */
	public void buildConnections() {
		
		//retrieves 2 GraphNodes from array
		for(int i=0;i<array.size();i++){
			for(int j=i;j<array.size();j++){
				
				//checks for containment, and if contained, adds the connection.
				//The connection is weighted by the difference in dimension
				if(isWithin(array.get(i).getName(),array.get(j).getName())){
					matrix.get(i).set(j, array.get(i).getDim()-array.get(j).getDim());
				}
			}
		}
	}
	
	/**<h1>Get Graph Node</h1><p>
	 * 
	 * This Function retrieves the GraphNode that is associated with the face that it is passed.
	 * 
	 * @param s - Name of the face.
	 * @return The graph node that is associated with the face.
	 */
	private GraphNode getGN(String s){
		//walks the array and checks the names
		//returns it if it is found, else null
		for(int i=0;i<array.size();i++){
			if(s.equals(array.get(i).getName())){return array.get(i);}
		}
		
		return null;
	}
	
	/**<h1>Return Coefficient Variable</h1><p>
	 * 
	 * This function returns the <b><i>net</i></b> number of paths that exist between two faces.
	 * It calls the recursive function <i>getIntCoef()</i> if it is not a trivial case.
	 * 
	 * @param b - Larger dimensional (Big) face.
	 * @param s - Smaller dimensional (Small) face.
	 * @return The <b><i>net</i></b> number of paths that exist between the two faces
	 */
	public int returnCoefVar(String b, String s){
		if(!isWithin(b, s)){
			return 0;
		}else if(b.equals(s)){
			return 1;
		}
		GraphNode BGN = getGN(b);
		GraphNode SGN = getGN(s);
		
		return getIntCoef(BGN,SGN);
	}
	
	/**<h1>Get Intermediate Coefficient</h1><p>
	 * 
	 * This recursive function obtains the <b><i>net</i></b> number of paths that exist between two GraphNodes.
	 * It checks if the difference in dimension is 1 and if there are no more intermediate nodes as the base cases.
	 * If there are more intermediate nodes, you sum up all of the coefficients of the intermediate nodes multiplied by the dimension diferential of the top node and the intermediate node.
	 * 
	 * @param BGN - Larger dimensional (Bigger) GraphNode
	 * @param SGN - Smaller dimensional (Smaller) GraphNode
	 * @return The <b><i>net</i></b> number of paths that exist between two GraphNodes.
	 */
	private int getIntCoef(GraphNode BGN, GraphNode SGN) {

		//Base Case#1: dimension differential is 1
		if(BGN.getDim()-SGN.getDim()==1){
			return 1;
		}
		
		//get the indexes of the nodes in the array
		int bIndex = getIndex(BGN);
		int sIndex = getIndex(SGN);
		
		//creates a list of nodes that are intermediate between the two nodes
		ArrayList<GraphNode> intermediateNodes = new ArrayList<GraphNode>();
		//walks all of the other nodes within the two
		for(int i =bIndex+1; i<sIndex;i++){
			//if the node is within the bigger one, and the smaller one is within the node, add it to the list
			if(isWithin(BGN.getName(), array.get(i).getName())&&isWithin(array.get(i).getName(),SGN.getName())){
				intermediateNodes.add(array.get(i));
			}
		}
		
		//Base Case#2: no intermediate nodes
		if(intermediateNodes.isEmpty()){
			return coefDimDif(BGN,SGN);
		}
		
		//Refine:
		//set the dimension Differential to the the main coefficient
		int newRet = coefDimDif(BGN, SGN);
		
		//add all of the possibilities for the intermediate nodes
		//each addition will be the dimension differential between the top node and the intermediate node MULTIPLIED by the coefficient of the intermediate node and the small node
		for(int i=0;i<intermediateNodes.size();i++){
			newRet += coefDimDif(BGN, intermediateNodes.get(i))*getIntCoef(intermediateNodes.get(i), SGN);
		}
		
		//returns the new coefficient
		return newRet;
	}

	/**<h1>Dimensional Differential</h1><p>
	 * 
	 * This function returns the dimensional differential of the two GraphNodes.
	 * If the nodes are separated by an odd number of dimensions, it will return 1.
	 * If the nodes are separated by an even number of dimensions, it will return -1.
	 * 
	 * @param BGN - Larger dimensional (Bigger) GraphNode
	 * @param SGN - Smaller dimensional (Smaller) GraphNode
	 * @return The dimensional differential between the GraphNodes.
	 */
	private int coefDimDif(GraphNode BGN, GraphNode SGN) {
		if((BGN.getDim()-SGN.getDim())%2==0){
			return -1;
		}else{
			return 1;
		}
	}

	/**<h1>Get Index</h1><p>
	 * 
	 * This function returns the index of a graph node within the array.
	 * 
	 * @param node - GraphNode to be looked up.
	 * @return The index of the GraphNode.
	 */
	private int getIndex(GraphNode node) {
		//walks the array until if finds the node it is looking for
		for(int i = 0; i<array.size();i++){
			if(array.get(i).equals(node)){return i;}
		}
		return 0;
	}

	/**<h1>Is Within</h1><p>
	 * 
	 * This function checks to see if one face is within another.
	 * 
	 * @param b - Larger dimensional (Big) face.
	 * @param s - Smaller dimensional (Small) face.
	 * @return A true/false statement regarding the containment of faces.
	 */
	private boolean isWithin(String b, String s){
		for(int i=0;i<s.length();i++){
			if(!b.contains(Character.toString(s.charAt(i)))){return false;}
		}
		return true;
	}
	
}
