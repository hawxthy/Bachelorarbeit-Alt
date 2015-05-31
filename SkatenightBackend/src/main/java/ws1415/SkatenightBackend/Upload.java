package ws1415.SkatenightBackend;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ws1415.SkatenightBackend.model.UserGroupPicture;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by Bernd Eissing on 26.05.2015.
 */
public class Upload extends HttpServlet {
    private static final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        String pictureId = request.getParameter("id");

        if(pictureId == null || pictureId.isEmpty()){
            throw new IllegalArgumentException("Es wurde keine Id zum Laden eines UserGroupPictures angegeben");
        }

        List<BlobKey> blobKeys = blobstoreService.getUploads(request).get("file");
        if(!blobKeys.isEmpty()){
            BlobKey blobKey = blobKeys.get(0);
            try{
                // Das UserGroupPicture vom Server laden, damit der BlobKey gesetzt werden kann
                UserGroupPicture groupPicture = ofy().load().type(UserGroupPicture.class).id(Long.parseLong(pictureId)).safe();
                groupPicture.setPictureBlobKey(blobKey);
                ofy().save().entities(groupPicture).now();

                // Nun den BlobKey für das Bild wieder an den Clienten senden, damit das Bild in der app verwendet werden kann
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(blobKey);
                response.getWriter().flush();
                response.getWriter().close();
            }catch(IOException e){
                // Beim Fehler den BlobKey löschen
                blobstoreService.delete(blobKey);
                e.printStackTrace();
            }
        }
    }
}