package id.kasandra.retail;

public class GetItem {
	private int id;
	private String name, code, image, price_sell, timeStamp;

	public GetItem() {
	}

	public GetItem(int id, String name, String code, String image,
                   String price_sell, String timeStamp) {
		super();
		this.id = id;
		this.name = name;
		this.code = code;
		this.image = image;
		this.price_sell = price_sell;
		this.timeStamp = timeStamp;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getPrice_sell() {
		return price_sell;
	}

	public void setPrice_sell(String price_sell) {
		this.price_sell = price_sell;
	}
}
