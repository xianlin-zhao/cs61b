import org.junit.Test;
import static org.junit.Assert.*;

public class TestOffByOne {

    // You must use this CharacterComparator and not instantiate
    // new ones, or the autograder might be upset.
    static CharacterComparator offByOne = new OffByOne();

    // Your tests go here.
    @Test
    public void testOffByOne1() {
        assertFalse(offByOne.equalChars('a', 'w'));
        assertFalse(offByOne.equalChars('y', 'q'));
        assertTrue(offByOne.equalChars('x', 'y'));
        assertTrue(offByOne.equalChars('d', 'c'));
    }

    @Test
    public void testOffByOne2() {
        assertFalse(offByOne.equalChars('a', 'A'));
        assertFalse(offByOne.equalChars('R', 'q'));
        assertFalse(offByOne.equalChars('&', '|'));
        assertTrue(offByOne.equalChars('&', '%'));
    }
}
