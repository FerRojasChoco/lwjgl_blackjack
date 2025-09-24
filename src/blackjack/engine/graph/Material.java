package blackjack.engine.graph;

import java.util.*;

public class Material {

    private List<Mesh> meshList;
    private String texturePath;

    public Material(){
        meshList = new ArrayList<>();
    }

    //free resources
    public void cleanup(){
        meshList.forEach(Mesh::cleanup);
    }

    //getters and setters
    public List<Mesh> getMeshList() {
        return meshList;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }
    
}
