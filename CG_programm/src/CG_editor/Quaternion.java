package CG_editor;

public class Quaternion
{
	private float xq;
	private float yq;
	private float zq;
	private float wq;
        
        public Vector4 GetForward()
	{
		return new Vector4(0,0,1,1).Rotate(this);
	}

	public Vector4 GetBack()
	{
		return new Vector4(0,0,-1,1).Rotate(this);
	}

	public Vector4 GetUp()
	{
		return new Vector4(0,1,0,1).Rotate(this);
	}

	public Vector4 GetDown()
	{
		return new Vector4(0,-1,0,1).Rotate(this);
	}

	public Vector4 GetRight()
	{
		return new Vector4(1,0,0,1).Rotate(this);
	}

	public Vector4 GetLeft()
	{
		return new Vector4(-1,0,0,1).Rotate(this);
	}
	
	public float GetX()
	{
		return xq;
	}

	public float GetY()
	{
		return yq;
	}

	public float GetZ()
	{
		return zq;
	}

	public float GetW()
	{
		return wq;
	}

	public Quaternion(float x, float y, float z, float w)
	{
		this.xq = x;
		this.yq = y;
		this.zq = z;
		this.wq = w;
	}

	public Quaternion(Vector4 axis, float angle)
	{
		float sinHalfAngle = (float)Math.sin(angle / 2);
		float cosHalfAngle = (float)Math.cos(angle / 2);

		this.xq = axis.GetX() * sinHalfAngle;
		this.yq = axis.GetY() * sinHalfAngle;
		this.zq = axis.GetZ() * sinHalfAngle;
		this.wq = cosHalfAngle;
	}

	public float Length()
	{
		return (float)Math.sqrt(xq * xq + yq * yq + zq * zq + wq * wq);
	}
	
	public Quaternion Normalized()
	{
		float length = Length();
		
		return new Quaternion(xq / length, yq / length, zq / length, wq / length);
	}
	
	public Quaternion Negative()
	{
		return new Quaternion(-xq, -yq, -zq, wq);
	}

	public Quaternion Mul(float r)
	{
		return new Quaternion(xq * r, yq * r, zq * r, wq * r);
	}

	public Quaternion Mul(Quaternion r)
	{
		float w_ = wq * r.GetW() - xq * r.GetX() - yq * r.GetY() - zq * r.GetZ();
		float x_ = xq * r.GetW() + wq * r.GetX() + yq * r.GetZ() - zq * r.GetY();
		float y_ = yq * r.GetW() + wq * r.GetY() + zq * r.GetX() - xq * r.GetZ();
		float z_ = zq * r.GetW() + wq * r.GetZ() + xq * r.GetY() - yq * r.GetX();
		
		return new Quaternion(x_, y_, z_, w_);
	}
	
	public Quaternion Mul(Vector4 r)
	{
		float w_ = -xq * r.GetX() - yq * r.GetY() - zq * r.GetZ();
		float x_ =  wq * r.GetX() + yq * r.GetZ() - zq * r.GetY();
		float y_ =  wq * r.GetY() + zq * r.GetX() - xq * r.GetZ();
		float z_ =  wq * r.GetZ() + xq * r.GetY() - yq * r.GetX();
		
		return new Quaternion(x_, y_, z_, w_);
	}

	public Quaternion Sub(Quaternion r)
	{
		return new Quaternion(xq - r.GetX(), yq - r.GetY(), zq - r.GetZ(), wq - r.GetW());
	}

	public Quaternion Add(Quaternion r)
	{
		return new Quaternion(xq + r.GetX(), yq + r.GetY(), zq + r.GetZ(), wq + r.GetW());
	}
        
        
        public static  Quaternion QuaternionFromEuler(float ax, float ay, float az) {
            Vector4 vx = new Vector4(1, 0, 0);
            Vector4 vy = new Vector4(0, 1, 0);
            Vector4 vz = new Vector4(0, 0, 1);
            Quaternion qx = new Quaternion(vx, ax);
            Quaternion qy = new Quaternion(vy, ay);
            Quaternion qz = new Quaternion(vz, az);
            Quaternion qt = qx.Mul(qy);
            qt = qt.Mul(qz);
            return qt;
        }
	public Matrix ToRotationMatrix()
	{
		Vector4 forward =  new Vector4(2.0f * (xq * zq - wq * yq), 2.0f * (yq * zq + wq * xq), 1.0f - 2.0f * (xq * xq + yq * yq));
		Vector4 up = new Vector4(2.0f * (xq * yq + wq * zq), 1.0f - 2.0f * (xq * xq + zq * zq), 2.0f * (yq * zq - wq * xq));
		Vector4 right = new Vector4(1.0f - 2.0f * (yq * yq + zq * zq), 2.0f * (xq * yq - wq * zq), 2.0f * (xq * zq + wq * yq));

		return new Matrix().CreateRotation(forward, up, right);
	}

	public float Dot(Quaternion r)
	{
		return xq * r.GetX() + yq * r.GetY() + zq * r.GetZ() + wq * r.GetW();
	}

	
	public Quaternion(Matrix rot)
	{
		float trace = rot.Get(0, 0) + rot.Get(1, 1) + rot.Get(2, 2);

		if(trace > 0)
		{
			float s = 0.5f / (float)Math.sqrt(trace+ 1.0f);
			wq = 0.25f / s;
			xq = (rot.Get(1, 2) - rot.Get(2, 1)) * s;
			yq = (rot.Get(2, 0) - rot.Get(0, 2)) * s;
			zq = (rot.Get(0, 1) - rot.Get(1, 0)) * s;
		}
		else
		{
			if(rot.Get(0, 0) > rot.Get(1, 1) && rot.Get(0, 0) > rot.Get(2, 2))
			{
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.Get(0, 0) - rot.Get(1, 1) - rot.Get(2, 2));
				wq = (rot.Get(1, 2) - rot.Get(2, 1)) / s;
				xq = 0.25f * s;
				yq = (rot.Get(1, 0) + rot.Get(0, 1)) / s;
				zq = (rot.Get(2, 0) + rot.Get(0, 2)) / s;
			}
			else if(rot.Get(1, 1) > rot.Get(2, 2))
			{
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.Get(1, 1) - rot.Get(0, 0) - rot.Get(2, 2));
				wq = (rot.Get(2, 0) - rot.Get(0, 2)) / s;
				xq = (rot.Get(1, 0) + rot.Get(0, 1)) / s;
				yq = 0.25f * s;
				zq = (rot.Get(2, 1) + rot.Get(1, 2)) / s;
			}
			else
			{
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.Get(2, 2) - rot.Get(0, 0) - rot.Get(1, 1));
				wq = (rot.Get(0, 1) - rot.Get(1, 0) ) / s;
				xq = (rot.Get(2, 0) + rot.Get(0, 2) ) / s;
				yq = (rot.Get(1, 2) + rot.Get(2, 1) ) / s;
				zq = 0.25f * s;
			}
		}

		float length = (float)Math.sqrt(xq * xq + yq * yq + zq * zq + wq * wq);
		xq /= length;
		yq /= length;
		zq /= length;
		wq /= length;
	}

	


}
