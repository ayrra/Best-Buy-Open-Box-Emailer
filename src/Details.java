
public class Details {
	private String url;
	private Double price;
	private String productDetails;
	private String date;
	
	
	public Details(String url, Double price, String productDetails, String date) {
		this.url = url;
		this.price = price;
		this.productDetails = productDetails;
		this.date = date;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public Double getPrice() {
		return this.price;
	}
	
	public String getDetails() {
		return this.productDetails;
	}
	
	public String getDate() {
		return this.date;
	}
	
	
}
