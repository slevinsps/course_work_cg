package CG_editor;

import CG_editor.Vector4;
import CG_editor.Vector4;

public class Ray {
    public Vector4 origin;
    public Vector4 direction;
    public Ray () {
	origin = new Vector4(0,0,0);
	direction = new Vector4(1,0,0);
    }

    public Ray (Vector4 o, Vector4 d) {
            origin = o;
            direction = d;
    }
    
    public Vector4 getRayOrigin () { return origin; }
    public Vector4 getRayDirection () { return direction; }
}
