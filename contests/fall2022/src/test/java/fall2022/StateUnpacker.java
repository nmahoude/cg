package fall2022;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import fast.read.FastReader;

public class StateUnpacker {

	public static void main(String[] args) {

		String input = """
^10 264
^0 0 0 0 4608 8704 12800 18944 0 0 2560 20992 16896 18944 12800 8704 20992 16896 8704 12800 16896
^0 0 0 0 16912 18944 16896 12800 0 0 0 16896 12800 12800 16896 18944 16896 12800 0 16896 16896
^0 4608 0 12800 8704 16896 0 0 0 0 16896 20992 20992 16896 16896 16896 8704 12800 0 18944 8704
^0 0 8704 20992 8752 0 0 0 0 0 0 18944 16896 8704 18944 8704 8704 20992 8704 0 0
^16896 20992 16896 16896 0 2576 0 0 13602 0 0 2560 12800 0 16896 8704 0 16896 16896 20992 16896
^8704 16896 16912 20992 18960 0 0 0 0 0 0 0 16896 18944 18944 0 18944 21024 16928 16896 8704
^20992 0 12800 12800 18944 16912 10752 2560 0 0 0 14848 8704 20992 20992 16896 18944 0 12800 0 20992
^16896 8704 8704 20992 16896 12800 20992 16896 16896 0 0 10752 16896 16896 20992 12800 0 0 0 8704 16896
^8704 0 16896 16896 8720 12800 16896 18944 16896 0 0 0 16896 18944 16896 12848 8704 0 16896 0 8704
^8704 20992 0 16896 12800 18944 20992 20992 16896 16896 8704 16896 16896 20992 21008 18944 12800 16896 0 20992 8704
""";

		String cleanInput = Stream.of(input.split("\n")).filter(s -> s.length() != 0 && s.charAt(0) == '^')
		    .map(s -> s.replace("^", " ")) // remove ^
		    .collect(Collectors.joining());

		FastReader in = FastReader.fromString(cleanInput);
		int myMatters = in.nextInt();
		int oppMatters = in.nextInt();
		System.out.println(myMatters+" "+oppMatters);
		try {
			while (true) {
				int packed = in.nextInt();
				System.out.println(State.unpack(packed));
			}
		} catch (Exception e) {
			// ignore
		}
	}

}
