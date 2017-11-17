package edu.uwec.math.research.SHDiagClassVersion;

import java.util.ArrayList;

public class HasseGraph extends Thread{
	private Thread t;

	public Thread getThread(){
		return t;
	}

	public void setThread(Thread t){
		this.t=t;
	}

	public void start(){
		if(t==null){
			t = new Thread(this);
			t.start();
		}

	}



	//Dimension of Hasse Graph
	private int n = 0;

	//instances of fixed n-faces
	LList<String>[] dimList;





	private String sdpStr = "";

	//semi-direct product
	private int[] sdp;

	//L
	private int[] L;

	//coordPerm
	private String coordPerm;

	//list of points as to what dimension they are needed in
	private LList<Point> list = new LList<Point>();

	//
	private LList<Point> permlist = new LList<Point>();

	//array of characters that are needed for permuting coordinates
	private final char[] arrayChars = new char[]{' ','a','b','c','d','e','f','g'};

	public HasseGraph(int n, int[] L, int[] sdp1){
		this.n = n;
		this.L = L;

		//need to make sdp into an array length 7
		this.sdp = new int[]{0,0,0,0,0,0,0};
		for(int i=0;i<sdp1.length;i++){
			this.sdp[i]=sdp1[i];
		}

		dimList = new LList[9];
		for(int i=0;i<dimList.length;i++){
			dimList[i]=new LList<String>();
		}

	}

