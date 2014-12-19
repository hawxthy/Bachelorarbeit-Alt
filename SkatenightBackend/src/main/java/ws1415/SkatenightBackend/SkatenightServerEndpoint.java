package ws1415.SkatenightBackend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;


/**
 * Die ServerAPI, die Api-Methoden zur Verfügung stellt.
 *
 * @author Richard, Daniel
 */
@Api(name = "skatenightAPI",
    version = "v1",
    clientIds = {Constants.ANDROID_USER_CLIENT_ID, Constants.ANDROID_HOST_CLIENT_ID,
            Constants.WEB_CLIENT_ID, com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID},
    audiences = {Constants.ANDROID_AUDIENCE},
    namespace = @ApiNamespace(ownerDomain = "skatenight.com", ownerName = "skatenight"))
public class SkatenightServerEndpoint {
    private PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(
            "transactions-optional");
    private long lastFieldUpdateTime = 0;

//    /**
//     * Fügt die angegebene Mail-Adresse als Veranstalter hinzu.
//     * @param mail Die hinzuzufügende Mail-Adresse
//     */
//    public void addHost(@Named("mail") String mail) {
//        PersistenceManager pm = pmf.getPersistenceManager();
//        try {
//            Query q = pm.newQuery(Host.class);
//            q.setFilter("email == emailParam");
//            q.declareParameters("String emailParam");
//            List<Host> results = (List<Host>) q.execute(mail);
//            if (results.isEmpty()) {
//                Host h = new Host();
//                h.setEmail(mail);
//                pm.makePersistent(h);
//            }
//        } finally {
//            pm.close();
//        }
//    }
//
//    /**
//     * Entfernt die angegebene Mail-Adresse aus den Veranstaltern.
//     * @param mail Die zu entfernende Mail-Adresse
//     */
//    public void removeHost(@Named("mail") String mail) {
//        PersistenceManager pm = pmf.getPersistenceManager();
//        try {
//            Query q = pm.newQuery(Host.class);
//            q.setFilter("email == emailParam");
//            q.declareParameters("String emailParam");
//            List<Host> results = (List<Host>) q.execute(mail);
//            if (!results.isEmpty()) {
//                pm.deletePersistentAll(results);
//            }
//        } finally {
//            pm.close();
//        }
//    }

    /**
     * Prüft, ob die angegebene Mail-Adresse zu einem authorisierten Veranstalter-Account gehört.
     *
     * @param mail Die zu prüfende Mail
     * @return true, wenn der Account ein authorisierter Veranstalter ist, false sonst
     */
    public BooleanWrapper isHost(@Named("mail") String mail) {
        PersistenceManager pm = pmf.getPersistenceManager();
        try {
            Query q = pm.newQuery(Host.class);
            q.setFilter("email == emailParam");
            q.declareParameters("String emailParam");
            List<Host> results = (List<Host>) q.execute(mail);
            return new BooleanWrapper(!results.isEmpty());
        } finally {
            pm.close();
        }
    }

