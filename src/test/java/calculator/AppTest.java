package calculator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	try {
			assertEquals(calculator.App.calculateInput("add(1,2)"), 3);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	try {
			assertEquals(calculator.App.calculateInput("add(1,mult(2,3))"),7);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	try {
			assertEquals(calculator.App.calculateInput("mult(add(2,2),div(9,3))"), 12);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	try {
			assertEquals(calculator.App.calculateInput("let(a,5,add(a,a))"), 10);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	try {
			assertEquals(calculator.App.calculateInput("let(a,5,let(b,mult(a,10),add(b,a)))"), 55);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	try {
			assertEquals(calculator.App.calculateInput("let(a,let(b,10,add(b,b)),let(b,20,add(a,b)))"), 40);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
