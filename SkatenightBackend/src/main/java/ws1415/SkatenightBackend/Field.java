package ws1415.SkatenightBackend;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.repackaged.org.codehaus.jackson.annotate.JsonCreator;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Klasse für die Informationsfelder einer Veranstaltung
 * <p/>
 * Created by Bernd Eissing on 02.12.2014.
 */
public class Field implements Serializable {
    private String title;
    private String value;
    private int type;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
