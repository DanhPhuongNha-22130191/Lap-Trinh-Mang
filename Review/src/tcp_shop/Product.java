package tcp_shop;


public class Product {
	int productID;
	String name;
	int count;
	double price;

	public Product(int productID, String name, int count, double price) {
		this.productID = productID;
		this.name = name;
		this.count = count;
		this.price = price;
	}

	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return productID + "\t" + name + "\t" + count + "\t" + price;
	}

}
