package com.healthycoderapp;

import static org.junit.Assert.assertThrows;
import static org.junit.Assume.assumeTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class BMICalculatorTest {
	
	private String environment = "dev";
	
	@Nested
	class FindCoderWithTheWorstBMI{
		
		void should_ReturnCoderWithWorstBMI_When_CoderListNotEmpty() {
			
			//given
			List<Coder> coders = new ArrayList();
			coders.add(new Coder(1.85, 60));
			coders.add(new Coder(1.65, 95));
			coders.add(new Coder(1.90, 75));
			
			//when
			Coder worstCoder = BMICalculator.findCoderWithWorstBMI(coders);
			
			//then
			assertAll(
			() -> assertEquals(1.65, worstCoder.getHeight()),
			() -> assertEquals(95, worstCoder.getWeight()));
		}
		
		@Test
		void should_ReturnCoderWithWorstBMIInMs_When_CoderListHas10000Elements() {
			
			//given
			List<Coder> coders = new ArrayList();
			for(int i=0; i<10000; i++) {
					coders.add(new Coder(1.0+i, 1.0+i));
			}
			
			//when
			Executable executable = () -> BMICalculator.findCoderWithWorstBMI(coders);
			
			//then
			assertTimeout(Duration.ofMillis(500), executable);
		}
		
		@Test
		void should_ReturnNull_When_CoderListEmpty() {
			
			//given
			List<Coder> coders = new ArrayList();
			
			//when
			Coder worstCoder = BMICalculator.findCoderWithWorstBMI(coders);
			
			//then
			assertNull(worstCoder);
		}
	}
	
	@Nested
	class GetBMIScoreTests{
		
		@Test
		void should_ReturnCorrectBMIScoreArray_When_CoderListEmpty() {
			
			//given
			assumeTrue(BMICalculatorTest.this.environment.equals("prod"));
			List<Coder> coders = new ArrayList();
			coders.add(new Coder(1.80, 60));
			coders.add(new Coder(1.82, 98.0));
			coders.add(new Coder(1.82, 64.7));
			double[] expected = {18.52, 29.59, 19.53};
			
			//when
			double BMIScores[] = BMICalculator.getBMIScores(coders);
			
			//then
			assertArrayEquals(expected, BMIScores);
		}
	}
	
	@Nested
	class isDietRecommendedTests{
		
		@ParameterizedTest
		@ValueSource(doubles = {89.0, 95.0, 110.0})
		void should_ReturnTrue_When_DietRecommended(Double coderWeight) {
			
			//given
			double weight = coderWeight;
			double height = 1.72;
			
			//when
			boolean recommended = BMICalculator.isDietRecommended(weight, height);
			
			//then
			assertTrue(recommended);
		}
		
		@ParameterizedTest(name = "weight={0}, height={1}")
		@CsvSource(value = {"89.0, 1.72", "95.0, 1.75", "110.0, 1.78"})
		void should_ReturnTrue_When_DietIsRecommended(Double coderWeight, Double coderHeight) {
			
			//given
			double weight = coderWeight;
			double height = coderHeight;
			
			//when
			boolean recommended = BMICalculator.isDietRecommended(weight, height);
			
			//then
			assertTrue(recommended);
		}
		
		@ParameterizedTest(name = "weight={0}, height={1}")
		@CsvFileSource(resources = "/diet-recommended-input-data.csv", numLinesToSkip = 1)
		void should_ReturnTrue_When_DietIsRecommendedFile(Double coderWeight, Double coderHeight) {
			
			//given
			double weight = coderWeight;
			double height = coderHeight;
			
			//when
			boolean recommended = BMICalculator.isDietRecommended(weight, height);
			
			//then
			assertTrue(recommended);
		}
		
		@Test
		void should_ReturnFalse_When_DietNotRecommended() {
			
			//given
			double weight = 65.0;
			double height = 1.95;
			
			//when
			boolean recommended = BMICalculator.isDietRecommended(weight, height);
			
			//then
			assertFalse(recommended);
		}
		
		@Test
		void should_ThrowArithmeticException_When_heightZero() {
			
			//given
			double weight = 65.0;
			double height = 0.0;
			
			//when
			Executable executable = () -> BMICalculator.isDietRecommended(weight, height);
			
			//then
			assertThrows(ArithmeticException.class, executable);
		}
		
	}
	
}
