package CG_editor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
public class OBJModel
{
	private class OBJIndex
	{
		private int vertexIndex;
		private int texCoordIndex;
		private int normalIndex;

		public int GetVertexIndex()   { return vertexIndex; }
		public int GetTexCoordIndex() { return texCoordIndex; }
		public int GetNormalIndex()   { return normalIndex; }

		public void SetVertexIndex(int val)   { vertexIndex = val; }
		public void SetTexCoordIndex(int val) { texCoordIndex = val; }
		public void SetNormalIndex(int val)   { normalIndex = val; }

		@Override
		public int hashCode()
		{
			final int BASE = 17;
			final int MULTIPLIER = 31;

			int result = BASE;

			result = MULTIPLIER * result + vertexIndex;
			result = MULTIPLIER * result + texCoordIndex;
			result = MULTIPLIER * result + normalIndex;

			return result;
		}
	}

	private List<Vector4> positions;
	private List<Vector4> texCoords;
	private List<Vector4> normals;
	private List<OBJIndex> indices;
	private boolean        hasTexCoords;
	private boolean        hasNormals;

	private static String[] RemoveEmptyStrings(String[] data)
	{
		List<String> result = new ArrayList<String>();
		
		for(int i = 0; i < data.length; i++)
			if(!data[i].equals(""))
				result.add(data[i]);
		
		String[] res = new String[result.size()];
		result.toArray(res);
		
		return res;
	}

	public OBJModel(String fileName) throws IOException
	{
		positions = new ArrayList<Vector4>();
		texCoords = new ArrayList<Vector4>();
		normals = new ArrayList<Vector4>();
		indices = new ArrayList<OBJIndex>();
		hasTexCoords = false;
		hasNormals = false;

		BufferedReader meshReader = null;

		meshReader = new BufferedReader(new FileReader(fileName));
		String line;

		while((line = meshReader.readLine()) != null)
		{
			String[] tokens = line.split(" ");
			tokens = RemoveEmptyStrings(tokens);

			if(tokens.length == 0 || tokens[0].equals("#"))
                            continue;
			else if(tokens[0].equals("v"))
			{
                            positions.add(new Vector4(Float.valueOf(tokens[1]),
                                            Float.valueOf(tokens[2]),
                                            Float.valueOf(tokens[3]),1));
			}
			else if(tokens[0].equals("vt"))
			{
                            texCoords.add(new Vector4(Float.valueOf(tokens[1]),
                                            1.0f - Float.valueOf(tokens[2]),0,0));
			}
			else if(tokens[0].equals("vn"))
			{
                            normals.add(new Vector4(Float.valueOf(tokens[1]),
                                            Float.valueOf(tokens[2]),
                                            Float.valueOf(tokens[3]),0));
			}
			else if(tokens[0].equals("f"))
			{
                            for(int i = 0; i < tokens.length - 3; i++)
                            {
                                indices.add(ParseOBJIndex(tokens[1]));
                                indices.add(ParseOBJIndex(tokens[2 + i]));
                                indices.add(ParseOBJIndex(tokens[3 + i]));
                            }
			}
		}

		
		meshReader.close();
	}

	public Model ToIndexedModel()
	{
		Model result = new Model();
		Model normalModel = new Model();
		Map<OBJIndex, Integer> resultIndexMap = new HashMap<OBJIndex, Integer>();
		Map<Integer, Integer> normalIndexMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();

		for(int i = 0; i < indices.size(); i++)
		{
			OBJIndex currentIndex = indices.get(i);

			Vector4 currentPosition = positions.get(currentIndex.GetVertexIndex());
			Vector4 currentTexCoord;
			Vector4 currentNormal;

			if(hasTexCoords)
				currentTexCoord = texCoords.get(currentIndex.GetTexCoordIndex());
			else
				currentTexCoord = new Vector4(0,0,0,0);

			if(hasNormals)
				currentNormal = normals.get(currentIndex.GetNormalIndex());
			else
				currentNormal = new Vector4(0,0,0,0);

			Integer modelVertexIndex = resultIndexMap.get(currentIndex);

			if(modelVertexIndex == null)
			{
				modelVertexIndex = result.GetPositions().size();
				resultIndexMap.put(currentIndex, modelVertexIndex);

				result.GetPositions().add(currentPosition);
				result.GetTexCoords().add(currentTexCoord);
				if(hasNormals)
					result.GetNormals().add(currentNormal);
			}

			Integer normalModelIndex = normalIndexMap.get(currentIndex.GetVertexIndex());

			if(normalModelIndex == null)
			{
				normalModelIndex = normalModel.GetPositions().size();
				normalIndexMap.put(currentIndex.GetVertexIndex(), normalModelIndex);

				normalModel.GetPositions().add(currentPosition);
				normalModel.GetTexCoords().add(currentTexCoord);
				normalModel.GetNormals().add(currentNormal);
				normalModel.GetTangents().add(new Vector4(0,0,0,0));
			}

			result.GetIndices().add(modelVertexIndex);
			normalModel.GetIndices().add(normalModelIndex);
			indexMap.put(modelVertexIndex, normalModelIndex);
		}

		if(!hasNormals)
		{
			normalModel.CalcNormals();

			for(int i = 0; i < result.GetPositions().size(); i++)
				result.GetNormals().add(normalModel.GetNormals().get(indexMap.get(i)));
		}

		normalModel.CalcTangents();

		for(int i = 0; i < result.GetPositions().size(); i++)
			result.GetTangents().add(normalModel.GetTangents().get(indexMap.get(i)));

		return result;
	}

	private OBJIndex ParseOBJIndex(String token)
	{
		String[] values = token.split("/");

		OBJIndex result = new OBJIndex();
		result.SetVertexIndex(Integer.parseInt(values[0]) - 1);

		if(values.length > 1)
		{
                    if(!values[1].isEmpty())
                    {
                        hasTexCoords = true;
                        result.SetTexCoordIndex(Integer.parseInt(values[1]) - 1);
                    }

                    if(values.length > 2)
                    {
                        hasNormals = true;
                        result.SetNormalIndex(Integer.parseInt(values[2]) - 1);
                    }
		}

		return result;
	}
}
