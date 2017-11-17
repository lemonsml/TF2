package edu.uwec.math.research.SHDiagClassVersion;
import java.util.ArrayList;

public class Heap<E extends Comparable <E>>{

	private ArrayList<E> array;
	private int numOfItems;      

	/**Constructor
	 */
	public Heap()
	{	
		array = new ArrayList<E>();
		numOfItems = 0;


	}
	/** returns true of the heap is empty
	 */
	public boolean isEmpty(){
		if(array.size()==0){
			return true;
		}else{
			return false;
		}
	}

	/** returns true is the position in the arraylist 
	 *  represents a leaf
	 */
	public boolean isLeaf( int pos){
		boolean hasLeft = true;
		boolean hasRight = true;
		E Left = null;
		E Right = null;
		try{
			Left = array.get(leftChild(pos));
		}catch(IndexOutOfBoundsException e){
			hasLeft=false;
		}
		if(Left==null){
			hasLeft=false;
		}
		
		try{
			Right=array.get(rightChild(pos));
		}catch(IndexOutOfBoundsException e){
			hasRight=false;
		}
		if(Right==null){
			hasRight=false;
		}

		if(hasLeft||hasRight){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * Return the element with the minimum key, and remove it from the heap.
	 * @return the element with the minimum key, or null if heap empty.
	 */
	public E extractMin() {
		if(isEmpty()){return null;}
		E minData = array.get(0);
		array.set(0, array.get(array.size()-1));
		array.remove(array.size()-1);
		numOfItems--;
		if(array.size()>1){
			siftDown(0);
		}
		return minData;
	}

	public void swapChildWithParent(int childIndex){
		int parentIndex = parent(childIndex);
		E temp = array.get(parentIndex);
		array.set(parentIndex, array.get(childIndex));
		array.set(childIndex, temp);
	}

	/**
	 * Insert an element into the heap.  
	 * Keep in heap order
	 * @param element the element to insert
	 */
	public void insert(E element) {
		array.add(element);
		int loc = array.size()-1;
		while(loc>0&&array.get(loc).compareTo(array.get(parent(loc)))<0){
			swapChildWithParent(loc);
			loc=(loc-1)/2;
		}
		numOfItems++;

	}


	/**
	 * Return the element with the minimum key, without removing it from the queue.
	 * @return the element with the minimum key, or null if queue empty.
	 */
	public E minimum() {
		try{
			return array.get(0);
		}catch(IndexOutOfBoundsException e){
			return null;
		}
	}

	/**
	 * Restore the min-heap property.  When this method is called, the min-heap
	 * property holds everywhere, except possibly at node i and its children.
	 * When this method returns, the min-heap property holds everywhere.
	 *
	 * @param i the position of the possibly bad spot in the heap
	 */


	private void siftDown(int i) {
		boolean wellSet=false;
		while(!isLeaf(i)&&!wellSet){
			int left = leftChild(i);
			int right = rightChild(i);
			//position of the smallest
			int smallest;
			if((left<(array.size()-1))&&(array.get(left).compareTo(array.get(i))<0)){
				smallest = left;
			}else{
				smallest = i;
			}
			if((right<(array.size()-1))&&(array.get(right).compareTo(array.get(smallest))<=0)){
				smallest = right;
			}else{
				smallest = smallest;
			}
			if(array.get(i).compareTo(array.get(smallest))<=0){
				wellSet=true;
			}else{
				swap(i,smallest);
				i=smallest;
			}

		}
	} 



	/**
	 * Swap two locations i and j in ArrayList array.
	 * 
	 * @param i first position
	 * @param j second position
	 */
	private void swap(int i, int j) {
		E temp = array.get(i);
		array.set(i, array.get(j));
		array.set(j, temp);
	}

	/**
	 * Return the index of the left child of node i.
	 * @param i index of the parent node
	 * @return index of the left child of node i
	 */
	private int leftChild(int i) {
		return (2*i)+1;

	}


	/**
	 * Return the index of the right child of node i.
	 * @param i index of parent
	 * @return the index of the right child of node i
	 */
	private int rightChild(int i) {
		return (2*i)+2;
	}

	/**
	 * Return the index of the parent of node i
	 * (Parent of root will be -1)
	 * @param i index of the child
	 * @return index of the parent of node i
	 */
	private int parent(int i) {
		return (i-1)/2;
	}


}
