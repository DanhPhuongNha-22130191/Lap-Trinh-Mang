package rmi_shop_manager;

import java.io.Serializable;

public class Product implements Serializable{
	int productId;
	String name;
	int count;
	double price;

	public Product(int productId, String name, int count, double price) {
		this.productId = productId;
		this.name = name;
		this.count = count;
		this.price = price;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productID) {
		this.productId = productID;
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
		return productId + "\t" + name + "\t" + count + "\t" + price;
	}

}
