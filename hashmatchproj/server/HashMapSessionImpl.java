package ProjetoSD.hashmatchproj.server;

import java.io.Serializable;

public class HashMapSessionImpl implements HashMatchSessionRI, Serializable {

    DBMockup dbMockup;

    public HashMapSessionImpl() {
       this.dbMockup = dbMockup;
    }

}
