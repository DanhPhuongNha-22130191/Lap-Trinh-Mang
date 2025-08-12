package cau2;

public class Product {
	int id;
	String name;
	int count;
	double price;

	

	public Product(int id, String name, int count, double price) {
		super();
		this.id = id;
		this.name = name;
		this.count = count;
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		return id + "\t" + name + "\t" + count + "\t" + price;
	}

}
