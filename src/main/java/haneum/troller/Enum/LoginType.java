package haneum.troller.Enum;

public enum LoginType {
    NORMAL("normal"),
    KAKAO("kakao");

    private final String label;

    LoginType(String label) {
        this.label = label;
    }
    public String label() {
        return label;
    }
}
