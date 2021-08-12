package goods.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Review {
	
	private String barcode;
	private String client;
	private Date createTime;
	private String review;
	
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	
	public Review() {
		super();
	}
	
	public Review(String barcode, String client, Date createTime, String review) {
		super();
		this.barcode = barcode;
		this.client = client;
		this.createTime = createTime;
		this.review = review;
	}
	
	public String reviewToTxt() {
		// Convert Review Object to store as txt format
		return barcode + "|" + client + "|" + dateFormat.format(createTime) + "|" + review;
	}
	
	public static Review txtToReview(String reviewString) throws GoodsException {
		// Convert stored txt format to Review Object
		Review review = null;
		try {
			String[] info = reviewString.split("\\|");
			review = new Review(info[0], info[1], dateFormat.parse(info[2]), info[3]);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoodsException("Get exception when parse txt to Review " + e.getMessage());
		}
		return review;
	}

	@Override
	public String toString() {
		return client + " [" + dateFormat.format(createTime) + "]:" + review;
	}

	public String getBarcode() {
		return barcode;
	}

	public String getClient() {
		return client;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public String getReview() {
		return review;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setReview(String review) {
		this.review = review;
	}
		
}
