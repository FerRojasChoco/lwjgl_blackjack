package blackjack.engine.graph;

import blackjack.engine.Consts;
import blackjack.engine.scene.Entity;
// import blackjack.engine.Window;
import blackjack.engine.scene.Scene;
import blackjack.engine.scene.lights.AmbientLight;
import blackjack.engine.scene.lights.DirLight;
import blackjack.engine.scene.lights.PointLight;
import blackjack.engine.scene.lights.SceneLights;
import blackjack.engine.scene.lights.SpotLight;

import java.util.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL30.*;

public class SceneRender {

    private ShaderProgram shaderProgram;

    private UniformsMap uniformsMap;

    public SceneRender(){

        //create two shader module data instances (one for each shader module) and with them create a shader program
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();

        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/scene.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/scene.frag", GL_FRAGMENT_SHADER));

        shaderProgram = new ShaderProgram(shaderModuleDataList);

        createUniforms();
    }


    //draw the mesh into the screen
    //iterate over the meshes stored in the scene instance, bind them and draw the vertices of the VAO
    public void render(Scene scene){

        shaderProgram.bind();
        updateLights(scene);

        //set uniforms before drawing elements
        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
        uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
        uniformsMap.setUniform("txtSampler", 0);
        
        Collection<Model> models = scene.getModelMap().values();
        TextureCache textureCache = scene.getTextureCache();
        Entity selectedEntity = scene.getSelectedEntity();

        for(Model model : models){

            List<Entity> entities = model.getEntitiesList();

            for(Material material : model.getMaterialList()) {

                String normalMapPath = material.getNormalMapPath();
                boolean hasNormalMapPath = normalMapPath != null;

                uniformsMap.setUniform("material.hasNormalMap", hasNormalMapPath ? 1 : 0);
                uniformsMap.setUniform("material.ambient", material.getAmbientColor());
                uniformsMap.setUniform("material.diffuse", material.getDiffusecolor());
                uniformsMap.setUniform("material.specular", material.getSpecularColor());
                uniformsMap.setUniform("material.reflectance", material.getReflectance());

                Texture texture = textureCache.getTexture(material.getTexturePath());
                glActiveTexture(GL_TEXTURE0);
                texture.bind();

                if (hasNormalMapPath) {
                    Texture normalMapTexture = textureCache.getTexture(normalMapPath);
                    glActiveTexture(GL_TEXTURE1);
                    normalMapTexture.bind();
                }

                for (Mesh mesh : material.getMeshList()) {

                    glBindVertexArray(mesh.getVaoId());

                    for(Entity entity : entities){
                        
                        uniformsMap.setUniform("selected",
                                selectedEntity != null && selectedEntity.getId().equals(entity.getId()) ? 1 : 0);
                                
                        uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                       
                        glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
                    
                    }
                }
            }
        }

        glBindVertexArray(0);

        shaderProgram.unbind();

    }

    //free resources
    public void cleanup(){
        shaderProgram.cleanup();
    }

    public void createUniforms(){

        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("modelMatrix");
        uniformsMap.createUniform("txtSampler");
        uniformsMap.createUniform("normalSampler");
        uniformsMap.createUniform("viewMatrix");

        uniformsMap.createUniform("material.ambient");
        uniformsMap.createUniform("material.diffuse");
        uniformsMap.createUniform("material.specular");
        uniformsMap.createUniform("material.reflectance");
        uniformsMap.createUniform("material.hasNormalMap");

        uniformsMap.createUniform("ambientLight.factor");
        uniformsMap.createUniform("ambientLight.color");

        uniformsMap.createUniform("selected");

        for (int i = 0; i < Consts.MAX_POINT_LIGHTS; i++){
            String name = "pointLights[" + i + "]";
            uniformsMap.createUniform(name + ".position");
            uniformsMap.createUniform(name + ".color");
            uniformsMap.createUniform(name + ".intensity");
            uniformsMap.createUniform(name + ".att.constant");
            uniformsMap.createUniform(name + ".att.linear");
            uniformsMap.createUniform(name + ".att.exponent");
        }
        
        for (int i = 0; i < Consts.MAX_SPOT_LIGHTS; i++){
            String name = "spotLights[" + i + "]";
            uniformsMap.createUniform(name + ".pl.position");
            uniformsMap.createUniform(name + ".pl.color");
            uniformsMap.createUniform(name + ".pl.intensity");
            uniformsMap.createUniform(name + ".pl.att.constant");
            uniformsMap.createUniform(name + ".pl.att.linear");
            uniformsMap.createUniform(name + ".pl.att.exponent");
            uniformsMap.createUniform(name + ".conedir");
            uniformsMap.createUniform(name + ".cutoff");
        }

        uniformsMap.createUniform("dirLight.color");
        uniformsMap.createUniform("dirLight.direction");
        uniformsMap.createUniform("dirLight.intensity");

    }

