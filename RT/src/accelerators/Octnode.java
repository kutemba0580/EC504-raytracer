/**
 * 
 */
package accelerators;

import java.util.ArrayList;
import java.util.Arrays;

import objects.SceneObject;
import geometry.BBox;
import geometry.Pt;
import geometry.Ray;

/**
 * @author DavidsMac
 * contains 8 children octnodes/leafs is intersectable
 */
public class Octnode {
	
	Octnode children[] = null;
	BBox bbox;
	boolean occupied = false;
	int depth;
	int maxdepth;
	
	public Octnode(BBox bb,int dep, int mdep){
		bbox = new BBox(bb);
		depth = dep;
		maxdepth = mdep;
	}
	
	public void split(){
		Pt centerpt = bbox.lerp(0.5f,0.5f, 0.5f);
		Pt[] corners = bbox.getCorners();
		
		//if depth is less than max depth initialize to ocnode, if on last level initialize to ocleaf
		if(depth < maxdepth){ 
			children = new Octnode[8];
		} else{
			children = new Octleaf[8];
		}
		for(int i=0 ; i<8; i++){
			children[i] = new Octleaf(new BBox(corners[i], centerpt), depth+1, maxdepth);
		}
	}
	
	/**
	 * check to see if P lies within Ocnode.
	 * @param P point to compare with Ocnode's bounding box
	 * @return true if P lies within Ocnodes bounding box. False otherwise.
	 */
	private boolean contains(Pt P){
		return bbox.inside(P);
	}
	
	public void insert(SceneObject scnobj, BBox objbb){
		if(occupied == false){
			occupied = true; //I have something in me!
			split();
		}
		ArrayList<Pt> corners = new ArrayList<Pt>(Arrays.asList(objbb.getCorners()));
		for(int i=0 ; i<8; i++){
			for(int j=0; j < corners.size(); j++){
				if(children[i].contains(corners.get(j))){
					children[i].insert(scnobj, objbb);
					corners.remove(j);
					break;
				}
			}
		}
		
		
	}
	
	public boolean IntersectP(Ray ray, ArrayList<SceneObject> lastIntersectedObject){
		boolean intersected = false;
	    if(occupied && bbox.IntersectP(ray, new float[2])){
	    	for(int i = 0; i<8 ; i++){
	    		intersected = intersected || children[i].IntersectP(ray, lastIntersectedObject);
	    	}
	    }
	   	return intersected; 
	}
}
