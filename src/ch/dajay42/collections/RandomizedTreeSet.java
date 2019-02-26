package ch.dajay42.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ch.dajay42.util.ArbitraryFixedBinaryString;


public class RandomizedTreeSet<E extends Comparable<? super E>> implements Set<E>, Serializable{

	private static final long serialVersionUID = 1L;

	enum Handedness{ROOT, LEFT, RIGHT, INVALID}
	
	class Node{
		E value;
		ArbitraryFixedBinaryString priority;
		
		Node parent = null;
		Node left = null;
		Node right = null;
		
		Node(E value){
			this.value = value;
			this.priority = new ArbitraryFixedBinaryString();
		}

		E getValue() {
			return value;
		}

		ArbitraryFixedBinaryString getPriority() {
			return priority;
		}
		
		Handedness getHandedness(){
			if(parent == null){
				return Handedness.ROOT;
			}else if(parent.left == this){
				return Handedness.LEFT;
			}else if(parent.right == this){
				return Handedness.RIGHT;
			}else{
				return Handedness.INVALID;
			}				
		}
		
		void destroy(){
			if(left != null){
				left.destroy();
			}
			if(right != null){
				right.destroy();
			}
			value = null;
			priority = null;
			left = null;
			right = null;
			parent = null;
		}
		
		@Override
		public String toString() {
			return "Node("+value.toString()+"; "+priority.toString()+")";
		}
	}
	

	void rotateLeft(Node x){
		Node p,u,v,w,y;
		
		Handedness h = x.getHandedness();
		//get
		p = x.parent;
		y = x.right;
		u = x.left;
		v = y.left;
		w = y.right;
		
		//set
		x.parent = y;
		x.left = u;
		x.right = v;
		y.left = x;
		y.right = w;
		
		if(u != null) u.parent = x;
		if(v != null) v.parent = x;
		if(w != null) w.parent = y;
		
		switch (h) {
		case ROOT:
			root = y; y.parent = null;
			break;

		case LEFT:
			p.left = y; y.parent = p;
			break;

		case RIGHT:
			p.right = y; y.parent = p;
			break;

		default:
			break;
		}
	}

	void rotateRight(Node y){
		Node p,u,v,w,x;
		Handedness h = y.getHandedness();
		//get
		p = y.parent;
		x = y.left;
		u = x.left;
		v = x.right;
		w = y.right;
		
		//set
		x.left = u;
		x.right = y;
		y.parent = x;
		y.left = v;
		y.right = w;
		
		if(u != null) u.parent = x;
		if(v != null) v.parent = y;
		if(w != null) w.parent = y;
		
		switch (h) {
		case ROOT:
			root = x; x.parent = null;
			break;

		case LEFT:
			p.left = x; x.parent = p;
			break;

		case RIGHT:
			p.right = x; x.parent = p;
			break;

		default:
			break;
		}
	}
	void restorePriorities(Node n){
		restorePrioritiesUp(n);
		restorePrioritiesDown(n);
	}
		
	void restorePrioritiesUp(Node n){
		while(n.parent != null && (n.priority.compareTo(n.parent.priority) < 0)){
			Handedness h = n.getHandedness();
			switch(h){
				case LEFT:
					rotateRight(n.parent);
					break;
				case RIGHT:
					rotateLeft(n.parent);
					break;
				default:
					break;
			}
		}
	}
	
	void restorePrioritiesDown(Node n){
		while(n.left != null || n.right != null){
			boolean takeLeftRotateRight;
			if(n.left != null && n.right != null){
				if(n.left.priority.compareTo(n.right.priority) > 0){
					//take right
					takeLeftRotateRight = false;
				}else{
					//take left
					takeLeftRotateRight = true;
				}
			}else if(n.left != null && n.right == null){
				takeLeftRotateRight = true;
			}else if(n.left == null && n.right != null){
				takeLeftRotateRight = false;
			}else{
				break;
			}
			
			if(takeLeftRotateRight){
				if(n.priority.compareTo(n.left.priority) > 0)
					rotateRight(n);
				else
					break;
			}else{
				if(n.priority.compareTo(n.right.priority) > 0)
					rotateLeft(n);
				else
					break;
			}
		}
	}
	
	void removeNode(Node n){
		while(n.left != null && n.right != null){
			if(n.left.priority.compareTo(n.right.priority) > 0){
				//take right
				rotateLeft(n);
			}else{
				//take left
				rotateRight(n);
			}
		}
		Node child;
		if(n.left != null){
			child = n.left;
		}else{
			child = n.right;
		}
		
		if(child != null)
			child.parent = n.parent;
		
		Handedness h = n.getHandedness();
		switch(h){
		case ROOT:
			root = child;
			break;
		case LEFT:
			n.parent.left = child;
			break;
		case RIGHT:
			n.parent.right = child;
			break;
		default:
			break;
		}
		
	}
	private Node root;
	private int size;
	
	protected Node getNodeOf(E arg0) {
		Node node = root;
		int c;
		while(node != null){
			c = node.getValue().compareTo(arg0);
			if(c == 0){
				return node;
			}else if(c > 0){
				node = node.left;
			}else{
				node = node.right;
			}
		}
		return null;
	}

