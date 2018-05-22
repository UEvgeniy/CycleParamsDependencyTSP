package ui;

public enum ExportDataType {

    DISTR_CYCLE_LEN("Distribution: cycles length"),
    DISTR_CYCLE_TO_CITY("Distribution: cycles to cities"),
    SERIALIZE_MATRIXES("Serialize into matrixes (*.rm)"),
    SERIALIZE_DATASET("Serialize into dataset (*.ds)"),
    LIST("List of cycles lengths"),
    TABLE_FUNCTIONS("Table of cycle parametres");

    String value;
    ExportDataType(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
