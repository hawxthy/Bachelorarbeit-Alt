package ws1415.common.controller;

import com.skatenight.skatenightAPI.model.Event;
import com.skatenight.skatenightAPI.model.EventMetaData;

import java.io.IOException;
import java.util.List;

import ws1415.common.net.ServiceProvider;
import ws1415.common.task.ExtendedTask;
import ws1415.common.task.ExtendedTaskDelegate;

/**
 * Stellt Funktionen zur Verarbeitung von Events bereit.
 * @author Richard Schulze
 */
public abstract class EventController {

    /**
     * Ruft eine Liste aller Events ab, die auf dem Server gespeichert sind. Es werden dabei nur die
     * Metadaten der Events abgerufen.
     * @param handler Der Handler, der die abgerufene Liste übergeben bekommt.
     */
    public static void listEventsMetaData(ExtendedTaskDelegate<Void, List<EventMetaData>> handler) {
        new ExtendedTask<Void, Void, List<EventMetaData>>(handler) {
            @Override
            protected List<EventMetaData> doInBackground(Void... params) {
                try {
                    return ServiceProvider.getService().eventEndpoint().getEventsMetaData().execute().getItems();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();
    }

    /**
     * Ruft das Event mit der angebenen ID ab.
     * @param handler   Der Handler, der das abgerufene Event übergeben bekommt.
     * @param eventId   Die ID des abzurufenden Events.
     */
    public static void getEvent(ExtendedTaskDelegate<Void, Event> handler, final long eventId) {
        new ExtendedTask<Void, Void, Event>(handler) {
            @Override
            protected Event doInBackground(Void... params) {
                try {
                    return ServiceProvider.getService().eventEndpoint().getEvent(eventId).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();
    }

    /**
     * Erstellt das angegebene Event auf dem Server.
     * @param handler    Der Handler, der das erstellte Event übergeben bekommt.
     * @param event      Das zu erstellende Event.
     */
    public static void createEvent(ExtendedTaskDelegate<Void, Event> handler, final Event event) {
        new ExtendedTask<Void, Void, Event>(handler) {
            @Override
            protected Event doInBackground(Void... params) {
                try {
                    return ServiceProvider.getService().eventEndpoint().createEvent(event).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();
    }

    /**
     * Löscht das Event mit der angegebenen ID.
     * @param handler    Der Handler, der über den Status des Task informiert wird.
     * @param eventId    Die ID des zu löschenden Events.
     */
    public static void deleteEvent(ExtendedTaskDelegate<Void, Void> handler, final long eventId) {
        new ExtendedTask<Void, Void, Void>(handler) {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ServiceProvider.getService().eventEndpoint().deleteEvent(eventId).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        }.execute();
    }


    public static void joinEvent(ExtendedTaskDelegate<Void, Void> handler, final long eventId) {
        // TODO
    }


    public static void leaveEvent(ExtendedTaskDelegate<Void, Void> handler, final long eventId) {
        // TODO
    }

}
