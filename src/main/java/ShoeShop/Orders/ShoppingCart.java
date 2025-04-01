package ShoeShop.Orders;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private int id;
    private List<Item> cartItemList;

    public ShoppingCart() {
        this.cartItemList = new ArrayList<Item>();
    }

    public List<Item> getItemsInCart() {
        return cartItemList;
    }

    public void setItemsInCart(List<Item> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
