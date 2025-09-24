//Load vertices data into GPU so it can be used for render, it receives an array of floats which represents a structured coord system for drawing a figure

package blackjack.engine.graph;

import org.lwjgl.opengl.GL30;
// import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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

    public Mesh(float[] positions, float[] colors, int[] indices){
        numVertices = indices.length;

        vboIdList = new ArrayList<>();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        //Positions VBO (index 0 for attribs)
        int vboId = glGenBuffers();
        vboIdList.add(vboId);

        FloatBuffer positionsBuffer = MemoryUtil.memCallocFloat(positions.length);
        positionsBuffer.put(0, positions);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        //Colors VBO (index 1 for attribs)
        vboId = glGenBuffers();
        vboIdList.add(vboId);

        FloatBuffer colorsBuffer = MemoryUtil.memCallocFloat(colors.length);
        colorsBuffer.put(0, colors);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);

        glEnableVertexAttribArray(1);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);


        //Index VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);

        IntBuffer indicesBuffer = MemoryUtil.memCallocInt(indices.length);
        indicesBuffer.put(0, indices);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //unbind VBOs and VAO after all setup is completed
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        //free mem resources
        MemoryUtil.memFree(positionsBuffer);
        MemoryUtil.memFree(colorsBuffer);
        MemoryUtil.memFree(indicesBuffer);
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
