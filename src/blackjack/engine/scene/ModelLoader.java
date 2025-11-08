package blackjack.engine.scene;

import org.joml.Vector3f;

/*this class is used to load 3D models with Assimp (open asset import library)   */

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import blackjack.engine.Consts;
import blackjack.engine.graph.*;

import java.io.File;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.assimp.Assimp.*;


public class ModelLoader {

    private ModelLoader(){

    }

    public static Model loadModel(String modelId, String modelPath, TextureCache textureCache){

        return loadModel(
            modelId, modelPath, textureCache,
            aiProcess_JoinIdenticalVertices | aiProcess_Triangulate |
            aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace |
            aiProcess_LimitBoneWeights | aiProcess_GenBoundingBoxes |
            aiProcess_PreTransformVertices
        );

    }

    public static Model loadModel(String modelId, String modelPath, TextureCache textureCache, int flags){

        //load file path and model         
        File file = new File(modelPath);

        if (!file.exists()){
            throw new RuntimeException("MODEL PATH DOES NOT EXIST [" + modelPath + "]");
        }

        String modelDir = file.getParent();

        AIScene aiScene = aiImportFile(modelPath, flags);

        if (aiScene == null){
            throw new RuntimeException("ERROR LOADING MODEL [modelPath: " + modelPath + "]");
        }

        /*
         * process materials contained in the models, they define color and textures
         * to be used by the meshes that compose the models
         */

        int numMaterials = aiScene.mNumMaterials();
        List<Material> materialList = new ArrayList<>();

        for (int i = 0; i < numMaterials; i++){
            
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            materialList.add(processMaterial(aiMaterial, modelDir, textureCache));

        }

        /*
         * process the different meshes, a model can define several meshes
         * and each of them can use one of the materials defined for the model
         * that is why this is done after materials and link to them, to avoid
         * repeating binding calss when rendering
         */
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Material defaultMaterial = new Material();

        for (int i = 0; i < numMeshes; i++){

            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh);
            int materialIdx = aiMesh.mMaterialIndex();

            Material material;
            
            if (materialIdx >= 0 && materialIdx < materialList.size()){
                material = materialList.get(materialIdx);
            }
            else{
                material = defaultMaterial;
            }

            material.getMeshList().add(mesh);
        }

        if (!defaultMaterial.getMeshList().isEmpty()){
            materialList.add(defaultMaterial);
        }

        return new Model(modelId, materialList);
    }



    private static Material processMaterial(AIMaterial aiMaterial, String modelDir, TextureCache textureCache){

        Material material = new Material();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            
            AIColor4D color = AIColor4D.create();

            //~~ light work haha ~~//
            //ambient
            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color);
            if(result == aiReturn_SUCCESS)
                material.setAmbientColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));

            //diffuse
            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
            if(result == aiReturn_SUCCESS)
                material.setDiffusecolor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            
            //specular
            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color);
            if(result == aiReturn_SUCCESS)
                material.setSpecularColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            
            //reflectance
            float reflectance = 0.0f;
            float[] shininessFacotr = new float[]{0.0f};
            int[] pMax = new int[]{1};

            result = aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0, shininessFacotr, pMax);
            
            if (result == aiReturn_SUCCESS)
                reflectance = shininessFacotr[0];
            
            material.setReflectance(reflectance);

            //texture
            AIString aiTexturePath = AIString.calloc(stack);

            aiGetMaterialTexture(
                aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, 
                (IntBuffer) null, null, null, null,
                 null, null
            );

            String texturePath = aiTexturePath.dataString();

            if (texturePath != null && texturePath.length() > 0){

                material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
                textureCache.createTexture(material.getTexturePath());
                material.setDiffusecolor(Consts.DEFAULT_COLOR);

            }
            
            //normalsMap
            AIString aiNormalMapPath = AIString.calloc(stack);
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS, 0, aiNormalMapPath, 
                    (IntBuffer) null, null, null, null, null, null);

            String normalMapPath = aiNormalMapPath.dataString();

            if (normalMapPath != null && normalMapPath.length() > 0) {
                material.setNormalMapPath(modelDir + File.separator + new File(normalMapPath).getName());
                textureCache.createTexture(material.getNormalMapPath());
            }

            return material;
        }
    }

    private static Mesh processMesh(AIMesh aiMesh){

        float[] vertices = processVertices(aiMesh);
        float[] normals = processNormals(aiMesh);
        float[] textCoords = processTextCoords(aiMesh);
        int[] indices = processIndices(aiMesh);

        //load data for tangents and bitangents
        float[] tangents = processTangents(aiMesh, normals);
        float[] bitangents = processBitangents(aiMesh, normals);

        //texture coords may not have been populated, we need at least the empty slots
        if (textCoords.length == 0){
            int numElements = (vertices.length / 3) * 2;
            textCoords = new float[numElements];
        }

        AIAABB aabb = aiMesh.mAABB();
        Vector3f aabbMin = new Vector3f(aabb.mMin().x(), aabb.mMin().y(), aabb.mMin().z());
        Vector3f aabbMax = new Vector3f(aabb.mMax().x(), aabb.mMax().y(), aabb.mMax().z());

        return new Mesh(vertices, normals, tangents, bitangents, textCoords, indices, aabbMin, aabbMax);
    }

    /*the process verts, textCoords and indices just invoke the 
    * corresponding method over over the AIMesh instance that 
    * returns the desired data and store it into an array */
    private static int[] processIndices(AIMesh aiMesh){

        List<Integer> indices = new ArrayList<>();
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();

        for (int i = 0; i < numFaces; i++){

            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();

            while (buffer.remaining() > 0){
                indices.add(buffer.get());
            }
        }

        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    private static float[] processNormals(AIMesh aiMesh){

        AIVector3D.Buffer buffer = aiMesh.mNormals();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;

        while (buffer.remaining() > 0){
            AIVector3D normal = buffer.get();
            data[pos++] = normal.x();
            data[pos++] = normal.y();
            data[pos++] = normal.z();
        }
        return data;
    }

    private static float[] processTextCoords(AIMesh aiMesh){

        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);

        if (buffer == null){
            return new float[]{};
        }

        float[] data = new float[buffer.remaining() * 2];
        int pos = 0;

        while (buffer.remaining() > 0){

            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = 1 - textCoord.y();

        }

        return data;
    }

    private static float[] processVertices(AIMesh aiMesh){

        AIVector3D.Buffer buffer = aiMesh.mVertices();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;

        while (buffer.remaining() > 0){

            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = textCoord.y();
            data[pos++] = textCoord.z();

        }
        
        return data;
    }

    private static float[] processBitangents(AIMesh aiMesh, float[] normals){
        AIVector3D.Buffer buffer = aiMesh.mBitangents();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D aiBitangent = buffer.get();
            data[pos++] = aiBitangent.x();
            data[pos++] = aiBitangent.y();
            data[pos++] = aiBitangent.z();
        }

        // Assimp may not calculate tangents with models that do not have texture coordinates. Just create empty values
        if (data.length == 0) {
            data = new float[normals.length];
        }
        return data;
    }

    private static float[] processTangents(AIMesh aiMesh, float[] normals) {

        AIVector3D.Buffer buffer = aiMesh.mTangents();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D aiTangent = buffer.get();
            data[pos++] = aiTangent.x();
            data[pos++] = aiTangent.y();
            data[pos++] = aiTangent.z();
        }

        // Assimp may not calculate tangents with models that do not have texture coordinates. Just create empty values
        if (data.length == 0) {
            data = new float[normals.length];
        }
        return data;
    }
}
