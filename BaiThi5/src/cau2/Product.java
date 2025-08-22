package cau2;

import java.io.Serializable;

public class Product implements Serializable {
	int idsp;
	String name;
	int count;
	double price;

	public Product(int idsp, String name, int count, double price) {
		super();
		this.idsp = idsp;
		this.name = name;
		this.count = count;
		this.price = price;
	}

	public int getIdsp() {
		return idsp;
	}

	public void setIdsp(int idsp) {
		this.idsp = idsp;
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
		return idsp + "\t" + name + "\t" + count + "\t" + price;
	}

}
