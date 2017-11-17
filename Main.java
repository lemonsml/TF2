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
 * This is the main driver class for the program. All of the generation of permutations, Points, faces, 
 * Graphs,and connections are initiated within this class. This class takes a user input (from command 
 * line arguments or through arguments in eclipse) for a specific L. It then determines the dimension
 * that we are dealing with and the permutations of both coordinate values and points. From the 
 * dimension, it then figures all of the n-dimensional faces within the semi-hypercube, and from the 
 * permutations, it can figure out which of these faces are fixed within the permutation. Once all of 
 * the fixed faces are calculated, they are then put into a graph (along with info on their dimension),
 * and weighted connections of containment are made. From these connections, we can calculate how many
 * paths there are from one level to the next, and we can count these to give us the coefficients to
 * the generating function of the Hasse graph.
 * 
 ******************************************************************************************************
 *
 ******************************************************************************************************
 */

/*
 ******************************************************************************************************
 * Items to be fixed or improved upon
 ******************************************************************************************************
 *
 * 1) 	The program needs to be able to implement the full automorphism group, so there needs to be a way
 * 		for it to take into account the semi-direct product of (Z_2)^n.
 * 
 * 2)	It would be nice to be able to go into 7 or 8 dimensions. the names might not show up nicely,
 * 		but the character values for the names of each point would still be different
 * 		=> Possibly go caps for a-x and then caps Greek for 1-8.
 * 		<= Need to update points class and permutation, and all fixed face options
 * 
 ******************************************************************************************************
 *
 ******************************************************************************************************
 */

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	//lists of fixed dimensional faces
	public static ArrayList<String> fixededges = new ArrayList<String>();
	public static ArrayList<String> fixedface2D = new ArrayList<String>();
	public static ArrayList<String> fixedface3D = new ArrayList<String>();
	public static ArrayList<String> fixedface4Dtet = new ArrayList<String>();
	public static ArrayList<String> fixedshD4 = new ArrayList<String>();
	public static ArrayList<String> fixedface5DSimp = new ArrayList<String>();
	public static ArrayList<String> fixedshD5 = new ArrayList<String>();
	public static ArrayList<String> fixedface6DSimp = new ArrayList<String>();
	public static ArrayList<String> fixedshD6 = new ArrayList<String>();
	public static String sdpStr = "";


	public static int[] sdp = new int[7];

	//list of points as to what dimension they are needed in
	public static ArrayList<Point>[] listOfPtsPerDim = new ArrayList[8];

	//array of characters that are needed for permuting coordinates
	public static final char[] arrayChars = new char[]{' ','a','b','c','d','e','f','g'};
	
	//n=6
	public static int[][] arrayOfSDPs = new int[][]{    {0,0,0,0,0,0,0},
		{0,0,0,0,1,1,0},
		{0,0,0,1,0,1,0},
		{0,0,0,1,1,0,0},
		{0,0,1,0,0,1,0},
		{0,0,1,0,1,0,0},
		{0,0,1,1,0,0,0},
		{0,0,1,1,1,1,0},
		{0,1,0,0,0,1,0},
		{0,1,0,0,1,0,0},
		{0,1,0,1,0,0,0},
		{0,1,0,1,1,1,0},
		{0,1,1,0,0,0,0},
		{0,1,1,0,1,1,0},
		{0,1,1,1,0,1,0},
		{0,1,1,1,1,0,0},
		{1,0,0,0,0,1,0},
		{1,0,0,0,1,0,0},
		{1,0,0,1,0,0,0},
		{1,0,0,1,1,1,0},
		{1,0,1,0,0,0,0},
		{1,0,1,0,1,1,0},
		{1,0,1,1,0,1,0},
		{1,0,1,1,1,0,0},
		{1,1,0,0,0,0,0},
		{1,1,0,0,1,1,0},
		{1,1,0,1,0,1,0},
		{1,1,0,1,1,0,0},
		{1,1,1,0,0,1,0},
		{1,1,1,0,1,0,0},
		{1,1,1,1,0,0,0},
		{1,1,1,1,1,1,0}
		};

	//n=5
