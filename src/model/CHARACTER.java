package model;
// holds information about player characters
public enum CHARACTER {
	BLUE("view/resources/characters/blue.png", "view/resources/characters/red_life.png"),
	RED("view/resources/characters/red.png", "view/resources/characters/red_life.png"),
	YELLOW("view/resources/characters/yellow.png", "view/resources/characters/red_life.png"),
	GREEN("view/resources/characters/green.png", "view/resources/characters/red_life.png");

	private String urlCharacter;
	private String urlLife;

	private CHARACTER(String urlCharacter, String urlLife) {
		this.urlCharacter = urlCharacter;
		this.urlLife = urlLife;
	}

	public String getUrl() {
		return this.urlCharacter;
	}

	public String getUrlLife() {
		return urlLife;
	}
}
