//Load vertices data into GPU so it can be used for render, it receives an array of floats which represents a structured coord system for drawing a figure

package blackjack.engine.graph;

import org.lwjgl.opengl.GL30;
// import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL30.*;

/*
 * Vertex Buffer Object (VBO) is a mem buffer stored in the GPU's memory that stores vertices
 * Vertex Array Object (VAO) is an object that contains one or more VBOs which are usually called attribute lists
 * Each attribute can hold one type of data (pos, color, texture, etc)
 */

public class Mesh {

    private int numVertices;
    private int vaoId;
    private List<Integer> vboIdList;

    public Mesh(float[] positions, int numVertices){
        this.numVertices = numVertices;
        vboIdList = new ArrayList<>();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        //positions VBO
        int vboId = glGenBuffers();
        vboIdList.add(vboId);

        FloatBuffer positionsBuffer = MemoryUtil.memCallocFloat(positions.length);
        positionsBuffer.put(0, positions);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        MemoryUtil.memFree(positionsBuffer);
    }

    //free resources
    public void cleanup(){
        
        vboIdList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(vaoId);

    }

    //getters
    public int getNumVertices() {
        return numVertices;
    }

    public final int getVaoId() {
        return vaoId;
    }

}