	protected Node first(Node root){
		Node node = root;
		while(node.left != null){
			node = node.left;
		}
		return node;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>(){
			Node previous = null;
			Node node = null;
			@Override
			public boolean hasNext() {
				Node hasnode = node;
				if(hasnode == null){
					hasnode = first(root);
					if(hasnode != null) return true;
				}
				//in-order walk
				if(hasnode.right != null){
					hasnode = first(hasnode.right);
				}else{
					//get parent of closest left-handed ancestor
					while(hasnode.getHandedness().equals(Handedness.RIGHT)){
						hasnode = hasnode.parent;
					}
					hasnode = hasnode.parent;
				}
				return hasnode != null;
			}
	
			@Override
			public E next() {
				if(node == null){
					node = first(root);
					return node.value;
				}
				//in-order walk
				previous = node;
				if(node.right != null){
					node = first(node.right);
				}else{
					//get parent of closest non-right-handed ancestor
					while(node.getHandedness().equals(Handedness.RIGHT)){
						node = node.parent;
					}
					node = node.parent;
				}
				return node.value;
			}
			
			@Override
			public void remove() {
				removeNode(node);
				node = previous;
			}
		};
	}

	public RandomizedTreeSet() {
		size = 0;
		root = null;
	}
	

	public RandomizedTreeSet(Collection<E> collection) {
		this();
		this.addAll(collection);
	}

	@Override
	public boolean add(E arg0) {
		Node newNode = new Node(arg0);
		if(root == null){
			root = newNode;
			size++;
			return true;
		}
		
		Node node = root;
		int c;
		while(node != null){
			c = node.getValue().compareTo(arg0);
			if(c == 0){
				return false;
			}else if(c > 0){
				if(node.left == null){
					node.left = newNode;
					newNode.parent = node;
					size++;
					restorePrioritiesUp(newNode);
					return true;
				}else{
					node = node.left;
				}
			}else{
				if(node.right == null){
					node.right = newNode;
					newNode.parent = node;
					size++;
					restorePrioritiesUp(newNode);
					return true;
				}else{
				node = node.right;
				}
			}
		}
		return false;
	}


	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		boolean b = false;
		for(E e : arg0){
			b |= add(e);
		}
		return b;
	}


	@Override
	public void clear() {
		root.destroy();
		root = null;
		size = 0;
	}

	public boolean contains(E arg0) {
		Node node = getNodeOf(arg0);
		return (node != null);
	}
	
	@Override
	public boolean contains(Object arg0) {
		return false;
	}


	@Override
	public boolean containsAll(Collection<?> arg0) {
		boolean b = true;
		for(Object o : arg0){
			b &= contains(o);
		}
		return b;
	}


	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	public boolean remove(E arg0){
		Node node = getNodeOf(arg0);
		if(node != null){
			removeNode(node);
			size--;
			return true;
		}else{
			return false;
		}

	}
	@Override
	public boolean remove(Object arg0) {
		return false;
	}


	@Override
	public boolean removeAll(Collection<?> arg0) {
		boolean b = false;
		for(Object o : arg0){
			b |= remove(o);
		}
		return b;
	}


	@Override
	public boolean retainAll(Collection<?> arg0) {
		RandomizedTreeSet<E> diff = new RandomizedTreeSet<>(this);
		diff.removeAll(arg0);
		
		if(!diff.isEmpty()){
			this.removeAll(diff);
			return true;
		}else{
			return false;
		}
	}


	@Override
	public int size() {
		return size;
	}


	@Override
	public Object[] toArray() {
		Object[] objects = new Object[size];
		int i = 0;
		for(E e : this){
			objects[i] = e;
			i++;
		}
		return objects;
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] arg0) throws ArrayStoreException{
		if(arg0.length <= size){
			int i = 0;
			for(E e : this){
				arg0[i] = (T) e;
				i++;
			}
			if(i < arg0.length){
				arg0[i] = null;
			}
			return arg0;
		}else{
			T[] ret = Arrays.copyOf(arg0, size);
			int i = 0;
			for(E e : this){
				ret[i] = (T) e;
				i++;
			}
			return ret;
		}
	}

	public List<String> prettyPrint(){
		return new PrettyPrinter().prettyPrint();
	}
	
	class PrettyPrinter{

		public List<String> prettyPrint(){
			return prettyPrint(root);
		}
		
		final String sBar = "|  ";
		final String sSpace = "   ";
		final String sBranch = "";
		final String sLeaf = "+--@";
		final String sRight = "r-";
		final String sLeft = "L-";
		final String sRoot = "o-";
		
		public List<String> prettyPrint(Node node){
			List<String> theList = new ArrayList<String>();
			if (node == null){
				theList.add(sLeaf);
				return theList;
			}

			String paddingL;
			String paddingR;
			String point;
			
			Handedness h = node.getHandedness();
			switch (h) {
			case ROOT:
				paddingL = sSpace;
				paddingR = sSpace;
				point = sRoot;
				break;
			case LEFT:
				paddingL = sSpace;
				paddingR = sBar;
				point = sLeft;
				break;
			case RIGHT:
				paddingL = sBar;
				paddingR = sSpace;
				point = sRight;
				break;
			default:
				paddingL = sBar;
				paddingR = sBar;
				point = sRoot;
				break;
			}
			
			if(node.right != null){
				List<String> r = prettyPrint(node.right);
				for(String s : r){
					theList.add(paddingR + s);
				}
				theList.add(paddingR+sBar);
			}
			
			theList.add(point + sBranch + node.toString());
			
			if(node.left != null){
				theList.add(paddingL+sBar);
				List<String> l = prettyPrint(node.left);
				for(String s : l){
					theList.add(paddingL + s);
				}
			}
			return theList;
		}
	}
	
	public E pop(){
		if(root != null){
			E head = root.value;
			removeNode(root);
			return head;
		}else{
			return null;
		}
	}
}
