package in.co.androidadda.wallpapertrending;

/**
 * Created by Yugansh Tyagi on 11-09-2017.
 */

public class Categories_Model_Class  {
    String category_name;
    int category_image_id;

    public Categories_Model_Class(String category_name, int category_image_id) {
        this.category_name = category_name;
        this.category_image_id = category_image_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public int getCategory_image_id() {
        return category_image_id;
    }
}

