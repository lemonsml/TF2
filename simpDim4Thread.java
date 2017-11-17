package edu.uwec.math.research.SHDiagClassVersion;

public class simpDim4Thread extends Thread {
	private Thread t;
	private LList<String> AllDim4;
	private LList<String> fixed;
	private LList<String> face3d;
	private LList<Point>	list2;
	private LList<Point> permlist2;
	
	public simpDim4Thread(LList<Point> list2, LList<Point> permlist2, LList<String> face3d){
		this.list2=list2;
		this.permlist2=permlist2;
		this.face3d=face3d;
		AllDim4=new LList<String>();
		fixed=new LList<String>();
	}

	public Thread getT() {
		return t;
	}

	public void setT(Thread t) {
		this.t = t;
	}

	public LList<String> getAllDim4() {
		return AllDim4;
	}

	public void setAllDim4(LList<String> allDim4) {
		AllDim4 = allDim4;
	}

	public LList<String> getFixed() {
		return fixed;
	}

	public void setFixed(LList<String> fixed) {
		this.fixed = fixed;
	}

	public void start(){
		if(t==null){
			t = new Thread(this);
			t.start();
		}
	}

	@Override
	public void run(){
		//list of all legitimate 4-D Simplicies
		LList<String> face4Dsimp = new LList<String>();

		for(int i=0;i<face3d.length();i++){
			for(int j=0; j<list2.length();j++){
				if(!face3d.getValue(i).contains(Character.toString(list2.getValue(j).name))){
					String oldSimp = face3d.getValue(i);
					char c = list2.getValue(j).name;
					String newLowerSimpAlph = "";
					boolean good = true;
					for(int k=0; k<oldSimp.length();k++){
						String newLowerSimp = oldSimp.substring(0, k)+oldSimp.substring(k+1)+c;
						newLowerSimpAlph = HasseGraph.alphabetize(HasseGraph.returnPts(newLowerSimp,list2));
						if(!face3d.contains(newLowerSimpAlph)){
							good=false;
							break;
						}
					}



					if(good){
						String newUpperSimpAlph = HasseGraph.alphabetize(HasseGraph.returnPts((oldSimp+c), list2));
						if(!AllDim4.contains(newUpperSimpAlph)){
							AllDim4.append(newUpperSimpAlph);
							if(HasseGraph.fixedFace(newUpperSimpAlph, list2, permlist2)){
								fixed.append(newUpperSimpAlph);
							}
						}

					}
				}
			}
		}
	}
	

}
