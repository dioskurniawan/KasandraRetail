package id.kasandra.retail;

public class TransactionItem {

	private String id;
	private String title;
	private String price;
	private String date;
	
	public TransactionItem(){
		this.id = null;
		this.title = null;
		this.price = null;
		this.date = null;
	}
	
	public TransactionItem(String id, String title, String dep, String date){
		this.id = id;
		this.title = title;
		this.price = dep;
		this.date = date;
	}

	public String getID(){
		return this.id;
	}

	public void setID(String id){
		this.id = id;
	}

	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getPrice(){
		return this.price;
	}
	
	public void setPrice(String dep){
		this.price = dep;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public void setDate(String date){
		this.date = date;
	}

}