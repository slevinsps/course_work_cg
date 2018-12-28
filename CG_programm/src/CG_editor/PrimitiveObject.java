package CG_editor;

public class PrimitiveObject {
    public PrimitiveObject () {};
    public ColorCG getColor (Vector4 pos) { 
        return new ColorCG(0.0f, 0.0f, 0.0f); 
    }
	
    public Vector4 getNormalAt(Vector4 intersection_position) {
            return new Vector4(0, 0, 0);
    }

    public float findIntersection(Ray ray) {
            return 0;
    }
    
}
