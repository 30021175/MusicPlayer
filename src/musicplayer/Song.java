package musicplayer;
/**
 * <p>
 * Song Class
 * </p>
 * @author 30021175 - Willian Bernatzki Woellner
 * @since 2020-11-08
 * @version 1.0.0
 */
public class Song {
    /**
     * Attributes
     */
    private String title;
    private String URL;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the URL
     */
    public String getURL() {
        return URL;
    }

    /**
     * @param URL the URL to set
     */
    public void setURL(String URL) {
        this.URL = URL;
    }
    
    /**
     * Song Constructor Method
     * @param title String
     * @param URL String
     */
    public Song(String title, String URL){
        this.title = title;
        this.URL = URL;
    }
}
