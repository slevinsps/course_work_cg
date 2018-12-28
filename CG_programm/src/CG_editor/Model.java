package CG_editor;

import java.util.ArrayList;
import java.util.List;
import CG_editor.Vector4;

public class Model
{
	private List<Vector4> positions;
	private List<Vector4> texCoords;
	private List<Vector4> normals;
	private List<Vector4> tangents;
	private List<Integer>  indices;

	public Model()
	{
		positions = new ArrayList<Vector4>();
		texCoords = new ArrayList<Vector4>();
		normals = new ArrayList<Vector4>();
		tangents = new ArrayList<Vector4>();
		indices = new ArrayList<Integer>();
	}

	public void CalcNormals()
	{
		for(int i = 0; i < indices.size(); i += 3)
		{
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);

			Vector4 v1 = positions.get(i1).Sub(positions.get(i0));
			Vector4 v2 = positions.get(i2).Sub(positions.get(i0));

			Vector4 normal = v1.Cross(v2).Normalized();

			normals.set(i0, normals.get(i0).Add(normal));
			normals.set(i1, normals.get(i1).Add(normal));
			normals.set(i2, normals.get(i2).Add(normal));
		}

		for(int i = 0; i < normals.size(); i++)
			normals.set(i, normals.get(i).Normalized());
	}

	public void CalcTangents()
	{
		for(int i = 0; i < indices.size(); i += 3)
		{
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);

			Vector4 edge1 = positions.get(i1).Sub(positions.get(i0));
			Vector4 edge2 = positions.get(i2).Sub(positions.get(i0));

			float deltaU1 = texCoords.get(i1).GetX() - texCoords.get(i0).GetX();
			float deltaV1 = texCoords.get(i1).GetY() - texCoords.get(i0).GetY();
			float deltaU2 = texCoords.get(i2).GetX() - texCoords.get(i0).GetX();
			float deltaV2 = texCoords.get(i2).GetY() - texCoords.get(i0).GetY();

			float dividend = (deltaU1*deltaV2 - deltaU2*deltaV1);
			float f = dividend == 0 ? 0.0f : 1.0f/dividend;

			Vector4 tangent = new Vector4(
					f * (deltaV2 * edge1.GetX() - deltaV1 * edge2.GetX()),
					f * (deltaV2 * edge1.GetY() - deltaV1 * edge2.GetY()),
					f * (deltaV2 * edge1.GetZ() - deltaV1 * edge2.GetZ()),
					0);
			
			tangents.set(i0, tangents.get(i0).Add(tangent));
			tangents.set(i1, tangents.get(i1).Add(tangent));
			tangents.set(i2, tangents.get(i2).Add(tangent));
		}

		for(int i = 0; i < tangents.size(); i++)
			tangents.set(i, tangents.get(i).Normalized());
	}

	public List<Vector4> GetPositions() { return positions; }
	public List<Vector4> GetTexCoords() { return texCoords; }
	public List<Vector4> GetNormals() { return normals; }
	public List<Vector4> GetTangents() { return tangents; }
	public List<Integer>  GetIndices() { return indices; }
}
