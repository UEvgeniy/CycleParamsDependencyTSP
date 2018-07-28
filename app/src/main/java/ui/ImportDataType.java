package ui;

public enum ImportDataType implements DataType {

    TXT_MATRIXES("Txt matrixes files"),
    OBJ_MATRIXES("Obj matrixes files"),
    OBJ_DATASET("Obj dataset file"),
    TXT_COMPLEXITY("Txt complexity");

    String value;
    ImportDataType(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