    /**
     * Aktualisiert das auf dem Server gespeicherte Event-Objekt.
     *
     * @param user Der User, der das Event-Objekt aktualisieren möchte.
     * @param e    Das neue Event-Objekt.
     */
    public void createEvent(User user, Event e) throws OAuthRequestException, IOException {
        if (user == null) {
            throw new OAuthRequestException("no user submitted");
        }
        if (!isHost(user.getEmail()).value) {
            throw new OAuthRequestException("user is not a host");
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        try {
            if (e != null) {
                Query q = pm.newQuery(Route.class);
                q.setFilter("name == nameParam");
                q.declareParameters("String nameParam");
                List<Route> results = (List<Route>) q.execute(e.getRoute().getName());
                if (!results.isEmpty()) {
                    e.setRoute(results.get(0));
                }
                // Weil sonst Nullpointer beim Editieren kommmt
                e.setKey(null);
                pm.makePersistent(e);
            }
        } finally {
            pm.close();
        }
    }

    /**
     * Dient Testzwecken, damit das Event ohne credentials gesetzt werden kann.
     */
    public void setEventTestMethod(Event e) {
        PersistenceManager pm = pmf.getPersistenceManager();
        try {
            // Altes Event-Objekt löschen
            List<Event> events = (List<Event>) pm.newQuery(Event.class).execute();
            pm.deletePersistentAll(events);
            if (e != null) {
                Query q = pm.newQuery(Route.class);
                q.setFilter("name == nameParam");
                q.declareParameters("String nameParam");
                List<Route> results = (List<Route>) q.execute(e.getRoute().getName());
                if (!results.isEmpty()) {
                    e.setRoute(results.get(0));
                }
                pm.makePersistent(e);
            }
        } finally {
            pm.close();
        }
    }

    /**
     * Aktualisiert die für die angegebene Mail-Adresse gespeicherte Position auf dem Server. Falls
     * kein Member-Objekt für die Mail-Adresse existiert, so wird ein neues Objekt angelegt.
     * @param mail Die Mail-Adresse des zu aktualisierenden Member-Objekts.
     * @param latitude
     * @param longitude
     */
    public void updateMemberLocation(@Named("mail") String mail,
                                     @Named("latitude") double latitude,
                                     @Named("longitude") double longitude) {
        if (mail != null) {
            Member m = getMember(mail);
            if (m == null) {
                // Neuen Member anlegen
                m = new Member();
                m.setEmail(mail);
                /**
                 * Als Name wird zurzeit die Mail-Adresse verwendet, da noch keine Eingabe-
                 * möglichkeit für den Namen besteht. (sollte im nächsten Sprint übernommen werden)
                 */
                m.setName(mail);
            }
            m.setLatitude(latitude);
            m.setLongitude(longitude);
            m.setUpdatedAt(new Date());

            calculateCurrentWaypoint(m);

            PersistenceManager pm = pmf.getPersistenceManager();
            try {
                pm.makePersistent(m);
            } finally {
                pm.close();
            }

            // Überprüfen ob mehr als 5 Minuten seit dem letzten Update vergangen sind.
            if (System.currentTimeMillis()-lastFieldUpdateTime >= 300000) {
                calculateField(m.getCurrentEventId());
                lastFieldUpdateTime = System.currentTimeMillis();
            }
        }
    }

    private void calculateCurrentWaypoint(Member member) {
        Long eventId = member.getCurrentEventId();
        if (eventId != null) {
            Event event = getEvent(member.getCurrentEventId());
            if (event != null) {
                Integer currentWaypoint = member.getCurrentWaypoint();
                if (currentWaypoint == null) {
                    member.setCurrentWaypoint(0);
                }
                List<RoutePoint> points = event.getRoute().getRoutePoints();
                if (currentWaypoint < points.size()-1) {
                    RoutePoint current = points.get(currentWaypoint);
                    RoutePoint next = points.get(currentWaypoint+1);
                    float distanceCurrent = distance(current.getLatitude(), current.getLongitude(), member.getLatitude(), member.getLongitude());
                    float distanceNext = distance(next.getLatitude(), next.getLongitude(), member.getLatitude(), member.getLongitude());
                    boolean findNextWaypoint = false;
                    if (distanceCurrent < Constants.MAX_NEXT_WAYPOINT_DISTANCE) {
                        findNextWaypoint = true;
                    }
                    if (distanceNext < distanceCurrent) {
                        if (distanceNext < Constants.MAX_NEXT_WAYPOINT_DISTANCE) {
                            member.setCurrentWaypoint(currentWaypoint+1);
                            findNextWaypoint = false;
                        }
                        else {
                            findNextWaypoint = true;
                        }
                    }
                    if (findNextWaypoint) {
                        // Den nächsten Wegpunkt finden:
                        float minDistance = Float.POSITIVE_INFINITY;
                        for (int i = currentWaypoint; i < points.size(); i++) {
                            float distance = distance(
                                    member.getLatitude(), member.getLongitude(),
                                    points.get(i).getLatitude(), points.get(i).getLongitude());

                            if (distance < 50.0f && distance < minDistance) {
                                member.setCurrentWaypoint(i);
                                minDistance = distance;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Berechnet anhand der aktuellen Positionen der Member das Feld.
     * Wobei das Feld um den Wegpunkte herum gebaut wird, welcher die meisten Member enthält
     * @param id event Id
     */
    private void calculateField(@Named("id") long id) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Event event = getEvent(id);
        List<RoutePoint> points = event.getRoute().getRoutePoints();
        List<Member> members = getMembersFromEvent(event.getKey().getId());

        // array erstellen welches an der stelle n die Anzahl der Member enthält welche am RoutePoint n sind.
        int memberCountPerRoutePoint[] = new int[points.size()];
        for (Member member : members) {
            if (member.getCurrentEventId() != null && member.getCurrentEventId() == id && member.getCurrentWaypoint() != null) {
                memberCountPerRoutePoint[member.getCurrentWaypoint()] = memberCountPerRoutePoint[member.getCurrentWaypoint()]+1;
            }
        }

        // Den index des RoutePoints speichern an welchen die meisten Member sind.
        //int mostMemberPerWaypoint = -1;
        int mostMemberIndex = 0;
        for (int i = 0; i < memberCountPerRoutePoint.length; i++) {
            if (memberCountPerRoutePoint[i] > memberCountPerRoutePoint[mostMemberIndex]) {
                mostMemberIndex = i;
            }
        }

        // Vom mostMemberIndex rückwärts gehen bis 2 aufeinanderfolgende RoutePoints jeweils weniger
        // als 5 Member haben
        int first = mostMemberIndex;
        while (first > 0) {
            if (memberCountPerRoutePoint[first-1] >= Constants.MIN_WAYPOINT_MEMBER_COUNT) {
                first--;
            } else if(first > 1 && memberCountPerRoutePoint[first-2] >= Constants.MIN_WAYPOINT_MEMBER_COUNT) {
                first-=2;
            } else {
                break;
            }
        }

        // Vom mostMemberIndex vorwaärts gehen bis 2 aufeinanderfolgende RoutePoints jeweils weniger
        // als 5 Member haben
        int last = mostMemberIndex;
        while (last < memberCountPerRoutePoint.length-1) {
            if (memberCountPerRoutePoint[last+1] >= Constants.MIN_WAYPOINT_MEMBER_COUNT) {
                last++;
            } else if(last < memberCountPerRoutePoint.length-2 && memberCountPerRoutePoint[last+2] >= Constants.MIN_WAYPOINT_MEMBER_COUNT) {
                last+=2;
            } else {
                break;
            }
        }

        event.setRouteFieldFirst(first);
        event.setRouteFieldLast(last);
        updateEvent(event);
    }

    /**
     * Kopie der Funktion zur Distanzberechnung zwischen 2 Koordinaten aus dem Android Source code.
     *
     * @param startLat
     * @param startLon
     * @param endLat
     * @param endLon
     * @return
     */
    private float distance(double startLat, double startLon,
                           double endLat, double endLon) {
        // Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
        // using the "Inverse Formula" (section 4)
        int MAXITERS = 20;
        // Convert lat/long to radians
        startLat *= Math.PI / 180.0;
        endLat *= Math.PI / 180.0;
        startLon *= Math.PI / 180.0;
        endLon *= Math.PI / 180.0;
        double a = 6378137.0; // WGS84 major axis
        double b = 6356752.3142; // WGS84 semi-major axis
        double f = (a - b) / a;
        double aSqMinusBSqOverBSq = (a * a - b * b) / (b * b);
        double L = endLon - startLon;
        double A = 0.0;
        double U1 = Math.atan((1.0 - f) * Math.tan(startLat));
        double U2 = Math.atan((1.0 - f) * Math.tan(endLat));
        double cosU1 = Math.cos(U1);
        double cosU2 = Math.cos(U2);
        double sinU1 = Math.sin(U1);
        double sinU2 = Math.sin(U2);
        double cosU1cosU2 = cosU1 * cosU2;
        double sinU1sinU2 = sinU1 * sinU2;
        double sigma = 0.0;
        double deltaSigma = 0.0;
        double cosSqAlpha = 0.0;
        double cos2SM = 0.0;
        double cosSigma = 0.0;
        double sinSigma = 0.0;
        double cosLambda = 0.0;
        double sinLambda = 0.0;
        double lambda = L; // initial guess
        for (int iter = 0; iter < MAXITERS; iter++) {
            double lambdaOrig = lambda;
            cosLambda = Math.cos(lambda);
            sinLambda = Math.sin(lambda);
            double t1 = cosU2 * sinLambda;
            double t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda;
            double sinSqSigma = t1 * t1 + t2 * t2; // (14)
            sinSigma = Math.sqrt(sinSqSigma);
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda; // (15)
            sigma = Math.atan2(sinSigma, cosSigma); // (16)
            double sinAlpha = (sinSigma == 0) ? 0.0 :
                    cosU1cosU2 * sinLambda / sinSigma; // (17)
            cosSqAlpha = 1.0 - sinAlpha * sinAlpha;
            cos2SM = (cosSqAlpha == 0) ? 0.0 :
                    cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha; // (18)
            double uSquared = cosSqAlpha * aSqMinusBSqOverBSq; // defn
            A = 1 + (uSquared / 16384.0) * // (3)
                    (4096.0 + uSquared *
                            (-768 + uSquared * (320.0 - 175.0 * uSquared)));
            double B = (uSquared / 1024.0) * // (4)
                    (256.0 + uSquared *
                            (-128.0 + uSquared * (74.0 - 47.0 * uSquared)));
            double C = (f / 16.0) *
                    cosSqAlpha *
                    (4.0 + f * (4.0 - 3.0 * cosSqAlpha)); // (10)
            double cos2SMSq = cos2SM * cos2SM;
            deltaSigma = B * sinSigma * // (6)
                    (cos2SM + (B / 4.0) *
                            (cosSigma * (-1.0 + 2.0 * cos2SMSq) -
                                    (B / 6.0) * cos2SM *
                                            (-3.0 + 4.0 * sinSigma * sinSigma) *
                                            (-3.0 + 4.0 * cos2SMSq)));
            lambda = L +
                    (1.0 - C) * f * sinAlpha *
                            (sigma + C * sinSigma *
                                    (cos2SM + C * cosSigma *
                                            (-1.0 + 2.0 * cos2SM * cos2SM))); // (11)
            double delta = (lambda - lambdaOrig) / lambda;
            if (Math.abs(delta) < 1.0e-12) {
                break;
            }
        }
        float distance = (float) (b * A * (sigma - deltaSigma));
        return distance;
    }

    public List<Event> getCurrentEventsForMember(@Named("email") String email) {
        //  Nur Events ausgeben die auch JETZT stattfinden.
        List<Event> out = new ArrayList<Event>();
        List<Event> eventList = getAllEvents();
        for (Event e : eventList) {
            if (e.getMemberList().contains(email)) {
                out.add(e);
            }
        }
        return out;
    }

    /**
     * Liefert das auf dem Server hinterlegte Member-Objekt mit der angegebenen Mail.
     * @param email Die Mail-Adresse des Member-Objekts, das abgerufen werden soll.
     * @return Das aktuelle Member-Objekt.
     */
    public Member getMember(@Named("email") String email) {
        Member member = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        try {
            Query q = pm.newQuery(Member.class);
            q.setFilter("email == emailParam");
            q.declareParameters("String emailParam");
            List<Member> results = (List<Member>) q.execute(email);
            if (!results.isEmpty()) {
                member = results.get(0);
            }
        } catch (JDOObjectNotFoundException e) {
            // Wird geworfen, wenn kein Objekt mit dem angegebenen Schlüssel existiert
            // In diesem Fall null zurückgeben
            return null;
        } finally {
            pm.close();
        }
        return member;
    }

    /**
     *
     */
    public void addMemberToEvent(@Named("id") long keyId, @Named("email") String email) {
        Event event = getEvent(keyId);

        //  Andern des currentEvent entfernen!

        PersistenceManager pm = pmf.getPersistenceManager();
        Member m = getMember(email);
        m.setCurrentEventId(event.getKey().getId());

        try {
            pm.makePersistent(m);
        }
        finally {
            pm.close();
        }


        ArrayList<String> memberKeys = event.getMemberList();
        if (!memberKeys.contains(email)) {
            memberKeys.add(email);
            event.setMemberList(memberKeys);

            updateEvent(event);
        }
    }

    public void removeMemberFromEvent(@Named("id") long keyId, @Named("email") String email) {
        Event event = getEvent(keyId);

        ArrayList<String> memberKeys = event.getMemberList();
        if (memberKeys.contains(email)) {
            memberKeys.remove(email);
            event.setMemberList(memberKeys);

            updateEvent(event);
        }
    }

    public List<Member> getMembersFromEvent(@Named("id") long keyId) {
        Event event = getEvent(keyId);

        List<Member> members = new ArrayList<Member>(event.getMemberList().size());
        for (String key: event.getMemberList()) {
            members.add(getMember(key));
        }
        return members;
    }

    /**
     * Speichert die angegebene Route auf dem Server
     *
     * @param user  Der User, der die Route hinzufügen möchte
     * @param route zu speichernde Route
     */
    public void addRoute(User user, Route route) throws OAuthRequestException {
        if (user == null) {
            throw new OAuthRequestException("no user submitted");
        }
        if (!isHost(user.getEmail()).value) {
            throw new OAuthRequestException("user is not a host");
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        if (route != null) {
            try {
                pm.makePersistent(route);
            } finally {
                pm.close();
            }
        }
    }

    /**
     * Gibt eine Liste von allen gespeicherten Routen zurück
     *
     * @return Liste der Routen.
     */
    public List<Route> getRoutes() {
        PersistenceManager pm = pmf.getPersistenceManager();

        try {
            List<Route> result = (List<Route>) pm.newQuery(Route.class).execute();
            if (result.isEmpty()) {
                return new ArrayList<Route>();
            } else {
                return result;
            }
        } finally {
            pm.close();
        }
    }

    /**
     * Lösche Route vom Server
     *
     * @param user Der User, der die Route löschen möchte
     * @param id   Die ID der zu löschenden Route.
     * @return true, wenn die Route gelöscht wurde, sonst false
     */
    public BooleanWrapper deleteRoute(User user, @Named("id") long id) throws OAuthRequestException {
        if (user == null) {
            throw new OAuthRequestException("no user submitted");
        }
        if (!isHost(user.getEmail()).value) {
            throw new OAuthRequestException("user is not a host");
        }

        List<Event> eventList = getAllEvents();
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getRoute().getKey().getId() == id) {
                return new BooleanWrapper(false);
            }
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        try {
            for (Route r : (List<Route>) pm.newQuery(Route.class).execute()) {
                if (r.getKey().getId() == id) {
                    pm.deletePersistent(r);
                    return new BooleanWrapper(true);
                }
            }
        } finally {
            pm.close();
        }
        return new BooleanWrapper(false);
    }

    /**
     * Durchsucht alle auf dem Server gespeicherten Events nach dem übergebenen Event und gibt dieses,
     * falls vorhanden zurück.
     *
     * @param keyId Id von dem Event
     * @return Das Event, null falls keins gefunden wurde
     */
    public Event getEvent(@Named("id") long keyId) {
        List<Event> eventList = getAllEvents();
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getKey().getId() == keyId) {
                return eventList.get(i);
            }
        }
        return null;
    }

    /**
     * Löscht  das übergebene Event vom Server, falls dieses existiert
     *
     * @param keyId die Id von dem Event
     * @param user  der User, der die Operation aufruft
     * @throws OAuthRequestException
     */
    public BooleanWrapper deleteEvent(@Named("id") long keyId, User user) throws OAuthRequestException {
        if (user == null) {
            throw new OAuthRequestException("no user submitted");
        }
        if (!isHost(user.getEmail()).value) {
            throw new OAuthRequestException("user is not a host");
        }
        PersistenceManager pm = pmf.getPersistenceManager();
        try {
            Event event = getEvent(keyId);
            if (event != null) {
                pm.makePersistent(event);
                pm.deletePersistent(event);
                return new BooleanWrapper(true);
            }
        } finally {
            pm.close();
        }
        return new BooleanWrapper(false);
    }

    private void updateEvent(Event event) {
        PersistenceManager pm = pmf.getPersistenceManager();

        Key key = event.getRoute().getKey();
        Route route = pm.getObjectById(Route.class, key);
        if (route != null) {
            event.setRoute(route);
        }

        try {
            pm.makePersistent(event);
        }
        finally {
            pm.close();
        }
    }

    /**
     * Bearbeitet das Event mit der Id des übergebenen Events mit den Daten des übergebenen Events.
     *
     * @param event das zu bearbeitende Event mit den neuen Daten
     * @param user User, der die Methode aufruft
     * @return true, wenn Aktion erfolgreich, false sonst
     * @throws OAuthRequestException
     * @throws IOException
     */
    public BooleanWrapper editEvent(Event event, User user) throws OAuthRequestException, IOException {
        long keyId = event.getKey().getId();
        BooleanWrapper b = deleteEvent(keyId, user);
        if(b.value) {
            createEvent(user, event);
        }

        return new BooleanWrapper(b.value);
    }

    /**
     * Gibt eine ArrayList von allen auf dem Server gespeicherten Events zurück.
     *
     * @return Liste mit allen Events
     */
    public List<Event> getAllEvents() {
        PersistenceManager pm = pmf.getPersistenceManager();

        try {
            List<Event> result = (List<Event>) pm.newQuery(Event.class).execute();
            if (result.isEmpty()) {
                return new ArrayList<Event>();
            } else {
                return result;
            }
        } finally {
            pm.close();
        }
    }

}












