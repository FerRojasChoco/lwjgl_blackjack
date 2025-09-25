package blackjack.engine.scene;

//this class hold 3D scene elements (models, etc)
//currently it just tores the meshes (sets of vertices) of the models we want to dray

import blackjack.engine.graph.Model;
import blackjack.engine.graph.TextureCache;

import java.util.*;

public class Scene {

    private Map<String, Model> modelMap;
    private Projection projection;
    private TextureCache textureCache;
    private Camera camera;

    public Scene(int width, int height){
        
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();
        camera = new Camera();
    }

    public void addEntity(Entity entity){

        String modelId = entity.getModelID();
        Model model = modelMap.get(modelId);

        if (model == null){
            throw new RuntimeException("COULD NOT FIND MODEL [" + modelId + "]");
        }

        model.getEntitiesList().add(entity);
    
    }

    public void addModel(Model model){
        modelMap.put(model.getId(), model);
    }

    //update projection matrix when the window is resized so it scales to the new size
    public void resize(int width, int height){
        projection.updateProjMatrix(width, height);
    }

    //free resources
    public void cleanup(){
        modelMap.values().forEach(Model::cleanup);
    }

    //getters
    public Map<String, Model> getModelMap() {
        return modelMap;
    }

    public Projection getProjection() {
        return projection;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public Camera getCamera() {
        return camera;
    }
}
