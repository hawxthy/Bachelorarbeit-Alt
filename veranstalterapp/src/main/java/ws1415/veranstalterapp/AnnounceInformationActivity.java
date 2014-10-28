package ws1415.veranstalterapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import com.appspot.skatenight_ms.skatenightAPI.model.Event;
import com.appspot.skatenight_ms.skatenightAPI.model.Text;

/**
 * Klasse zum veröffentlichen von neuen Veranstaltungen.
 *
 * Created by Bernd Eissing, Marting Wrodarczyk on 21.10.2014.
 */
public class AnnounceInformationActivity extends Activity{
    // Die Viewelemente für das Event
    private EditText editTextTitle;
    private EditText editTextFee;
    private EditText editTextLocation;
    private EditText editTextDescription;

    // Die Buttons für Zeit und Datum
    private Button datePickerButton;
    private Button timePickerButton;

    // Attribute für das Datum
    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 1;
    static final int TIME_DIALOG_ID = 2;

    // Attribute für die Zeit
    private int hour;
    private int minute;

    // das neu erstellte Event
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce_information);

        // Initialisiere die View Elemente
        editTextTitle = (EditText) findViewById(R.id.announce_info_title_edittext);
        editTextFee = (EditText) findViewById(R.id.announce_info_fee_edittext);
        editTextLocation = (EditText) findViewById(R.id.announce_info_location_edittext);
        editTextDescription = (EditText) findViewById(R.id.announce_info_description_edittext);

        // Initialisiere die Buttons
        timePickerButton = (Button) findViewById(R.id.announce_info_time_button);
        datePickerButton = (Button) findViewById(R.id.announce_info_date_button);

        // Setze die aktuelle Zeit und das Datum
        setCurrentDateOnView();
    }

    /**
     * ... später
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.announce_information, menu);
        return true;
    }

    /**
     * ... später
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Die onClick Methode welche aufgerufen wird, wenn der datePickerButton gedrückt wird.
     * @param view View, von dem aus der Button gedrückt wurde
     */
    public void showDatePickerDialog(View view){
        showDialog(DATE_DIALOG_ID);
    }

    /**
     * Die onClick Methode welche aufgerufen wird, wenn der datePickerButton gedrückt wird.
     * @param view View, von dem aus der Button gedrückt wurde
     */
    public void showTimePickerDialog(View view){
        showDialog(TIME_DIALOG_ID);
    }

    /**
     * Erstellt je nach id ein DatePicker- oder TimePicker Dialog.
     * @param id DatePickerDialog falls id = 1, TimePickerDialog falls id = 2
     * @return Dialog Fenster
     */
    @Override
    protected Dialog onCreateDialog(int id){
        switch(id){
            case DATE_DIALOG_ID:
                // setze das Datum des Pickers als das angegebene Datum für das Event
                return new DatePickerDialog(this, datePickerListener,  year, month, day);
            case TIME_DIALOG_ID:
                // setze die Zeit des Picker als angegebene Zeit für das Event
                return new TimePickerDialog(this, timePickerListener, hour, minute, true);
        }
        return null;
    }

    /**
     * Setzt das angegebene Datum bei einer Änderung als Text auf den datePickerButton.
     */
    private OnDateSetListener datePickerListener = new OnDateSetListener(){

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay){
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into Button
            datePickerButton.setText(day +"."+ (month+1) +"."+year);
        }
    };

    /**
     * Setzt das aktuelle Datum als Text auf den datePickerButton.
     */
    public void setCurrentDateOnView(){
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set selected date into Button
        datePickerButton.setText(day +"."+ (month+1) +"."+year);
    }

    /**
     * Setzt einen Listener, welcher die ausgewählte Uhrzeit bei einer Änderung setzt und auf
     * den timePickerButton setzt.
     */
    private OnTimeSetListener timePickerListener = new OnTimeSetListener(){

        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            hour = selectedHour;
            minute = selectedMinute;
            if(minute < 10){
                minute = 0+ selectedMinute;
                timePickerButton.setText(hour +":"+minute+" Uhr");
            }else{
                timePickerButton.setText(hour +":"+minute+" Uhr");
            }
        }
    };

    /**
     * Hier soll was passieren wenn der Versanstalter den Prozess abbricht!
     */
    public void cancelInfo(View view){
       // TODO Muss noch implementiert werden
    }

    /**
     * Liest die eingegebenen Informationen aus, erstellt ause diesen ein Event und fügt dieses
     * Event dem Server hinzu. Momentan wir noch das alte Event überschrieben.
     */
    public void applyInfo(View view){

        // Zeige einen Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.areyousure);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Weise die Werte aus den Feldern Variablen zu, um damit dann das Event zu setzen.
                String title = editTextTitle.getText().toString();
                String fee = editTextFee.getText().toString();
                String date = day+ "." +month+ "." +year+ " " +hour+ ":" +minute+ " Uhr";
                String location =editTextLocation.getText().toString();
                Text description = new Text();
                description.setValue(editTextDescription.getText().toString());

                // Erstelle ein neue Event
                event = new Event();

                // Setze die Attribute vom Event
                event.setTitle(title);
                event.setFee(fee);
                event.setDate(date);
                event.setLocation(location);
                event.setDescription(description);
                new CreateEventTask().execute(event);
                Toast.makeText(AnnounceInformationActivity.this,
                        getResources().getString(R.string.eventcreated), Toast.LENGTH_LONG);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }
}
