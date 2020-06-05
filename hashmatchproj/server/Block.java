package ProjetoSD.hashmatchproj.server;

import java.io.Serializable;

public class Block implements Serializable {

    public boolean isFinished;
    public boolean isOcupied;
    public boolean isLast;
    public long startLine;
    public long endLine;

    public Block(boolean isFinished, boolean isOcupied, long startLine, long endLine, boolean isLast) {
        this.isFinished = isFinished;
        this.isOcupied = isOcupied;
        this.startLine = startLine;
        this.endLine = endLine;
        this.isLast = isLast;

    }
}
