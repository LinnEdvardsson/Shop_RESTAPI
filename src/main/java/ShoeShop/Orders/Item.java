package ShoeShop.Orders;

import ShoeShop.Products.Product;

public class Item {

    private final Product product;
    private int quantity = 1;

    public Item(Product product) {
        this.product = product;
    }

    public Item(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
