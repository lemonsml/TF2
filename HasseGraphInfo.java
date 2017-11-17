package edu.uwec.math.research.SHDiagClassVersion;

public class HasseGraphInfo {
	private int[] L;
	private int[] sdp;
	
	public HasseGraphInfo (int[] L, int[] sdp){
		this.L=L;
		this.sdp=sdp;
	}

	public int[] getL() {
		return L;
	}

	public void setL(int[] L) {
		this.L = L;
	}

	public int[] getSdp() {
		return sdp;
	}

	public void setSdp(int[] sdp) {
		this.sdp = sdp;
	}
}
