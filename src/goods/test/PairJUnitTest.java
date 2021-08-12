package goods.test;

import static org.junit.Assert.*;

import org.junit.Test;

import goods.util.Pair;

public class PairJUnitTest {

	@Test
	public void testToString() {
		Pair<Integer, String> pair = new Pair<Integer, String>();
		pair.setFirst(1);
		pair.setSecond("test");
		
		System.out.println(pair.toString());
		
		Pair<Integer, String> pair2 = new Pair<Integer, String>(1, "test");		
		assertEquals("Expected pair equal to pair2", pair, pair2);
	}

	
}
