import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
    // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }

    @Test
    public void testIsPalindrome1() {
        String s1 = "catrsd";
        String s2 = "abccba";
        String s3 = "awawa";
        assertFalse(palindrome.isPalindrome(s1));
        assertTrue(palindrome.isPalindrome(s2));
        assertTrue(palindrome.isPalindrome(s3));
    }

    @Test
    public void testIsPalindrome2() {
        String s1 = "aw";
        String s2 = "v";
        String s3 = "";
        assertFalse(palindrome.isPalindrome(s1));
        assertTrue(palindrome.isPalindrome(s2));
        assertTrue(palindrome.isPalindrome(s3));
    }

    @Test
    public void testIsPalindrome3() {
        CharacterComparator offByOne = new OffByOne();
        String s1 = "aw";
        String s2 = "v";
        String s3 = "abcddab";
        String s4 = "";
        assertFalse(palindrome.isPalindrome(s1, offByOne));
        assertTrue(palindrome.isPalindrome(s2, offByOne));
        assertTrue(palindrome.isPalindrome(s3, offByOne));
        assertTrue(palindrome.isPalindrome(s4, offByOne));
    }
}
