package pe.gob.sunass.vma.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum TipoArchivo {
    XLS("application/vnd.ms-excel"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    DOC("application/msword"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    PDF("application/pdf");

    private final String mimeType;

    TipoArchivo(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    @JsonValue
    public Map<String, String> toJson() {
        Map<String, String> map = new HashMap<>();
        map.put("nombre", this.name());
        map.put("mimeType", this.mimeType);
        return map;
    }
}
