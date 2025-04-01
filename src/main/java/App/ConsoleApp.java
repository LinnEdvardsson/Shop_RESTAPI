package App;

import Customer.Customer;
import Repository.Repository;
import ShoeShop.LJShoeShop;
import ShoeShop.Orders.Item;
import ShoeShop.Products.Category;
import ShoeShop.Products.Product;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleApp {

    Scanner input = new Scanner(System.in);
    LJShoeShop shop;

    boolean running = true;
    UserState currentState = UserState.LOGIN;
    Customer loggedInCustomer = null;

    public ConsoleApp() throws IOException {
        shop = new LJShoeShop();
        shop.updateStore();
    }

    public void run() {
        while (running) {
            switch (currentState) {
                case LOGIN -> logInPrompt();
                case MAIN_MENU -> menuPrompt();
            }
        }
    }

    public void validateLogIn(String username, String password) {
        try {
            Repository repository = new Repository();
            loggedInCustomer = repository.login(username, password);
            if (loggedInCustomer == null) {
                System.out.println("Invalid username or password");
            } else {
                currentState = UserState.MAIN_MENU;
                loggedInCustomer.setShoppingCart(repository.getShoppingCart(loggedInCustomer));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void logInPrompt() {
        System.out.println("LOG IN");
        System.out.print("Username:\n-> ");
        String username = input.nextLine();
        System.out.print("Password:\n-> ");
        String password = input.nextLine();
        validateLogIn(username, password);
    }

    public void menuPrompt() {
        System.out.println("\nWELCOME TO LJ SHOES!");
        System.out.println(
                "[1] Shop" +
                        "\n[2] Order history" +
                        "\n[3] Shopping cart" +
                        "\n[4] Log out");
        System.out.print("-> ");
        handleMainMenuInput(input.nextLine());
    }

    public void handleMainMenuInput(String input) {
        switch (input) {
            case "1" -> {
                categoriesPrompt();
            }
            case "2" -> {
                shop.orderHistory(loggedInCustomer);
            }
            case "3" -> {
                showShoppingCart();
            }
            case "4" -> {
                loggedInCustomer = null;
                currentState = UserState.LOGIN;
            }
            case "5" -> {
                running = false;
            }
        }
    }

    public void categoriesPrompt() {
        System.out.println("Choose a category: ");
        getCategoryNames();

        int categoryChoice = takeMenuChoice();

        Category chosenCategory = getCategoryChoice(categoryChoice);
        validateCategoryChoice(chosenCategory);
    }

    public int takeMenuChoice(){
        System.out.println("-> ");
        int choice = 0;
        try {
            choice = Integer.parseInt(input.nextLine());
        }
        catch (Exception e) {
            System.out.println("Invalid choice. Returning to main menu.");
        }
        return choice;
    }

    public void validateCategoryChoice(Category category) {
        if(category == null) {
            System.out.println("Invalid category choice. Returning to menu.");
        }
        else{
            handleCategoryChoice(category);
        }
    }

    public Category getCategoryChoice(int choice){
        for (int i = 0; i < shop.getLJcategories().size(); i++) {
            if (choice > 0 && choice <= shop.getLJcategories().size()) {
                return shop.getLJcategories().get(choice - 1);
            }
        }
        return null;
    }

    public void getCategoryNames(){
        int count = 1;
        for (Category lJcategory : shop.getLJcategories()) {
            System.out.println("[" + (count++) + "] " + lJcategory.getCategoryName());
        }
    }

    public void handleCategoryChoice(Category chosenCategory) {
        if (!chosenCategory.getProductsInCategory().isEmpty()) {
            getProductsForCategory(chosenCategory);
            handleProductChoice(chosenCategory);
        } else {
            System.out.println("No products found");
        }
    }

    public void getProductsForCategory(Category chosenCategory) {
        for (int i = 0; i < chosenCategory.getProductsInCategory().size(); i++) {
            Product product = chosenCategory.getProductsInCategory().get(i);
            System.out.println("\n[" + (i + 1) + "] " + product.getProductName() +
                    "\nBrand: " + product.getSpec().getBrand() +
                    "\nColor: " + product.getSpec().getColor() +
                    "\nSize: " + product.getSpec().getSize() +
                    "\nPrice: " + product.getSpec().getPrice());
        }
    }

    public void handleProductChoice(Category chosenCategory) {
        int productChoice = takeMenuChoice();

        if (productChoice > 0 && productChoice <= chosenCategory.getProductsInCategory().size()) {
            Product selectedProduct = chosenCategory.getProductsInCategory().get(productChoice - 1);
            System.out.println("Selected product: " + selectedProduct.getProductName());
             shop.addProductToCart(selectedProduct, loggedInCustomer);
        } else {
            System.out.println("Invalid choice. Returning to menu");
        }
    }

    public void showShoppingCart() {
        System.out.println("--YOUR SHOPPING CART--");
        shop.loadShoppingCartItems(loggedInCustomer);
        if (loggedInCustomer.getShoppingCart().getItemsInCart().isEmpty()) {
            System.out.println("Your shopping cart is empty!");
        } else {
            showCartItems();
        }
    }

     public void showCartItems(){
         for (Item item : loggedInCustomer.getShoppingCart().getItemsInCart()) {
             System.out.println(item.getProduct().getProductName() + " - qty: " + item.getQuantity());
         }
         shoppingCartMenu();
     }

    public void shoppingCartMenu(){
        System.out.println("[1] Make order" + "\n[2] Clear shopping cart" + "\n[3] Back to menu");
        System.out.print("-> ");
        handleShoppingCartInput(input.nextLine());
    }

    public void handleShoppingCartInput(String choice){
        switch (choice) {
            case "1" -> shop.placeOrder(loggedInCustomer);
            case "2" -> shop.clearShoppingcart(loggedInCustomer);
            case "3" -> currentState = UserState.MAIN_MENU;
            default -> System.out.println("Invalid choice. Returning to menu.");
        }
    }

}