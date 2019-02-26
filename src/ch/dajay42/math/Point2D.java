package ch.dajay42.math;

import java.io.Serializable;
import java.lang.Math;
import java.util.Collection;
import java.util.Comparator;

/**Mathematical 2D point/vector. Holds state in Cartesian format.
 * 
 */
public class Point2D implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private double x;

	private double y;
	
	private double lcache;
	private boolean haslcache = false;
	
	/**Create a new zeroed Point2D;
	 * x and y are 0.0.
	 */
	public Point2D(){
		this(0.0d, 0.0d);
	}
	/**Create a new Point2D based on parameters;
	 * x and y.
	 * @param x x
	 * @param y y
	 */
	public Point2D(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	/**Creates new Point2D equal to difference between
	 * Point2D start and Point2D end.
	 * @param start
	 * @param end
	 */
	public Point2D(Point2D start, Point2D end){
		this(end.x - start.x, end.y - start.y);
	}
	
	/**Creates new Point2D equal to Point2D other.
	 * @param other
	 */
	public Point2D(Point2D other){
		this(other.x, other.y);
		lcache = other.lcache;
		haslcache = other.haslcache;
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		haslcache = false;
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		haslcache = false;
		this.y = y;
	}
	/**Returns the 2-norm length of the Point2D. 
	 * Probably by far the most expensive function of this class, 
	 * so we use caching.
	 * @return length
	 */
	public double length(){
		if(!haslcache){
			//for some reason, this is a lot slower
			//lcache = Math.hypot(x, y);
			lcache = Math.sqrt(x*x+y*y);
			haslcache = true;
		}
		return lcache;
	}
	
	/**Inverts the direction of the Point2D,
	 * returns itself.
	 * @return this
	 */
	public Point2D invert(){
		x = -x;
		y = -y;
		
		return this;
	}
	
	/**Inverts the direction of a copy of the Point2D,
	 * returns the copy.
	 * @return inverted copy
	 */
	public Point2D getInverse(){
		return (new Point2D(this)).invert();
	}

	/**Sets the length of the Point2D to 1,
	 * unless it is 0, returns itself.
	 * @return this
	 */
	public Point2D normalize(){
		double len = length();
		if(len > 0d && len != 1d)
			stretch(1/len);
		
		return this;
	}
	
	/**Sets the length of a copy of the Point2D to 1,
	 * unless it is 0, returns it.
	 * @return normalized copy
	 */
	public Point2D getNormalized(){
		return (new Point2D(this)).normalize();
	}
	
	/**Returns new Point2D equal to sum
	 * of this and other.
	 * @param other
	 * @return new Point2D equal to sum
	 */
	public Point2D getSum(Point2D other){
		return new Point2D(this).add(other);
	}
	
	/**Adds Point2D other to this,
	 * returns itself
	 * @param other Point2D to add
	 * @return this
	 */
	public Point2D add(Point2D other){
		x += other.x;
		y += other.y;
		haslcache = false;
		
		return this;
	}
	
	/**Multiplies the length of the Point2D by scalar,
	 * returns itself.
	 * @param scalar
	 * @return this
	 */
	public Point2D stretch(double scalar){
		x *= scalar;
		y *= scalar;
		lcache *= Math.abs(scalar);
		
		return this;
	}
	
	/**Multiplies the length of a copy of the Point2D by scalar,
	 * returns the copy.
	 * @param scalar
	 * @return this
	 */
	public Point2D getStretched(double scalar){
		
		return (new Point2D(this)).stretch(scalar);
	}
	
	/**Sets the length of the Point2D to l,
	 * while keeping the direction,
	 * iff the length was non-zero before,
	 * otherwise does nothing;
	 * returns itself.
	 * @param l the new length
	 * @return this
	 */
	public Point2D setLength(double l){
		double len = length();
		if(len > 0){
			double scalar = l/len;
			x *= scalar;
			y *= scalar;
			lcache = l;
		}
		return this;
	}
	
	/**If the length of the Point2D is bigger than max,
	 * sets it to max, otherwise does nothing;
	 * returns itself.
	 * Undefined behavior if max is nonpositive.
	 * @param max length
	 * @return this
	 */
	public Point2D clamp(double max){
		double len = length();
		if(len > 0){
			double f = max/len;
			if(f < 1)
				stretch(f);
		}
		return this;
	}
	
	/**If the length of the Point2D is bigger than max,
	 * sets it to max, otherwise does nothing;
	 * returns a copy.
	 * Undefined behavior if max is nonpositive.
	 * @param max length
	 * @return clamped copy of this
	 */
	public Point2D getClamped(double max){
		return (new Point2D(this)).clamp(max);
	}
	
	/**Returns a copy of the Point2D,
	 * rotated by 90 degrees.
	 * @param clockwise specifies whether to rotate clockwise or counterclockwise
	 * @return rotated copy of this
	 */
	public Point2D getNormal(boolean clockwise){
		Point2D normal;
		if(clockwise){
			normal = new Point2D(y, -x);
		}else{
			normal = new Point2D(-y, x);
		}
		normal.haslcache = this.haslcache;
		normal.lcache = this.lcache;
		
		return normal;
	}
	
	/**Computes the dot product of this and other
	 * @param other Point2D
	 * @return dot product
	 */
	public double dot(Point2D other){
		return (this.x * other.x + this.y * other.y);
	}
	
	public double cross(Point2D other) {
		return (this.x * other.y - this.y * other.x);
	}
	
	public Point2D mirrorX(){
		this.x = -x;
		return this;
	}
	
	public Point2D getMirroredX(){
		return new Point2D(this).mirrorX();
	}
	
	public Point2D mirrorY(){
		this.y = -y;
		return this;
	}
	
	public Point2D getMirroredY(){
		return new Point2D(this).mirrorY();
	}
	
	/**Multiplies the x-value of the Point2D by scalar,
	 * returns itself.
	 * @param scalar
	 * @return this
	 */
	public Point2D stretchX(double scalar){
		x *= scalar;
		haslcache = false;
		
		return this;
	}
	
	/**Multiplies the x-value of a copy of the Point2D by scalar,
	 * returns the copy.
	 * @param scalar
	 * @return this
	 */
	public Point2D getStretchedX(double scalar){
		
		return (new Point2D(this)).stretchX(scalar);
	}
	
	/**Multiplies the y-value of the Point2D by scalar,
	 * returns itself.
	 * @param scalar
	 * @return this
	 */
	public Point2D stretchY(double scalar){
		y *= scalar;
		haslcache = false;
		
		return this;
	}
	
	/**Multiplies the y-value of a copy of the Point2D by scalar,
	 * returns the copy.
	 * @param scalar
	 * @return this
	 */
	public Point2D getStretchedY(double scalar){
		
		return (new Point2D(this)).stretchY(scalar);
	}
	
	
	/** Returns a Point2D that is equal to the sum
	 * of all elements of collection
	 * @param collection collection of Point2Ds
	 * @return sum of all elements
	 */
	public static Point2D sumAll(Collection<Point2D> collection){
		double x = 0, y = 0;
		for(Point2D item : collection){
			x += item.x;
			y += item.y;
		}
		return new Point2D(x,y);
	}
	
	/** Returns a Point2D that is equal to the sum
	 * of all elements of array
	 * @param array array of Point2Ds
	 * @return sum of all elements
	 */
	public static Point2D sumAll(Point2D[] array){
		double x = 0, y = 0;
		for(Point2D item : array){
			x += item.x;
			y += item.y;
		}
		return new Point2D(x,y);
	}
	
	public static final Comparator<Point2D> LENGTH_COMPARATOR = Comparator.comparingDouble(Point2D::length);
	
	/**Creates a new Point2D with x, y equal to the polar coordinates r, phi,
	 * <br/> where phi = 0 is aligned with x+
	 * <p/>This is very inefficient when compared to the regular constructors.
	 * <br/>Whenever possible, use Cartesian coordinates instead.
	 * @param r length
	 * @param phi angle in radians
	 * @return
	 */
	public static Point2D makeFromPolar(double r, double phi){
		return new Point2D(r * Math.cos(phi), r * Math.sin(phi));
	}
	
	
	public double getAngle(){
		return Math.atan2(y,x);
	}
	
	public Point2D getRotated(double angle){
		return makeFromPolar(length(), getAngle() + angle);
	}
	
}
