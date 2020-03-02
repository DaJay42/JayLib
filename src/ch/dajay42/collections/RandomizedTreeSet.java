package ch.dajay42.collections;

import ch.dajay42.util.GloballyUniquePriority;

import java.util.*;


public class RandomizedTreeSet<E> implements Set<E>, Queue<E>{
	
	private Node root;
	private int size;
	
	private final Comparator<E> comparator;
	
	public RandomizedTreeSet() {
		size = 0;
		root = null;
		comparator = null;
	}
	
	public RandomizedTreeSet(Comparator<E> comparator) {
		size = 0;
		root = null;
		this.comparator = comparator;
	}
	
	public RandomizedTreeSet(Collection<E> collection) {
		this();
		this.addAll(collection);
	}
	
	public RandomizedTreeSet(Collection<E> collection, Comparator<E> comparator) {
		this(comparator);
		this.addAll(collection);
	}
	
	
	private int compareValues(E l, E r){
		if(comparator != null)
			return comparator.compare(l, r);
		else
			//noinspection unchecked
			return ((Comparable<? super E>) l).compareTo(r);
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
		while(true){
			c = compareValues(node.getValue(), arg0);
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
	}
	
	@Override
	public boolean offer(E e){
		return add(e);
	}
	
	@Override
	public E remove(){
		if(root != null){
			E head = root.value;
			removeNode(root);
			return head;
		}else{
			throw new NoSuchElementException();
		}
	}
	
	@Override
	public E poll(){
		if(root != null){
			E head = root.value;
			removeNode(root);
			return head;
		}else{
			return null;
		}
	}
	
	@Override
	public E element(){
		if(root != null){
			return root.value;
		}else{
			throw new NoSuchElementException();
		}
	}
	
	@Override
	public E peek(){
		if(root != null){
			return root.value;
		}else{
			return null;
		}
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
	
	@Override
	public boolean contains(Object arg0) {
		Node node = getNodeOf(arg0);
		return (node != null);
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
	
	@Override
	public boolean remove(Object arg0) {
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
		//noinspection SuspiciousMethodCalls
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
				((Object[]) arg0)[i] = e;
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
	
	
	
	
	enum Handedness{ROOT, LEFT, RIGHT, INVALID}
	
	class Node{
		E value;
		GloballyUniquePriority priority;
		
		Node parent = null;
		Node left = null;
		Node right = null;
		
		Node(E value){
			this.value = value;
			this.priority = new GloballyUniquePriority();
		}
		
		E getValue() {
			return value;
		}
		
		GloballyUniquePriority getPriority() {
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
	
	@SuppressWarnings("unchecked")
	Node getNodeOf(Object arg0) throws ClassCastException{
		E e = (E) arg0;
		Node node = root;
		while(node != null){
			int c = compareValues(e, node.getValue());
			if(c == 0){
				return node;
			}else if(c < 0){
				node = node.left;
			}else{
				node = node.right;
			}
		}
		return null;
	}
	
	private Node firstChild(Node current){
		if(current == null) return null;
		while(current.left != null){
			current = current.left;
		}
		return current;
	}
	
	private Node lastChild(Node current){
		if(current == null) return null;
		while(current.right != null){
			current = current.right;
		}
		return current;
	}
	
	private Node nextNode(Node current){
		if(current == null) return null;
		//in-order Walk
		if(current.right != null){
			return firstChild(current.right);
		} else {
			//get parent of closest non-right-handed ancestor
			while(current.getHandedness().equals(Handedness.RIGHT)){
				current = current.parent;
			}
			return current.parent;
		}
	}
	
	private Node prevNode(Node current){
		if(current == null) return null;
		//reverse in-order Walk
		if(current.left != null){
			return lastChild(current.left);
		} else {
			//get parent of closest non-left-handed ancestor
			while(current.getHandedness().equals(Handedness.LEFT)){
				current = current.parent;
			}
			return current.parent;
		}
	}
	
	@SuppressWarnings("SuspiciousNameCombination")
	private void rotateLeft(Node x){
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
	
	@SuppressWarnings("SuspiciousNameCombination")
	private void rotateRight(Node y){
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
	
	private void restorePriorities(Node n){
		restorePrioritiesUp(n);
		restorePrioritiesDown(n);
	}
	
	private void restorePrioritiesUp(Node n){
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
	
	private void restorePrioritiesDown(Node n){
		while(n.left != null || n.right != null){
			boolean takeLeftRotateRight;
			//take smaller between right and left
			if(n.left != null && n.right != null){
				takeLeftRotateRight = n.left.priority.compareTo(n.right.priority) <= 0;
			}else {
				takeLeftRotateRight = n.left != null;
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
	
	private void removeNode(Node n){
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
	
	@Override
	public Iterator<E> iterator() {
		return new RandomizedTreeSetIterator();
	}
	
	
	public List<String> prettyPrint(){
		return new PrettyPrinter().prettyPrint();
	}
	
	class PrettyPrinter{

		List<String> prettyPrint(){
			return prettyPrint(root);
		}
		
		final String sBar = "|  ";
		final String sSpace = "   ";
		final String sBranch = "";
		final String sLeaf = "+--@";
		final String sRight = "r-";
		final String sLeft = "L-";
		final String sRoot = "o-";
		
		List<String> prettyPrint(Node node){
			List<String> theList = new ArrayList<>();
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
	
	private class RandomizedTreeSetIterator implements Iterator<E>{
		Node node = null;
		boolean first = true;
		
		@Override
		public boolean hasNext() {
			if(first){
				return root != null;
			}
			else {
				return nextNode(node) != null;
			}
		}
		
		@Override
		public E next() {
			if(first){
				first = false;
				node = firstChild(root);
			}
			else {
				node = nextNode(node);
			}
			if(node == null) throw new NoSuchElementException();
			return node.value;
		}
		
		@Override
		public void remove() {
			Node prev = prevNode(node);
			if(prev == null){
				first = true;
			}
			removeNode(node);
			node = prev;
		}
	}
}
