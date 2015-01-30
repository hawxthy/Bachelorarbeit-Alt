package ws1415.SkatenightBackend;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import ws1415.SkatenightBackend.gcm.Message;
import ws1415.SkatenightBackend.gcm.MessageType;
import ws1415.SkatenightBackend.gcm.RegistrationManager;
import ws1415.SkatenightBackend.gcm.Sender;

/**
 * Wird periodisch von einem Cron-Job aufgerufen und such nach beginnenden Events, über die die
 * Benutzer noch nicht informiert wurden.
 *
 * @author Richard
 */
public class EventStartServlet extends HttpServlet {
    private static final Logger _logger = Logger.getLogger(EventStartServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PersistenceManager pm = null;
        try {
            PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(
                    "transactions-optional");
            pm = pmf.getPersistenceManager();
            RegistrationManager registrationManager;
            Query q = pm.newQuery(RegistrationManager.class);
            List<RegistrationManager> result = (List<RegistrationManager>) q.execute();
            if (!result.isEmpty()) {
                registrationManager = result.get(0);
            } else {
                registrationManager = new RegistrationManager();
            }

            // Startende Events abrufen
            List<Event> startingEvents = new LinkedList<>();
            Date startingDate, currentDate = new Date();
            q = pm.newQuery(Event.class);
            for (Event e : (List<Event>) q.execute()) {
                if (!e.isNotificationSend()) {
                    startingDate = FieldType.getFusedDate(e);
                    if (startingDate.before(currentDate)) {
                        startingEvents.add(e);
                    }
                }
            }

            for (Event e : startingEvents) {
                // Registration-IDs abrufen
                Member member;
                Set<String> ids = new HashSet<>();
                for (String s : e.getMemberList()) {
                    ids.addAll(registrationManager.getUserIds(s));
                    try {
                        q = pm.newQuery(Member.class);
                        q.setFilter("email == emailParam");
                        q.declareParameters("String emailParam");
                        List<Member> results = (List<Member>) q.execute(s);
                        if (!results.isEmpty()) {
                            member = results.get(0);
                            member.setCurrentEventId(e.getKey().getId());
                            pm.makePersistent(member);
                        }
                    } catch (JDOObjectNotFoundException ex) {
                        // Wird geworfen, wenn kein Objekt mit dem angegebenen Schlüssel existiert
                        // In diesem Fall null zurückgeben
                        member = null;
                    }
                }

                if (!ids.isEmpty()) {
                    Sender sender = new Sender(Constants.GCM_API_KEY);
                    Message m = new Message.Builder()
                            .delayWhileIdle(false)
                            .timeToLive(3600)
                            .addData("type", MessageType.EVENT_START_MESSAGE.name())
                            .addData("eventId", Long.toString(e.getKey().getId()))
                            .build();
                    sender.send(m, new LinkedList<>(ids), 5);

                    String event_title = FieldType.getUniqueField(FieldType.TITLE.getId(), e).getValue();
                    m = new Message.Builder()
                            .delayWhileIdle(false)
                            .timeToLive(3600)
                            .addData("type", MessageType.NOTIFICATION_MESSAGE.name())
                            .addData("title", "Event beginnt")
                            .addData("content", "Das Event " + event_title + " startet!")
                            .build();
                    sender.send(m, new LinkedList<>(ids), 5);
                }
                e.setNotificationSend(true);
                pm.makePersistent(e);
            }
        } catch (Exception ex) {
            _logger.info("error processing cron job: " + ex);
        } finally {
            if (pm != null) pm.close();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}