//	public static int[][] arrayOfSDPs = new int[][]{    
//		{0,0,0,0,0,0,0},
//		{0,0,0,1,1,0,0},
//		{0,0,1,0,1,0,0},
//		{0,0,1,1,0,0,0},
//		{0,1,0,0,1,0,0},
//		{0,1,0,1,0,0,0},
//		{0,1,1,0,0,0,0},
//		{0,1,1,1,1,0,0},
//		{1,0,0,0,1,0,0},
//		{1,0,0,1,0,0,0},
//		{1,0,1,0,0,0,0},
//		{1,0,1,1,1,0,0},
//		{1,1,0,0,0,0,0},
//		{1,1,0,1,1,0,0},
//		{1,1,1,0,1,0,0},
//		{1,1,1,1,0,0,0}
//};

	/**<h1>MAIN</h1><p>
	 * 
	 * This is the beginning of the program.
	 * This section sets up all of the possible points, and checks to see if what the user entered is a legitimate variable for the program to run
	 * All of the points are added to the dimensions that they go into.
	 * The program then checks each argument to see whether it is valid to run the program or not.
	 * If the style is correct, and all the numbers are correct for the dimension, then the program will compute all pertinent information for that L.
	 * If there are more arguments, it will loop until all arguments are looked at.
	 * 
	 * @param args - User imputed L
	 */
	public static void main(String[] args) {
		String[] send = new String[1];
		System.out.println("Please enter in the full and correct L.");
		System.out.println("This means that the number of places in the tuple = n.");
		System.out.println("E.x. (0,2,0,0) or (2,2,0,0,0,0) or (1,1,0,1,0,0,0)");
		System.out.println("If you wish to add a Semi Direct Product, please do so with a '+'.");
		System.out.println("E.x. (0,2,0,0)+(1,0,0,1) or (2,2,0,0,0,0)+(1,0,1,0,1,1)");
		Scanner scan = new Scanner(System.in);
		send[0]=scan.next();
		getLFromFULL(send);




	}

	public static void getLFromFULL(String[] args){
		//initialize list of points
		for(int i=0;i<listOfPtsPerDim.length;i++){
			listOfPtsPerDim[i]=new ArrayList<Point>();
		}

		//adds the points to where they need to be added
		listOfPtsPerDim[1].add(new Point('1', 0, 0, 0, 0, 0, 0, 0));

		listOfPtsPerDim[2].add(new Point('2', 1, 1, 0, 0, 0, 0, 0));

		listOfPtsPerDim[3].add(new Point('3', 1, 0, 1, 0, 0, 0, 0));
		listOfPtsPerDim[3].add(new Point('4', 0, 1, 1, 0, 0, 0, 0));

		listOfPtsPerDim[4].add(new Point('5', 1, 1, 1, 1, 0, 0, 0));
		listOfPtsPerDim[4].add(new Point('6', 0, 0, 1, 1, 0, 0, 0));
		listOfPtsPerDim[4].add(new Point('7', 0, 1, 0, 1, 0, 0, 0));
		listOfPtsPerDim[4].add(new Point('8', 1, 0, 0, 1, 0, 0, 0));

		listOfPtsPerDim[5].add(new Point('a', 1, 0, 0, 0, 1, 0, 0));
		listOfPtsPerDim[5].add(new Point('b', 0, 1, 0, 0, 1, 0, 0));
		listOfPtsPerDim[5].add(new Point('c', 0, 0, 1, 0, 1, 0, 0));
		listOfPtsPerDim[5].add(new Point('d', 0, 0, 0, 1, 1, 0, 0));
		listOfPtsPerDim[5].add(new Point('e', 1, 1, 1, 0, 1, 0, 0));
		listOfPtsPerDim[5].add(new Point('f', 1, 1, 0, 1, 1, 0, 0));
		listOfPtsPerDim[5].add(new Point('g', 1, 0, 1, 1, 1, 0, 0));
		listOfPtsPerDim[5].add(new Point('h', 0, 1, 1, 1, 1, 0, 0));

		listOfPtsPerDim[6].add(new Point('i', 0, 0, 0, 0, 1, 1, 0));
		listOfPtsPerDim[6].add(new Point('j', 1, 1, 0, 0, 1, 1, 0));
		listOfPtsPerDim[6].add(new Point('k', 1, 0, 1, 0, 1, 1, 0));
		listOfPtsPerDim[6].add(new Point('l', 0, 1, 1, 0, 1, 1, 0));
		listOfPtsPerDim[6].add(new Point('m', 1, 1, 1, 1, 1, 1, 0));
		listOfPtsPerDim[6].add(new Point('n', 0, 0, 1, 1, 1, 1, 0));
		listOfPtsPerDim[6].add(new Point('o', 0, 1, 0, 1, 1, 1, 0));
		listOfPtsPerDim[6].add(new Point('p', 1, 0, 0, 1, 1, 1, 0));
		listOfPtsPerDim[6].add(new Point('q', 1, 0, 0, 0, 0, 1, 0));
		listOfPtsPerDim[6].add(new Point('r', 0, 1, 0, 0, 0, 1, 0));
		listOfPtsPerDim[6].add(new Point('s', 0, 0, 1, 0, 0, 1, 0));
		listOfPtsPerDim[6].add(new Point('t', 0, 0, 0, 1, 0, 1, 0));
		listOfPtsPerDim[6].add(new Point('u', 1, 1, 1, 0, 0, 1, 0));
		listOfPtsPerDim[6].add(new Point('v', 1, 1, 0, 1, 0, 1, 0));
		listOfPtsPerDim[6].add(new Point('w', 1, 0, 1, 1, 0, 1, 0));
		listOfPtsPerDim[6].add(new Point('x', 0, 1, 1, 1, 0, 1, 0));

		listOfPtsPerDim[7].add(new Point(('!'), 0, 0, 0, 0, 0, 1, 1));
		listOfPtsPerDim[7].add(new Point(('"'), 0, 0, 0, 0, 1, 0, 1));
		listOfPtsPerDim[7].add(new Point(('#'), 0, 0, 0, 1, 0, 0, 1));
		listOfPtsPerDim[7].add(new Point(('$'), 0, 0, 1, 0, 0, 0, 1));
		listOfPtsPerDim[7].add(new Point(('%'), 0, 1, 0, 0, 0, 0, 1));
		listOfPtsPerDim[7].add(new Point(('&'), 1, 0, 0, 0, 0, 0, 1));
		listOfPtsPerDim[7].add(new Point(('*'), 1, 1, 1, 0, 0, 0, 1));
		listOfPtsPerDim[7].add(new Point((','), 1, 1, 0, 1, 0, 0, 1));
		listOfPtsPerDim[7].add(new Point(('A'), 1, 1, 0, 0, 1, 0, 1));
		listOfPtsPerDim[7].add(new Point(('B'), 1, 1, 0, 0, 0, 1, 1));
		listOfPtsPerDim[7].add(new Point(('C'), 1, 0, 1, 1, 0, 0, 1));
		listOfPtsPerDim[7].add(new Point(('D'), 1, 0, 1, 0, 1, 0, 1));
		listOfPtsPerDim[7].add(new Point(('E'), 1, 0, 1, 0, 0, 1, 1));
		listOfPtsPerDim[7].add(new Point(('F'), 1, 0, 0, 1, 1, 0, 1));
		listOfPtsPerDim[7].add(new Point(('G'), 1, 0, 0, 1, 0, 1, 1));
		listOfPtsPerDim[7].add(new Point(('H'), 1, 0, 0, 0, 1, 1, 1));
		listOfPtsPerDim[7].add(new Point(('I'), 0, 1, 1, 1, 0, 0, 1));
		listOfPtsPerDim[7].add(new Point(('J'), 0, 1, 1, 0, 1, 0, 1));
		listOfPtsPerDim[7].add(new Point(('K'), 0, 1, 1, 0, 0, 1, 1));
		listOfPtsPerDim[7].add(new Point(('L'), 0, 1, 0, 1, 1, 0, 1));
		listOfPtsPerDim[7].add(new Point(('M'), 0, 1, 0, 1, 0, 1, 1));
		listOfPtsPerDim[7].add(new Point(('N'), 0, 1, 0, 0, 1, 1, 1));
		listOfPtsPerDim[7].add(new Point(('O'), 0, 0, 1, 1, 1, 0, 1));
		listOfPtsPerDim[7].add(new Point(('P'), 0, 0, 1, 1, 0, 1, 1));
		listOfPtsPerDim[7].add(new Point(('Q'), 0, 0, 1, 0, 1, 1, 1));
		listOfPtsPerDim[7].add(new Point(('R'), 0, 0, 0, 1, 1, 1, 1));
		listOfPtsPerDim[7].add(new Point(('S'), 1, 1, 1, 1, 1, 0, 1));
		listOfPtsPerDim[7].add(new Point(('T'), 1, 1, 1, 1, 0, 1, 1));
		listOfPtsPerDim[7].add(new Point(('U'), 1, 1, 1, 0, 1, 1, 1));
		listOfPtsPerDim[7].add(new Point(('V'), 1, 1, 0, 1, 1, 1, 1));
		listOfPtsPerDim[7].add(new Point(('W'), 1, 0, 1, 1, 1, 1, 1));
		listOfPtsPerDim[7].add(new Point(('X'), 0, 1, 1, 1, 1, 1, 1));

		//loops for all of the arguments
		for(int i =0; i<args.length;i++){
			if(args[i].contains("+")){
				int a=args[i].length();
				int b=0;
				while(args[i].charAt(b)!='+'){
					b++;
				}
				String end = args[i].substring(b+1);
				String begin = args[i].substring(0, b);
				if(end.length()!=begin.length()){
					System.out.println("Please enter a correct semi-direct product");
					System.exit(0);
				}

				if(!(end.charAt(0)=='('&&end.charAt(end.length()-1)==')')){
					System.out.println("Please enter a correct semi-direct product");
					System.exit(0);
				}

				int[] check = new int[7];

				for(int j=1;j<end.length()-1;j++){
					if(j%2==0&&end.charAt(j)!=','){
						System.out.println("Please enter a correct semi-direct product");
						System.exit(0);
					}
					if(j%2==1&&!isNumeric(Character.toString(end.charAt(j)))){
						System.out.println("Please enter a correct semi-direct product");
						System.exit(0);
					}else if(j%2==1){
						check[(j-1)/2]=Integer.parseInt(Character.toString(end.charAt(j)));
					}
				}

				sdp=check;

				int sdpcount=0;
				for(int j=0;j<sdp.length;j++){
					sdpcount+=sdp[j];
				}

				if(sdpcount%2!=0){
					System.out.println("Please enter a correct semi-direct product");
					System.exit(0);
				}

				String blah = "(";
				for(int j = 0; j<sdp.length-1;j++){
					blah+=sdp[j];
					if(j!=sdp.length-2){blah+=",";}
				}

				sdpStr=end;

				//		System.out.println("SDP = "+blah+")");

				checkAndRun(begin);
			}else{
				for(int j=0; j<sdp.length;j++){
					sdp[j]=0;
				}
				sdpStr="[0]";
				checkAndRun(args[i]);
			}


		}
	}

	public static void checkAndRun(String stringgy){
		//checks the argument to see if it has ()
		String s=stringgy;
		boolean good = true;
		if(!(s.charAt(0)=='('&&s.charAt(s.length()-1)==')')){
			good=false;
		}

		//checks for commas and correct integers.
		for(int j=1;j<s.length()-2;j++){
			if(j%2==0&&s.charAt(j)!=','){
				good=false;
			}
			if(j%2==1&&!isNumeric(Character.toString(s.charAt(j)))){
				good=false;
			}
		}

		//get the dimension from the numbers imputed
		int calcDim = 0;
		if(good=true){
			for(int j=1;j<((double)s.length())/2;j++){
				calcDim+=  j*Integer.parseInt(Character.toString(s.charAt((j*2-1))));
			}
		}

		//get the dimension from the number of numbers
		int dimentionOfL=0;
		for(int j=0;j<8;j++){
			if(2*j+1==s.length()){
				dimentionOfL=j;
				break;
			}
		}

		//if all the formating and all dimensions line up, run program
		//otherwise, report to user
		if(good&&calcDim==dimentionOfL){
			
			runSHDiagnostics(s, dimentionOfL);
		}else{
			System.out.println("Please enter a valid L, not: "+s);
			System.out.println("");
			System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////////");
			System.out.println("");
		}
	}

	private static int[] runGoodRuined(String coordPerm) {

		int[] goodBad = new int[2];
		if(sdpStr.equals("[0]")){
			System.out.println("all good");
		}else{
			for(int i=0;i<coordPerm.length();i++){
				if(coordPerm.charAt(i)=='('){
					i++;
					int countBad=0;
					while(coordPerm.charAt(i)!=')'){
						if(isNumeric(Character.toString(coordPerm.charAt(i)))){
							int a=Integer.parseInt(Character.toString(coordPerm.charAt(i)));
							int b=(a*2)-1;
							if(sdpStr.charAt(b)=='1'){
								countBad++;
							}
						}
						
						
						i++;
					}
					if(countBad%2==0){
						goodBad[0]++;
					}else{
						goodBad[1]++;
					}
				}
			}
		}
		System.out.println(coordPerm);
		System.out.println("Good: "+goodBad[0]+" Bad: "+goodBad[1]);
		return goodBad;
		
	}

	/**<h1>Semi-Hypercube Diagnostics</h1><p>
	 * 
	 * This is the main portion of this program.<p>
	 * 
	 * This portion runs a full diagnostics on what a type of permutation, L, can do to a Semi-Hypercube.
	 * It creates the permutation of the coordinates based off of the L given.
	 * It then generates all of the faces within a n-Dimensional Semi-Hypercube, and figures out what ones are fixed within the permutation of the coordinates.
	 * A Hasse graph is then created using a graph data structure.
	 * From that Hasse graph, we can then count paths, and find the coefficients of the generating function for the graph.
	 * That can then be used to help describe the algebra associated with multiple graphs.
	 * 
	 * @param argstr - The string from the argument - L.
	 * @param dimentionOfL - Dimension to be used.
	 */
	private static void runSHDiagnostics(String argstr, int dimentionOfL){
		
		PrintWriter writer=null;
		try {
			writer = new PrintWriter("C:/Users/mitch/Desktop/hello.CSV");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		//creates a list of all points needed depending on the dimension of the SH
		ArrayList<Point> list = new ArrayList<Point>();
		for(int i =1; i<dimentionOfL+1;i++){
			boolean added=list.addAll(listOfPtsPerDim[i]);
			if(added==false){
				System.out.println("Didn't work correctly...");
				System.exit(0);
			}
		}


		//creates the permutation of the coordinates
		String coordPerm="";
		int a=0;
		//takes the largest cycle in L and works its way down.
		for(int j=argstr.length()/2;j>0;j--){
			//gets the number of cycles of that length
			int num = Integer.parseInt(Character.toString(argstr.charAt((j*2-1))));
			//if there are cycles of that lenght
			if(num!=0){
				//generates a permutation that many times
				for(int i=0;i<num;i++){
					coordPerm+="(";
					for(int k=0;k<j;k++){
						a++;
						coordPerm+=a;
					}
					coordPerm+=")";
				}
			}
		}

		//prints the coordinate permutation
		System.out.println("Coordinate Permutation: "+coordPerm);
		
	//	int[] goodBad = runGoodRuined(coordPerm);
		
		//prints out the original set of points
		//	System.out.println("Points: " + list);
		//	System.out.println(" ");

		//creates the lists of permuted points
		ArrayList<Point> permlist = new ArrayList<Point>();
		for (int i=0; i<list.size(); i++){
			permlist.add(new Point(list.get(i).name, (list.get(i).getPart(getPrevChar(1, coordPerm))+sdp[0])%2, (list.get(i).getPart(getPrevChar(2, coordPerm))+sdp[1])%2,(list.get(i).getPart(getPrevChar(3, coordPerm))+sdp[2])%2, (list.get(i).getPart(getPrevChar(4, coordPerm))+sdp[3])%2, (list.get(i).getPart(getPrevChar(5, coordPerm))+sdp[4])%2, (list.get(i).getPart(getPrevChar(6, coordPerm))+sdp[5])%2, (list.get(i).getPart(getPrevChar(7, coordPerm))+sdp[6])%2));
		}

		//prints the permuted points
		//	System.out.println("Permuted Points: "+permlist);
		//	System.out.println("");

		//prints out the permutation of points
		String longPerm = findPermutation(list, permlist);
		System.out.println(longPerm);
		//	System.out.println("");

		//gets the fixed points within the permutatin
		ArrayList<String> fixedPts = pts(list, permlist);

		//gets the full amount of n-dimensional faces
		//also appends the list of fixed n-dimensional faces
		ArrayList<String> edges = faceDim1(list, permlist);
		ArrayList<String> face2D = faceDim2(list, permlist, edges);
		ArrayList<String> face3D = faceDim3(list, permlist, face2D);
		ArrayList<String> face4Dtet = simpDim4(list, permlist, face3D);
		ArrayList<String> face4DSH = shDim4(list, permlist, face3D, face2D);
		ArrayList<String> face5Dtet = simpDim5(list, permlist, face4Dtet);
		ArrayList<String> face5DSH = shDim5(list, permlist, face4DSH);	
		ArrayList<String> face6Dtet = simpDim6(list, permlist, face5Dtet);
		ArrayList<String> face6DSH = shDim6(list, permlist, face5DSH);


		//creates a dimensionally sorted array of lists of fixed faces
		ArrayList<String>[] dimList = new ArrayList[9];

		//add zero point
		dimList[0] = new ArrayList<String>();
		dimList[0].add("");

		//add 0dim
		dimList[1]=fixedPts;

		//add 1 dim
		dimList[2]=fixededges;

		//add 2 dim
		dimList[3]=fixedface2D;

		//add 3 dim
		dimList[4]=fixedface3D;

		//add 4 dim
		fixedshD4.addAll(fixedface4Dtet);
		dimList[5]=fixedshD4;

		//add 5 dim
		fixedshD5.addAll(fixedface5DSimp);
		dimList[6]=fixedshD5;

		//add 5 dim
		fixedshD6.addAll(fixedface6DSimp);
		dimList[7]=fixedshD6;

		//add 6 dim (if needed - not used in function)
		dimList[8] = new ArrayList<String>();
		if(dimentionOfL==7){
			System.out.println("Fixed 7-D Semihypercubes: 1");
			dimList[8].add("!"+'"'+"#$%&*,12345678ABCDEFGHIJKLMNOPQRSTUVWXabcdefghijklmnopqrstuvwx");
		}

		//creates the graph
		Graph G = new Graph();

		//adds points (faces) to the graph in order of higher dimension to lower dimension 
		for(int i = dimList.length-1; i>=0;i--){
			for(int j=0; j<dimList[i].size();j++){
				G.addPoint(dimList[i].get(j), i-1);
			}
		}

		//creates the weighted connections in the graph
		G.buildConnections();

		//array to hold the coefficients of the generating function
		int[] coefArray = new int[9];

		//for a jump size j
		for(int j=0;j<dimentionOfL+2;j++){
			//	System.out.println("Jump: "+j);
			//get top level k
			//k can go down to j-1
			System.out.println("=======================================================================");
			writer.println("Jump: "+j);
			writer.println("Top Lvl, Bottom Lvl, Count, SH->SH, SH->Simp, Simp->Simp, Simp->SH");
			System.out.println("Jump +"+j);
			for(int k=dimentionOfL+1;k>j-1;k--){
				int test=0;
				//lower level
				int m=k-j;
				int[] typeOfPath = new int[4];
				//gets the lists from both levels
				ArrayList<String> upperList = dimList[k];
				ArrayList<String> lowerList = dimList[m];
				
				
				//cross reference all strings on each level to check for containment
				for(int x=0;x<upperList.size();x++){
					String upperStr = upperList.get(x);
					for(int y=0;y<lowerList.size();y++){
						String lowerStr = lowerList.get(y);
						int each=G.returnCoefVar(upperStr, lowerStr);
						
						boolean usimp = false;
						boolean lsimp = false;
						String utype = "";
						String ltype = "";
						if(fixedface4Dtet.contains(upperStr) || fixedface5DSimp.contains(upperStr)){
							utype = "tet";
							usimp = true;
						}else{
							utype = "SH";
							
						}
						if(fixedface4Dtet.contains(lowerStr) || fixedface5DSimp.contains(lowerStr)){
							ltype = "tet";
							lsimp=true;
						}else{
							ltype = "SH";
						}
						int typeofpathnum = -1;
						if(!usimp && !lsimp){typeofpathnum = 0;}//sh to sh
						if(!usimp &&  lsimp){typeofpathnum = 1;}//sh to simp
						if( usimp &&  lsimp){typeofpathnum = 2;}//simp to simp
						if( usimp && !lsimp){typeofpathnum = 3;}//simp to sh... shouldn't happen
						
			//			System.out.println(utype+"("+upperStr+") -> "+ltype+"("+lowerStr+"): "+each);
						
						typeOfPath[typeofpathnum] += each;
						coefArray[j]+=each;
						test+=each;
					}
				}
						writer.println((k-1)+","+(m-1)+","+test+","+typeOfPath[0]+","+typeOfPath[1]+","+typeOfPath[2]+","+typeOfPath[3]);
						System.out.println((k-1)+" to "+(m-1)+": "+test+"- sh-sh:"+typeOfPath[0]+", sh-simp:"+typeOfPath[1]+", simp-simp:"+typeOfPath[2]+", simp - sh:"+typeOfPath[3]);

			}
			writer.println("Total:,"+coefArray[j]);
			writer.println(",");
			System.out.println("tot = "+coefArray[j]);
		}

		//prints out the coefficient
		//also adds the coefficients
		//takes into account the alternating pattern
		String yes = "[";
		int total = 0;
		for(int i=0;i<coefArray.length;i++){
			if(i%2==0){
				//		System.out.println("coefficient of t^"+i+" term = "+coefArray[i]);
				total+=coefArray[i];
				yes+=coefArray[i];
			}else{
				//		System.out.println("coefficient of t^"+i+" term = "+(-1*coefArray[i]));
				total-=coefArray[i];
				yes+=-1*coefArray[i];
			}
			if(i!=coefArray.length-1){yes+=",";}
		}

		//prints out the total of the coefficients
		//	System.out.println("total of coefficients: "+total);
		System.out.println(argstr+"+"+sdpStr+" = "+yes+"]: coef Tot = "+total);

		
		
	//	writer.println(longPerm+","+((char)34)+sdpStr+((char)34)+",total,"+fixedPts.size()+","+fixededges.size()+","+fixedface2D.size()+","+fixedface3D.size()+","+
//				(fixedface4Dtet.size()+fixedshD4.size())+","+(fixedface5DSimp.size()+fixedshD5.size())+","+(fixedface6DSimp.size()+fixedshD6.size())+","+dimList[8].size()+","+
//				coefArray[0]+","+coefArray[1]+","+coefArray[2]+","+coefArray[3]+","+coefArray[4]+","+coefArray[5]+","+coefArray[6]+","+coefArray[7]+","+coefArray[8]+","+total);
//		writer.println(", Good: "+goodBad[0]+",Simp,,,,,"+fixedface4Dtet.size()+","+fixedface5DSimp.size()+","+fixedface6DSimp.size()+",0");
//		writer.println(", Bad: "+goodBad[1]+",SHC,,,,,"+fixedshD4.size()+","+fixedshD5.size()+","+fixedshD6.size()+","+dimList[8].size());
		

		System.out.println("");
		System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////////");
		System.out.println("");

		//clears all existing data for next L
		fixededges = new ArrayList<String>();
		fixedface2D = new ArrayList<String>();
		fixedface3D = new ArrayList<String>();
		fixedface4Dtet = new ArrayList<String>();
		fixedshD4 = new ArrayList<String>();
		fixedface5DSimp = new ArrayList<String>();
		fixedshD5 = new ArrayList<String>();
		fixedface6DSimp = new ArrayList<String>();
		fixedshD6 = new ArrayList<String>();
		sdp = new int[7];
		
	//}
		writer.close();

	}


	/**<h1>Is Numeric</h1><p>
	 * 
	 * This function checks to see if a string is numeric or not.
	 * 
	 * @param str - String to be checked
	 * @return true if the string is numeric and false if it is not
	 */
	public static boolean isNumeric(String str){  
		try  {  
			double d = Double.parseDouble(str);  
		}catch(NumberFormatException nfe){  
			return false;  
		}  
		return true;  
	}

	/**<h1>Get Pervious Character</h1><p>
	 * 
	 * This function is used to return the character value of a coordinate that was previous in the coordinate permutation.
	 * 
	 * @param a - numeric place of the coordinate value.
	 * @param s - coordinate permutation.
	 * @return
	 */
	public static char getPrevChar(int a, String s){
		//checks to see if the number is in the permutation
		//if it isn't, then it stays the same
		char look = arrayChars[a];
		if(!s.contains(Integer.toString(a))){
			return look;
		}

		//checks the characters within the permutation, forgetting about '(' and ')' values
		for(int i=0;i<s.length();i++){
			if(s.charAt(i)=='('){
			}else if(s.charAt(i)==')'){
			}else if(Integer.parseInt(Character.toString(s.charAt(i)))==a){
				//once found, check if the previous character is numeric
				//if it is, that is the index of the array of characters
				//if not, go until you find a ')' and the one before that is the index of the array of characters
				if(isNumeric(Character.toString(s.charAt(i-1)))){
					return arrayChars[Integer.parseInt(Character.toString(s.charAt(i-1)))];
				}else{
					for(int j=i;j<s.length();j++){
						if(s.charAt(j)==')'){
							return arrayChars[Integer.parseInt(Character.toString(s.charAt(j-1)))];
						}
					}
				}
			}
		}

		//shouldn't ever need but added to make java work
		return arrayChars[a];
	}

	/**<h1>0-D Faces (Points)</h1><p>
	 * 
	 * This function returns a list of fixed points within the permutation.
	 * It checks every point within the before list and sees if it stays the same in the after list.
	 * 
	 * @param list - The list of points before the permutation.
	 * @param permlist - The list of points after the permutation.
	 * @return A list of points that are fixed.
	 */
	private static ArrayList<String> pts(ArrayList<Point> list, ArrayList<Point> permlist) {

		//the list of legitimate fixed points
		ArrayList<String> fixedPts=new ArrayList<String>();

		//checks to see if each point in the pre list is in the post list
		for(int i = 0;i<list.size();i++){
			if(list.get(i).equals(permlist.get(i))){
				fixedPts.add(Character.toString(list.get(i).name));

			}
		}

		//prints out pertinent information
		System.out.println("Fixed 0-D Faces (Vertecies): "+fixedPts.size());
		//	System.out.println(fixedPts);
		//	System.out.println("");

		//returns the list
		return fixedPts;
	}

	/**<h1>Find Permutation</h1><p>
	 * 
	 * This function finds the permutation of vertices with the given L.
	 * It uses the set of points before and after the permutation to figure ot what point moves to what next point.
	 * A cycle is then created, and given back as a String.
	 * 
	 * @param list - The list of points before the permutation
	 * @param permlist - The list of points after the permutation
	 * @return A string that depicts the permutation of each point.
	 */
	public static String findPermutation(ArrayList<Point> list, ArrayList<Point> permlist){

		//creates a copy of list
		ArrayList<Point> check = new ArrayList<Point>();
		check.addAll(list);

		//the permutaiton string
		String permutation = "";


		while (check.size()!=0){

			//creates a disjoint permutation list with the first item in list added
			ArrayList<Point> perm = new ArrayList<Point>();
			perm.add(check.get(0));

			//removes the point it was moved to
			Point ptItMovedTo = check.get(0);
			check.remove(0);

			//add all points until you get to a point within the list
			boolean finishedPerm = false;
			while (!finishedPerm){
				int i=0;
				boolean found = false;
				while (i<permlist.size() && !found){
					if(ptItMovedTo.equals(permlist.get(i))){
						found = true;
					} else {
						i++;
					}
				}
				ptItMovedTo = getPoint(permlist.get(i).name, list);
				if(!perm.contains(ptItMovedTo)){
					perm.add(ptItMovedTo);
					check.remove(check.indexOf(ptItMovedTo));
				} else {
					finishedPerm = true;
				}
			}

			//creates the string
			permutation+="(";
			for(int r=0; r<perm.size(); r++){
				permutation+=perm.get(r).name;
				if(r+1<perm.size()){
					//permutation+=",";
				}
			}
			permutation+=")";

		}

		//returns the string
		return permutation;
	}

	/**<h1>Fixed Face</h1><p>
	 * 
	 * This function checks to see if a face is fixed within the permutation.
	 * It gets the Points within the face, for both before and after the permutation.
	 * It them checks to see if the lists contain each other (within the coordinate values).
	 * If one is missing, then it is not a fixed face.
	 * 
	 * @param name - The name of the face
	 * @param list - The list of points before the permutation
	 * @param permlist - The list of points after the permutation
	 * @return A true/false statement regarding if the face is fixed under the permutation.
	 */
	public static boolean fixedFace (String name, ArrayList<Point> list, ArrayList<Point> permlist){

		//gets the list of points for before and after the permutation
		ArrayList<Point> pts = getFacePts(name, list);
		ArrayList<Point> permpts = getFacePts(name, permlist);

		//checks whether each face contains the same coordinate values after.
		boolean fixed = true;
		int i=0;
		while (i<name.length() && fixed){
			if (!(permpts.contains(pts.get(i)))){fixed = false;}
			i++;
		}

		//returns the boolean if it is fixed or not.
		return fixed;

	}

	/**<h1>Get Face Points</h1><p>
	 * 
	 * This function creates an ArrayList%ltPoint&gt of each point used within the face.
	 * 
	 * @param face - The face of points
	 * @param list - List of points
	 * @return The list of points used for the face.
	 */
	public static ArrayList<Point> getFacePts(String face, ArrayList<Point> list){

		//The list of points used
		ArrayList<Point> pts = new ArrayList<Point>();

		//finds each point associated with the char and adds it to the list
		for(int i=0; i<face.length(); i++){
			pts.add(getPoint(face.charAt(i), list));
		}

		//returns the list
		return pts;
	}

	/**<h1>Alphabetize</h1><p>
	 * 
	 * This function returns a face in alphabetical order after being given an array of points in that face.
	 * 
	 * @param facePts - Array of points within the face
	 * @return An alphabetized version of the face.
	 */
	public static String alphabetize(ArrayList<Point> facePts){

		//views two points
		for (int z=0; z<facePts.size(); z++){
			for(int y=z+1; y<facePts.size(); y++){

				//gets the name for each point
				char n1 = facePts.get(z).name;
				char n2 = facePts.get(y).name;

				//flips point if needed
				if ((int)n2 < (int)n1){
					Point temp = facePts.get(z);
					facePts.set(z, facePts.get(y));
					facePts.set(y, temp);
				}

			}
		}

		//creates the string for the face
		String newFace = "";
		for (int z=0; z<facePts.size(); z++){
			newFace+=facePts.get(z).name;
		}

		//returns alphabetized face
		return newFace;
	}

	/**<h1>isEdge</h1><p>
	 * 
	 * This function checks to see if two points form an edge or not.
	 * This is done by add the coordinate values of the points mod 2, and then summing them up.
	 * The total of the addition mod 2 will become 0 if the two are the same and 1 if they are different.
	 * The grand total is will then be only 2, because an edge can only differ in two spots.<p>
	 * 
	 * @param a - First Point
	 * @param b - Second Point
	 * @return A true or false statement whether the two points connect to form an edge.
	 */
	public static  boolean isEdge(Point a, Point b){

		//get the total of each coordinate value
		int countA = (a.a+b.a)%2;
		int countB = (a.b+b.b)%2;
		int countC = (a.c+b.c)%2;
		int countD = (a.d+b.d)%2;
		int countE = (a.e+b.e)%2;
		int countF = (a.f+b.f)%2;
		int countG = (a.g+b.g)%2;

		//add up the totals
		int finalCount = countA+countB+countC+countD+countE+countF+countG;

		//returns if the two points connect to form an edge
		return finalCount==2;
	}

	/**<h1>1-D Faces (Edges)</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an ArrayList&ltString&gt that holds all of the possible 1-D Faces (Edges). 
	 * It uses the function <i>isEdge()</i> to determine if two points are edges or not.
	 * 
	 * The function also checks if each legitimate 2-D Face (Triangle) is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list - List of all points within the system.
	 * @param permlist - List of all points within the system once they are permuted.
	 * @return The ArrayList&ltString&gt with all of the possible 1-D Faces (Edges).
	 */
	public static ArrayList<String> faceDim1(ArrayList<Point> list, ArrayList<Point> permlist){

		//list of legitimate 1-D faces
		ArrayList<String> edges = new ArrayList<String>();

		//selects two points
		for (int i=0; i<list.size(); i++){
			for (int j = i+1; j<list.size(); j++){
				//creates the name
				String edge = list.get(i).name+""+list.get(j).name;
				if(edge.equals("ac")){
					boolean no = false;
				}
				//checks if is edge, and adds it if necessary.
				if(isEdge(list.get(i), list.get(j))){
					edges.add(edge);

					//check to see if it is fixed under the permutation
					if(fixedFace(edge, list, permlist)){
						fixededges.add(edge);
					}
				}
			}
		}

		//prints out all pertinant information
		System.out.println("Fixed 1-D Faces (Edges): "+fixededges.size());
		//	System.out.println(fixededges);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<edges.size();i++){
			H.insert(edges.get(i));
		}

		ArrayList<String> finalList = new ArrayList<String>();
		for(int i=0;i<edges.size();i++){
			finalList.add(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}

	/**<h1>2-D Faces (Triangles)</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an ArrayList&ltString&gt that holds all of the possible 2-D Faces (Triangles). 
	 * This is generated by taking a combination of 3 points, and of those 3 points, comparing all 3 combinations of 2 of those points.
	 * If each possible combination of 2 points is a 1-D Face (Edge), then the combination of 3 points is a 2-D Face (Triangle).<p>
	 * 
	 * The function also checks if each legitimate 2-D Face (Triangle) is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list - List of all points within the system.
	 * @param permlist - List of all points within the system once they are permuted.
	 * @param edges - List of all 1-D Face (Edges) within the system.
	 * @return The ArrayList&ltString&gt with all of the possible 2-D Faces (Triangle).
	 */
	public static ArrayList<String> faceDim2(ArrayList<Point> list, ArrayList<Point> permlist, ArrayList<String> edges){

		//list of all legitimate 2-D faces
		ArrayList<String> face2D = new ArrayList<String>();

		for(int i =0; i<edges.size();i++){
			for(int j=0;j<list.size();j++){
				if(!edges.get(i).contains(Character.toString(list.get(j).name))){

					String e = edges.get(i);
					char c = list.get(j).name;
					if(edges.contains((e.charAt(0)+""+c)) && edges.contains((e.charAt(1)+""+c))){
						String name = alphabetize(returnPts((e+c), list));
						if(!face2D.contains(name)){
							face2D.add(name);
							if(fixedFace(name, list, permlist)){
								fixedface2D.add(name);
							}
						}
					}

				}
			}
		}


		//prints out all pertinent information
		System.out.println("Fixed 2-D Faces (Triangles): "+fixedface2D.size());
		//	System.out.println(fixedface2D);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<face2D.size();i++){
			H.insert(face2D.get(i));
		}

		ArrayList<String> finalList = new ArrayList<String>();
		for(int i=0;i<face2D.size();i++){
			finalList.add(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}

	/**<h1>3-D Faces (Tetrahedron)</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an ArrayList&ltString&gt that holds all of the possible 3-D Faces (Tetrahedron). 
	 * This is generated by taking a combination of 4 points, and of those 4 points, comparing all 4 combinations of 3 of those points.
	 * If each possible combination of 3 points is a 2-D Face (Triangle), then the combination of 4 points is a 3-D Face (Tetrahedron).<p>
	 * 
	 * The function also checks if each legitimate 3-D Face (Tetrahedron) is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list - List of all points within the system.
	 * @param permlist - List of all points within the system once they are permuted.
	 * @param face2D - List of all 2-D Face (Triangles) within the system.
	 * @return The ArrayList&ltString&gt with all of the possible 3-D Faces (Tetrahedron).
	 */
	public static ArrayList<String> faceDim3(ArrayList<Point> list, ArrayList<Point> permlist, ArrayList<String> face2D){

		//list of all legitimate 3-D Faces
		ArrayList<String> face3D = new ArrayList<String>();


		for(int i=0;i<face2D.size();i++){
			for(int j=0; j<list.size();j++){
				if(!face2D.get(i).contains(Character.toString(list.get(j).name))){
					String oldSimp = face2D.get(i);
					char c = list.get(j).name;
					String newLowerSimpAlph = "";
					boolean good = true;
					for(int k=0; k<oldSimp.length();k++){
						String newLowerSimp = oldSimp.substring(0, k)+oldSimp.substring(k+1)+c;
						newLowerSimpAlph = alphabetize(returnPts(newLowerSimp,list));
						if(!face2D.contains(newLowerSimpAlph)){
							good=false;
							break;
						}
					}



					if(good){
						String newUpperSimpAlph = alphabetize(returnPts((oldSimp+c), list));
						if(!face3D.contains(newUpperSimpAlph)){
							face3D.add(newUpperSimpAlph);
							if(fixedFace(newUpperSimpAlph, list, permlist)){
								fixedface3D.add(newUpperSimpAlph);
							}
						}

					}
				}
			}
		}

		//prints out all pertinent information
		System.out.println("Fixed 3-D Faces (Tetrahedren): "+fixedface3D.size());
		//	System.out.println(fixedface3D);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<face3D.size();i++){
			H.insert(face3D.get(i));
		}

		ArrayList<String> finalList = new ArrayList<String>();
		for(int i=0;i<face3D.size();i++){
			finalList.add(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}

	/**<h1>4-D Simplicies</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an ArrayList&ltString&gt that holds all of the possible 4-D Simplicies. 
	 * This is generated by taking a combination of 5 points, and of those 5 points, comparing all 5 combinations of 4 of those points.
	 * If each possible combination of 4 points is a 3-D Face (Tetrahedron), then the combination of 5 points is a 4-D simplex.<p>
	 * 
	 * The function also checks if each legitimate 4-D simplex is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list - List of all points within the system.
	 * @param permlist - List of all points within the system once they are permuted.
	 * @param face3D - List of all 3-D Faces (Tetrahedron) within the system.
	 * @return The ArrayList&ltString&gt with all of the possible 4-D Simplicies.
	 */
	public static ArrayList<String> simpDim4(ArrayList<Point> list, ArrayList<Point> permlist, ArrayList<String> face3D){

		//list of all legitimate 4-D Simplicies
		ArrayList<String> face4Dsimp = new ArrayList<String>();

		for(int i=0;i<face3D.size();i++){
			for(int j=0; j<list.size();j++){
				if(!face3D.get(i).contains(Character.toString(list.get(j).name))){
					String oldSimp = face3D.get(i);
					char c = list.get(j).name;
					String newLowerSimpAlph = "";
					boolean good = true;
					for(int k=0; k<oldSimp.length();k++){
						String newLowerSimp = oldSimp.substring(0, k)+oldSimp.substring(k+1)+c;
						newLowerSimpAlph = alphabetize(returnPts(newLowerSimp,list));
						if(!face3D.contains(newLowerSimpAlph)){
							good=false;
							break;
						}
					}



					if(good){
						String newUpperSimpAlph = alphabetize(returnPts((oldSimp+c), list));
						if(!face4Dsimp.contains(newUpperSimpAlph)){
							face4Dsimp.add(newUpperSimpAlph);
							if(fixedFace(newUpperSimpAlph, list, permlist)){
								fixedface4Dtet.add(newUpperSimpAlph);
							}
						}

					}
				}
			}
		}


		//print out pertinent information
		System.out.println("Fixed 4-D Simplicies: "+fixedface4Dtet.size());
		//	System.out.println(fixedface4Dtet);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<face4Dsimp.size();i++){
			H.insert(face4Dsimp.get(i));
		}

		ArrayList<String> finalList = new ArrayList<String>();
		for(int i=0;i<face4Dsimp.size();i++){
			finalList.add(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}

	public static ArrayList<Point> returnPts(String s,ArrayList<Point> list){
		ArrayList<Point> pts = new ArrayList<Point>();
		for(int i=0;i<s.length();i++){
			pts.add(getPoint(s.charAt(i), list));
		}
		return pts;
	}

	/**<h1>4-D Semi-Hypercubes</h1> <p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an ArrayList&ltString&gt that holds all of the possible 4-D Semi-Hypercubes. 
	 * This is generated by taking two 3-D Semi-Hypercubes and comparing them.
	 * If they do not have the same points, and each point is only bad with 1 others, then it is a good 4-D Semi-Hypercube.<p>
	 * 
	 * The function also checks if each legitimate 4-D Semi-Hypercube is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list - List of all points within the system.
	 * @param permlist - List of all points within the system once they are permuted.
	 * @param face4DSH - List of all 3-D Semi-Hypercubes within the system.
	 * @return The ArrayList&ltString&gt with all of the possible 4-D Semi-Hypercubes
	 */
	private static ArrayList<String> shDim4(ArrayList<Point> list, ArrayList<Point> permlist, ArrayList<String> face3D, ArrayList<String> face2D) {

		ArrayList<String> shD4 = new ArrayList<String>();

		//gets the first 4-D SH to compare to
		for (int i =0; i<face3D.size(); i++){

			//creates a list of points for the first 4-D SH
			ArrayList<Point> first = new ArrayList<Point>();

			//adds all of the points within the 4-D face at location i of the list to first
			for (int r=0; r<face3D.get(i).length(); r++){
				first.add(getPoint(face3D.get(i).charAt(r), list));
			}

			//gets the second 4-D SH to compare to
			for (int j=i+1; j<face3D.size(); j++){ 

				//creates a list of points for the second 4-D SH
				ArrayList<Point> second = new ArrayList<Point>();

				//adds all of the points wihin the 4-D SH at location j of the list to second
				for (int r=0; r<face3D.get(j).length(); r++){
					second.add(getPoint(face3D.get(j).charAt(r), list));
				}

				//checks all points within the two lists of points
				//if there is overlap, then the two do not make a 5-D SH 
				int i1=0;
				boolean good = true;
				while (good && i1<first.size()){
					int j1=0;
					while (good && j1<second.size()){

						if(first.get(i1).equals(second.get(j1))){
							good = false;
						} else{
							j1++;
						}

					}
					if(good){i1++;}
				}

				//if the two 4-D SH do not have overlap
				//check to see if any one point has a bad edge with more than 5 other points.
				if (good){
					//create the new 'face'
					String face = face3D.get(i)+""+face3D.get(j);
					String alphFa = alphabetize(returnPts(face, list));

					ArrayList<Character> ptsBad = new ArrayList<Character>();
					//check each combination
					boolean bad = false;
					int k=0;
					while (k<face.length() && !bad){
						int l=k+1;


						//number of bad edges per point
						int countBad = 0;
						while (l<face.length() && !bad){

							//gets each point associated with the character
							Point pt1 = getPoint(face.charAt(k), list);
							Point pt2 = getPoint(face.charAt(l), list);

							//if it isn't an edge, increase the count
							//if count > 1, not a good 4-D SH
							if(!(isEdge(pt1, pt2))){
								if(ptsBad.contains(pt1.name)||ptsBad.contains(pt2.name)){
									bad=true;
								}else{
									ptsBad.add(pt1.name);
									ptsBad.add(pt2.name);
								}
								countBad++;
								if(countBad > 1){
									bad = true;
								}
							}
							l++;
						}
						k++;
					}

					//if still good SH, it is a SH
					if(!bad){

						//creates a list of Points that is then alphabetized and then placed into a string
						ArrayList<Point> facePts = new ArrayList<Point>();
						for (int z=0; z<face.length(); z++){
							facePts.add(getPoint(face.charAt(z), list));
						}
						String newFace = alphabetize(facePts);

						//checks if the list already contains the new face and adds it if not
						if (!(shD4.contains(newFace))){
							shD4.add(newFace);

							//checks if the face is fixed under the permutation
							if(fixedFace(newFace, list, permlist)){
								fixedshD4.add(newFace);
							}
						}
					}
				}
			}
		}

		//prints out all pertinent information
		System.out.println("Fixed 4-D Semihypercubes: " + fixedshD4.size());
		//	System.out.println(fixedshD4);
		//	System.out.println("");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<shD4.size();i++){
			H.insert(shD4.get(i));
		}

		ArrayList<String> finalList = new ArrayList<String>();
		for(int i=0;i<shD4.size();i++){
			finalList.add(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}



	/**<h1>5-D Simplicies</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an ArrayList&ltString&gt that holds all of the possible 5-D Simplicies. 
	 * This is generated by taking a combination of 6 points, and of those 6 points, comparing all 6 combinations of 5 of those points.
	 * If each possible combination of 5 points is a 4-D simplex, then the combination of 6 points is a 6-D simplex.<p>
	 * 
	 * The function also checks if each legitimate 5-D simplex is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list - List of all points within the system.
	 * @param permlist - List of all points within the system once they are permuted.
	 * @param face4DSimp - List of all 4-D Simplicies within the system.
	 * @return The ArrayList&ltString&gt with all of the possible 5-D Simplicies
	 */
	public static ArrayList<String> simpDim5(ArrayList<Point> list, ArrayList<Point> permlist, ArrayList<String> face4D){

		//list of legitimate 5-D Simplicies
		ArrayList<String> face5Dsimp = new ArrayList<String>();

		for(int i=0;i<face4D.size();i++){
			for(int j=0; j<list.size();j++){
				if(!face4D.get(i).contains(Character.toString(list.get(j).name))){
					String oldSimp = face4D.get(i);
					char c = list.get(j).name;
					String newLowerSimpAlph = "";
					boolean good = true;
					for(int k=0; k<oldSimp.length();k++){
						String newLowerSimp = oldSimp.substring(0, k)+oldSimp.substring(k+1)+c;
						newLowerSimpAlph = alphabetize(returnPts(newLowerSimp,list));
						if(!face4D.contains(newLowerSimpAlph)){
							good=false;
							break;
						}
					}



					if(good){
						String newUpperSimpAlph = alphabetize(returnPts((oldSimp+c), list));
						if(!face5Dsimp.contains(newUpperSimpAlph)){
							face5Dsimp.add(newUpperSimpAlph);
							if(fixedFace(newUpperSimpAlph, list, permlist)){
								fixedface5DSimp.add(newUpperSimpAlph);
							}
						}

					}
				}
			}
		}


		//print out pertinent information
		System.out.println("Fixed 5-D Simplicies: "+fixedface5DSimp.size());
		//	System.out.println(fixedface5DSimp);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<face5Dsimp.size();i++){
			H.insert(face5Dsimp.get(i));
		}

		ArrayList<String> finalList = new ArrayList<String>();
		for(int i=0;i<face5Dsimp.size();i++){
			finalList.add(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}

	/**<h1>5-D Semi-Hypercubes</h1> <p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an ArrayList&ltString&gt that holds all of the possible 5-D Semi-Hypercubes. 
	 * This is generated by taking two 4-D Semi-Hypercubes and comparing them.
	 * If they do not have the same points, and each point is only bad with 5 others, then it is a good 5-D Semi-Hypercube.<p>
	 * 
	 * The function also checks if each legitimate 5-D Semi-Hypercube is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list - List of all points within the system.
	 * @param permlist - List of all points within the system once they are permuted.
	 * @param face4DSH - List of all 4-D Semi-Hypercubes within the system.
	 * @return The ArrayList&ltString&gt with all of the possible 5-D Semi-Hypercubes
	 */
	private static ArrayList<String> shDim5(ArrayList<Point> list, ArrayList<Point> permlist, ArrayList<String> face4DSH) {

		ArrayList<String> shD5 = new ArrayList<String>();

		//gets the first 4-D SH to compare to
		for (int i =0; i<face4DSH.size(); i++){

			//creates a list of points for the first 4-D SH
			ArrayList<Point> first = new ArrayList<Point>();

			//adds all of the points within the 4-D face at location i of the list to first
			for (int r=0; r<face4DSH.get(i).length(); r++){
				first.add(getPoint(face4DSH.get(i).charAt(r), list));
			}

			//gets the second 4-D SH to compare to
			for (int j=i+1; j<face4DSH.size(); j++){ 

				//creates a list of points for the second 4-D SH
				ArrayList<Point> second = new ArrayList<Point>();

				//adds all of the points wihin the 4-D SH at location j of the list to second
				for (int r=0; r<face4DSH.get(j).length(); r++){
					second.add(getPoint(face4DSH.get(j).charAt(r), list));
				}

				//checks all points within the two lists of points
				//if there is overlap, then the two do not make a 5-D SH 
				int i1=0;
				boolean good = true;
				while (good && i1<first.size()){
					int j1=0;
					while (good && j1<second.size()){

						if(first.get(i1).equals(second.get(j1))){
							good = false;
						} else{
							j1++;
						}

					}
					if(good){i1++;}
				}

				//if the two 4-D SH do not have overlap
				//check to see if any one point has a bad edge with more than 5 other points.
				if (good){
					//create the new 'face'
					String face = face4DSH.get(i)+""+face4DSH.get(j);

					//check each combination
					boolean bad = false;
					int k=0;
					while (k<face.length() && !bad){
						int l=k+1;

						//number of bad edges per point
						int countBad = 0;
						while (l<face.length() && !bad){

							//gets each point associated with the character
							Point pt1 = getPoint(face.charAt(k), list);
							Point pt2 = getPoint(face.charAt(l), list);

							//if it isn't an edge, increase the count
							//if count > 5, not a good 5-D SH
							if(!(isEdge(pt1, pt2))){
								countBad++;
								if(countBad > 5){
									bad = true;
								}
							}
							l++;
						}
						k++;
					}

					//if still good SH, it is a SH
					if(!bad){

						//creates a list of Points that is then alphabetized and then placed into a string
						ArrayList<Point> facePts = new ArrayList<Point>();
						for (int z=0; z<face.length(); z++){
							facePts.add(getPoint(face.charAt(z), list));
						}
						String newFace = alphabetize(facePts);

						//checks if the list already contains the new face and adds it if not
						if (!(shD5.contains(newFace))){
							shD5.add(newFace);

							//checks if the face is fixed under the permutation
							if(fixedFace(newFace, list, permlist)){
								fixedshD5.add(newFace);
							}
						}
					}
				}
			}
		}

		//prints out all pertinent information
		System.out.println("Fixed 5-D Semihypercubes: " + fixedshD5.size());
		//	System.out.println(fixedshD5);
		//	System.out.println("");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<shD5.size();i++){
			H.insert(shD5.get(i));
		}

		ArrayList<String> finalList = new ArrayList<String>();
		for(int i=0;i<shD5.size();i++){
			finalList.add(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;
	}

	/**<h1>6-D Simplicies</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an ArrayList&ltString&gt that holds all of the possible 5-D Simplicies. 
	 * This is generated by taking a combination of 6 points, and of those 6 points, comparing all 6 combinations of 5 of those points.
	 * If each possible combination of 5 points is a 4-D simplex, then the combination of 6 points is a 6-D simplex.<p>
	 * 
	 * The function also checks if each legitimate 5-D simplex is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list - List of all points within the system.
	 * @param permlist - List of all points within the system once they are permuted.
	 * @param face4DSimp - List of all 4-D Simplicies within the system.
	 * @return The ArrayList&ltString&gt with all of the possible 5-D Simplicies
	 */
	public static ArrayList<String> simpDim6(ArrayList<Point> list, ArrayList<Point> permlist, ArrayList<String> face5D){

		//list of legitimate 5-D Simplicies
		ArrayList<String> face6Dsimp = new ArrayList<String>();

		for(int i=0;i<face5D.size();i++){
			for(int j=0; j<list.size();j++){
				if(!face5D.get(i).contains(Character.toString(list.get(j).name))){
					String oldSimp = face5D.get(i);
					char c = list.get(j).name;
					String newLowerSimpAlph = "";
					boolean good = true;
					for(int k=0; k<oldSimp.length();k++){
						String newLowerSimp = oldSimp.substring(0, k)+oldSimp.substring(k+1)+c;
						newLowerSimpAlph = alphabetize(returnPts(newLowerSimp,list));
						if(!face5D.contains(newLowerSimpAlph)){
							good=false;
							break;
						}
					}



					if(good){
						String newUpperSimpAlph = alphabetize(returnPts((oldSimp+c), list));
						if(!face6Dsimp.contains(newUpperSimpAlph)){
							face6Dsimp.add(newUpperSimpAlph);
							if(fixedFace(newUpperSimpAlph, list, permlist)){
								fixedface6DSimp.add(newUpperSimpAlph);
							}
						}

					}
				}
			}
		}

		//print out pertinent information
		System.out.println("Fixed 6-D Simplicies: "+fixedface6DSimp.size());
		//	System.out.println(fixedface6DSimp);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<face6Dsimp.size();i++){
			H.insert(face6Dsimp.get(i));
		}

		ArrayList<String> finalList = new ArrayList<String>();
		for(int i=0;i<face6Dsimp.size();i++){
			finalList.add(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}


	/**<h1>6-D Semi-Hypercubes</h1> <p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an ArrayList&ltString&gt that holds all of the possible 6-D Semi-Hypercubes. 
	 * This is generated by taking two 5-D Semi-Hypercubes and comparing them.
	 * If they do not have the same points, and each point is only bad with 16 others, then it is a good 6-D Semi-Hypercube.<p>
	 * 
	 * The function also checks if each legitimate 6-D Semi-Hypercube is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list - List of all points within the system.
	 * @param permlist - List of all points within the system once they are permuted.
	 * @param face4DSH - List of all 5-D Semi-Hypercubes within the system.
	 * @return The ArrayList&ltString&gt with all of the possible 6-D Semi-Hypercubes
	 */
	private static ArrayList<String> shDim6(ArrayList<Point> list, ArrayList<Point> permlist, ArrayList<String> face5DSH) {

		ArrayList<String> shD6 = new ArrayList<String>();

		//gets the first 5-D SH to compare to
		for (int i =0; i<face5DSH.size(); i++){

			//creates a list of points for the first 5-D SH
			ArrayList<Point> first = new ArrayList<Point>();

			String firstSH = face5DSH.get(i);

			//adds all of the points within the 5-D face at location i of the list to first
			for (int r=0; r<firstSH.length(); r++){
				first.add(getPoint(firstSH.charAt(r), list));
			}

			//gets the second 5-D SH to compare to
			for (int j=i+1; j<face5DSH.size(); j++){ 

				//creates a list of points for the second 5-D SH
				ArrayList<Point> second = new ArrayList<Point>();

				String seccondSH = face5DSH.get(j);

				//adds all of the points wihin the 5-D SH at location j of the list to second
				for (int r=0; r<seccondSH.length(); r++){
					second.add(getPoint(seccondSH.charAt(r), list));
				}

				boolean good = true;

				//checks if the face is already in there
				ArrayList<Point> strPts= new ArrayList<Point>();
				strPts.addAll(first);
				strPts.addAll(second);
				String newFace= alphabetize(strPts);
				if ((shD6.contains(newFace))){good=false;}

				//checks all points within the two lists of points
				//if there is overlap, then the two do not make a 6-D SH 
				if(good){
					int a = 0;
					while(a<newFace.length()&&good){
						char tester = newFace.charAt(a);
						String subStr = newFace.substring(a+1);
						if(subStr.contains(Character.toString(tester))){good=false;}

						a++;
					}
				}	

				//if the two 5-D SH do not have overlap and it is not already in the list
				//check to see if any one point has a bad edge with more than 5 other points.
				if (good){
					//create the new 'face'
					String face = newFace;

					//check each combination
					boolean bad = false;
					int k=0;
					while (k<face.length() && !bad){
						int l=k+1;

						//number of bad edges per point
						int countBad = 0;
						while (l<face.length() && !bad){

							//gets each point associated with the character
							Point pt1 = getPoint(face.charAt(k), list);
							Point pt2 = getPoint(face.charAt(l), list);

							//if it isn't an edge, increase the count
							//if count > 16, not a good 6-D SH
							if(!(isEdge(pt1, pt2))){
								countBad++;
								if(countBad > 16){
									bad = true;
								}
							}
							l++;
						}
						k++;
					}

					//if still good SH, it is a SH
					if(!bad){

						//adds the face to the list
						shD6.add(newFace);
						//checks if the face is fixed under the permutation
						if(fixedFace(newFace, list, permlist)){
							fixedshD6.add(newFace);
						}
					}
				}
			}
		}

		//prints out all pertinent information
		System.out.println("Fixed 6-D Semihypercubes: " + fixedshD6.size());
		//	System.out.println(fixedshD6);
		//	System.out.println("");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<shD6.size();i++){
			H.insert(shD6.get(i));
		}

		ArrayList<String> finalList = new ArrayList<String>();
		for(int i=0;i<shD6.size();i++){
			finalList.add(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;
	}

	/**
	 * This function is used to obtain the specific Point (Object) that is associated with a character within a specific ArrayList%ltPoint&gt<p>
	 * 
	 * @param name - The character that is associated with the Point that is being looked for.
	 * @param list - The list of Points that are to be looked through
	 * @return The Point (Object) that is associated with the character 'name' in the ArrayList 'list'
	 */
	public static Point getPoint(char name, ArrayList<Point> list){

		//creates a dumy point
		Point pt = new Point('z',0,0,0,0,0,0,0);
		int j = 0;
		boolean found = false;

		//walks the list
		while(!found && j<list.size()){

			//compares names and sets the dummy point to the specific point if it matches
			if(name==list.get(j).name){
				pt=list.get(j);
				found=true;
			} else {
				j++;
			}

		}

		return pt;

	}
}
