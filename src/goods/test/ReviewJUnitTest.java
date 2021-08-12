package goods.test;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import goods.model.GoodsException;
import goods.model.Review;

public class ReviewJUnitTest {

	@Test
	public void testGetBarcode() {
		Review review = new Review("123","client1", new Date(), "testreview");
		assertEquals("Expected childGoods category as 123", "123", review.getBarcode());
	}
	

	@Test
	public void testGetClient() {
		Review review = new Review("123","client1", new Date(), "testreview");
		assertEquals("Expected childGoods client as client1", "client1", review.getClient());
	}

	@Test
	public void testGetCreateTime() {
		Date date = new Date();
		Review review = new Review("123","client1", date, "testreview");
		assertEquals(date, review.getCreateTime());
	}

	@Test
	public void testGetReview() {
		Review review = new Review("123","client1", new Date(), "testreview");
		assertEquals("testreview", review.getReview());
	}

	@Test
	public void testReviewToTxt() {
		Review review = new Review("123","client1", new Date(), "testreview");
		String reviewString = review.reviewToTxt();
		assertTrue(reviewString.startsWith("123|client1"));
		assertTrue(reviewString.endsWith("testreview"));
	}

	@Test
	public void testTxtToReview() {
		try {
			Review review = Review.txtToReview("123|client1|01-30-2021 16:01:07|testreview");
			assertEquals("Expected childGoods category as 123", "123", review.getBarcode());
		} catch (GoodsException e) {
			e.printStackTrace();
			fail();
		}
		
		try {
			// for incorrect date, expected get exception
			Review.txtToReview("123|client1|01-30-2021 test|testreview");
			fail();
		} catch (GoodsException e) {
			System.out.println(e.getMessage());
		}
	}

}
