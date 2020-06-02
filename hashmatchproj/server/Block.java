package ProjetoSD.hashmatchproj.server;

import java.io.Serializable;

public class Block implements Serializable {

    public boolean isFinished;
    public boolean isOcupied;
    public long startLine;
    public long endLine;

    public Block(boolean isFinished, boolean isOcupied, long startLine, long endLine) {
        this.isFinished = isFinished;
        this.isOcupied = isOcupied;
        this.startLine = startLine;
        this.endLine = endLine;
    }
}
