package blackjack.engine.scene;

//this class hold 3D scene elements (models, etc)
//currently it just tores the meshes (sets of vertices) of the models we want to dray

import blackjack.engine.graph.Mesh;

import java.util.*;

public class Scene {

    private Map<String, Mesh> meshMap;

    public Scene(){
        meshMap = new HashMap<>();
    }

    public void addMesh(String meshId, Mesh mesh){
        meshMap.put(meshId, mesh);
    }

    //free resources
    public void cleanup(){
        meshMap.values().forEach(Mesh::cleanup);
    }

    //getters
    public Map<String, Mesh> getMeshMap() {
        return meshMap;
    }

}
