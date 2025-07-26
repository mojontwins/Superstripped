package net.minecraft.src;

public class GameRule {
	private String caption;
	private String description;

	private int id;
	
	private static int lastId;
	
	public GameRule() {
		this.setId(lastId ++);
	}

	public GameRule withCaption(String caption) {
		this.setCaption(caption);
		return this;
	}
	
	public GameRule wihtDescription(String description) {
		this.setDescription(description);
		return this;
	}
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
