package ws1415.veranstalterapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.skatenight.skatenightAPI.model.Event;
import com.skatenight.skatenightAPI.model.Field;
import com.skatenight.skatenightAPI.model.Route;

import org.w3c.dom.Text;

import ws1415.veranstalterapp.R;
import ws1415.veranstalterapp.activity.ChooseRouteActivity;
import ws1415.veranstalterapp.activity.ShowRouteActivity;
import ws1415.veranstalterapp.util.EventUtils.TYPE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ws1415.veranstalterapp.util.EventUtils;

/**
 * Created by Bernd Eissing on 28.11.2014.
 */
public class ShowCursorAdapter extends BaseAdapter{
    private List<Field> fieldList = new ArrayList<Field>();
    private Context context;
    private LayoutInflater inflater;
    private Event event;

    /**
     * Konstruktor, der den Inhalt der Liste festlegt.
     *
     * @param context Context, von dem aus der Adapter aufgerufen wird
     * @param fieldList List von den Routen
     */
    public ShowCursorAdapter(Context context, List<Field> fieldList, Event event){
        this.context = context;
        this.fieldList = fieldList;
        this.event = event;
    }

    /**
     * Gibt die Anzhl der bearbeitenden EventFelder in der Liste zurück.
     *
     * @return Anzahl der EventFelder
     */
    @Override
    public int getCount(){
        if(fieldList == null){
            return 0;
        } else{
            return fieldList.size();
        }
    }

    /**
     * Gibt das bearbeitende Feld an der Stelle i in der Liste zurück.
     *
     * @param i Stelle
     * @return Feld
     */
    @Override
    public Field getItem(int i){
        return fieldList.get(i);
    }

    /**
     *Gibt die Id der Route in der Liste zurück.
     *
     * @param i
     * @return
     */
    @Override
    public long getItemId(int i){
        return i;
    }

    /**
     * Klasse zum Halten der GUI Elemente für ein ButtonField.
     */
    private class HolderButtonField{
        TextView title;
        Button button;
    }

    /**
     * Klasse zum Halten der GUI Elemente für ein TextField.
     */
    private class HolderTextField{
        TextView title;
        TextView content;
    }

    /**
     * Klasse zum Halten der GUI Elemente für ein ImageField.
     */
    private class HolderImageField{
        TextView title;
        ImageView image;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup){
        View view = null;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(getItem(position).getType().equals(TYPE.TITLE.name()) ||
           getItem(position).getType().equals(TYPE.LOCATION.name()) ||
           getItem(position).getType().equals(TYPE.DESCRIPTION.name()) ||
           getItem(position).getType().equals(TYPE.SIMPLETEXT.name())){
            HolderTextField holder = new HolderTextField();
            view = inflater.inflate(R.layout.list_view_item_show_information_text_field, viewGroup, false);
            holder.title = (TextView) view.findViewById(R.id.list_view_item_show_information_text_field_textView_title);
            holder.content = (TextView) view.findViewById(R.id.list_view_item_show_information_text_field_textView_content);
            holder.title.setText(fieldList.get(position).getTitle());
            holder.content.setText(fieldList.get(position).getValue().toString());

        }else if(getItem(position).getType().equals(TYPE.PICTURE)){
            HolderImageField holder = new HolderImageField();
            view = inflater.inflate(R.layout.list_view_item_show_information_image_field, viewGroup, false);
            holder.title = (TextView) view.findViewById(R.id.list_view_item_show_information_image_field_textView_title);
            holder.image = (ImageView) view.findViewById(R.id.list_view_item_show_information_image_field_imageView);
            holder.title.setText(fieldList.get(position).getTitle());
            // TODO muss noch richtig gemacht werden
            //holder.image.setImageBitmap(fieldList.get(position).getValue());

        }else if(getItem(position).getType().equals(TYPE.ROUTE.name())){
            HolderButtonField holder = new HolderButtonField();
            view = inflater.inflate(R.layout.list_view_item_show_information_button_field, viewGroup, false);
            holder.title = (TextView) view.findViewById(R.id.list_view_item_show_information_button_field_textView_title);
            holder.button = (Button) view.findViewById(R.id.list_view_item_show_information_button_field_button);
            holder.title.setText(fieldList.get(position).getTitle());
            holder.button.setText(((Route)fieldList.get(position).getValue()).getName());
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ShowRouteActivity.class);
                    intent.putExtra("show_route_extra_route", (((Route) fieldList.get(position).getValue()).getRouteData()).getValue());
                            context.startActivity(intent);
                }
            });

        }else if(getItem(position).getType().equals(TYPE.FEE.name())){
            HolderTextField holder = new HolderTextField();
            view = inflater.inflate(R.layout.list_view_item_show_information_text_field, viewGroup, false);
            holder.title = (TextView) view.findViewById(R.id.list_view_item_show_information_text_field_textView_title);
            holder.content = (TextView) view.findViewById(R.id.list_view_item_show_information_text_field_textView_content);
            holder.title.setText(fieldList.get(position).getTitle());
            holder.content.setText(fieldList.get(position).getValue().toString()+"€");

        }else if(getItem(position).getType().equals(TYPE.TIME.name())){
            HolderTextField holder = new HolderTextField();
            view = inflater.inflate(R.layout.list_view_item_show_information_text_field, viewGroup, false);
            holder.title = (TextView) view.findViewById(R.id.list_view_item_show_information_text_field_textView_title);
            holder.content = (TextView) view.findViewById(R.id.list_view_item_show_information_text_field_textView_content);
            holder.title.setText(fieldList.get(position).getTitle());
            // TODO muss noch richtig gemacht werden
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            holder.content.setText(dateFormat.format((Date)fieldList.get(position).getValue()));

        }else if(getItem(position).getType().equals(TYPE.LINK.name())){
            HolderTextField holder = new HolderTextField();
            view = inflater.inflate(R.layout.list_view_item_show_information_text_field, viewGroup, false);
            holder.title = (TextView) view.findViewById(R.id.list_view_item_show_information_text_field_textView_title);
            holder.content = (TextView) view.findViewById(R.id.list_view_item_show_information_text_field_textView_content);
            holder.content.setText((String)fieldList.get(position).getValue());
            final String link = holder.content.getText().toString();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = link;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
            });
        }
        return view;
    }
}