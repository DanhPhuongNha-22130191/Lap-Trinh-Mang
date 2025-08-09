package easy;

public class MergeAlternately {
	private String mergeAlternately(String word1, String word2) {
		String str = "";
		int i = 0, j = 0;
		while (i < word1.length() || j < word2.length()) {
			if (i < word1.length()) {
				str += word1.charAt(i);
				i++;
			}
			if (j < word2.length()) {
				str += word2.charAt(j);
				j++;
			}
		}
		return str;
	}

	public static void main(String[] args) {
		MergeAlternately ex1 = new MergeAlternately();
		String word1 = "abc";
		String word2 = "def";
		System.out.println(ex1.mergeAlternately(word1, word2));
	}
}