	@Override
	public void run(){



		//sets starting points
		setPoints();

		//gets coordinate permutation
		setCoordPerm();
		System.out.println(coordPerm);

		//sets permuted points
		for (int i=0; i<list.length(); i++){
			permlist.append(new Point(list.getNode(i).getData().name, (list.getNode(i).getData().getPart(getPrevChar(1, coordPerm))+sdp[0])%2, (list.getNode(i).getData().getPart(getPrevChar(2, coordPerm))+sdp[1])%2,(list.getNode(i).getData().getPart(getPrevChar(3, coordPerm))+sdp[2])%2, (list.getNode(i).getData().getPart(getPrevChar(4, coordPerm))+sdp[3])%2, (list.getNode(i).getData().getPart(getPrevChar(5, coordPerm))+sdp[4])%2, (list.getNode(i).getData().getPart(getPrevChar(6, coordPerm))+sdp[5])%2, (list.getNode(i).getData().getPart(getPrevChar(7, coordPerm))+sdp[6])%2));
		}

		//prints out the permutation of points
		String longPerm = findPermutation(list, permlist);
		System.out.println(longPerm);

		//gets the fixed points within the permutatin
		LList<String> fixedPts = pts(list, permlist);

		//gets the full amount of n-dimensional faces
		//also appends the list of fixed n-dimensional faces
		LList<String> edges = faceDim1(list, permlist);
		LList<String> face2D = faceDim2(list, permlist, edges);
		LList<String> face3D = faceDim3(list, permlist, face2D);
		simpDim4Thread d4simp=new simpDim4Thread(list, permlist, face3D);
		d4simp.start();
		try {
			d4simp.getT().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		LList<String> face4Dtet = simpDim4(list, permlist, face3D);
		LList<String> face4DSH = shDim4(list, permlist, face3D, face2D);
		LList<String> face5Dtet = simpDim5(list, permlist, face4Dtet);
		LList<String> face5DSH = shDim5(list, permlist, face4DSH);	
		LList<String> face6Dtet = simpDim6(list, permlist, face5Dtet);
		LList<String> face6DSH = shDim6(list, permlist, face5DSH);

		//add zero point
		dimList[0].append("");

		//add 7 dim (if needed - not used in function)
		if(n==7){
			System.out.println("Fixed 7-D Semihypercubes: 1");
			dimList[8].append("!"+'"'+"#$%&*,12345678ABCDEFGHIJKLMNOPQRSTUVWXabcdefghijklmnopqrstuvwx");
		}
		
		System.out.println("FINISHED IN THREAD");
	}







	//=========================================================================
	//=========================================================================
	//=========================================================================
	//Helper Methods
	//=========================================================================
	//=========================================================================
	//=========================================================================








	/**<h1>Find Permutation</h1><p>
	 * 
	 * This function finds the permutation of vertices with the given L.
	 * It uses the set of points before and after the permutation to figure ot what point moves to what next point.
	 * A cycle is then created, and given back as a String.
	 * 
	 * @param list2 - The list of points before the permutation
	 * @param permlist2 - The list of points after the permutation
	 * @return A string that depicts the permutation of each point.
	 */
	private String findPermutation(LList<Point> list2, LList<Point> permlist2){

		//creates a copy of list
		LList<Point> check = new LList<Point>();

		for(int z=0;z<list2.length();z++){
			check.append(list2.getValue(z));
		}



		//the permutaiton string
		String permutation = "";


		while (check.length()!=0){

			//creates a disjoint permutation list with the first item in list added
			LList<Point> perm = new LList<Point>();
			perm.append(check.getValue(0));

			//removes the point it was moved to
			Point ptItMovedTo = (Point) check.getValue(0);
			check.remove(0);

			//add all points until you get to a point within the list
			boolean finishedPerm = false;
			while (!finishedPerm){
				int i=0;
				boolean found = false;
				while (i<permlist2.length() && !found){
					if(ptItMovedTo.equals(permlist2.getValue(i))){
						found = true;
					} else {
						i++;
					}
				}
				ptItMovedTo = getPoint(permlist2.getValue(i).getName(), list2);
				if(!perm.contains(ptItMovedTo)){
					perm.append(ptItMovedTo);
					check.remove(check.indexOf(ptItMovedTo));
				} else {
					finishedPerm = true;
				}
			}

			//creates the string
			permutation+="("+perm.getValue(0).name;

			for(int r=perm.length()-1; r>0; r--){
				permutation+=perm.getValue(r).name;
			}
			permutation+=")";

		}

		//returns the string
		return permutation;
	}




	/**
	 * This function is used to obtain the specific Point (Object) that is associated with a character within a specific LList%ltPoint&gt<p>
	 * 
	 * @param name - The character that is associated with the Point that is being looked for.
	 * @param list - The list of Points that are to be looked through
	 * @return The Point (Object) that is associated with the character 'name' in the LList 'list'
	 */
	private static Point getPoint(char name, LList<Point> list){

		//creates a dumy point
		Point pt = new Point('+',0,0,0,0,0,0,0);
		int j = 0;
		boolean found = false;

		//walks the list
		while(!found && j<list.length()){

			//compares names and sets the dummy point to the specific point if it matches
			if(name==list.getValue(j).name){
				pt=list.getValue(j);
				found=true;
			} else {
				j++;
			}

		}

		return pt;

	}


	/**<h1>Get Pervious Character</h1><p>
	 * 
	 * This function is used to return the character value of a coordinate that was previous in the coordinate permutation.
	 * 
	 * @param a - numeric place of the coordinate value.
	 * @param s - coordinate permutation.
	 * @return
	 */
	private char getPrevChar(int a, String s){
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

	/**
	 * 
	 */
	private void setCoordPerm() {
		coordPerm="";
		int a=0;
		//takes the largest cycle in L and works its way down.
		for(int j=n-1;j>=0;j--){
			//gets the number of cycles of that length
			int num = L[j];
			//if there are cycles of that lenght
			if(num!=0){
				//generates a permutation that many times
				for(int i=0;i<num;i++){
					coordPerm+="(";
					for(int k=0;k<j+1;k++){
						a++;
						coordPerm+=a;
					}
					coordPerm+=")";
				}
			}
		}

	}

	private void setPoints(){

		if(n>=1){
			list.append(new Point('1', 0, 0, 0, 0, 0, 0, 0));
		}

		if(n>=2){
			list.append(new Point('2', 1, 1, 0, 0, 0, 0, 0));
		}

		if(n>=3){
			list.append(new Point('3', 1, 0, 1, 0, 0, 0, 0));
			list.append(new Point('4', 0, 1, 1, 0, 0, 0, 0));
		}

		if(n>=4){
			list.append(new Point('5', 1, 1, 1, 1, 0, 0, 0));
			list.append(new Point('6', 0, 0, 1, 1, 0, 0, 0));
			list.append(new Point('7', 0, 1, 0, 1, 0, 0, 0));
			list.append(new Point('8', 1, 0, 0, 1, 0, 0, 0));
		}

		if(n>=5){
			list.append(new Point('a', 1, 0, 0, 0, 1, 0, 0));
			list.append(new Point('b', 0, 1, 0, 0, 1, 0, 0));
			list.append(new Point('c', 0, 0, 1, 0, 1, 0, 0));
			list.append(new Point('d', 0, 0, 0, 1, 1, 0, 0));
			list.append(new Point('e', 1, 1, 1, 0, 1, 0, 0));
			list.append(new Point('f', 1, 1, 0, 1, 1, 0, 0));
			list.append(new Point('g', 1, 0, 1, 1, 1, 0, 0));
			list.append(new Point('h', 0, 1, 1, 1, 1, 0, 0));
		}

		if(n>=6){
			list.append(new Point('i', 0, 0, 0, 0, 1, 1, 0));
			list.append(new Point('j', 1, 1, 0, 0, 1, 1, 0));
			list.append(new Point('k', 1, 0, 1, 0, 1, 1, 0));
			list.append(new Point('l', 0, 1, 1, 0, 1, 1, 0));
			list.append(new Point('m', 1, 1, 1, 1, 1, 1, 0));
			list.append(new Point('n', 0, 0, 1, 1, 1, 1, 0));
			list.append(new Point('o', 0, 1, 0, 1, 1, 1, 0));
			list.append(new Point('p', 1, 0, 0, 1, 1, 1, 0));
			list.append(new Point('q', 1, 0, 0, 0, 0, 1, 0));
			list.append(new Point('r', 0, 1, 0, 0, 0, 1, 0));
			list.append(new Point('s', 0, 0, 1, 0, 0, 1, 0));
			list.append(new Point('t', 0, 0, 0, 1, 0, 1, 0));
			list.append(new Point('u', 1, 1, 1, 0, 0, 1, 0));
			list.append(new Point('v', 1, 1, 0, 1, 0, 1, 0));
			list.append(new Point('w', 1, 0, 1, 1, 0, 1, 0));
			list.append(new Point('x', 0, 1, 1, 1, 0, 1, 0));
		}

		if(n>=7){
			list.append(new Point(('!'), 0, 0, 0, 0, 0, 1, 1));
			list.append(new Point(('"'), 0, 0, 0, 0, 1, 0, 1));
			list.append(new Point(('#'), 0, 0, 0, 1, 0, 0, 1));
			list.append(new Point(('$'), 0, 0, 1, 0, 0, 0, 1));
			list.append(new Point(('%'), 0, 1, 0, 0, 0, 0, 1));
			list.append(new Point(('&'), 1, 0, 0, 0, 0, 0, 1));
			list.append(new Point(('*'), 1, 1, 1, 0, 0, 0, 1));
			list.append(new Point((','), 1, 1, 0, 1, 0, 0, 1));
			list.append(new Point(('A'), 1, 1, 0, 0, 1, 0, 1));
			list.append(new Point(('B'), 1, 1, 0, 0, 0, 1, 1));
			list.append(new Point(('C'), 1, 0, 1, 1, 0, 0, 1));
			list.append(new Point(('D'), 1, 0, 1, 0, 1, 0, 1));
			list.append(new Point(('E'), 1, 0, 1, 0, 0, 1, 1));
			list.append(new Point(('F'), 1, 0, 0, 1, 1, 0, 1));
			list.append(new Point(('G'), 1, 0, 0, 1, 0, 1, 1));
			list.append(new Point(('H'), 1, 0, 0, 0, 1, 1, 1));
			list.append(new Point(('I'), 0, 1, 1, 1, 0, 0, 1));
			list.append(new Point(('J'), 0, 1, 1, 0, 1, 0, 1));
			list.append(new Point(('K'), 0, 1, 1, 0, 0, 1, 1));
			list.append(new Point(('L'), 0, 1, 0, 1, 1, 0, 1));
			list.append(new Point(('M'), 0, 1, 0, 1, 0, 1, 1));
			list.append(new Point(('N'), 0, 1, 0, 0, 1, 1, 1));
			list.append(new Point(('O'), 0, 0, 1, 1, 1, 0, 1));
			list.append(new Point(('P'), 0, 0, 1, 1, 0, 1, 1));
			list.append(new Point(('Q'), 0, 0, 1, 0, 1, 1, 1));
			list.append(new Point(('R'), 0, 0, 0, 1, 1, 1, 1));
			list.append(new Point(('S'), 1, 1, 1, 1, 1, 0, 1));
			list.append(new Point(('T'), 1, 1, 1, 1, 0, 1, 1));
			list.append(new Point(('U'), 1, 1, 1, 0, 1, 1, 1));
			list.append(new Point(('V'), 1, 1, 0, 1, 1, 1, 1));
			list.append(new Point(('W'), 1, 0, 1, 1, 1, 1, 1));
			list.append(new Point(('X'), 0, 1, 1, 1, 1, 1, 1));
		}
	}

	/**<h1>Is Numeric</h1><p>
	 * 
	 * This function checks to see if a string is numeric or not.
	 * 
	 * @param str - String to be checked
	 * @return true if the string is numeric and false if it is not
	 */
	private boolean isNumeric(String str){  
		try  {  
			double d = Double.parseDouble(str);  
		}catch(NumberFormatException nfe){  
			return false;  
		}  
		return true;  
	}

	/**<h1>Fixed Face</h1><p>
	 * 
	 * This function checks to see if a face is fixed within the permutation.
	 * It gets the Points within the face, for both before and after the permutation.
	 * It them checks to see if the lists contain each other (within the coordinate values).
	 * If one is missing, then it is not a fixed face.
	 * 
	 * @param name - The name of the face
	 * @param list2 - The list of points before the permutation
	 * @param permlist2 - The list of points after the permutation
	 * @return A true/false statement regarding if the face is fixed under the permutation.
	 */
	static boolean fixedFace (String name, LList<Point> list2, LList<Point> permlist2){

		//gets the list of points for before and after the permutation
		LList<Point> pts = getFacePts(name, list2);
		LList<Point> permpts = getFacePts(name, permlist2);

		//checks whether each face contains the same coordinate values after.
		boolean fixed = true;
		int i=0;
		while (i<name.length() && fixed){
			if (!(permpts.contains(pts.getValue(i)))){fixed = false;}
			i++;
		}

		//returns the boolean if it is fixed or not.
		return fixed;

	}

	/**<h1>Get Face Points</h1><p>
	 * 
	 * This function creates an LList%ltPoint&gt of each point used within the face.
	 * 
	 * @param face - The face of points
	 * @param list2 - List of points
	 * @return The list of points used for the face.
	 */
	private static LList<Point> getFacePts(String face, LList<Point> list2){

		//The list of points used
		LList<Point> pts = new LList<Point>();

		//finds each point associated with the char and adds it to the list
		for(int i=0; i<face.length(); i++){
			pts.append(getPoint(face.charAt(i), list2));
		}

		//returns the list
		return pts;
	}

	/**<h1>Alphabetize</h1><p>
	 * 
	 * This function returns a face in alphabetical order after being given an array of points in that face.
	 * 
	 * @param lList - Array of points within the face
	 * @return An alphabetized version of the face.
	 */
	static String alphabetize(LList<Point> lList){

		//views two points
		for (int z=0; z<lList.length(); z++){
			for(int y=z+1; y<lList.length(); y++){

				//gets the name for each point
				char n1 = lList.getValue(z).name;
				char n2 = lList.getValue(y).name;

				//flips point if needed
				if ((int)n2 < (int)n1){
					Point temp = lList.getValue(z);
					lList.getNode(z).setData(lList.getValue(y));
					lList.getNode(y).setData(temp);
				}

			}
		}

		//creates the string for the face
		String newFace = "";
		for (int z=0; z<lList.length(); z++){
			newFace+=lList.getValue(z).name;
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
	private static  boolean isEdge(Point a, Point b){

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


	static LList<Point> returnPts(String s,LList<Point> list2){
		LList<Point> pts = new LList<Point>();
		for(int i=0;i<s.length();i++){
			pts.append(getPoint(s.charAt(i), list2));
		}
		return pts;
	}




















	//=========================================================================
	//=========================================================================
	//=========================================================================
	//fixed stuff methods
	//=========================================================================
	//=========================================================================
	//=========================================================================























	private LList<String> pts(LList<Point> list2, LList<Point> permlist2) {

		//the list of legitimate fixed points
		LList<String> fixedPts=new LList<String>();

		//checks to see if each point in the pre list is in the post list
		for(int i = 0;i<list2.length();i++){
			if(list2.getValue(i).equals(permlist2.getValue(i))){
				fixedPts.append(Character.toString(list2.getValue(i).name));
				dimList[1].append(Character.toString(list2.getValue(i).name));

			}
		}

		//prints out pertinent information
		System.out.println("Fixed 0-D Faces (Vertecies): "+fixedPts.length());
		//	System.out.println(fixedPts);
		//	System.out.println("");

		//returns the list
		return fixedPts;
	}



	/**<h1>1-D Faces (Edges)</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an LList&ltString&gt that holds all of the possible 1-D Faces (Edges). 
	 * It uses the function <i>isEdge()</i> to determine if two points are edges or not.
	 * 
	 * The function also checks if each legitimate 2-D Face (Triangle) is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list2 - List of all points within the system.
	 * @param permlist2 - List of all points within the system once they are permuted.
	 * @return The LList&ltString&gt with all of the possible 1-D Faces (Edges).
	 */
	private  LList<String> faceDim1(LList<Point> list2, LList<Point> permlist2){

		//list of legitimate 1-D faces
		LList<String> edges = new LList<String>();
;

		//selects two points
		for (int i=0; i<list2.length(); i++){
			for (int j = i+1; j<list2.length(); j++){
				//creates the name
				String edge = list2.getValue(i).name+""+list2.getValue(j).name;

				//checks if is edge, and adds it if necessary.
				if(isEdge(list2.getValue(i), list2.getValue(j))){
					edges.append(edge);

					//check to see if it is fixed under the permutation
					if(fixedFace(edge, list2, permlist2)){
						dimList[2].append(edge);

					}
				}
			}
		}

		//prints out all pertinant information
		System.out.println("Fixed 1-D Faces (Edges): "+dimList[2].length());
		//	System.out.println(fixededges);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<edges.length();i++){
			H.insert(edges.getValue(i));
		}

		LList<String> finalList = new LList<String>();
		for(int i=0;i<edges.length();i++){
			finalList.append(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}

	/**<h1>2-D Faces (Triangles)</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an LList&ltString&gt that holds all of the possible 2-D Faces (Triangles). 
	 * This is generated by taking a combination of 3 points, and of those 3 points, comparing all 3 combinations of 2 of those points.
	 * If each possible combination of 2 points is a 1-D Face (Edge), then the combination of 3 points is a 2-D Face (Triangle).<p>
	 * 
	 * The function also checks if each legitimate 2-D Face (Triangle) is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list2 - List of all points within the system.
	 * @param permlist2 - List of all points within the system once they are permuted.
	 * @param edges - List of all 1-D Face (Edges) within the system.
	 * @return The LList&ltString&gt with all of the possible 2-D Faces (Triangle).
	 */
	private  LList<String> faceDim2(LList<Point> list2, LList<Point> permlist2, LList<String> edges){

		//list of all legitimate 2-D faces
		LList<String> face2D = new LList<String>();

		for(int i =0; i<edges.length();i++){
			for(int j=0;j<list2.length();j++){
				if(!edges.getValue(i).contains(Character.toString(list2.getValue(j).name))){

					String e = edges.getValue(i);
					char c = list2.getValue(j).name;
					if(edges.contains((e.charAt(0)+""+c)) && edges.contains((e.charAt(1)+""+c))){
						String name = alphabetize(returnPts((e+c), list2));
						if(!face2D.contains(name)){
							face2D.append(name);
							if(fixedFace(name, list2, permlist2)){
								dimList[3].append(name);
							}
						}
					}

				}
			}
		}


		//prints out all pertinent information
		System.out.println("Fixed 2-D Faces (Triangles): "+dimList[3].length());
		//	System.out.println(fixedface2D);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<face2D.length();i++){
			H.insert(face2D.getValue(i));
		}

		LList<String> finalList = new LList<String>();
		for(int i=0;i<face2D.length();i++){
			finalList.append(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}

	/**<h1>3-D Faces (Tetrahedron)</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an LList&ltString&gt that holds all of the possible 3-D Faces (Tetrahedron). 
	 * This is generated by taking a combination of 4 points, and of those 4 points, comparing all 4 combinations of 3 of those points.
	 * If each possible combination of 3 points is a 2-D Face (Triangle), then the combination of 4 points is a 3-D Face (Tetrahedron).<p>
	 * 
	 * The function also checks if each legitimate 3-D Face (Tetrahedron) is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list2 - List of all points within the system.
	 * @param permlist2 - List of all points within the system once they are permuted.
	 * @param face2d - List of all 2-D Face (Triangles) within the system.
	 * @return The LList&ltString&gt with all of the possible 3-D Faces (Tetrahedron).
	 */
	private LList<String> faceDim3(LList<Point> list2, LList<Point> permlist2, LList<String> face2d){

		//list of all legitimate 3-D Faces
		LList<String> face3D = new LList<String>();


		for(int i=0;i<face2d.length();i++){
			for(int j=0; j<list2.length();j++){
				if(!face2d.getValue(i).contains(Character.toString(list2.getValue(j).name))){
					String oldSimp = face2d.getValue(i);
					char c = list2.getValue(j).name;
					String newLowerSimpAlph = "";
					boolean good = true;
					for(int k=0; k<oldSimp.length();k++){
						String newLowerSimp = oldSimp.substring(0, k)+oldSimp.substring(k+1)+c;
						newLowerSimpAlph = alphabetize(returnPts(newLowerSimp,list2));
						if(!face2d.contains(newLowerSimpAlph)){
							good=false;
							break;
						}
					}



					if(good){
						String newUpperSimpAlph = alphabetize(returnPts((oldSimp+c), list2));
						if(!face3D.contains(newUpperSimpAlph)){
							face3D.append(newUpperSimpAlph);
							if(fixedFace(newUpperSimpAlph, list2, permlist2)){
								dimList[4].append(newUpperSimpAlph);
							}
						}

					}
				}
			}
		}

		//prints out all pertinent information
		System.out.println("Fixed 3-D Faces (Tetrahedren): "+dimList[4].length());
		//	System.out.println(fixedface3D);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<face3D.length();i++){
			H.insert(face3D.getValue(i));
		}

		LList<String> finalList = new LList<String>();
		for(int i=0;i<face3D.length();i++){
			finalList.append(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}



	/**<h1>4-D Simplicies</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an LList&ltString&gt that holds all of the possible 4-D Simplicies. 
	 * This is generated by taking a combination of 5 points, and of those 5 points, comparing all 5 combinations of 4 of those points.
	 * If each possible combination of 4 points is a 3-D Face (Tetrahedron), then the combination of 5 points is a 4-D simplex.<p>
	 * 
	 * The function also checks if each legitimate 4-D simplex is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list2 - List of all points within the system.
	 * @param permlist2 - List of all points within the system once they are permuted.
	 * @param face3d - List of all 3-D Faces (Tetrahedron) within the system.
	 * @return The LList&ltString&gt with all of the possible 4-D Simplicies.
	 */
	private LList<String> simpDim4(LList<Point> list2, LList<Point> permlist2, LList<String> face3d){

		//list of all legitimate 4-D Simplicies
		LList<String> face4Dsimp = new LList<String>();
		LList<String> fixed = new LList<String>();

		for(int i=0;i<face3d.length();i++){
			for(int j=0; j<list2.length();j++){
				if(!face3d.getValue(i).contains(Character.toString(list2.getValue(j).name))){
					String oldSimp = face3d.getValue(i);
					char c = list2.getValue(j).name;
					String newLowerSimpAlph = "";
					boolean good = true;
					for(int k=0; k<oldSimp.length();k++){
						String newLowerSimp = oldSimp.substring(0, k)+oldSimp.substring(k+1)+c;
						newLowerSimpAlph = alphabetize(returnPts(newLowerSimp,list2));
						if(!face3d.contains(newLowerSimpAlph)){
							good=false;
							break;
						}
					}



					if(good){
						String newUpperSimpAlph = alphabetize(returnPts((oldSimp+c), list2));
						if(!face4Dsimp.contains(newUpperSimpAlph)){
							face4Dsimp.append(newUpperSimpAlph);
							if(fixedFace(newUpperSimpAlph, list2, permlist2)){
								dimList[5].append(newUpperSimpAlph);
								fixed.append(newUpperSimpAlph);
							}
						}

					}
				}
			}
		}


		//print out pertinent information
		System.out.println("Fixed 4-D Simplicies: "+fixed.length());
		//	System.out.println(fixedface4Dtet);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<face4Dsimp.length();i++){
			H.insert(face4Dsimp.getValue(i));
		}

		LList<String> finalList = new LList<String>();
		for(int i=0;i<face4Dsimp.length();i++){
			finalList.append(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}


	/**<h1>4-D Semi-Hypercubes</h1> <p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an LList&ltString&gt that holds all of the possible 4-D Semi-Hypercubes. 
	 * This is generated by taking two 3-D Semi-Hypercubes and comparing them.
	 * If they do not have the same points, and each point is only bad with 1 others, then it is a good 4-D Semi-Hypercube.<p>
	 * 
	 * The function also checks if each legitimate 4-D Semi-Hypercube is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list2 - List of all points within the system.
	 * @param permlist2 - List of all points within the system once they are permuted.
	 * @param face4DSH - List of all 3-D Semi-Hypercubes within the system.
	 * @return The LList&ltString&gt with all of the possible 4-D Semi-Hypercubes
	 */
	private LList<String> shDim4(LList<Point> list2, LList<Point> permlist2, LList<String> face3d, LList<String> face2d) {

		LList<String> shD4 = new LList<String>();
		LList<String> fixed = new LList<String>();

		//gets the first 4-D SH to compare to
		for (int i =0; i<face3d.length(); i++){

			//creates a list of points for the first 4-D SH
			LList<Point> first = new LList<Point>();

			//adds all of the points within the 4-D face at location i of the list to first
			for (int r=0; r<face3d.getValue(i).length(); r++){
				first.append(getPoint(face3d.getValue(i).charAt(r), list2));
			}

			//gets the second 4-D SH to compare to
			for (int j=i+1; j<face3d.length(); j++){ 

				//creates a list of points for the second 4-D SH
				LList<Point> second = new LList<Point>();

				//adds all of the points wihin the 4-D SH at location j of the list to second
				for (int r=0; r<face3d.getValue(j).length(); r++){
					second.append(getPoint(face3d.getValue(j).charAt(r), list2));
				}

				//checks all points within the two lists of points
				//if there is overlap, then the two do not make a 5-D SH 
				int i1=0;
				boolean good = true;
				while (good && i1<first.length()){
					int j1=0;
					while (good && j1<second.length()){

						if(first.getValue(i1).equals(second.getValue(j1))){
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
					String face = face3d.getValue(i)+""+face3d.getValue(j);


					LList<Character> ptsBad = new LList<Character>();
					//check each combination
					boolean bad = false;
					int k=0;
					while (k<face.length() && !bad){
						int l=k+1;


						//number of bad edges per point
						int countBad = 0;
						while (l<face.length() && !bad){

							//gets each point associated with the character
							Point pt1 = getPoint(face.charAt(k), list2);
							Point pt2 = getPoint(face.charAt(l), list2);

							//if it isn't an edge, increase the count
							//if count > 1, not a good 4-D SH
							if(!(isEdge(pt1, pt2))){
								if(ptsBad.contains(pt1.name)||ptsBad.contains(pt2.name)){
									bad=true;
								}else{
									ptsBad.append(pt1.name);
									ptsBad.append(pt2.name);
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
						LList<Point> facePts = new LList<Point>();
						for (int z=0; z<face.length(); z++){
							facePts.append(getPoint(face.charAt(z), list2));
						}
						String newFace = alphabetize(facePts);

						//checks if the list already contains the new face and adds it if not
						if (!(shD4.contains(newFace))){
							shD4.append(newFace);

							//checks if the face is fixed under the permutation
							if(fixedFace(newFace, list2, permlist2)){
								dimList[5].append(newFace);
								fixed.append(newFace);
							}
						}
					}
				}
			}
		}

		//prints out all pertinent information
		System.out.println("Fixed 4-D Semihypercubes: " + fixed.length());
		//	System.out.println(fixedshD4);
		//	System.out.println("");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<shD4.length();i++){
			H.insert(shD4.getValue(i));
		}

		LList<String> finalList = new LList<String>();
		for(int i=0;i<shD4.length();i++){
			finalList.append(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}



	/**<h1>5-D Simplicies</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an LList&ltString&gt that holds all of the possible 5-D Simplicies. 
	 * This is generated by taking a combination of 6 points, and of those 6 points, comparing all 6 combinations of 5 of those points.
	 * If each possible combination of 5 points is a 4-D simplex, then the combination of 6 points is a 6-D simplex.<p>
	 * 
	 * The function also checks if each legitimate 5-D simplex is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list2 - List of all points within the system.
	 * @param permlist2 - List of all points within the system once they are permuted.
	 * @param face4DSimp - List of all 4-D Simplicies within the system.
	 * @return The LList&ltString&gt with all of the possible 5-D Simplicies
	 */
	private LList<String> simpDim5(LList<Point> list2, LList<Point> permlist2, LList<String> face4Dtet){

		//list of legitimate 5-D Simplicies
		LList<String> face5Dsimp = new LList<String>();
		LList<String> fixed = new LList<String>();

		for(int i=0;i<face4Dtet.length();i++){
			for(int j=0; j<list2.length();j++){
				if(!face4Dtet.getValue(i).contains(Character.toString(list2.getValue(j).name))){
					String oldSimp = face4Dtet.getValue(i);
					char c = list2.getValue(j).name;
					String newLowerSimpAlph = "";
					boolean good = true;
					for(int k=0; k<oldSimp.length();k++){
						String newLowerSimp = oldSimp.substring(0, k)+oldSimp.substring(k+1)+c;
						newLowerSimpAlph = alphabetize(returnPts(newLowerSimp,list2));
						if(!face4Dtet.contains(newLowerSimpAlph)){
							good=false;
							break;
						}
					}



					if(good){
						String newUpperSimpAlph = alphabetize(returnPts((oldSimp+c), list2));
						if(!face5Dsimp.contains(newUpperSimpAlph)){
							face5Dsimp.append(newUpperSimpAlph);
							if(fixedFace(newUpperSimpAlph, list2, permlist2)){
								dimList[6].append(newUpperSimpAlph);
								fixed.append(newUpperSimpAlph);
							}
						}

					}
				}
			}
		}


		//print out pertinent information
		System.out.println("Fixed 5-D Simplicies: "+fixed.length());
		//	System.out.println(fixedface5DSimp);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<face5Dsimp.length();i++){
			H.insert(face5Dsimp.getValue(i));
		}

		LList<String> finalList = new LList<String>();
		for(int i=0;i<face5Dsimp.length();i++){
			finalList.append(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}

	/**<h1>5-D Semi-Hypercubes</h1> <p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an LList&ltString&gt that holds all of the possible 5-D Semi-Hypercubes. 
	 * This is generated by taking two 4-D Semi-Hypercubes and comparing them.
	 * If they do not have the same points, and each point is only bad with 5 others, then it is a good 5-D Semi-Hypercube.<p>
	 * 
	 * The function also checks if each legitimate 5-D Semi-Hypercube is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list2 - List of all points within the system.
	 * @param permlist2 - List of all points within the system once they are permuted.
	 * @param face4dsh - List of all 4-D Semi-Hypercubes within the system.
	 * @return The LList&ltString&gt with all of the possible 5-D Semi-Hypercubes
	 */
	private LList<String> shDim5(LList<Point> list2, LList<Point> permlist2, LList<String> face4dsh) {

		LList<String> shD5 = new LList<String>();
		LList<String> fixed = new LList<String>();

		//gets the first 4-D SH to compare to
		for (int i =0; i<face4dsh.length(); i++){

			//creates a list of points for the first 4-D SH
			LList<Point> first = new LList<Point>();

			//adds all of the points within the 4-D face at location i of the list to first
			for (int r=0; r<face4dsh.getValue(i).length(); r++){
				first.append(getPoint(face4dsh.getValue(i).charAt(r), list2));
			}

			//gets the second 4-D SH to compare to
			for (int j=i+1; j<face4dsh.length(); j++){ 

				//creates a list of points for the second 4-D SH
				LList<Point> second = new LList<Point>();

				//adds all of the points wihin the 4-D SH at location j of the list to second
				for (int r=0; r<face4dsh.getValue(j).length(); r++){
					second.append(getPoint(face4dsh.getValue(j).charAt(r), list2));
				}

				//checks all points within the two lists of points
				//if there is overlap, then the two do not make a 5-D SH 
				int i1=0;
				boolean good = true;
				while (good && i1<first.length()){
					int j1=0;
					while (good && j1<second.length()){

						if(first.getValue(i1).equals(second.getValue(j1))){
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
					String face = face4dsh.getValue(i)+""+face4dsh.getValue(j);

					//check each combination
					boolean bad = false;
					int k=0;
					while (k<face.length() && !bad){
						int l=k+1;

						//number of bad edges per point
						int countBad = 0;
						while (l<face.length() && !bad){

							//gets each point associated with the character
							Point pt1 = getPoint(face.charAt(k), list2);
							Point pt2 = getPoint(face.charAt(l), list2);

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
						LList<Point> facePts = new LList<Point>();
						for (int z=0; z<face.length(); z++){
							facePts.append(getPoint(face.charAt(z), list2));
						}
						String newFace = alphabetize(facePts);

						//checks if the list already contains the new face and adds it if not
						if (!(shD5.contains(newFace))){
							shD5.append(newFace);

							//checks if the face is fixed under the permutation
							if(fixedFace(newFace, list2, permlist2)){
								dimList[6].append(newFace);
								fixed.append(newFace);
							}
						}
					}
				}
			}
		}

		//prints out all pertinent information
		System.out.println("Fixed 5-D Semihypercubes: " + fixed.length());
		//	System.out.println(fixedshD5);
		//	System.out.println("");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<shD5.length();i++){
			H.insert(shD5.getValue(i));
		}

		LList<String> finalList = new LList<String>();
		for(int i=0;i<shD5.length();i++){
			finalList.append(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;
	}

	/**<h1>6-D Simplicies</h1><p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an LList&ltString&gt that holds all of the possible 5-D Simplicies. 
	 * This is generated by taking a combination of 6 points, and of those 6 points, comparing all 6 combinations of 5 of those points.
	 * If each possible combination of 5 points is a 4-D simplex, then the combination of 6 points is a 6-D simplex.<p>
	 * 
	 * The function also checks if each legitimate 5-D simplex is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list2 - List of all points within the system.
	 * @param permlist2 - List of all points within the system once they are permuted.
	 * @param face4DSimp - List of all 4-D Simplicies within the system.
	 * @return The LList&ltString&gt with all of the possible 5-D Simplicies
	 */
	private LList<String> simpDim6(LList<Point> list2, LList<Point> permlist2, LList<String> face5Dtet){

		//list of legitimate 5-D Simplicies
		LList<String> face6Dsimp = new LList<String>();
		LList<String> fixed = new LList<String>();

		for(int i=0;i<face5Dtet.length();i++){
			for(int j=0; j<list2.length();j++){
				if(!face5Dtet.getValue(i).contains(Character.toString(list2.getValue(j).name))){
					String oldSimp = face5Dtet.getValue(i);
					char c = list2.getValue(j).name;
					String newLowerSimpAlph = "";
					boolean good = true;
					for(int k=0; k<oldSimp.length();k++){
						String newLowerSimp = oldSimp.substring(0, k)+oldSimp.substring(k+1)+c;
						newLowerSimpAlph = alphabetize(returnPts(newLowerSimp,list2));
						if(!face5Dtet.contains(newLowerSimpAlph)){
							good=false;
							break;
						}
					}



					if(good){
						String newUpperSimpAlph = alphabetize(returnPts((oldSimp+c), list2));
						if(!face6Dsimp.contains(newUpperSimpAlph)){
							face6Dsimp.append(newUpperSimpAlph);
							if(fixedFace(newUpperSimpAlph, list2, permlist2)){
								dimList[7].append(newUpperSimpAlph);
								fixed.append(newUpperSimpAlph);
							}
						}

					}
				}
			}
		}

		//print out pertinent information
		System.out.println("Fixed 6-D Simplicies: "+fixed.length());
		//	System.out.println(fixedface6DSimp);
		//	System.out.println(" ");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<face6Dsimp.length();i++){
			H.insert(face6Dsimp.getValue(i));
		}

		LList<String> finalList = new LList<String>();
		for(int i=0;i<face6Dsimp.length();i++){
			finalList.append(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;

	}


	/**<h1>6-D Semi-Hypercubes</h1> <p>
	 * 
	 * This function is used for multiple proponents. 
	 * It generates an LList&ltString&gt that holds all of the possible 6-D Semi-Hypercubes. 
	 * This is generated by taking two 5-D Semi-Hypercubes and comparing them.
	 * If they do not have the same points, and each point is only bad with 16 others, then it is a good 6-D Semi-Hypercube.<p>
	 * 
	 * The function also checks if each legitimate 6-D Semi-Hypercube is fixed under the permutation by calling the function <i>fixedFace()</i>.<p>
	 * 
	 * @param list2 - List of all points within the system.
	 * @param permlist2 - List of all points within the system once they are permuted.
	 * @param face4DSH - List of all 5-D Semi-Hypercubes within the system.
	 * @return The LList&ltString&gt with all of the possible 6-D Semi-Hypercubes
	 */
	private LList<String> shDim6(LList<Point> list2, LList<Point> permlist2, LList<String> face5dsh) {

		LList<String> shD6 = new LList<String>();
		LList<String> fixed = new LList<String>();

		//gets the first 5-D SH to compare to
		for (int i =0; i<face5dsh.length(); i++){

			//creates a list of points for the first 5-D SH
			LList<Point> first = new LList<Point>();

			String firstSH = face5dsh.getValue(i);

			//adds all of the points within the 5-D face at location i of the list to first
			for (int r=0; r<firstSH.length(); r++){
				first.append(getPoint(firstSH.charAt(r), list2));
			}

			//gets the second 5-D SH to compare to
			for (int j=i+1; j<face5dsh.length(); j++){ 

				//creates a list of points for the second 5-D SH
				LList<Point> second = new LList<Point>();

				String seccondSH = face5dsh.getValue(j);

				//adds all of the points wihin the 5-D SH at location j of the list to second
				for (int r=0; r<seccondSH.length(); r++){
					second.append(getPoint(seccondSH.charAt(r), list2));
				}

				boolean good = true;

				//checks if the face is already in there
				LList<Point> strPts= new LList<Point>();

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
							Point pt1 = getPoint(face.charAt(k), list2);
							Point pt2 = getPoint(face.charAt(l), list2);

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
						shD6.append(newFace);
						//checks if the face is fixed under the permutation
						if(fixedFace(newFace, list2, permlist2)){
							dimList[7].append(newFace);
							fixed.append(newFace);
						}
					}
				}
			}
		}

		//prints out all pertinent information
		System.out.println("Fixed 6-D Semihypercubes: " + fixed.length());
		//	System.out.println(fixedshD6);
		//	System.out.println("");

		Heap<String> H = new Heap<String>();

		for(int i =0; i<shD6.length();i++){
			H.insert(shD6.getValue(i));
		}

		LList<String> finalList = new LList<String>();
		for(int i=0;i<shD6.length();i++){
			finalList.append(H.extractMin());
		}

		//returns list of all 1-D Faces (Edges)
		return finalList;
	}

}














