package ws1415.ps1415.useCases;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.skatenight.skatenightAPI.model.Event;
import com.skatenight.skatenightAPI.model.Field;
import com.skatenight.skatenightAPI.model.Route;

import com.skatenight.skatenightAPI.model.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ws1415.ps1415.Constants;
import ws1415.ps1415.R;
import ws1415.ps1415.ServiceProvider;
import ws1415.ps1415.activity.Settings;
import ws1415.ps1415.activity.ShowEventsActivity;
import ws1415.ps1415.activity.ShowInformationActivity;
import ws1415.ps1415.task.CreateEventTask;
import ws1415.ps1415.util.FieldType;

/**
 * Testet den Use Case "Anzeigen mehrerer Veranstaltungen".
 *
 * @author Tristan Rust
 */
public class ShowSeveralEventsTest extends ActivityInstrumentationTestCase2<ShowEventsActivity> {

    private ShowEventsActivity mActivity;

    private final String TEST_TITLE = "TestTitle";
    private final String TEST_FEE = "100";
    private final String TEST_DATE = "1.12.2015";
    private final String TEST_TIME = "20:00 Uhr";
    private final String TEST_LOCATION = "Münster";
    private final String TEST_DESCRIPTION = "TestDescription";

    // ShowEventsActivity UI Elemente
    private ListView mList;
    private ListAdapter mListData;

    public ShowSeveralEventsTest() {
        super(ShowEventsActivity.class);

        /*
        testEvent = new Event();

        ArrayList<Field> eventData = new ArrayList<Field>();
        Field fieldData = new Field();

        fieldData.setTitle("Titel");
        fieldData.setValue(TEST_TITLE);
        fieldData.setType(FieldType.TITLE.getId());
        fieldData.setData(new Text());
        eventData.add(fieldData);

        fieldData.setTitle("Ort");
        fieldData.setValue(TEST_LOCATION);
        fieldData.setType(FieldType.LOCATION.getId());
        fieldData.setData(new Text());
        eventData.add(fieldData);

        fieldData.setTitle("Datum");
        fieldData.setValue(TEST_DATE);
        fieldData.setType(FieldType.DATE.getId());
        fieldData.setData(new Text());
        eventData.add(fieldData);

        fieldData.setTitle("Zeit");
        fieldData.setValue(TEST_TIME);
        fieldData.setType(FieldType.TIME.getId());
        fieldData.setData(new Text());
        eventData.add(fieldData);

        fieldData.setTitle("Gebühr");
        fieldData.setValue(TEST_FEE);
        fieldData.setType(FieldType.FEE.getId());
        fieldData.setData(new Text());
        eventData.add(fieldData);

        fieldData.setTitle("Beschreibung");
        fieldData.setValue(TEST_DESCRIPTION);
        fieldData.setType(FieldType.DESCRIPTION.getId());
        fieldData.setData(new Text());
        eventData.add(fieldData);

        fieldData.setTitle("Route");
        fieldData.setValue("Testroute");
        fieldData.setType(FieldType.ROUTE.getId());
        fieldData.setData(new Text());
        eventData.add(fieldData);

        testRoute = new Route();
        testRoute.setName("Testroute");
        testRoute.setLength("10 km");
        testRoute.setRouteData(new Text().setValue("yrb|H_upm@AB?X??IBIDIFGHGJENELAL?DAB?B?B?@?B?" +
                "B?B?F?H@F@H@F@FBF@FDHDFDFDDFDFBF@D?FLDNBRBT@LD\\BZBL@^@X@j@At@?|AAd@?@?n@@r@Bb@D" +
                "^PhAVbBJj@Jp@Hj@TrBAr@HbB??yA\\{@XQFQDWHUH_@D]ECA}@Sa@MUGi@My@QC?OCOCI?ODYBUDa@J" +
                "c@Nc@RKFMJQPOTa@d@_BvB_@d@UXSPSLSJOFG@SDKB_@Dw@FQGs@DqBNK@QBO@OAWC_AMMC]Gg@IQCOA" +
                "[CWAa@?m@@e@@mBJI@SBO@c@FSD_@HC@g@L_@L[LOHC@_@PKFWNMHEB??IHIJGFGJEFGJEJA?CFENKd@" +
                "Qp@CHIXK\\[v@Yr@OX??a@q@QYOUIIk@w@a@q@Ua@Qa@S_@??aCoFQa@Q_A??[T??qAqGQkAKm@G_@Im" +
                "@MeAY_F??`AEb@Ah@Hv@Ft@Fl@HP@LA??BA@ABIBMBU@K@I@I@E?EBEBIBGFGJKVUTQRKRIJEn@WJCHC" +
                "FADAHANCHA^EPAPENAFABABAFEHEDGJKRWFG??LENCLAR@NBHYXw@ZaAL_@BMBKDQ@QR_BFq@Di@HO?u" +
                "@???a@Aa@?a@Ag@@U@W@SBOAIBM@AF[HYNa@Rk@Tg@Xa@??R[JWJa@Lk@d@kBF[DY??b@wBNo@FG@EDM" +
                "DIDKNWFI^]HK\\U??RMPMLM@C??Za@HQRe@Tk@l@uAP]Nk@Lo@DSF_@??DuB?Y?YBmAF}@??j@@\\BVB" +
                "VB~ATRBPDPDXDz@F??PRNHJLHRHPRh@DTHT??LZTd@Xf@JNNVb@l@`BdBbBjB??BNhBvB??LJ\\VJ@LP" +
                "j@t@FLXh@Vp@BF??b@vANh@d@fBh@pB@HDXBRBTB^Dj@?BBVCZ"));

        testEvent.setDynamicFields(eventData);
        testEvent.setRoute(testRoute);
        */

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Nutzer einloggen
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(getActivity(), "server:client_id:"+ Constants.WEB_CLIENT_ID);
        credential.setSelectedAccountName(credential.getAllAccounts()[0].name);
        ServiceProvider.login(credential);

        // Touchmode ausschalten, damit auch die UI Elemente getestet werden können
        setActivityInitialTouchMode(false);

        // Löschen aller Events
        /*List<Event> list = ServiceProvider.getService().skatenightServerEndpoint().getAllEvents().execute().getItems();
        if (list != null) {
            for (Event e : list) {
                ServiceProvider.getService().skatenightServerEndpoint().deleteEvent(e.getKey().getId()).execute();
            }
        }*/

        /*
        Thread.sleep(1000);
        // Neues Test Event anlegen
        //ServiceProvider.getService().skatenightServerEndpoint().createEvent(testEvent).execute();
        new CreateEventTask().execute(testEvent);
        Thread.sleep(1000);
        */

        // Die ShowEventsActivity starten
        mActivity = getActivity();

        Thread.sleep(5000);
        // Holt sich die Event Listen-Elemente
        mList = (ListView) mActivity.findViewById(R.id.activity_show_events_list_view);
        mListData = mList.getAdapter();
    }

