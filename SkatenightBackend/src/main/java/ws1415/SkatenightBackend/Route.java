package ws1415.SkatenightBackend;

import com.google.appengine.api.datastore.Key;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Repräsentiert eine Strecke mit den zugehörigen Metadaten.
 * Created by Richard on 31.10.2014.
 */
@PersistenceCapable
public class Route {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    @Persistent
    private String name;
    /**
     * Speichert die Koordinaten der Route als komprimierten String ab.
     */
    @Persistent
    private String routeData;
    @Persistent
    private String length;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Key getKey() {
        return key;
    }

    public String getRouteData() {
        return routeData;
    }

    public void setRouteData(String routeData) {
        this.routeData = routeData;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}