package ShoeShop.Products;

public class Product {

    private int id;
    private String productName;
    private Specification spec;
    private int stockQuantity;

    public Product(int id, String productName, int stockQuantity, Specification spec) {
        this.id = id;
        this.productName = productName;
        this.spec = spec;
        this.stockQuantity = stockQuantity;
    }

    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public Specification getSpec() {
        return spec;
    }

    public int getStockQuantity(){
        return stockQuantity;
    }
}