    /**
     * Prüfen, ob Events in der Liste vorhanden sind.
     */
    public void testPreConditions() {
        assertNotNull("Activity could not start!", mActivity);
        assertNotNull("selection listener on events list initialized", mList.getOnItemClickListener());
        assertNotNull("adapter for events initialized", mListData);

        // Mindestens ein Event muss existieren
        assertTrue("at least one event exists", mListData.getCount() > 0);
    }

    protected void tearDown() throws Exception {super.tearDown();}

    /**
     * Prüft, ob die Elemente sich in der Activity überschneiden bzw. wirklich gerendert werden.
     *
     * @throws java.lang.Exception
     */
    @SmallTest
    public void testViewsVisible() throws Exception {
        // Falls es mehr als zwei Elemente in der Liste gibt, prüfe, ob die ersten beiden sich überschneiden oder nicht
        if (mListData != null && mListData.getCount()>1) {
            ViewAsserts.assertOnScreen(mListData.getView(0, null, mList).getRootView(), mListData.getView(1, null, mList).getRootView());
            ViewAsserts.assertOnScreen(mListData.getView(1, null, mList).getRootView(), mListData.getView(0, null, mList).getRootView());
        }
    }

    /**
     * Prüft, ob ein Event ausgewählt werden kann und zudem die entsprechenden Informationen
     * detailiert angezeigt wird.
     *
     * @throws Exception
     *
     */
    @UiThreadTest
    public void testUseCaseUI() throws Exception {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(ShowInformationActivity.class.getName(), null, false);

        // Es wird ein neuer UI Thread gestartet & das erste Event ausgewählt und anschließend die ShowInformationActivity gestartet.
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int firstEvent = 0;
                mList.performItemClick(mList.getAdapter().getView(firstEvent, null, mList), firstEvent, mList.getAdapter().getItemId(firstEvent));
            }
        });

        // ShowInformationActivity starten
        ShowInformationActivity showInformationActivity = (ShowInformationActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNotNull("ShowInformationActivity didn't start!", showInformationActivity);

        // Die Views aus der ShowInformationActivity holen
        Thread.sleep(5000);



    }

}











