package blackjack.engine.scene;

//this class hold 3D scene elements (models, etc)
//currently it just tores the meshes (sets of vertices) of the models we want to dray

import blackjack.engine.graph.Mesh;

import java.util.*;

public class Scene {

    private Map<String, Mesh> meshMap;
    
    private Projection projection;

    public Scene(int width, int height){
        
        meshMap = new HashMap<>();
        
        projection = new Projection(width, height);
    }

    public void addMesh(String meshId, Mesh mesh){
        meshMap.put(meshId, mesh);
    }

    //update projection matrix when the window is resized so it scales to the new size
    public void resize(int width, int height){

        projection.updateProjMatrix(width, height);

    }

    //free resources
    public void cleanup(){
        meshMap.values().forEach(Mesh::cleanup);
    }

    //getters
    public Map<String, Mesh> getMeshMap() {
        return meshMap;
    }

    public Projection getProjection() {
        return projection;
    }

}
