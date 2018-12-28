package CG_editor;

import CG_editor.Matrix;
import CG_editor.Quaternion;
import CG_editor.Vector4;

public class Transform
{
	private Vector4   pos_t;
	private Quaternion rot_t;
	private Vector4   scale_t;
        private Vector4   rot_euler_t;
	public Transform()
	{
		this(new Vector4(0,0,0,0), new Vector4(1,1,1,1));
	}

	public Transform(Vector4 pos, Vector4 scale)
	{
		this(pos, new Quaternion(0,0,0,1), scale, new Vector4(0,0,0,1));
	}
        
        public Transform(Vector4 pos, Quaternion q, Vector4 scale)
	{
		this(pos, q, scale, new Vector4(0,0,0,1));
	}

	public Transform(Vector4 pos, Quaternion rot, Vector4 scale,  Vector4 rot_euler)
	{
		pos_t = pos;
		rot_t = rot;
		scale_t = scale;
                rot_euler_t = rot_euler; 
	}
        
        public Vector4 GetTransformedPos()
	{
		return pos_t;
	}

	public Quaternion GetTransformedRot()
	{
		return rot_t;
	}

	public Vector4 GetPos()
	{
		return pos_t;
	}

	public Quaternion GetRot()
	{
		return rot_t;
	}
        
        public Vector4 GetEulerRot()
	{
		return rot_euler_t;
	}

	public Vector4 GetScale()
	{
		return scale_t;
	}

	public Transform SetPos(Vector4 pos)
	{
		return new Transform(pos, rot_t, scale_t, rot_euler_t);
	}
        
        public Transform SetScale(Vector4 scale)
	{
		return new Transform(pos_t, rot_t, scale, rot_euler_t);
	}
        
	public Transform Rotate(Quaternion rotation)
	{
		return new Transform(pos_t, rotation.Mul(rot_t).Normalized(), scale_t, rot_euler_t);
	}
        
        public Transform RotateFromNull(float ox, float oy, float oz)
	{
            rot_euler_t = new Vector4(ox, oy, oz, 1);
            
            float to_rad = (float)Math.PI / 180;
            Quaternion rotation = Quaternion.QuaternionFromEuler(ox * to_rad, oy * to_rad, oz * to_rad);
            
            return new Transform(pos_t, rotation.Normalized(), scale_t, rot_euler_t);
	}

	public Matrix GetTransformation()
	{
		Matrix translationMatrix = new Matrix().CreateMovement(pos_t.GetX(), pos_t.GetY(), pos_t.GetZ());
		Matrix rotationMatrix = rot_t.ToRotationMatrix();
		Matrix scaleMatrix = new Matrix().CreateScale(scale_t.GetX(), scale_t.GetY(), scale_t.GetZ());

		return translationMatrix.Mul(rotationMatrix.Mul(scaleMatrix));
	}

	
}
