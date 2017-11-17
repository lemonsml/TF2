package edu.uwec.math.research.SHDiagClassVersion;

import java.util.ArrayList;

public class LList<E>{
	
	private LLNode<E> head = null;
	private int size=0;
	
	public void LList(){
		
	}

	//clears the list
	public void clear() {
		head = null;
		size=0;
	}

	//inserts a nod at an index
	public void insert(int index, Object item) {
		if(index<0||index>size){
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
		if(index==0){
			addFirst(item);
		}else{
			LLNode<E> node = getNode(index-1);
			addAfter(node,(E)item);
		}
		
	}
	
	//gets a node at an index
	public LLNode<E> getNode(int index){
		if(index<0||index>size){
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
		LLNode<E> node = head;
		for (int i = 0; i<index;i++){
			node=node.getNext();
		}
		return node;
	}
	
	//add to after
	public void addAfter(LLNode<E> node, E item){
		node.setNext(new LLNode<E>(item, node.getNext()));
		size++;
	}
	
	// add to first
	public void addFirst(Object item){
		head = new LLNode<E>((E)item,head);
		size++;
	}

	public void append(Object item) {
		if(size==0){
			addFirst(item);
		}else{
			insert(size,item);
		}
	}
	
	public LLNode getHead(){
		return head;
	}

	public void remove(int index) {
		// TODO Auto-generated method stub
		if(index<0||index>=size){
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
		if(index==0){
			removeFirst();
		}else{
			LLNode<E> node = getNode(index-1);
			removeAfter(node);
			
		}
		
		
	}

	private void removeAfter(LLNode<E> node) {
		LLNode<E> temp=node.getNext();
		node.setNext(temp.getNext());
		temp.setNext(null);
		size--;
	}

	private void removeFirst() {
		LLNode<E> temp = head;
		head=head.getNext();
		temp.setNext(null);
		size--;
	}

	public Object prev(int index) {
		if(index<1||index>size){
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
		return getNode(index-1).getData();
	}

	public Object next(int index) {
		if(index<0||index>size-1){
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
		return getNode(index+1).getData();
	}

	public int length() {
		return size;
	}

	public String backwardString() {
		int i=size-1;
		StringBuilder result = new StringBuilder();
		if(head==null){
			result.append("null");
		}else{

			LLNode<E> nodeRef = getNode(i);
			while(i>=0){
				result.append(nodeRef.getData());
				if(i!=0){
					result.append(" <== ");
					nodeRef=getNode(i-1);
				}
				i--;
				
			}
		}
		return result.toString();
	}

	public E getValue(int index) {
		
		return getNode(index).getData();
	}

	public int indexOf(Object it) {
		int index = -1;
		int i = 0;
		LLNode<E> node = head;
		boolean found = false;
		while(!found&&i<size){
			if(node.getData()==(E)it){
				index=i;
				found=true;
			}else{
				i++;
				node=node.getNext();
			}
		}
		return index;
	}
	
	//tostring
	public String toString(){
		LLNode<E> nodeRef = head;
		StringBuilder result = new StringBuilder();
		if(head==null){
			result.append("null");
		}else{
			
			while(nodeRef !=null){
				result.append(nodeRef.getData());
				if(nodeRef.getNext() != null){
					result.append(",");
				}
				nodeRef=nodeRef.getNext();
			}
		}
		return result.toString();
	}

	public boolean contains(E obj) {
		LLNode<E> ref=head;
		
		for(int i=0;i<size;i++){
			if(obj.equals(ref.getData())){
				return true;
			}
			ref=ref.getNext();
		}
		return false;
	}

	public void addAll(LList<Point> first) {
		for(int i=0;i<first.length();i++){
			this.append(first.getValue(i));
		}
		
	}

}
