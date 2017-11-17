package edu.uwec.math.research.SHDiagClassVersion;

public class LLNode<E> {
	private E data;
	private LLNode<E> next;
	
	public LLNode(){
		data=null;
		next=null;
	}
	
	public LLNode(E dataItem){
		data=dataItem;
		next=null;
	}
	
	public LLNode(E dataItem, LLNode<E> nodeRef){
		data = dataItem;
		next=nodeRef;
	}
	
	public LLNode<E> getNext(){
		return next;
	}

	public void setNext(LLNode<E> node){
		next=node;
	}
	
	public E getData(){
		return data;
	}
	
	public void setData(E data){
		this.data=data;
	}
}
