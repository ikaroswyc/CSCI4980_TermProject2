package relation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recommand {
	/** @METHOD */
	public HashMap<String, Integer> statRecommendedList(HashMap<String, Integer> freqNumMap, int totalSize) {
		HashMap<String, Integer> freqStatMap = new HashMap<String, Integer>();

		for (Map.Entry<String, Integer> e : freqNumMap.entrySet()) {
			String keyElem = e.getKey();
			Integer valFreq = e.getValue();
			freqStatMap.put(keyElem, ((valFreq * 100) / totalSize));
		}
		return freqStatMap;
	}

	/** @METHOD */
	public HashMap<String, Integer> createRecommendedList(List<String> relationClassTableList) {
		HashMap<String, Integer> freqNumMap = new HashMap<String, Integer>();

		for (int i = 0; i < relationClassTableList.size(); i++) {
			String keyElem = relationClassTableList.get(i);
			Integer valFreq = freqNumMap.get(keyElem);

			if (valFreq != null) {
				valFreq += 1;
				freqNumMap.put(keyElem, valFreq);
			}
			else {
				freqNumMap.put(keyElem, 1);
			}
		}
		return freqNumMap;
	}
}
