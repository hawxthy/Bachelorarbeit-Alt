package ws1415.ps1415.task;

import android.os.AsyncTask;

import com.skatenight.skatenightAPI.model.UserGroup;

import java.io.IOException;

import ws1415.ps1415.ServiceProvider;
import ws1415.ps1415.fragment.AllUsergroupsFragment;

/**
 * Created by Bernd Eissing on 30.01.2015.
 */
public class DeleteUserGroupTask extends AsyncTask<UserGroup, Void, Boolean> {
    private AllUsergroupsFragment asf;
    private UserGroup usergroup;

    public DeleteUserGroupTask(AllUsergroupsFragment asf) {
        this.asf = asf;
    }

    /**
     * Löscht die UserGroup vom Server
     *
     * @param params Die UserGroup die gelöscht werden soll
     */
    @Override
    protected Boolean doInBackground(UserGroup... params) {
        usergroup = params[0];
        try {
            ServiceProvider.getService().skatenightServerEndpoint().deleteUserGroup(
                    params[0].getName()).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Löscht UserGroup aus der Liste.
     *
     * @param result true, bei erfolgreicher Löschung, false andernfalls
     */
    @Override
    protected void onPostExecute(Boolean result) {
        if (result == true) {
            asf.deleteUserGroupFromList(usergroup);
        }
    }
}
