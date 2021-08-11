package com.streams;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TestPractice {
	
	@Test
	public void testTenPeopleUnder18() throws IOException {
		List<Person> people = MockData.getPeople().stream()
				.filter(person -> person.getAge() <= 18)
				.limit(10)
				.collect(Collectors.toList());
		
		Assert.assertEquals("People size not 10", 10, people.size());
		
		for (Person person : people) {
			Assert.assertEquals("Age not 18", true, person.getAge() <= 18);
		}
	}
	
	@Test
	public void testRanges() {
		IntStream.range(0, 10).forEach(i -> Assert.assertEquals("Index outside of range", true, i < 10));
		IntStream.rangeClosed(0, 10).forEach(i -> Assert.assertEquals("Index outside of range", true, i < 11));
	}
	
	@Test
	public void testIterate() {
		List<Person> people = Stream.iterate(new Person(0, "adam", "white", "test.com", "m", 0), 
				person -> new Person(person.getAge() + 2, "adam", "white", "test.com", "m", person.getAge() + 2))
			.limit(10)
			.collect(Collectors.toList());
		
		for (Person person: people) {
			Assert.assertEquals("Age not even", true, person.getAge() % 2 == 0);
		}
	}
	
	@Test
	public void testMin() throws IOException {
		Person person = MockData.getPeople().stream()
				.min((people1, people2) -> people1.getAge() - people2.getAge()).get();
		Assert.assertEquals("Age not 1", 1, person.getAge().intValue());
	}
	
	@Test
	public void testMax() throws IOException {
		Person person = MockData.getPeople().stream()
				.max((people1, people2) -> people1.getAge() + people2.getAge()).get();
		Assert.assertEquals("Age not 91", 91, person.getAge().intValue());
	}
	
	@Test
	public void testDistinct() throws Exception {
	    final List<Integer> numbers = ImmutableList.of(1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 9, 9, 9);

	    List<Integer> distinctNumbers = numbers.stream()
	        .distinct()
	        .collect(Collectors.toList());

	    assertThat(distinctNumbers).hasSize(9);
	 }
	
	@Test
	public void testMap() throws IOException {
		List<String> emails = MockData.getPeople().stream()
				.map(p -> p.getEmail()).collect(Collectors.toList());
		
		Assert.assertEquals("Email count is wrong", 1000, emails.size());
		
		Double maxPrice = MockData.getCars().stream().map(c -> c.getPrice())
				.max((price1, price2) -> {
					if (price1 < price2) return 1;
					else if (price1 > price2) return -1; 
					else return 0;
				}).get();
		
		Assert.assertEquals("Price not 5005.16", 5005.16, maxPrice.doubleValue(), 0.0);
	}
	
	@Test
	public void testFindFirst() throws IOException {
		Person person = MockData.getPeople().stream()
				.filter(p -> p.getAge() > 5 && p.getAge() < 10).findFirst().get();
		Assert.assertEquals("Age not 6", 6, person.getAge().intValue());
		
	}
	
	@Test
	public void testSum() throws IOException {
		Integer total_age = MockData.getPeople().stream().mapToInt(Person::getAge).sum();
		Assert.assertEquals("Age not 50667", 50667, total_age.intValue());
	}
	
	@Test
	public void testAverage() throws IOException {
		Double total_age = MockData.getPeople().stream().mapToInt(Person::getAge).average().getAsDouble();
		Assert.assertEquals("Age not 50", 50, total_age.intValue());
	}
	
	@Test
	public void testGroupingBy() throws IOException {
		Map<Integer, Long> results = MockData.getPeople().stream().filter(p -> p.getAge() < 50)
				.collect(Collectors.groupingBy(Person::getAge, Collectors.counting()));
		
		Assert.assertEquals("Map count not 49", 49, results.size());
		
		Map<String, Optional<Double>> highestPriceByMake = MockData.getCars().stream().collect(
				Collectors.groupingBy(Car::getMake, 
						Collectors.mapping(Car::getPrice, 
								Collectors.reducing(BinaryOperator.maxBy(Comparator.naturalOrder())))));
		
		for (Map.Entry<String, Optional<Double>> entry : highestPriceByMake.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
			Assert.assertEquals("Price for Lexus not 94837.79", 94837.79, entry.getValue().get().doubleValue(), 0.0);
			return;
		}
	}
	
	@Test
	public void testReduction() throws IOException {
		Car car = MockData.getCars().stream().reduce((c, c2) -> c.getPrice() < c2.getPrice() ? c : c2).get();
		Assert.assertEquals("Price not 5005.16", 5005.16, car.getPrice(), 0.0);
	}
	
	@Test 
	public void testFlatMap() {
		List<ArrayList<String>> arrayListOfColors = Lists.newArrayList(
			      Lists.newArrayList("Green", "Green", "Blue"),
			      Lists.newArrayList("Red", "Red", "Blue"),
			      Lists.newArrayList("Yellow", "Yellow")
			  );
		
		List<String> colors = arrayListOfColors.stream().flatMap(List::stream)
				.filter(c -> !c.equals("Blue")).collect(Collectors.toList());
		
		for (String color: colors) {
			Assert.assertNotEquals("Color is blue", "Blue", color);
		}
	}
	
	@Test
	public void testJoining() {
		String[] colors = new String[] {"red", "green", "blue"};
		String color = Stream.of(colors).map(s -> s.toUpperCase()).collect(Collectors.joining("|"));
		Assert.assertEquals("Color string is wrong", "RED|GREEN|BLUE", color);
	}
	
	@Test
	public void testCollect() throws IOException {
		Set<String> emails = MockData.getPeople().stream().map(Person::getEmail)
				.collect(HashSet::new, HashSet::add, (set1, set2) -> set1.addAll(set2));
		Assert.assertEquals("Email count is not 1000", 1000, emails.size());
	}

}
