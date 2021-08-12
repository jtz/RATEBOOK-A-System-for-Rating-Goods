package goods.test;

import static org.junit.Assert.*;

import org.junit.Test;

import goods.model.ChildGoods;

public class ChildGoodsJUnitTest {

	@Test
	public void testSafeAge() {
		ChildGoods childGoods = new ChildGoods("123", "testName", "testBrand");
		assertTrue("Expected this is safe for child as 1", childGoods.safeAge(1));
		
		childGoods.setAgeRangeLow(1);
		assertTrue("Expected this is safe for child as 1", childGoods.safeAge(1));
		assertFalse("Expected this is not safe for child as 0", childGoods.safeAge(0));
		
		childGoods.setAgeRangeHigh(5);
		assertTrue("Expected this is safe for child as 1", childGoods.safeAge(1));
		assertFalse("Expected this is not safe for child as 0", childGoods.safeAge(0));
		assertFalse("Expected this is not safe for child as 8", childGoods.safeAge(8));
	}

}
