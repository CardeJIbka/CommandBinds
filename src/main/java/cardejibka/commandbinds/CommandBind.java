package cardejibka.commandbinds;


public record CommandBind(int keyCode, String text, String label) {

    public boolean isCommand() {
        return text.startsWith("/");
    }

    public String displayText() {
        return text.length() > 40 ? text.substring(0, 37) + "..." : text;
    }
}
