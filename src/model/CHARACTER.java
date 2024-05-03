package model;

public enum CHARACTER {
	BLUE("view/resources/characters/blue.png"),
	RED("view/resources/characters/red.png"),
	YELLOW("view/resources/characters/yellow.png"),
	GREEN("view/resources/characters/green.png");

	private String urlCharacter;

	private CHARACTER(String urlCharacter) {
		this.urlCharacter = urlCharacter;
	}

	public String getUrl() {
		return this.urlCharacter;
	}
}
