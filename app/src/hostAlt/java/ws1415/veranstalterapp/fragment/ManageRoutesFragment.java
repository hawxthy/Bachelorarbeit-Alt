package ws1415.veranstalterapp.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.skatenight.skatenightAPI.model.Route;
import com.skatenight.skatenightAPI.model.ServerWaypoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ws1415.common.task.ExtendedTask;
import ws1415.common.task.ExtendedTaskDelegateAdapter;
import ws1415.veranstalterapp.dialog.AddRouteDraftDialog;
import ws1415.veranstalterapp.adapter.MapsCursorAdapter;
import ws1415.veranstalterapp.R;
import ws1415.veranstalterapp.activity.ShowRouteActivity;
import ws1415.common.task.DeleteRouteTask;
import ws1415.common.task.QueryRouteTask;

/**
 * Dieses Fragment wird dazu genutzt um die Routen zu verwalten.
 *
 * @author Bernd Eissing, Martin Wrodarczyk
 */
public class ManageRoutesFragment extends Fragment {
    private ListView routeListView;
    private List<Route> routeList;
    private MapsCursorAdapter mAdapter;
    private MenuItem menuItemAddRoute;

    /**
     * Ruft Methode auf, um das add_route_item in der ActionBar zu setzen.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Aktualisiert die Liste der Strecken.
     */
    @Override
    public void onResume(){
        super.onResume();
        new QueryRouteTask(new ExtendedTaskDelegateAdapter<Void, List<Route>>() {
            @Override
            public void taskDidFinish(ExtendedTask task, List<Route> routes) {
                setRoutesToListView((ArrayList<Route>) routes);
            }
        }).execute();
    }

    /**
     * Initialisiert die ListView und setzt die Listener auf die ListView.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_routes, container, false);

        routeListView = (ListView) view.findViewById(R.id.manage_routes_route_list);
        routeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Ruft die showRouteActivity auf, die die ausgewählte Route anzeigt.
             *
             * @param adapterView
             * @param view
             * @param i Index der ausgewählten Route in der ListView
             * @param l
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ShowRouteActivity.class);
                intent.putExtra(ShowRouteActivity.EXTRA_TITLE, routeList.get(i).getName());
                intent.putExtra(ShowRouteActivity.EXTRA_ROUTE, routeList.get(i).getRouteData().getValue());
                ArrayList<ServerWaypoint> tmp = (ArrayList) routeList.get(i).getWaypoints();
                intent.putExtra(ShowRouteActivity.EXTRA_WAYPOINTS, (Serializable) tmp);
                startActivity(intent);
            }
        });

        routeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            /**
             * Läscht die Route vom Server und von der ListView
             *
             * @param adapterView
             * @param view
             * @param i Position der Route in der ListView
             * @param l
             * @return
             */
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                createSelectionsMenu(i);
                return true;
            }
        });
        return view;
    }

    /**
     * Füllt die ListView mit den Routen vom Server.
     *
     * @param results ArrayList von Routen
     */
    public void setRoutesToListView(ArrayList<Route> results) {
        routeList = results;
        mAdapter = new MapsCursorAdapter(getActivity(), results);
        routeListView.setAdapter(mAdapter);
    }

    /**
     * Erstellt einen Dialog, welcher aufgerufen wird, wenn ein Item in der ListView lange
     * ausgewählt wird. In diesem Dialog kann man dann auswählen, ob man die ausgewählte
     * Route löschen möchte.
     *
     * @param position
     */
    private void createSelectionsMenu(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(routeList.get(position).getName())
                .setItems(R.array.selections_menu_manage_routes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {
                        switch(index){
                            case 0:
                                Intent intent = new Intent(ManageRoutesFragment.this.getActivity(), AddRouteDraftDialog.class);
                                intent.putExtra(AddRouteDraftDialog.EXTRA_WAYPOINTS, (ArrayList<ServerWaypoint>)routeList.get(position).getWaypoints());
                                startActivity(intent);
                                break;
                            case 1:
                                deleteRoute(routeList.get(position));
                                break;
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    /**
     * Löscht Route aus der Liste.
     *
     * @param route zu löschende Route
     */
    public void deleteRouteFromList(Route route){
        mAdapter.removeListItem(routeList.indexOf(route));
        routeList.remove(route);
    }
    /**
     * Löscht die Route vom Server.
     *
     * @param route die zu löschende Route
     */
    public void deleteRoute(final Route route) {
        new DeleteRouteTask(new ExtendedTaskDelegateAdapter<Void, Boolean>() {
            @Override
            public void taskDidFinish(ExtendedTask task, Boolean aBoolean) {
                if(aBoolean != null && aBoolean == true) {
                    deleteRouteFromList(route);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.route_delete_failed), Toast.LENGTH_LONG).show();
                }
            }
        }).execute(route);
    }

    /**
     * Erstellt das ActionBar Menu
     *
     * @param menu
     * @param menuInflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.manage_routes, menu);
        menuItemAddRoute = menu.findItem(R.id.action_add_route);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public MenuItem getMenuItemAddRoute() {
        return menuItemAddRoute;
    }

    public List<Route> getRouteList() {
        return routeList;
    }
}