package ShoeShop;

import Customer.Customer;
import Repository.Repository;
import ShoeShop.Orders.Item;
import ShoeShop.Orders.Order;
import ShoeShop.Products.Category;
import ShoeShop.Products.OutOfStockItem;
import ShoeShop.Products.Product;

import java.io.IOException;
import java.util.List;

public class LJShoeShop {

    private List<Category> LJcategories;
    private List<Product> LJProducts;
    private Repository repository;
    private List<OutOfStockItem> outOfStock;

    public LJShoeShop() throws IOException {
        this.repository = new Repository();
    }

    public void updateStore() {
        try {
            LJcategories = repository.getCategories();
            LJProducts = repository.getProducts();
            repository.putProductsInCategories(LJcategories, LJProducts);
            outOfStock = repository.getProductsOutOfStock(LJProducts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCustomerInfo(Customer customer) {
        customer.setOrderHistory(repository.getOrderHistory(customer));
        repository.loadOrders(customer, LJProducts);
    }
    public void placeOrder(Customer customer) {
        updateStore();
        updateCustomerInfo(customer);
        int orderCountBefore = getOrderCount(customer);
        processOrder(customer);
        if (hasOrderSucceeded(orderCountBefore, customer)) {
            System.out.println("Order successful!");
        }
        else {
            handleFailedOrder(customer);
        }
    }

    private void processOrder(Customer customer){
        repository.placeOrder(customer.getShoppingCart(), customer);
        updateCustomerInfo(customer);
    }

    private int getOrderCount(Customer customer){
        return customer.getOrderHistory().size();
    }


    private boolean hasOrderSucceeded(int ordersBefore, Customer customer) {
        return getOrderCount(customer) > ordersBefore;
    }

    private void handleFailedOrder(Customer customer){
        for(Item item : customer.getShoppingCart().getItemsInCart()) {
            checkAvailability(item);
        }
    }

    public void checkAvailability(Item item){
        Product product = findProductById(item.getProduct().getId());
        if(product != null && product.getStockQuantity() < item.getQuantity()) {
            System.out.println("Adjust quantity for following product: " + item.getProduct().getProductName());
        }
    }

    public Product findProductById(int id){
        for(Product product : LJProducts){
            if (product.getId() == id){
                return product;
            }
        }
        return null;
    }

    public void orderHistory(Customer customer) {
        System.out.println(customer.getFirstName() + " " + customer.getLastName() + " ORDER HISTORY: ");
        updateCustomerInfo(customer);
        for (Order order : customer.getOrderHistory()) {
            System.out.println("\nOrdernumber: " + " " + order.getCustomerOrderID() + " " + "Ordered: " + " " + order.getOrderDate());
            OrderedDetails(order);
        }
        if (customer.getOrderHistory() == null || customer.getOrderHistory().isEmpty()) {
            System.out.println("No order history found for this customer.");
        }
    }

    public void OrderedDetails(Order order) {
        for(Item item : order.getProducts()){
            System.out.println("Product: " + item.getProduct().getProductName() + " qty: " + item.getQuantity());
        }
    }

    public void addProductToCart(Product product, Customer customer) {
        OutOfStockItem outOfStockItem = findOutOfStockItem(product);
        if (outOfStockItem != null) {
            handleOutOfStockItems(outOfStockItem);
        } else {
            repository.addItemToCart(customer, product);
        }
    }

    public void handleOutOfStockItems(OutOfStockItem item) {
        System.out.println("Product is out of stock since " + item.getOutOfStockSince());
    }

    public OutOfStockItem findOutOfStockItem(Product product) {
        for (OutOfStockItem item : outOfStock) {
            if (item.getProduct().getId() == product.getId()) {
                return item;
            }
        }
        return null;
    }

    public void loadShoppingCartItems(Customer customer) {
        updateStore();
        customer.setShoppingCart(repository.getShoppingCart(customer));
        customer.getShoppingCart().setItemsInCart(repository.loadShoppingCart(customer.getShoppingCart(), LJProducts));
    }

    public void clearShoppingcart(Customer customer){
        repository.ClearShoppingCart(customer);
    }

    public List<Category> getLJcategories() {
        return LJcategories;
    }

    public List<Product> getLJProducts() {
        return LJProducts;
    }

    public Repository getRepository() {
        return repository;
    }

    public List<OutOfStockItem> getOutOfStock() {
        return outOfStock;
    }
}
