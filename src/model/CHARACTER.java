package model;
// holds information about player characters
public enum CHARACTER {
	BLUE("view/resources/characters/blue.png", "view/resources/characters/red_life.png", "blue"),
	RED("view/resources/characters/red.png", "view/resources/characters/red_life.png", "red"),
	YELLOW("view/resources/characters/yellow.png", "view/resources/characters/red_life.png", "yellow"),
	GREEN("view/resources/characters/green.png", "view/resources/characters/red_life.png", "green");

	private String urlCharacter;
	private String urlLife;
	private String color;

	private CHARACTER(String urlCharacter, String urlLife, String color) {
		this.urlCharacter = urlCharacter;
		this.urlLife = urlLife;
		this.color = color;
	}

	public String getUrl() {
		return this.urlCharacter;
	}

	public String getUrlLife() {
		return urlLife;
	}

	public String getColor() {
		return color;
	}
}
