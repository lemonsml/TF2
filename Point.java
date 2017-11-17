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
 * This is the class that holds all of the information about each specific point. Each point has a 
 * unique name, along with a unique set of coordinates (a,b,c,d,e,f). They can be "compared" to see
 * if two points are equal, and that is only done using the coordinates, and not the name. You can 
 * also get specific coordinate values from the Point
 * 
 ******************************************************************************************************
 *
 ******************************************************************************************************
 */


public class Point {

	//instances
	char name;
	int a;
	int b;
	int c;
	int d;
	int e;
	int f;
	int g;
	
	/** <h1>Point</h1><p>
	 * 
	 * This function creates a point using the information given.
	 * 
	 * @param n - Name of the point
	 * @param a1 - Value for a
	 * @param b1 - Value for b
	 * @param c1 - Value for c
	 * @param d1 - Value for d
	 * @param e1 - Value for e
	 * @param f1 - Value for f
	 */
	public Point(char n, int a1, int b1, int c1, int d1, int e1, int f1, int g1){
		name=n;
		a=a1;
		b=b1;
		c=c1;
		d=d1;
		e=e1;
		f=f1;
		g=g1;
	}
	
	/**<h1>Get Part</h1><p>
	 * 
	 * This function returns the specific coordinate value that was requested.
	 * 
	 * @param c - Coordinate value holder.
	 * @return Coordinate value at the holder.
	 */
	public int getPart(char c){
		if(c=='a'){return a;}
		if(c=='b'){return b;}
		if(c=='c'){return this.c;}
		if(c=='d'){return d;}
		if(c=='e'){return e;}
		if(c=='f'){return f;}
		if(c=='g'){return g;}
		return 0;
	}
	
	public char getName(){
		return name;
	}
	
	@Override
	public String toString(){
		return name+": ("+a+","+b+","+c+","+d+","+e+","+f+","+g+")";
	}
	
	@Override
	public boolean equals(Object o){
		Point p=(Point) o;
		
		return (this.a==p.a && this.b==p.b && this.c==p.c && this.d==p.d && this.e==p.e && this.f==p.f && this.f==p.f);
	}
	
	
	
	
}
