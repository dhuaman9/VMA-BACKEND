package pe.gob.sunass.vma.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseEntity<T> implements Serializable {

	private Object _value;
	private String _message;
	private Map<String, String> _validations;
	private Boolean _success;
	private T _item;
	/*
	@XmlElementWrapper(name = "items")
	@XmlElement(name = "item")
	*/
	private List<T> _items;
	
	public ResponseEntity() {
		this._success = true;
		this._items = new ArrayList<T>();
	}

	public ResponseEntity(T item) {
		//this();
		this._success = true;
		this._item = item;
	}

	public ResponseEntity(List<T> items) {
		//this();
		this._success = true;
		this._items = items;
	}

	
	public Object getValue() {
		return _value;
	}

	public void setValue(Object _value) {
		this._value = _value;
	}

	public String getMessage() {
		return _message;
	}

	public Map<String, String> getValidations() {
		return _validations;
	}

	public void setValidations(Map<String, String> _validations) {
		this._validations = _validations;
	}
	
	public Boolean getSuccess() {
		return _success;
	}

	public void setSuccess(Boolean _success) {
		this._success = _success;
	}

	public T getItem() {
		return _item;
	}

	public List<T> getItems() {
		return _items;
	}

	
	public Integer getTotal() {
		/*System.out.println("total");
		System.out.println(this._paginator.getTotal());*/
		if (this._items == null)
			return null;
		if (this._items.size() == 0)
			return 0;
		/*
		if (this._paginator != null)
			return this._paginator.getTotal();
		*/
		return this._items.size();
	}

	public void setItem(T value) {
		this._item = value;
		this._success = this._item == null ? false : true;
	}

	public void setItems(List<T> value) {
		this._items = value;
		this._success = this._items == null || this._items.size() == 0 ? false : true;
	}

	public void setMessage(String value) {
		_message = value;
	}

	public void setMessage(Exception ex) {
		_success = false;
		this.setItems(null);
		_message = ex.getMessage();
	}

	public static ResponseEntity<?> ok() {
		ResponseEntity<?> response = new ResponseEntity<>();
		response.setMessage("Satisfactorio");
		response.setSuccess(true);
		return response;
	}

	public static <T> ResponseEntity<T> ok(T item) {
		ResponseEntity<T> response = new ResponseEntity<T>();
		response.setMessage("");
		response.setItem(item);
		response.setSuccess(true);
		return response;
	}

	public static <T> ResponseEntity<T> ok(List<T> items) {
		ResponseEntity<T> response = new ResponseEntity<T>();
		response.setMessage("");
		response.setItems(items);
		response.setSuccess(true);
		return response;
	}

	public static ResponseEntity<?> error(String message) {
		ResponseEntity<?> response = new ResponseEntity<>();
		response.setMessage(message);
		response.setItems(null);
		response.setItem(null);
		response.setSuccess(false);
		return response;
	}

	public static ResponseEntity<?> validators(Map<String, String> validations) {
		ResponseEntity<?> response = new ResponseEntity<>();
		response.setValidations(validations);
		response.setItems(null);
		response.setItem(null);
		response.setMessage("Error en la validación de campos");
		response.setSuccess(false);
		return response;
	}
}
