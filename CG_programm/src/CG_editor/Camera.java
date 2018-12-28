package CG_editor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Camera 
{
	private static final Vector4 Y_AXIS = new Vector4(0,1,0);

	private Transform transform;
	private Matrix projection;
        private Vector4 CameraRight;
        private Vector4 CameraDown;
        
        
        public Vector4 getCameraPosition () { return GetTransform().GetTransformedPos(); }
	public Vector4 getCameraDirection () { return GetTransform().GetRot().GetForward(); }
	public Vector4 getCameraRight () { return  CameraRight; }
	public Vector4 getCameraDown () { return CameraDown; }

	private Transform GetTransform()
	{
		return transform;
	}

	public Camera(Matrix projection)
	{
		this.projection = projection;
		this.transform = new Transform();
                this.CameraRight = Y_AXIS.Cross(getCameraDirection()).Normalized();
                this.CameraDown = this.CameraRight.Cross(getCameraDirection()).Normalized();
	}

	public Matrix GetViewProjection()
	{
		Matrix cameraRotation = GetTransform().GetTransformedRot().Negative().ToRotationMatrix();
		Vector4 cameraPos = GetTransform().GetTransformedPos().Mul(-1);

		Matrix cameraTranslation = new Matrix().CreateMovement(cameraPos.GetX(), cameraPos.GetY(), cameraPos.GetZ());

		return projection.Mul(cameraRotation.Mul(cameraTranslation));
	}


	public void Update(Input input, float delta)
	{
		final float sensitivityX = 2.66f * delta;
		final float sensitivityY = 2.0f * delta;
		final float movAmt = 5.0f * delta;

                
		if(input.GetKey(KeyEvent.VK_W))
			Move(GetTransform().GetRot().GetForward(), movAmt);
		if(input.GetKey(KeyEvent.VK_S))
			Move(GetTransform().GetRot().GetForward(), -movAmt);
		if(input.GetKey(KeyEvent.VK_A))
			Move(GetTransform().GetRot().GetLeft(), movAmt);
		if(input.GetKey(KeyEvent.VK_D))
			Move(GetTransform().GetRot().GetRight(), movAmt);
		
		if(input.GetKey(KeyEvent.VK_RIGHT))
                    Rotate(Y_AXIS, sensitivityX);
		if(input.GetKey(KeyEvent.VK_LEFT))
                    Rotate(Y_AXIS, -sensitivityX);
		if(input.GetKey(KeyEvent.VK_DOWN))
                    Rotate(GetTransform().GetRot().GetRight(), sensitivityY);
		if(input.GetKey(KeyEvent.VK_UP))
                    Rotate(GetTransform().GetRot().GetRight(), -sensitivityY);
	}
        
       
	public void Move(Vector4 dir, float amt)
	{
		transform = GetTransform().SetPos(GetTransform().GetPos().Add(dir.Mul(amt)));
                this.CameraRight = Y_AXIS.Cross(getCameraDirection()).Normalized();
                this.CameraDown = this.CameraRight.Cross(getCameraDirection());
	}

	public void Rotate(Vector4 axis, float angle)
	{
		transform = GetTransform().Rotate(new Quaternion(axis, angle));
                this.CameraRight = Y_AXIS.Cross(getCameraDirection()).Normalized();
                this.CameraDown = this.CameraRight.Cross(getCameraDirection());
	}
}
