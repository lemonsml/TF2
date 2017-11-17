package edu.uwec.math.research.SHDiagClassVersion;

public class TestingClass {

	public static void main(String[] args) {
		HasseGraphInfo[] a = {
		//new HasseGraphInfo(new int[]{2,1,0,0}, new int[]{0,0,0,0}),
		new HasseGraphInfo(new int[]{3,1,0,0,0}, new int[]{0,0,0,0,0})
		};
		
		HasseGraph[] graphs = new HasseGraph[a.length];
		Thread[] threads = new Thread[a.length];
		
		//creates threads and starts them
		for(int i=0;i<a.length;i++){
			graphs[i] = new HasseGraph(4, a[i].getL(), a[i].getSdp());
			graphs[i].start();
		}
		
		//joins threads
		for(int i=0;i<a.length;i++){
			try {
				graphs[i].getThread().join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		System.out.println("FINISHED");
		

	}

}
