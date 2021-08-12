package goods.model;

public class GoodsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public GoodsException(String message) {
		super(message);
		System.err.println(message);
	}
}
