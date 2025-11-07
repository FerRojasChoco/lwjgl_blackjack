package blackjack.engine.scene;

import blackjack.engine.IGuiInstance;

//this class hold 3D scene elements (models, etc)
//currently it just tores the meshes (sets of vertices) of the models we want to dray

//hold a reference for SceneLights to render lights

import blackjack.engine.graph.Model;
import blackjack.engine.graph.TextureCache;
import blackjack.engine.scene.lights.SceneLights;

import java.util.*;

public class Scene {

    private Map<String, Model> modelMap;
    private Projection projection;
    private TextureCache textureCache;
    private Camera camera;
    private IGuiInstance guiInstance;
    private SceneLights sceneLights;
    private Entity selectedEntity;

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

    //getters and setters
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

    public IGuiInstance getGuiInstance() {
        return guiInstance;
    }

    public void setGuiInstance(IGuiInstance guiInstance) {
        this.guiInstance = guiInstance;
    }

    public SceneLights getSceneLights() {
        return sceneLights;
    }

    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSceneLights(SceneLights sceneLights) {
        this.sceneLights = sceneLights;
    }

    public void setSelectedEntity(Entity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }
}
