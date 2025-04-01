package ShoeShop.Orders;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final int customerOrderID;
    private final String orderDate;
    private final List <Item> products;

    public Order(int customerOrderID, String dateOfOrder) {
        this.customerOrderID = customerOrderID;
        this.orderDate = dateOfOrder;
        products = new ArrayList<>();
    }

    public int getCustomerOrderID() {
        return customerOrderID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public List<Item> getProducts() { return products; }

    public void addItemToOrder(Item item){
        products.add(item);
    }

}
