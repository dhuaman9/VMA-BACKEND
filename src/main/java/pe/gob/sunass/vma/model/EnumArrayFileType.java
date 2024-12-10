package pe.gob.sunass.vma.model;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import pe.gob.sunass.vma.model.cuestionario.TipoArchivo;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class EnumArrayFileType implements UserType {
	
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Class<List> returnedClass() {
        return List.class;
    }

    @Override
    public boolean equals(Object x, Object y) {
        if (x == null) {
            return y == null;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(names[0]);
        if (value == null) {
            return null;
        }
        String[] strings = value.split(",");
        List<TipoArchivo> enums = new ArrayList<>();
        Arrays.stream(strings).forEach(mimeType -> {
            TipoArchivo tipoArchivo = Arrays.stream(TipoArchivo.values())
                    .filter(tipo -> tipo.getMimeType().equals(mimeType.trim()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown TipoArchivo value: " + mimeType));
            enums.add(tipoArchivo);
        });
        return enums;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            List<TipoArchivo> enums = (List<TipoArchivo>) value;
            String[] stringArray = enums.stream()
                    .map(TipoArchivo::getMimeType)
                    .toArray(String[]::new);
            String joinedString = String.join(",", stringArray);
            st.setString(index, joinedString);
        }
    }

    @Override
    public Object deepCopy(Object value) {
        return new ArrayList<>(Objects.nonNull(value) ? (List<TipoArchivo>) value : Collections.emptyList());
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) {
        return (Serializable) this.deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) {
        return this.deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) {
        return this.deepCopy(original);
    }
}