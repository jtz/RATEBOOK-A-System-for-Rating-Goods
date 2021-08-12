package goods.test;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import goods.model.ChildGoods;
import goods.model.FoodGoods;
import goods.model.Goods;
import goods.model.Goods.GoodsCategory;
import goods.model.GoodsException;
import goods.model.Review;

public class GoodsJUnitTest {

	@Test
	public void testGetGoodsCategory() {
		Goods good1 = new ChildGoods("123", "testName", "testBrand");
		assertEquals("Expected childGoods category as child", GoodsCategory.CHILD, good1.getGoodsCategory());
		
		Goods good2 = new FoodGoods("123", "testName", "testBrand");
		assertEquals("Expected foodGoods category as food", GoodsCategory.FOOD, good2.getGoodsCategory());
	}

	@Test
	public void testGoodsToTxt() {
		Goods good1 = new ChildGoods("123", "testName", "testBrand");
		String txt1 = good1.goodsToTxt();
		assertEquals("123|CHILD|testName|testBrand|-1.0|-1.0|0.0|0||", txt1);
		
		ChildGoods good2 = new ChildGoods("123", "testName", "testBrand");
		good2.setAgeRangeLow(1);
		good2.setAgeRangeHigh(3);
		String txt2 = good2.goodsToTxt();
		assertEquals("123|CHILD|testName|testBrand|-1.0|-1.0|0.0|0|1|3", txt2);
		
		
		Goods good3 = new FoodGoods("123", "testName", "testBrand");
		String txt3 = good3.goodsToTxt();
		assertEquals("123|FOOD|testName|testBrand|-1.0|-1.0|0.0|0", txt3);
	}

	@Test
	public void testTxtToGoods() {
		try {
			Goods good1 = Goods.txtToGoods("123|CHILD|testName|testBrand|0.0|0.0|0.0|0||");
			assertEquals("Expected childGoods category as child", GoodsCategory.CHILD, good1.getGoodsCategory());
		} catch (GoodsException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		try {
			// incorrect setting price
			Goods.txtToGoods("123|CHILD|testName|testBrand|test|0.0|0.0|0|||");
			fail("Expected get exception");
		} catch (GoodsException e) {
			System.out.println(e.getMessage());
		}		

	}

	@Test
	public void testGetBarcode() {
		Goods good1 = new ChildGoods("123", "testName", "testBrand");
		assertEquals("Expected get barcode 123", "123", good1.getBarcode());
	}

	@Test
	public void testGetName() {
		Goods good1 = new ChildGoods("123", "testName", "testBrand");
		assertEquals("Expected get name testName", "testName", good1.getName());
	}

	@Test
	public void testGetBrand() {
		Goods good1 = new ChildGoods("123", "testName", "testBrand");
		assertEquals("Expected get brand testBrand", "testBrand", good1.getBrand());
	}

	@Test
	public void testGetRateCount() {
		Goods good1 = new ChildGoods("123", "testName", "testBrand");
		good1.addRating(1);
		assertEquals("Expected get rate count 1", (Integer)1, good1.getRateCount());
		
		// rating as 7 is not valid
		good1.addRating(7);
		assertEquals("Expected get rate count 1", (Integer)1, good1.getRateCount());
		
		// rating as 3 is valid
		good1.addRating(3);
		assertEquals("Expected get rate count 2", (Integer)2, good1.getRateCount());
	}

	@Test
	public void testGetRating() {
		Goods good1 = new ChildGoods("123", "testName", "testBrand");
		good1.addRating(0);
		assertEquals("Expected get rating as 0", (Float)0f, good1.getRating());
		
		// rating as 7 is not valid
		good1.addRating(7);
		assertEquals("Expected get rating as 0", (Float)0f, good1.getRating());
		
		// rating as 5 is valid
		good1.addRating(5);
		assertEquals("Expected get rating as 2.5", (Float)2.5f, good1.getRating());
	}

	@Test
	public void testGetPriceRangeLow() {
		Goods good1 = new ChildGoods("123", "testName", "testBrand");
		assertEquals("Expected get price as -1, which mean not setting", Float.valueOf(-1.0f), good1.getPriceRangeLow());
		good1.addPrice(100f);
		assertEquals("Expected get price range low as 100", (Float)100f, good1.getPriceRangeLow());
		good1.addPrice(200f);
		// 200 > 100, so price range low still 100
		assertEquals("Expected get price range low as 100", (Float)100f, good1.getPriceRangeLow());
		good1.addPrice(50);
		// 50 < 100, so price range low become 50
		assertEquals("Expected get price range low as 100", (Float)50f, good1.getPriceRangeLow());		
	}

	@Test
	public void testGetPriceRangeHigh() {
		Goods good1 = new ChildGoods("123", "testName", "testBrand");
		assertEquals("Expected get price as -1, which mean not setting", Float.valueOf(-1.0f), good1.getPriceRangeHigh());
		good1.addPrice(100f);
		assertEquals("Expected get price range high as 100", (Float)100f, good1.getPriceRangeHigh());
		good1.addPrice(50f);
		// 50 < 100, so price range high still 100
		assertEquals("Expected get price range high as 100", (Float)100f, good1.getPriceRangeHigh());
		good1.addPrice(200);
		// 200 > 100, so price range high become 200
		assertEquals("Expected get price range high as 200", (Float)200f, good1.getPriceRangeHigh());	
	}

	@Test
	public void testGetReviews() {
		Goods good1 = new ChildGoods("123", "testName", "testBrand");
		assertEquals("Expected no review", 0, good1.getReviews().size());
		
		Review review = new Review("123", "test", new Date(), "testreview");
		good1.addReview(review);
		assertEquals("Expected get 1 review", 1, good1.getReviews().size());
		
		Review oneReview = good1.getReviews().get(0);
		assertEquals("Expected get review barcode 123", review.getBarcode(), oneReview.getBarcode());
		assertEquals("Expected get review client test", review.getClient(), oneReview.getClient());
		assertEquals("Expected get review review testreview", review.getReview(), oneReview.getReview());
	}

}
