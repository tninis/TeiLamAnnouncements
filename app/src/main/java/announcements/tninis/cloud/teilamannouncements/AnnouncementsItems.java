package announcements.tninis.cloud.teilamannouncements;

public class AnnouncementsItems {
    String title;
    String url;
    String date;

    public AnnouncementsItems(String title, String url, String date) {
        this.title = title;
        this.url = url;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
