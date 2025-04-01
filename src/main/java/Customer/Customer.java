package Customer;

import ShoeShop.Orders.Order;
import ShoeShop.Orders.ShoppingCart;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private final int id;
    private final String firstName;
    private final String lastName;
    private final long sscr;
    private LoginDetails loginDetails;
    private ShoppingCart shoppingCart;
    private List<Order> orderHistory;

    public Customer(int id, String firstName, String lastName, long sscr, LoginDetails loginDetails) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sscr = sscr;
        this.loginDetails = loginDetails;
        shoppingCart = new ShoppingCart();
        orderHistory = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<Order> orderHistory) {
        this.orderHistory = orderHistory;
    }

}