    //update the uniforms for lights for each render call
    private void updateLights(Scene scene){

        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();
    
        SceneLights sceneLights = scene.getSceneLights();
        
        AmbientLight ambientLight = sceneLights.getAmbientLight();
        uniformsMap.setUniform("ambientLight.factor", ambientLight.getIntensity());
        uniformsMap.setUniform("ambientLight.color", ambientLight.getColor());

        DirLight dirLight = sceneLights.getDirLight();
        
        Vector4f auxDir = new Vector4f(dirLight.getDirection(), 0);
        auxDir.mul(viewMatrix);
    
        uniformsMap.setUniform("dirLight.color", dirLight.getColor());
        uniformsMap.setUniform("dirLight.direction", dirLight.getDirection());
        uniformsMap.setUniform("dirLight.intensity", dirLight.getIntensity());
    
        List<PointLight> pointLights = sceneLights.getPointLights();
        int numPointLights = pointLights.size();
        PointLight pointLight;

        for (int i = 0; i < Consts.MAX_POINT_LIGHTS; i++){
            if (i < numPointLights){
                pointLight = pointLights.get(i);
            }
            else{
                pointLight = null;
            }

            String name = "pointLights[" + i + "]";
            updatePointLight(pointLight, name, viewMatrix);
        }
    
        List<SpotLight> spotLights = sceneLights.getSpotLights();
        int numSpotLights = spotLights.size();
        SpotLight spotLight;

        for (int i = 0; i < Consts.MAX_SPOT_LIGHTS; i++){
            if (i < numSpotLights){
                spotLight = spotLights.get(i);
            }
            else{
                spotLight = null;
            }

            String name = "spotLights[" + i + "]";
            updateSpotLight(spotLight, name, viewMatrix);
        }

    }

    private void updatePointLight(PointLight pointLight, String prefix, Matrix4f viewMatrix){
        
        Vector4f aux = new Vector4f();
        Vector3f lightPosition = new Vector3f();
        Vector3f color = new Vector3f();

        float intensity = 0.0f;
        float constant = 0.0f;
        float linear = 0.0f;
        float exponent = 0.0f;

        if (pointLight != null){
            aux.set(pointLight.getPosition(), 1);
            aux.mul(viewMatrix);

            lightPosition.set(aux.x, aux.y, aux.z);
            color.set(pointLight.getColor());

            intensity = pointLight.getIntensity();

            PointLight.Attenuation attenuation = pointLight.getAttenuation();
            constant = attenuation.getConstant();
            linear = attenuation.getLinear();
            exponent = attenuation.getExponent();
        }

        uniformsMap.setUniform(prefix + ".position", lightPosition);
        uniformsMap.setUniform(prefix + ".color", color);
        uniformsMap.setUniform(prefix + ".intensity", intensity);
        uniformsMap.setUniform(prefix + ".att.constant", constant);
        uniformsMap.setUniform(prefix + ".att.linear", linear);
        uniformsMap.setUniform(prefix + ".att.exponent", exponent);
    }

    private void updateSpotLight(SpotLight spotLight, String prefix, Matrix4f viewMatrix){
        PointLight pointLight = null;
        
        Vector3f coneDirection = new Vector3f();
        
        float cutoff = 0.0f;

        if (spotLight != null){
            coneDirection = spotLight.getConeDirection();
            cutoff = spotLight.getCutOff();
            pointLight = spotLight.getPointLight();
        }

        uniformsMap.setUniform(prefix + ".conedir", coneDirection);
        uniformsMap.setUniform(prefix + ".cutoff", cutoff);
        updatePointLight(pointLight,  prefix + ".pl", viewMatrix);
    }
    


}
