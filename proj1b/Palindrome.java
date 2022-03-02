public class Palindrome {
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> dq = new LinkedListDeque<Character>();
        for (int i = 0; i < word.length(); i++) {
            Character ch = word.charAt(i);
            dq.addLast(ch);
        }
        return dq;
    }

    public boolean isPalindrome(String word) {
//        int len = word.length();
//        for (int i = 0; i < len / 2; i++) {
//            if(word.charAt(i) != word.charAt(len - 1 - i)) {
//                return false;
//            }
//        }
//        return true;

        Deque<Character> dq = wordToDeque(word);
        int len = dq.size();
        for (int i = 0; i < len / 2; i++) {
            char ch1 = dq.removeFirst();
            char ch2 = dq.removeLast();
            if (ch1 != ch2) {
                return false;
            }
        }
        return true;
    }

    public boolean isPalindrome(String word, CharacterComparator cc) {
        Deque<Character> dq = wordToDeque(word);
        int len = dq.size();
        for (int i = 0; i < len / 2; i++) {
            char ch1 = dq.removeFirst();
            char ch2 = dq.removeLast();
            if (!cc.equalChars(ch1, ch2)) {
                return false;
            }
        }
        return true;
    }
}
