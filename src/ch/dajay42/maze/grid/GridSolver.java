package ch.dajay42.maze.grid;

import java.util.Arrays;

public class GridSolver{
	public static final int WALL = 2;
	public static final int NODE = -1;
	
	private final int[] data;
	private final int rowLen;
	private final int len;
	
	GridSolver(int[] data, int rowLen){
		this.len = data.length;
		if(len < 1 || len % rowLen != 0)
			throw new IllegalArgumentException();
		this.data = data;
		this.rowLen = rowLen;
	}
	
	private int getN(int i){
		return i < rowLen ? WALL : data[i-rowLen];
	}
	private int getS(int i){
		return i + rowLen >= len ? WALL : data[i+rowLen];
	}
	private int getW(int i){
		return i % rowLen < 1 ? WALL : data[i-1];
	}
	private int getE(int i){
		return (i % rowLen) + 1 >= rowLen ? WALL : data[i+1];
	}
	private int getNE(int i){
		return i < rowLen || (i % rowLen) + 1 >= rowLen ? WALL : data[i-rowLen+1];
	}
	private int getSE(int i){
		return i + rowLen >= len || (i % rowLen) + 1 >= rowLen ? WALL : data[i+rowLen+1];
	}
	private int getNW(int i){
		return i < rowLen || i % rowLen < 1 ? WALL : data[i-rowLen-1];
	}
	private int getSW(int i){
		return i + rowLen >= len || i % rowLen < 1 ? WALL : data[i+rowLen-1];
	}
	
	private boolean cornerCondition(int i){
		return (getW(i) == WALL && getN(i) == WALL && getNE(i) == WALL);
	}

	private boolean isCorner(int i){
		return (getW(i) == WALL ^ getE(i) == WALL) && (getN(i) == WALL ^ getS(i) == WALL);
	}
	
	void prune(){
		final int[] next = new int[len];
		while(!Arrays.equals(next,data)){
			System.arraycopy(next,0, data,0, len);
			Arrays.parallelSetAll(next, i -> {
				if(data[i] != 0){
					return data[i];
				}else if(getN(i) + getE(i) + getW(i) + getS(i) > WALL + WALL) {
					return WALL;
				}//TODO
				return 0;
			});
		}
	}
}
