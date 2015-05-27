package ws1415.ps1415.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skatenight.skatenightAPI.model.BlobKey;
import com.skatenight.skatenightAPI.model.UserInfo;
import com.skatenight.skatenightAPI.model.UserListData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ws1415.common.controller.UserController;
import ws1415.common.task.ExtendedTask;
import ws1415.common.task.ExtendedTaskDelegateAdapter;
import ws1415.common.util.ImageUtil;
import ws1415.ps1415.R;
import ws1415.ps1415.activity.ProfileActivity;
import ws1415.ps1415.util.UniversalUtil;
import ws1415.ps1415.util.UserImageLoader;

/**
 * Dieser Adapter wird genutzt, um eine Liste mit Benutzerinformationen zu füllen.
 *
 * @author Martin Wrodarczyk
 */
public class UserListAdapter extends BaseAdapter {
    private static final int NEXT_DATA_COUNT = 15;
    private List<String> mailData;
    private List<UserListData> mData;
    private LayoutInflater mInflater;
    private Context mContext;
    private Bitmap defaultBitmap;
    private boolean loadingData;

    /**
     * Erwartet die komplette Liste der E-Mail Adressen der Benutzer die angezeigt werden sollen.
     * Dabei werden zu Beginn nur die ersten NEXT_DATA_COUNT Benutzer angezeigt und beim Scrollen
     * werden die nächsten NEXT_DATA_COUNT Benutzer geladen.
     *
     * @param userMails Liste der E-Mail Adressen
     * @param context Context
     */
    public UserListAdapter(List<String> userMails, Context context) {
        this.mailData = userMails;
        mContext = context;
        mData = new ArrayList<>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        defaultBitmap = ImageUtil.getRoundedBitmap(BitmapFactory.
                decodeResource(context.getResources(), R.drawable.default_picture));
        addNextUserInfo(userMails);
    }

    /**
     * Ruft die Informationen der Benutzer der Ergebnisteilliste der Suche ab.
     *
     * @param userMails Ergebnis der Suche
     */
    private void addNextUserInfo(final List<String> userMails) {
        if (!loadingData) {
            int dataSize = (userMails.size() < NEXT_DATA_COUNT) ? userMails.size() : NEXT_DATA_COUNT;
            final List<String> subList = new ArrayList<>(userMails.subList(0, dataSize));
            loadingData = true;
            UserController.listUserInfo(new ExtendedTaskDelegateAdapter<Void, List<UserListData>>() {
                @Override
                public void taskDidFinish(ExtendedTask task, List<UserListData> userListDatas) {
                    mData.addAll(userListDatas);
                    mailData.removeAll(subList);
                    notifyDataSetChanged();
                    loadingData = false;
                }

                @Override
                public void taskFailed(ExtendedTask task, String message) {
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                    loadingData = false;
                }
            }, subList);
        }
    }

    private ProgressDialog showLoading(){
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public UserListData getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class Holder {
        private ImageView picture;
        private TextView primaryText;
        private TextView secondaryText;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Holder holder;

        if(i == getCount()-1 && !mailData.isEmpty()) addNextUserInfo(mailData);

        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.list_view_item_user, viewGroup, false);
            holder.picture = (ImageView) convertView.findViewById(R.id.list_item_user_picture);
            holder.primaryText = (TextView) convertView.findViewById(R.id.list_item_user_primary);
            holder.secondaryText = (TextView) convertView.findViewById(R.id.list_item_user_secondary);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        UserListData item = getItem(i);
        UserInfo userInfo = item.getUserInfo();
        BlobKey userPicture = item.getUserPicture();

        String primaryText = setUpPrimaryText(userInfo);
        String secondaryText = setUpSecondaryText(userInfo);

        UserImageLoader.getInstance(mContext).displayImage(userPicture, holder.picture);
        holder.primaryText.setText(primaryText);
        holder.secondaryText.setText(secondaryText);

        return convertView;
    }

    private void setImage(final ImageView userPictureView, final BlobKey userPictureKey) {
        if (userPictureKey != null) {
            UserController.getUserPicture(new ExtendedTaskDelegateAdapter<Void, Bitmap>() {
                @Override
                public void taskDidFinish(ExtendedTask task, Bitmap bitmap) {
                    userPictureView.setImageBitmap(ImageUtil.getRoundedBitmap(bitmap));
                }

                @Override
                public void taskFailed(ExtendedTask task, String message) {
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                }
            }, userPictureKey);
        }
    }

    private String setUpPrimaryText(UserInfo userInfo) {
        String firstName = userInfo.getFirstName();
        String lastName = userInfo.getLastName().getValue();
        return (lastName == null) ? firstName : firstName + " " + lastName;
    }

    private String setUpSecondaryText(UserInfo userInfo) {
        String city = userInfo.getCity().getValue();
        String dateOfBirth = userInfo.getDateOfBirth().getValue();
        Integer age = null;
        if (dateOfBirth != null) {
            try {
                Date dateOfBirthDate = ProfileActivity.DATE_OF_BIRTH_FORMAT.parse(dateOfBirth);
                Calendar dob = Calendar.getInstance();
                dob.setTime(dateOfBirthDate);
                age = UniversalUtil.calculateAge(dob);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String secondaryText = "";
        if (age != null && city != null) {
            secondaryText = mContext.getString(R.string.from_city) + " " + city + ", "
                    + mContext.getResources().getQuantityString(R.plurals.years_old, age, age);
        } else if (age != null) {
            secondaryText = mContext.getResources().getQuantityString(R.plurals.years_old, age, age);
        } else if (city != null) {
            secondaryText = mContext.getString(R.string.from_city) + " " + city;
        }
        return secondaryText;
    }

    /**
     * Entfernt den Benutzer mit der übergebenen Postion aus der Liste.
     *
     * @param position Position in der Liste
     */
    public void removeUser(int position){
        mData.remove(position);
        notifyDataSetChanged();
    }
}
