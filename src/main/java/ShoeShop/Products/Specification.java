package ShoeShop.Products;

public class Specification {

    private final double price;
    private final int size;
    private final String color;
    private final String brand;

    public Specification(double price, int size, String color, String brand) {
        this.price = price;
        this.size = size;
        this.color = color;
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public String getBrand() {
        return brand;
    }
}
