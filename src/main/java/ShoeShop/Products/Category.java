package ShoeShop.Products;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private final String categoryName;
    private final int categoryID;
    private final List<Product> productsInCategory;

    public Category(String categoryName, int categoryID) {
        this.categoryName = categoryName;
        this.categoryID = categoryID;
        productsInCategory = new ArrayList<>();
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public List<Product> getProductsInCategory() {
        return productsInCategory;
    }

    public void addProductToCategory(Product product) {
        productsInCategory.add(product);
    }

}
