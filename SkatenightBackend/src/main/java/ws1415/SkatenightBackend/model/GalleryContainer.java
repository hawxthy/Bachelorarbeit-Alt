package ws1415.SkatenightBackend.model;

import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

/**
 * Interface für Klassen, die eine Menge von Gallerien verwalten können.
 * @author Richard Schulze
 */
public interface GalleryContainer {
    /**
     * Fügt die angegebene Gallery diesem Container hinzu.
     * @param user       Der Benutzer, der die Gallery hinzufügen möchte.
     * @param gallery    Die hinzuzufügende Gallery.
     */
    void addGallery(User user, Gallery gallery) throws OAuthRequestException;

    /**
     * Entfernt die angegebene Gallery aus diesem Container.
     * @param user       Der Benutzer, der die Gallery entfernen möchte.
     * @param gallery    Die zu entfernende Gallery.
     */
    void removeGallery(User user, Gallery gallery) throws OAuthRequestException;

    /**
     * Prüft, ob der angegebene Benutzer diesem Container einer Gallery hinzufügen kann.
     * @param user    Der Benutzer, der die Gallery hinzufügen möchte.
     * @return true, wenn der Benutzer die Gallery hinzufügen kann, sonst false.
     */
    boolean canAddGallery(User user);

    /**
     * Prüft, ob der angegebene Benutzer eine Gallery in diesem Container editieren darf.
     * @param user    Der Benutzer, der die Gallery editieren möchte.
     * @return true, wenn der Benutzer die Gallery editieren darf, sonst false.
     */
    boolean canEditGallery(User user);

    /**
     * Prüft, ob der angegebene Benutzer eine Gallery in diesem Container löschen darf.
     * @param user    Der Benutzer, der die Gallery löschen möchte.
     * @return true, wenn der Benutzer die Gallery löschen darf, sonst false.
     */
    boolean canRemoveGallery(User user);

    /**
     * Prüft, ob der angegebene Benutzer Gallerien in diesem Container Bilder hinzufügen darf.
     * @param user                  Der Benutzer, der ein Bild hinzufügen möchte.
     * @param picture               Das Bild, das hinzugefügt werden soll.
     * @return true, wenn der Benutzer Bilder hinzufügen darf, sonst false.
     */
    boolean canAddPictures(User user, Picture picture);

    /**
     * Prüft, ob der angegebene Benutzer Bilder aus Gallerien dieses Containers entfernen darf.
     * @param user                  Der Benutzer, der ein Bild entfernen möchte.
     * @param picture               Das Bild, das entfernt werden soll.
     * @return true, wenn der Benutzer das Bild entfernen darf, sonst false.
     */
    boolean canRemovePictures(User user, Picture picture);
}