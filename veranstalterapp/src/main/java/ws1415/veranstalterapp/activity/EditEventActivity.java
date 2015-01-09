package ws1415.veranstalterapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.skatenight.skatenightAPI.model.Event;
import com.skatenight.skatenightAPI.model.Route;

import ws1415.veranstalterapp.adapter.AnnounceCursorAdapter;
import ws1415.veranstalterapp.R;
import ws1415.veranstalterapp.task.EditEventTask;
import ws1415.veranstalterapp.task.GetEventTask;
import ws1415.veranstalterapp.util.EventUtils;
import ws1415.veranstalterapp.util.FieldType;

/**
 * Die Activity zum ändern der Attribute eines Events und zum eventuellen hinzufügen weiterer
 * dynamischer Felder.
 *
 * Created by Bernd Eissing, Martin Wrodarczyk.
 */
public class EditEventActivity extends Activity {
    // Adapter für die ListView von activity_edit_event_list_view
    private AnnounceCursorAdapter listAdapter;

    // Die ListView von der xml datei activity_edit_event
    private ListView listView;

    // Die Buttons für Cancel, Apply, Edit
    private Button applyButton;
    private Button cancelButton;
    private Button editButton;

    // Das Attribut Route
    private Route route;

    // das zu ändernde Event
    private Event event;

    /**
     * Erstellt die View, initialisiert die Attribute, setzt die Listener für die Buttons.
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);


        new GetEventTask(this).execute(getIntent().getLongExtra("event", 0));
        ChooseRouteActivity.giveEditEventActivity(this);

        // Initialisiere die Buttons
        applyButton = (Button) findViewById(R.id.edit_event_info_apply_button);
        cancelButton = (Button) findViewById(R.id.edit_event_info_cancel_button);
        editButton = (Button) findViewById(R.id.edit_event_info_edit_button);

        // Setze die Listener für die Buttons
        setButtonListener();
    }

    /**
     * Methode zum setzen der Listener für die Buttons "timePickerButton", "datePickerButton",
     * "applyButton", "cancelButton", "routePickerButton".
     */
    private void setButtonListener() {
        // Setze die Listener für Cancel und Apply Buttons
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!listAdapter.getEditMode()) {
                    cancelInfo(true);
                }
                Toast.makeText(EditEventActivity.this, getResources().getString(R.string.announce_info_edit_mode_string), Toast.LENGTH_LONG);
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!listAdapter.getEditMode()) {
                    applyInfo();
                }
                Toast.makeText(EditEventActivity.this, getResources().getString(R.string.announce_info_edit_mode_string), Toast.LENGTH_LONG);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(editButton.getText().equals(getResources().getString(R.string.announce_info_start_edit_button))){
                    listAdapter.startEditMode();
                    editButton.setText(getResources().getString(R.string.announce_info_exit_edit_button));
                }else if(editButton.getText().equals(getResources().getString(R.string.announce_info_exit_edit_button))){
                    listAdapter.exitEditMode();
                    editButton.setText(getResources().getString(R.string.announce_info_start_edit_button));
                }
            }
        });
    }



    /**
     * Ließt die eingegebenen Informationen aus, erstellt ause diesen ein Event und fügt dieses
     * Event dem Server hinzu. Momentan wir noch das alte Event überschrieben.
     */
    public void applyInfo() {

        // Zeige einen Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.areyousure);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int titleId = EventUtils.getInstance(EditEventActivity.this).getUniqueFieldId(FieldType.TITLE, event);

                // Überprüfen ob wirklich alle daten des Events gesetzt sind
                if (titleId != -1 && !((EditText) listView.getChildAt(titleId).findViewById(R.id.list_view_item_announce_information_uniquetext_editText)).getText().toString().isEmpty()){

                    // Setze die Attribute vom Event
                    EventUtils.getInstance(EditEventActivity.this).setEventInfo(event, listView);

                    // Erstelle Event auf dem Server
                    new EditEventTask().execute(event);

                    // Benachrichtige den Benutzer mit einem Toast
                    Toast.makeText(EditEventActivity.this, getResources().getString(R.string.eventcreated), Toast.LENGTH_LONG).show();

                    finish();

                    // Update die Informationen in ShowInformationFragment
                    HoldTabsActivity.updateInformation();
                } else {
                    cancelInfo(false);
                }

            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Gibt momentan einen Toast aus, wenn der Benutzer auf den Cancel Button drückt
     * wird ausgegeben, dass das Event nicht editiert wure
     */
    public void cancelInfo(boolean allSet) {
        if (allSet) {
            Toast.makeText(this, "Wurde nicht editiert", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Nicht alle Felder ausgefüllt", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Setzt den Adapter auf die ListView und füllt diese mit den vorhandenen
     * Informationen aus der FieldList vom übergebenen Event
     *
     * @param e Das Event
     */
    public void setEventDataToView(Event e){
        event = e;
        listAdapter = new AnnounceCursorAdapter(this, e.getDynamicFields(), e);

        listView = (ListView) findViewById(R.id.activity_edit_event_list_view);
        listView.setAdapter(listAdapter);
    }

    public AnnounceCursorAdapter getAdapter(){
        return listAdapter;
    }
}