package fi.oulu.tol.sqat.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.oulu.tol.sqat.GildedRose;
import fi.oulu.tol.sqat.Item;

public class GildedRoseTest {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	}

	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	}
	
	@Test
	public void testMain() {
	    GildedRose.main(null);
	    
	    assertEquals("Failed print output", "OMGHAI!", outContent.toString().trim());
		
	}
	
	@Test
	public void exampleTest() {
		//create an inn, add an item, and simulate one day
		GildedRose inn = new GildedRose();
		inn.setItem(new Item("+5 Dexterity Vest", 0, 20));
		
		inn.oneDay();
		
		//access a list of items, get the quality of the one set
		List<Item> items = inn.getItems();
		int quality = items.get(0).getQuality();
		
		//assert quality has decreased by one
		assertEquals("Failed quality for Dexterity Vest", 19, quality);
	}
	
	@Test
	public void TestSulfuras() {
		//create an inn, add Sulfuras, a special item which never decreases in quality, and simulate one day
		GildedRose inn = new GildedRose();
		inn.setItem(new Item("Sulfuras, Hand of Ragnaros", 0, 80));
		// test what happens if the sellin is less than 0
		inn.setItem(new Item("Sulfuras, Hand of Ragnaros", -1, 80));
		// test what happens if the sellin is more than 0
		inn.setItem(new Item("Sulfuras, Hand of Ragnaros", 1, 80));
		
		inn.oneDay();
		
		//access a list of items, get the quality of the one set
		List<Item> items = inn.getItems();
		int quality0 = items.get(0).getQuality();
		int quality1 = items.get(0).getQuality();
		int quality2 = items.get(2).getQuality();
		
		//assert quality has not decreased
		assertEquals("Failed quality for Hand of Sulfuras", 80, quality0);
		assertEquals("Failed quality for Hand of Sulfuras", 80, quality1);
		assertEquals("Failed quality for Hand of Sulfuras", 80, quality2);
	}
	
	@Test
	public void TestBackstagePass() {
		// create an inn, add a Backstage Pass, which has special properties:
		// * Quality increases by 2 when there are 10 days or less
		// * Quality increases by 3 when there are 5 days or less 
		// * Quality drops to 0 after the concert
		GildedRose inn = new GildedRose();
		// quality should increase by 1
		inn.setItem(new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20));
		// quality should increase by 1
		inn.setItem(new Item("Backstage passes to a TAFKAL80ETC concert", 11, 20));
		// quality should increase by 2
		inn.setItem(new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20));
		// quality should increase by 2
		inn.setItem(new Item("Backstage passes to a TAFKAL80ETC concert", 6, 20));
		// quality should increase by 3
		inn.setItem(new Item("Backstage passes to a TAFKAL80ETC concert", 5, 20));
		// quality should be zero
		inn.setItem(new Item("Backstage passes to a TAFKAL80ETC concert", -1, 20));
		
		// simulate one day
		inn.oneDay();
		
		//access a list of items, get the quality of each
		List<Item> items = inn.getItems();
		int quality0 = items.get(0).getQuality();
		int quality1 = items.get(1).getQuality();
		int quality2 = items.get(2).getQuality();
		int quality3 = items.get(3).getQuality();
		int quality4 = items.get(4).getQuality();
		int quality5 = items.get(5).getQuality();
		
		//assert quality has changed
		assertEquals("Failed quality for Backstage Pass", 21, quality0);
		assertEquals("Failed quality for Backstage Pass", 21, quality1);
		assertEquals("Failed quality for Backstage Pass", 22, quality2);
		assertEquals("Failed quality for Backstage Pass", 22, quality3);
		assertEquals("Failed quality for Backstage Pass", 23, quality4);
		assertEquals("Failed quality for Backstage Pass", 0, quality5);
	}
	
	@Test
	public void TestBackstagePassLowSellinAndQualityOver50() {
		// create an inn, add a Backstage Pass, which has special properties:
		// * Quality increases by 2 when there are 10 days or less
		// * Quality increases by 3 when there are 5 days or less 
		// * Quality drops to 0 after the concert
		// * The Quality of an item is never more than 50
		GildedRose inn = new GildedRose();
		// quality should increase by 3
		inn.setItem(new Item("Backstage passes to a TAFKAL80ETC concert", 5, 50));

		// simulate one day
		inn.oneDay();
		
		//access a list of items, get the quality of each
		List<Item> items = inn.getItems();
		int quality = items.get(0).getQuality();

		//assert quality has changed
		assertEquals("Failed quality for Backstage Pass", 50, quality);

	}
	
	@Test
	public void TestAgedBrie() {
		//create an inn, add Aged Brie, which has special properties:
		// * "Aged Brie" actually increases in Quality the older it gets
		// * The Quality of an item is never more than 50
		GildedRose inn = new GildedRose();
		inn.setItem(new Item("Aged Brie", 2, 0));
		inn.setItem(new Item("Aged Brie", -5, 50));
		inn.setItem(new Item("Aged Brie", -5, 25));
		
		// simulate one day
		inn.oneDay();
		
		//access a list of items, get the quality of the one set
		List<Item> items = inn.getItems();
		int quality0 = items.get(0).getQuality();
		int quality1 = items.get(1).getQuality();
		int quality2 = items.get(2).getQuality();
		
		//assert quality has increased but is no more than 50
		assertEquals("Failed quality for Aged Brie", 1, quality0);
		assertEquals("Failed quality for Aged Brie", 50, quality1);
		assertEquals("Failed quality for Aged Brie", 27, quality2);
	}
	
	@Test
	public void TestNonSpecialItems() {
		//create an inn, add a non-special item, which has properties:
		// * All items have a SellIn value which denotes the number of days we have to sell the item
		// * All items have a Quality value which denotes how valuable the item is
		// * At the end of each day our system lowers both values for every item
		// * Once the sell by date has passed, Quality degrades twice as fast

		GildedRose inn = new GildedRose();
		// Quality and SellIn should decrease by 1
		inn.setItem(new Item("Elixir of the Mongoose", 5, 7));
		// Quality should decrease by 2 and SellIn by 1
		inn.setItem(new Item("Elixir of the Mongoose", -1, 7));

		// simulate one day
		inn.oneDay();
		
		//access a list of items, get the quality of the one set
		List<Item> items = inn.getItems();
		int quality0 = items.get(0).getQuality();
		int sellin0 = items.get(0).getSellIn();
		int quality1 = items.get(1).getQuality();
		int sellin1 = items.get(1).getSellIn();
		
		//assert quality and sellin have decreased
		assertEquals("Failed quality for Elixir of the Mongoose", 6, quality0);
		assertEquals("Failed sell in for Elixir of the Mongoose", 4, sellin0);
		assertEquals("Failed quality for Elixir of the Mongoose", 5, quality1);
		assertEquals("Failed sell in for Elixir of the Mongoose", -2, sellin1);

	}
	
	@Test
	public void TestNegativeQuality() {
		//create an inn, add a non-special item, which has properties:
		// * The Quality of an item is never negative
		GildedRose inn = new GildedRose();

		// Quality should not decrease, SellIn should decrease by 1
		inn.setItem(new Item("Elixir of the Mongoose", 0, 0));
		
		// simulate one day
		inn.oneDay();
		
		//access a list of items, get the quality of the one set
		List<Item> items = inn.getItems();
		int quality = items.get(0).getQuality();
		int sellin = items.get(0).getSellIn();
		
		
		//assert quality and sellin have decreased
		assertEquals("Failed quality for Elixir of the Mongoose", 0, quality);
		assertEquals("Failed sell in for Elixir of the Mongoose", -1, sellin);

	}
	
	@Test
	public void TestQualityOver50WithNormalItem() {
		//create an inn, add a non-special item, which has properties:
		// * The Quality of an item is never more than 50
		GildedRose inn = new GildedRose();

		inn.setItem(new Item("Elixir of the Mongoose", 5, 51));
		
		// simulate one day
		inn.oneDay();
		
		//access a list of items, get the quality of the one set
		List<Item> items = inn.getItems();
		int quality = items.get(0).getQuality();
		int sellin = items.get(0).getSellIn();
		
		
		//assert quality and sellin has decreased
		assertEquals("Failed quality for Elixir of the Mongoose", 50, quality);
		assertEquals("Failed sell in for Elixir of the Mongoose", 4, sellin);

	}
}
