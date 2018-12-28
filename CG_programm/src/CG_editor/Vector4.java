package CG_editor;

public class Vector4
{
	private final float x;
	private final float y;
	private  float z;
	private final float w;

	public Vector4(float x, float y, float z, float w)
	{
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
	}

	public Vector4(float x, float y, float z)
	{
            this(x, y, z, 1.0f);
	}
        
        public float GetX()
	{
		return x;
	}

	public float GetY()
	{
		return y;
	}

	public float GetZ()
	{
		return z;
	}

	public float GetW()
	{
		return w;
	}

	public float Length()
	{
            return (float)Math.sqrt(x * x + y * y + z * z + w * w);
	}

	public float Dot3(Vector4 r)
	{
            return x * r.GetX() + y * r.GetY() + z * r.GetZ();// + w * r.GetW();
	}
        
        public float Dot(Vector4 r)
	{
            return x * r.GetX() + y * r.GetY() + z * r.GetZ() + w * r.GetW();
	}

	public Vector4 Cross(Vector4 r)
	{
            float x_ = y * r.GetZ() - z * r.GetY();
            float y_ = z * r.GetX() - x * r.GetZ();
            float z_ = x * r.GetY() - y * r.GetX();

            return new Vector4(x_, y_, z_, 0);
	}

	public Vector4 Normalized()
	{
		float length = Length3(); 

		return new Vector4(x / length, y / length, z / length, w / length);
	}

	public Vector4 Rotate(Vector4 axis, float angle)
	{
		float sinAngle = (float)Math.sin(-angle);
		float cosAngle = (float)Math.cos(-angle);

		return this.Cross(axis.Mul(sinAngle)).Add(           
				(this.Mul(cosAngle)).Add(                
						axis.Mul(this.Dot(axis.Mul(1 - cosAngle))))); 
	}

	public Vector4 Rotate(Quaternion rotation)
	{
		Quaternion w = rotation.Mul(this).Mul(rotation.Negative());

		return new Vector4(w.GetX(), w.GetY(), w.GetZ(), 1.0f);
	}

	public Vector4 Lerp(Vector4 dest, float lerpFactor)
	{
		return dest.Sub(this).Mul(lerpFactor).Add(this);
	}

	public Vector4 Add(Vector4 r)
	{
		return new Vector4(x + r.GetX(), y + r.GetY(), z + r.GetZ(), w + r.GetW());
	}

	public Vector4 Add(float r)
	{
		return new Vector4(x + r, y + r, z + r, w + r);
	}

	public Vector4 Sub(Vector4 r)
	{
		return new Vector4(x - r.GetX(), y - r.GetY(), z - r.GetZ(), w - r.GetW());
	}

	public Vector4 Sub(float r)
	{
		return new Vector4(x - r, y - r, z - r, w - r);
	}

	public Vector4 Mul(Vector4 r)
	{
		return new Vector4(x * r.GetX(), y * r.GetY(), z * r.GetZ(), w * r.GetW());
	}

	public Vector4 Mul(float r)
	{
		return new Vector4(x * r, y * r, z * r, w * r);
	}


        public Vector4 Negative () {
		return new Vector4(-x, -y, -z, 1);
	}
        
        public float Length3 () {
            return (float)Math.sqrt((x*x) + (y*y) + (z*z));
	}

}
