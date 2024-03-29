package ws1415.ps1415.useCases;

import android.test.ActivityInstrumentationTestCase2;

import ws1415.ps1415.activity.ListEventsActivity;

/**
 * Testet den Use Case "Handhabung der aktuellen Position".
 * <strong>GPS muss aktiviert sein sowie eine Internetverbindung bestehen.</strong>
 *
 * @author Tristan Rust
 */
public class SendPositionSettingsTest extends ActivityInstrumentationTestCase2<ListEventsActivity> {

//    private ListEventsActivity mActivity;
//
//    // Test Daten zur Positionsübertragung
//    private final String TEST_EMAIL        = "tristan.rust@googlemail.com";
//    private final LatLng TEST_POSITION     = new LatLng(35.660866, 108.808594); // Somewhere in China
//
//    // ShowEventsActivty UI Elemente
//    private ListView mList;
//    private ListAdapter mListData;
//
//    // Das zu testende Event
//    private Event mEvent;
//
    public SendPositionSettingsTest() {
        super(ListEventsActivity.class);
    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        // Touchmode ausschalten, damit auch die UI Elemente getestet werden können
//        setActivityInitialTouchMode(false);
//
//        // Die ListEventsActivity starten
//        getInstrumentation().getTargetContext().getApplicationContext();
//        mActivity = getActivity();
//        // Löschen der Voreinstellungen // wird nicht mehr benötigt
//        // Alles in den SharedPreferences löschen
//        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
//        // SharedPreferences.Editor editor = sharedPreferences.edit();
//        // editor.clear().commit();
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
//        sharedPreferences.edit().putBoolean("prefSendLocation", true).apply();
//
//        // Nutzer einloggen
//        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(getActivity(), "server:client_id:"+ Constants.WEB_CLIENT_ID);
//        credential.setSelectedAccountName(credential.getAllAccounts()[0].name);
//        ServiceProvider.login(credential);
//
//        Thread.sleep(5000); // Zeit zum initialisieren
//        // Holt sich die Event Listen-Elemente
//        mList = (ListView) mActivity.findViewById(R.id.eventList);
//        mListData = mList.getAdapter();
//        Thread.sleep(2000); // Zeit zum initialisieren
//
//        mEvent = (Event) mListData.getItem(0);
//    }
//
//    /**
//     * Prüfen, ob die Events in der Liste vorhanden sind, bzw. die UI Elemente initialisiert wurden.
//     */
//    @SmallTest
//    public void testPreConditions() {
//        assertNotNull("selection listener on events list initialized", mList.getOnItemClickListener());
//        assertNotNull("adapter for events initialized", mListData);
//
//        // Mindestens ein Event muss existieren
//        assertTrue("at least one event exists", mListData.getCount() > 0);
//    }
//
//    protected void tearDown() throws Exception {
//        super.tearDown();
//    }
//
//    /**
//     * Prüft, ob diese Views wirklich in der Activity existieren.
//     */
//    @SmallTest
//    public void testViews() {
//        assertNotNull(getActivity());
//        assertNotNull(mList);
//        assertNotNull(mListData);
//    }
//
//    /**
//     * Prüft, ob die Elemente sich in der Activity überschneiden bzw. wirklich gerendert werden.
//     *
//     * @throws java.lang.Exception
//     */
//    @SmallTest
//    public void testViewsVisible() throws Exception {
//        // Falls es mehr als zwei Elemente in der Liste gibt, prüfe, ob die ersten beiden sich überschneiden oder nicht
//        if (mListData != null && mListData.getCount()>1) {
//            ViewAsserts.assertOnScreen(mListData.getView(0, null, mList).getRootView(), mListData.getView(1, null, mList).getRootView());
//            ViewAsserts.assertOnScreen(mListData.getView(1, null, mList).getRootView(), mListData.getView(0, null, mList).getRootView());
//        }
//    }
//
//    /**
//     * Prüft, ob die Einstellungen ausgewählt bleiben.
//     *
//     * @throws java.lang.InterruptedException
//     */
//    @SmallTest
//    public void testSettingsUI() throws InterruptedException {
//        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SettingsActivity.class.getName(), null, false);
//
//        // Klick auf die Menüoption
//        // getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
//        getInstrumentation().invokeMenuActionSync(mActivity, R.id.action_settings, 0);
//
//        // UI Elemente lassen sich hier nicht ansprechen
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
//        assertNotNull("Could not initialize sharedPreferences!", sharedPreferences);
//        sharedPreferences.edit().putBoolean("prefSendLocation", true).apply();
//
//        Activity am = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 2000);
//        Thread.sleep(2500);
//
//        am.finish();
//
//        assertEquals(true, sharedPreferences.getBoolean("prefSendLocation", false));
//
//    }
//
//    /**
//     * Testet die Activity beim Beenden und wieder Neustarten.
//     * @throws java.lang.Exception
//     */
//    @SmallTest
//    public void testSettingsUIStateDestroy() throws InterruptedException {
//        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SettingsActivity.class.getName(), null, false);
//
//        // Klick auf die Menüoption
//        // getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
//        getInstrumentation().invokeMenuActionSync(mActivity, R.id.action_settings, 0);
//
//        // UI Elemente lassen sich hier nicht ansprechen
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
//        sharedPreferences.edit().putBoolean("prefSendLocation", true).apply();
//
//        Activity am = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 2000);
//        Thread.sleep(2500);
//
//        am.finish();
//
//        // Beenden & Neustarten der Activity
//        mActivity.finish();
//        mActivity = this.getActivity();
//
//        assertEquals(true, sharedPreferences.getBoolean("prefSendLocation", false));
//    }
//
//
//    /**
//     * Testet, ob die Activity weiter läuft, wenn diese pausiert hat. Large Test, da auf Ressourcen eines Servers, bzw.
//     * anderen Netzwerkes zugegriffen wird. <strong>GPS muss hierfür aktiviert sein.</strong>
//     * @throws java.lang.InterruptedException
//     */
//    @SmallTest
//    public void testSettingsUIStatePause() throws InterruptedException {
//        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SettingsActivity.class.getName(), null, false);
//
//        // Klick auf die Menüoption
//        getInstrumentation().invokeMenuActionSync(mActivity, R.id.action_settings, 0);
//
//        // UI Elemente lassen sich hier nicht ansprechen
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
//        sharedPreferences.edit().putBoolean("prefSendLocation", true).apply();
//
//        // Settings Activity starten und auf die Instanz warten
//        Activity am = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 2000);
//        Thread.sleep(2500);
//
//        // Pausieren
//        getInstrumentation().callActivityOnPause(am);
//
//        // Fortsetzen
//        getInstrumentation().callActivityOnResume(am);
//
//        am.finish();
//
//        assertEquals(true, sharedPreferences.getBoolean("prefSendLocation", false));
//    }
//
//    /**
//     * Testet, ob die Position an den Server übertragen wird, wenn der User dies aktiviert hat.
//     * Large Test, da auf Ressourcen eines Servers, bzw.
//     * anderen Netzwerkes abgerufen werden.
//     * <strong>GPS muss hierfür aktiviert sein.</strong>
//     *
//     * @throws java.lang.Exception
//     */
//    @LargeTest
//    public void testSendPosition() throws Exception {
//        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SettingsActivity.class.getName(), null, false);
//        assertNotNull("activityMonitor didn't start!", activityMonitor);
//
//        // Klick auf die Menüoption
//        getInstrumentation().invokeMenuActionSync(mActivity, R.id.action_settings, 0);
//
//        // UI Elemente lassen sich hier nicht ansprechen
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
//        sharedPreferences.edit().putBoolean("prefSendLocation", true).apply();
//
//        // Acitivty Monitor starten
//        Activity am = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 10000);
//        assertNotNull("SettingsActivity didn't start!", am);
//        Thread.sleep(2500); // Zeit zum initialisieren
//
//        // Setzen der Position auf den Server
//        //new UpdateLocationTask(null, TEST_EMAIL, TEST_POSITION.latitude, TEST_POSITION.longitude, mEvent.getKey().getId()).execute();
//
//        Thread.sleep(5000); // Zeit zum initialisieren
//
//        // Teilnehmer, der seine Position an den Server senden wird
//        Member m = ServiceProvider.getService().userEndpoint().getMember(TEST_EMAIL).execute();
//        assertEquals(TEST_POSITION.latitude, m.getLatitude());
//        assertEquals(TEST_POSITION.longitude, m.getLongitude());
//
//        // Prüfen, ob das Senden aktiviert ist
//        boolean active = sharedPreferences.getBoolean("prefSendLocation", true);
//        assertTrue(active);
//
//        // Starten des Hintergrundservices zur Standortermittlung, GPS muss aktiv sein
//        Intent service;
//        service = new Intent(mActivity.getBaseContext(), LocationTransmitterService.class);
//        service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mActivity.startService(service);
//        Thread.sleep(10000); // Zeit zum Senden
//
//        // Prüfen, ob die neuen Positionen auf dem Server übernommen worden sind
//        assertNotSame("Position not updated!", TEST_POSITION.latitude, m.getLatitude());
//        assertNotSame("Position not updated!", TEST_POSITION.longitude, m.getLongitude());
//
//        // Service wieder deaktivieren
//        sharedPreferences.edit().putBoolean("prefSendLocation", false).apply();
//        active = sharedPreferences.getBoolean("prefSendLocation", true);
//        assertFalse(active);
//
//        // Übertragenden Service stoppen
//        mActivity.stopService(service);
//        Thread.sleep(2000); // Zeit zum stoppen
//        // Gibt an, ob der Service noch läuft
//        boolean running = false;
//
//        // Prüfen, ob der Service noch läuft
//        ActivityManager manager = (ActivityManager) mActivity.getSystemService(mActivity.getApplication().ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo services : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if ("LocationTransmitterService".equals(services.service.getClassName())) {
//                running = true;
//            }
//        }
//
//        assertFalse("Service should be stopped!", running);
//
//        am.finish();
//
//    }
//
//    /**
//     * Testet, ob die Position an den Server übertragen wird, wenn der User dies aktiviert hat
//     * und an einem Event teilnimmt.
//     * Large Test, da auf Ressourcen eines Servers, bzw. anderen Netzwerkes zugegriffen wird.
//     * <strong>GPS muss hierfür aktiviert sein.</strong>
//     *
//     * @throws java.lang.Exception
//     */
//    @LargeTest
//    public void testAttend() throws Exception {
//        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(ShowInformationActivity.class.getName(), null, false);
//
//        // UI Elemente lassen sich hier nicht ansprechen - Senden in den Einstellungen aktivieren
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
//        sharedPreferences.edit().putBoolean("prefSendLocation", true).apply();
//
//        // Prüfen, ob das Senden aktiviert ist
//        boolean active = sharedPreferences.getBoolean("prefSendLocation", true);
//        assertTrue(active);
//
//        // Setzen der Position auf den Server
//        //new UpdateLocationTask(null, TEST_EMAIL, TEST_POSITION.latitude, TEST_POSITION.longitude, mEvent.getKey().getId()).execute();
//        Thread.sleep(5000); // Zeit zum initialisieren
//
//        // Teilnehmer, der seine Position an den Server senden wird
//        Member m = ServiceProvider.getService().userEndpoint().getMember(TEST_EMAIL).execute();
//        assertEquals(TEST_POSITION.latitude, m.getLatitude());
//        assertEquals(TEST_POSITION.longitude, m.getLongitude());
//
//        // Es wird ein neuer UI Thread gestartet & das erste Event ausgewählt und anschließend die showInformationActivity gestartet
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                int firstEvent = 0;
//                mList.performItemClick(mList.getAdapter().getView(firstEvent, null, mList), firstEvent, mList.getAdapter().getItemId(firstEvent));
//            }
//        });
//
//        // ShowInformationActivity wird gestartet
//        ShowInformationActivity showInformationActivity = (ShowInformationActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 20000);
//        assertNotNull("ShowInformationActivity started", showInformationActivity);
//
//        // Liste, die die TextView enthält
//        ListView listView = (ListView) showInformationActivity.findViewById(R.id.activity_show_information_list_view);
//        assertNotNull("listView is null!", listView);
//        //listView.setAdapter(showInformationActivity.getShowCursorAdapter());
//
//        Thread.sleep(3000); // Zeit zum Initialisieren
//        assertNotNull("listViewAdapter is null!", listView.getAdapter());
//
//       final Button attendButton = (Button) showInformationActivity.findViewById(R.id.show_info_attend_button);
//
//        // Es wird ein neuer UI Thread gestartet & das erste Event ausgewählt und anschließend die showInformationActivity gestartet
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // Teilnehmen an dem Event
//                attendButton.performClick();
//            }
//        });
//
//        Thread.sleep(10000); // Zeit zum senden an den Server
//
//        // Prüfen, ob die neuen Positionen auf dem Server übernommen worden sind
//        assertNotSame("Position not updated!", TEST_POSITION.latitude, m.getLatitude());
//        assertNotSame("Position not updated!", TEST_POSITION.longitude, m.getLongitude());
//
//       showInformationActivity.finish();
//
//    }
//
//
//



}












