package ShoeShop.Products;

import java.util.Date;

public class OutOfStockItem {

    private final Product product;
    private final Date outOfStockSince;

    public OutOfStockItem(Product product, Date outOfStockSince) {
        this.product = product;
        this.outOfStockSince = outOfStockSince;
    }

    public Product getProduct() {
        return product;
    }


    public Date getOutOfStockSince() {
        return outOfStockSince;
    }
}
