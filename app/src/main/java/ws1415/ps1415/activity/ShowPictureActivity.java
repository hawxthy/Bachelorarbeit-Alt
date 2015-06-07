package ws1415.ps1415.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.skatenight.skatenightAPI.model.GalleryMetaData;

import java.util.List;

import ws1415.ps1415.R;
import ws1415.ps1415.fragment.PictureFragment;

public class ShowPictureActivity extends Activity {
    public static final String EXTRA_PICTURE_ID = ShowPictureActivity.class.getName() + ".PictureId";
    /**
     * Die Position des Bildes im Adapter. Falls die Activity Änderungen an dem Bild vornimmt, wird
     * dieses Extra wieder an die aufrufende Activity zurück gegeben. Dort kann dann ggf. das einzelne
     * Bild neu geladen werden.
     */
    public static final String EXTRA_POSITION = ShowPictureActivity.class.getName() + ".Position";

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);

        position = getIntent().getIntExtra(EXTRA_POSITION, -1);
        if (getIntent().hasExtra(EXTRA_PICTURE_ID)) {
            PictureFragment fragment = (PictureFragment) getFragmentManager().findFragmentById(R.id.pictureFragment);
            fragment.loadPicture(getIntent().getLongExtra(EXTRA_PICTURE_ID, -1), position);
        } else {
            throw new RuntimeException("intent has to have extra " + EXTRA_PICTURE_ID);
        }
    }
}