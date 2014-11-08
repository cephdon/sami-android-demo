package io.samsungsami.androidclient;

public class Item {

	private String itemId;
	private String description;
	
	public Item(String itemId, String description) {
		super();
		this.itemId = itemId;
		this.description = description;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
