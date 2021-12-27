package utilities;

/**
 * Info of supported languages
 */
public enum LanguageType {

    JAVA(0, ".java"),
    PYTHON(1, ".py"),
    C(2, ".c"),
    CPP(3, ".cpp");

    private String extension;
    private int idx;

    LanguageType(int idx, String s) {
        this.extension = s;
        this.idx = idx;
    }

    public String getLanguage() {
        return this.toString();
    }

    public String getExtension() {
        return extension;
    }

    public int getIndex() {
        return idx;
    }
}
