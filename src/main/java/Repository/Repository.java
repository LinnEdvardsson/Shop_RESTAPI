package Repository;

import Customer.Customer;
import Customer.LoginDetails;
import ShoeShop.Orders.Item;
import ShoeShop.Orders.Order;
import ShoeShop.Orders.ShoppingCart;
import ShoeShop.Products.Category;
import ShoeShop.Products.OutOfStockItem;
import ShoeShop.Products.Product;
import ShoeShop.Products.Specification;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Repository {

    private Properties properties = new Properties();

    public Repository() throws IOException {
        properties.load(new FileInputStream("src/settings_logIn.properties"));
    }

    //hämtar customer som loggar in från databasen
    public Customer login(String username, String password) {
        String query = "SELECT Customer.id, Customer.firstName, Customer.lastName, Customer.sscr, " +
                "LoginDetails.username, LoginDetails.userPassword " +
                "FROM Customer " +
                "INNER JOIN LoginDetails ON LoginDetails.customerId = Customer.id " +
                "WHERE LoginDetails.username = ? AND LoginDetails.userPassword = ?";

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(query)){

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                long sscr = resultSet.getLong("sscr");
                String userUsername = resultSet.getString("username");
                String userPassword = resultSet.getString("userPassword");
                return new Customer(id, firstName, lastName, sscr, new LoginDetails(userUsername, userPassword));
            }
            return null;

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid connection to database while logging in", e);
        }
    }

    //laddar programmet med kategorier från databasen
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();

        String query = "SELECT * FROM Category";

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String categoryName = resultSet.getString("categoryName");
                Category category = new Category(categoryName, id);
                categories.add(category);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid connection to database while collection categories", e);
        }
        return categories;
    }


    //returnerar en lista med alla produkter + lägger in dem i tillhörande kategorier
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();

        String query = "SELECT Product.id as productId, Product.productName, Specification.price, Specification.shoeSize, Specification.color, Specification.brand, Inventory.quantity " +
                "from Product " +
                "inner join Specification on Specification.id = Product.specId " +
                "inner join Inventory on Inventory.productId = Product.id ";

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int productId = resultSet.getInt("productId");
                String productName = resultSet.getString("productName");
                double price = resultSet.getDouble("price");
                int shoeSize = resultSet.getInt("shoeSize");
                String color = resultSet.getString("color");
                String brand = resultSet.getString("brand");
                int quantity = resultSet.getInt("quantity");
                Product product = new Product(productId, productName, quantity, (new Specification(price, shoeSize, color, brand)));
                products.add(product);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Databasfel vid inläsning av produkter", e);
        }

        return products;
    }

    public void putProductsInCategories(List<Category> categories, List<Product> products) {
        String query = "SELECT * FROM ProductInCategory";
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int productId = resultSet.getInt("productId");
                int categoryId = resultSet.getInt("categoryId");
                for (Category category : categories) {
                    if (category.getCategoryID() == categoryId) {
                        for (Product product : products) {
                            if (product.getId() == productId) {
                                category.addProductToCategory(product);
                            }
                        }
                    }
                }
            }

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //laddar en shoppingcart med produkter som ligger i den i databasen
    public List<Item> loadShoppingCart(ShoppingCart cart, List<Product> products) {
        List<Item> items = new ArrayList<>();
        String query = "SELECT CartItem.productId, CartItem.quantity FROM CartItem where CartItem.cartId = ? ";
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
                PreparedStatement statement = connection.prepareStatement(query)){

            statement.setInt(1, cart.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int productId = resultSet.getInt("productId");
                int quantity = resultSet.getInt("quantity");

                for (Product product : products) {
                    if (product.getId() == productId) {
                        Item item = new Item(product);
                        item.setQuantity(quantity);
                        items.add(item);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public void ClearShoppingCart(Customer customer){

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
             CallableStatement callState = connection.prepareCall("CALL ClearShoppingCart(?)")) {

            callState.setInt(1, customer.getShoppingCart().getId());
            callState.execute();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    //hämtar shoppingcart tillhörande customer
    public ShoppingCart getShoppingCart(Customer customer) {
        ShoppingCart shoppingCart = new ShoppingCart();
        String query = "SELECT * FROM ShoppingCart WHERE ShoppingCart.customerId = ? ";

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, customer.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int shoppingCartId = resultSet.getInt("id");
                shoppingCart.setId(shoppingCartId);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }
        // Hämta orderhistorik för inloggad kund, retuneras som en lista.
    public List<Order> getOrderHistory(Customer loggedInCustomer) {
        List<Order> orderHistory = new ArrayList<>();
        String query = "SELECT CustomerOrder.id, CustomerOrder.dateOfOrder " +
                "FROM CustomerOrder " +
                "INNER JOIN Customer on Customer.id = CustomerOrder.customerId WHERE Customer.id = ? ";

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, loggedInCustomer.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int orderID = resultSet.getInt("id");
                String dateOfOrder = resultSet.getString("dateOfOrder");
                Order collectedOrder = new Order(orderID, dateOfOrder);
                orderHistory.add(collectedOrder);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while getting order history");
        }

        if (orderHistory.isEmpty()) {
            System.out.println("No orders found.");
        }
        return orderHistory;
    }
        //Hämtar alla produkter från tidigare ordrar och kollas ihop med rätt order baserat på orderId. Retuneras som lista.
    public void loadOrders(Customer customer, List<Product> productsInShop) {
        String query = "SELECT OrderedProduct.orderId, OrderedProduct.productId, OrderedProduct.quantity " +
                "FROM OrderedProduct " +
                "INNER JOIN CustomerOrder ON CustomerOrder.id = OrderedProduct.orderId " +
                "INNER JOIN Customer ON Customer.id = CustomerOrder.customerId " +
                "WHERE CustomerOrder.customerId = ? ";

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, customer.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int orderId = resultSet.getInt("orderId");
                int productID = resultSet.getInt("productId");
                int quantity = resultSet.getInt("quantity");
                for (Order order : customer.getOrderHistory()) {
                    if (order.getCustomerOrderID() == orderId) {
                        for (Product product : productsInShop) {
                            if (product.getId() == productID) {
                                Item item = new Item(product, quantity);
                                order.addItemToOrder(item);
                            }
                        }
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
            //Anropar SP addToCart för att lägga till varor i varukorgen som sparas i databasen.
    public void addItemToCart(Customer customer, Product product) {
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
        CallableStatement callState = connection.prepareCall("CALL AddToCart(?, ?, ?)")) {

            callState.setInt(1, customer.getId());
            callState.setInt(2, customer.getShoppingCart().getId());
            callState.setInt(3, product.getId());
            callState.execute();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<OutOfStockItem> getProductsOutOfStock(List<Product> products) {
        List<OutOfStockItem> outOfStock = new ArrayList<>();
        String query = "SELECT * FROM OutOfStock ";

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int productId = resultSet.getInt("productId");
                Date date = resultSet.getDate("soldOutSince");
                for (Product product : products) {
                    if (product.getId() == productId) {
                        OutOfStockItem item = new OutOfStockItem(product, date);
                        outOfStock.add(item);
                    }
                }
            }

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return outOfStock;
    }

    //Anropar SP PlaceOrder för att lägga en order, läggs även till i databasen.
    public void placeOrder(ShoppingCart shoppingCart, Customer customer) {
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connectionString"),
                properties.getProperty("username"),
                properties.getProperty("password"));
             CallableStatement callStatement = connection.prepareCall("CALL PlaceOrder(?,?)")) {

            callStatement.setInt(1, shoppingCart.getId());
            callStatement.setInt(2, customer.getId());
            callStatement.execute();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}


