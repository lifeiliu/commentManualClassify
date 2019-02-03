
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


public class Similarity {
    private static final int DEFAULT_K = 3;
    private int k;
    private static final Pattern SPACE_REG = Pattern.compile("\\s+");

    public Similarity(int k){
        if(k <= 0)
            throw new IllegalArgumentException("k has to be a positive number");
        this.k = k ;
    }


    public Similarity(){
        this(DEFAULT_K);
    }

    private Map<String, Integer> getShingles(final String s){
        HashMap<String,Integer> shingles = new HashMap<>();

        String deSpacedString = SPACE_REG.matcher(s).replaceAll(" ");

        for(int i = 0; i < s.length() - k + 1; i++){
            String shingle = deSpacedString.substring(i, i + k);
            Integer oldValue = shingles.get(shingle);
            if (oldValue != null){
                shingles.replace(shingle, oldValue + 1);
            }else {
                shingles.put(shingle, 1);
            }
        }

        return shingles;
    }

    public double jaccardSimilarity(String s1, String s2){
        if (s1 == null) {
            throw new NullPointerException("s1 must not be null");
        }

        if (s2 == null) {
            throw new NullPointerException("s2 must not be null");
        }

        if (s1.equals(s2)) {
            return 1;
        }

        Map<String, Integer> KShingle1 = getShingles(s1);
        Map<String, Integer> KShingle2 = getShingles(s2);

        Set<String> union = new HashSet<>();
        union.addAll(KShingle1.keySet());
        union.addAll(KShingle2.keySet());

        int intersection = KShingle1.keySet().size() + KShingle2.keySet().size() - union.size();

        return 1.0 * intersection / union.size();


    }

    private double norm(Map<String, Integer> shingles){
        double agg = 0;
        for (Map.Entry<String, Integer> entry : shingles.entrySet()){
            agg += 1.0 * entry.getValue() * entry.getValue();
        }

        return Math.sqrt(agg);
    }

    private double docProduct(Map<String, Integer> shingle1, Map<String, Integer> shingle2){
        Map<String, Integer> smallShingle = shingle2;
        Map<String, Integer> largeShingle = shingle1;
        if (shingle1.size() < shingle2.size()) {
            smallShingle = shingle1;
            largeShingle = shingle2;
        }

        double agg = 0;
        for (Map.Entry<String, Integer> entry : smallShingle.entrySet()) {
            Integer i = largeShingle.get(entry.getKey());
            if (i == null) {
                continue;
            }
            agg += 1.0 * entry.getValue() * i;
        }

        return agg;
    }

    public double cosinSimilarity(String s1, String s2){
        Map<String, Integer> shingle1 = getShingles(s1);
        Map<String, Integer> shingle2 = getShingles(s2);

        return docProduct(shingle1,shingle2)
                / (norm(shingle1) * norm(shingle2));
    }

}
